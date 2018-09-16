
import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.lang.reflect.Modifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * http://www.docjar.com/docs/api/sun/misc/Unsafe.html
 * http://mishadoff.com/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/
 *
 *
 *  Class sun.misc.Unsafe consists of 105 methods. There are, actually,
 *  few groups of important methods for manipulating with various entities.
 *  Here is some of them:

 *  Info. Just returns some low-level memory information.
 *
 *      addressSize
 *      pageSize
 *
 *  Objects. Provides methods for object and its fields manipulation.
 *
 *      allocateInstance
 *      objectFieldOffset
 *
 *  Classes. Provides methods for classes and static fields manipulation.
 *      staticFieldOffset
 *      defineClass
 *      defineAnonymousClass
 *      ensureClassInitialized
 *
 *  Arrays. Arrays manipulation.
 *      arrayBaseOffset
 *      arrayIndexScale
 *
 *  Synchronization. Low level primitives for synchronization.
 *      monitorEnter
 *      tryMonitorEnter
 *      monitorExit
 *      compareAndSwapInt
 *      putOrderedInt
 *
 *  Memory. Direct memory access methods.
 *      allocateMemory
 *      copyMemory
 *      freeMemory
 *      getAddress
 *      getInt
 *      putInt
 */
class UnsafeTest {
    static final Unsafe UNSAFE;

    static {
        try {
          Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
          unsafeField.setAccessible(true);
          UNSAFE = (Unsafe) unsafeField.get(null);
        } catch (Exception e) {
          throw new AssertionError();
        }
    }


    class A {
        private long a; // not initialized value
        private int ACCESS_ALLOWED = 1;

        public A() {
            this.a = 1; // initialization
        }

        public long a() {
            return this.a;
        }

        public boolean getAccess() {
            return ACCESS_ALLOWED == 42;
        }
    }

    static class SuperArray {
        private final static int BYTE = 1;

        private long size;
        private long address;

        public SuperArray(long size) {
            this.size = size;
            address = getUnsafe().allocateMemory(size * BYTE);
        }

        public void set(long i, byte value) {
            getUnsafe().putByte(address + i * BYTE, value);
        }

        public int get(long idx) {
            return getUnsafe().getByte(address + idx * BYTE);
        }

        public long size() {
            return size;
        }
    }

    public void allocateInstance() {

        A o1 = new A(); // constructor
        System.out.println(String.format("new A(): %s", o1.a())); // prints 1
        System.out.println(String.format("o1.getAccess(): %s", o1.getAccess()));
        try {
            Field f = o1.getClass().getDeclaredField("ACCESS_ALLOWED");
            UNSAFE.putInt(o1, UNSAFE.objectFieldOffset(f), 42);
            System.out.println(String.format("o1.getAccess(): %s", o1.getAccess()));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(" getDeclaredField(ACCESS_ALLOWED) error");
        }

        try {
            A o2 = A.class.newInstance(); // reflection
            System.out.println(String.format("A.class.newInstance(): %s", o2.a())); // prints 1
            A o3 = (A) UNSAFE.allocateInstance(A.class); // unsafe
            System.out.println(String.format("UNSAFE.allocateInstance %s", o3.a())); // prints 0
        } catch (InstantiationException e) {
            throw new RuntimeException(" newInstance error");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(" newInstance error");
        }

    }

    private static long normalize(int value) {
        if (value >= 0) {
            return value;
        }
        return (~0L >>> 32) & value;
    }

    public static long sizeOfSimple(Object object){
        return getUnsafe().getAddress(
            normalize(getUnsafe().getInt(object, 4L)) + 12L);
    }

    /*
     * go through all non-static fields including
     * all superclases, get offset for each field,
     * find maximum and add padding.
     *
     * In fact, for good, safe and accurate sizeof
     * function better to use java.lang.instrument
     * package, but it requires specifyng agent option
     * in your JVM.
     */
    public static long sizeOf(Object o) {
        Unsafe u = getUnsafe();
        HashSet<Field> fields = new HashSet<Field>();
        Class c = o.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }

