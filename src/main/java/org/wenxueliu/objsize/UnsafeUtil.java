package org.wenxueliu.objsize;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.wenxueliu.objsize.ExceptionUtil.rethrow;
import static org.wenxueliu.objsize.QuickMath.normalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for {@link sun.misc.Unsafe}.
 */
public final class UnsafeUtil {

    private static Logger log = LoggerFactory.getLogger(UnsafeUtil.class);
    /**
     * If this constant is {@code true}, then {@link #UNSAFE} refers to a usable {@link sun.misc.Unsafe} instance.
     */
    public static final boolean UNSAFE_AVAILABLE;

    /**
     * The {@link sun.misc.Unsafe} instance which is available and ready to use.
     */
    public static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            unsafe = findUnsafe();
            if (unsafe != null) {
                // test if unsafe has required methods...
                checkUnsafeInstance(unsafe);
            }
        } catch (Throwable t) {
            unsafe = null;
            logFailureToFindUnsafeDueTo(t);
        }
        UNSAFE = unsafe;
        UNSAFE_AVAILABLE = UNSAFE != null;
    }

    private UnsafeUtil() {
    }

    private static Unsafe findUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException se) {
            return AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
                @Override
                public Unsafe run() {
                    try {
                        Class<Unsafe> type = Unsafe.class;
                        try {
                            Field field = type.getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            return type.cast(field.get(type));
                        } catch (Exception e) {
                            for (Field field : type.getDeclaredFields()) {
                                if (type.isAssignableFrom(field.getType())) {
                                    field.setAccessible(true);
                                    return type.cast(field.get(type));
                                }
                            }
                        }
                    } catch (Throwable t) {
                        throw rethrow(t);
                    }
                    throw new RuntimeException("Unsafe unavailable");
                }
            });
        }
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private static void checkUnsafeInstance(Unsafe unsafe) {
        long arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
        byte[] buffer = new byte[(int) arrayBaseOffset + (2 * 8)];
        unsafe.putByte(buffer, arrayBaseOffset, (byte) 0x00);
        unsafe.putBoolean(buffer, arrayBaseOffset, false);
        unsafe.putChar(buffer, normalize(arrayBaseOffset, 2), '0');
        unsafe.putShort(buffer, normalize(arrayBaseOffset, 2), (short) 1);
        unsafe.putInt(buffer, normalize(arrayBaseOffset, 2), 2);
        unsafe.putFloat(buffer, normalize(arrayBaseOffset, 4), 3f);
        unsafe.putLong(buffer, normalize(arrayBaseOffset, 8), 4L);
        unsafe.putDouble(buffer, normalize(arrayBaseOffset, 8), 5d);
        unsafe.copyMemory(new byte[buffer.length], arrayBaseOffset, buffer, arrayBaseOffset, buffer.length);
    }

    private static void logFailureToFindUnsafeDueTo(final Throwable reason) {
        log.error("Unable to get an instance of Unsafe. Unsafe-based operations will be unavailable", reason);
    }
}