        // get offset
        long maxSize = 0;
        for (Field f : fields) {
            long offset = u.objectFieldOffset(f);
            if (offset > maxSize) {
                maxSize = offset;
            }
        }

        return ((maxSize/8) + 1) * 8;   // padding
    }


    private static long toAddress(Object obj) {
        Object[] array = new Object[] {obj};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        return normalize(getUnsafe().getInt(array, baseOffset));
    }

    private static Object fromAddress(long address) {
        Object[] array = new Object[] {null};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        getUnsafe().putLong(array, baseOffset, address);
        return array[0];
    }

    public static Object shallowCopy(Object obj) {
        long size = sizeOf(obj);
        long start = toAddress(obj);
        long address = getUnsafe().allocateMemory(size);
        getUnsafe().copyMemory(start, address, size);
        return fromAddress(address);
    }


    public static Unsafe getUnsafe() {
        Class cc = sun.reflect.Reflection.getCallerClass(2);
        if (cc.getClassLoader() != null)
            throw new SecurityException("Unsafe");
        return UNSAFE;
    }

    public static void changePasswd() {
        String password = new String("l00k@myHor$e");
        String fake = new String(password.replaceAll(".", "?"));
        System.out.println(password); // l00k@myHor$e
        System.out.println(fake); // ????????????

        getUnsafe().copyMemory(
                  fake, 0L, null, toAddress(password), sizeOf(password));

        System.out.println(password); // ????????????
        System.out.println(fake); // ????????????
    }

    public static void MultiInheritance() {
        long intClassAddress = normalize(getUnsafe().getInt(new Integer(0), 4L));
        long strClassAddress = normalize(getUnsafe().getInt("", 4L));
        getUnsafe().putAddress(intClassAddress + 36, strClassAddress);
        //(String) ((Object) (new Integer(666)));
    }


    private static byte[] getClassContent() throws Exception {
        File f = new File("/home/mishadoff/tmp/A.class");
        FileInputStream input = new FileInputStream(f);
        byte[] content = new byte[(int)f.length()];
        input.read(content);
        input.close();
        return content;
    }

    public static void createClassFromClassFile() {
        try {
            byte[] classContents = getClassContent();
        } catch (Exception e) {
        }
        //Class c = getUnsafe().defineClass(null, classContents, 0, classContents.length);
        //c.getMethod("a").invoke(c.newInstance(), null); // 1
    }

    public static void throwExceptin() {
        //doesn't need try catch
        getUnsafe().throwException(new IOException());
    }

    public static void superArray() {
        long SUPER_SIZE = (long)Integer.MAX_VALUE * 2;
        SuperArray array = new SuperArray(SUPER_SIZE);
        System.out.println("Array size:" + array.size()); // 4294967294
        long sum = 0;
        for (int i = 0; i < 100; i++) {
            array.set((long)Integer.MAX_VALUE + i, (byte)3);
            sum += array.get((long)Integer.MAX_VALUE + i);
        }
        System.out.println("Sum of 100 elements:" + sum);  // 300
    }


    public static void hidePasswd() {
        String password = new String("l00k@myHor$e");
        System.out.println(password); // ????????????
        Field stringValue = null;
        try {
            stringValue = String.class.getDeclaredField("value");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        stringValue.setAccessible(true);
        try {
            char[] mem = (char[]) stringValue.get(password);

            for (int i=0; i < mem.length; i++) {
              mem[i] = '?';
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(password); // ????????????
    }

    /**
     * Returns the underlying {@link sun.misc.Unsafe} instance.
     *
     * @return The underlying unsafe memory instance.
     */
    public final Unsafe unsafe() {
      return UNSAFE;
    }

    public int getPinterSize() {
        return UNSAFE.addressSize();
    }

    public int getPageSize() {
        return UNSAFE.pageSize();
    }
}

