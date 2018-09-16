
public class MinorGC {
    private static final int _1MB = 1024 * 1024;

    public static void testAllocation() {
        byte[] allocation1 = new byte[2 * _1MB];
        byte[] allocation2 = new byte[2 * _1MB];
        byte[] allocation3 = new byte[2 * _1MB];
        byte[] allocation4 = new byte[4 * _1MB];
    }

    public static void testPretenureSizeThreshold() {
        byte[] allocation;
        allocation = new byte[4 * _1MB];
    }

    public static void testTenuringThreshold() {
        byte[] allocation1 = new byte[_1MB / 4];
        byte[] allocation2 = new byte[4 * _1MB];
        byte[] allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        byte[] allocation4 = new byte[4 * _1MB];
    }

    public static void testTenuringThreshold2() {
        byte[] allocation1 = new byte[_1MB / 4];
        byte[] allocation2 = new byte[_1MB / 4];
        byte[] allocation3 = new byte[4 * _1MB];
        byte[] allocation4 = new byte[4 * _1MB];
        allocation4 = null;
        allocation4 = new byte[4 * _1MB];

    }

    public static void testHandlePromotion() {
        byte[] allocation1 = new byte[2 * _1MB];
        byte[] allocation2 = new byte[2 * _1MB];
        byte[] allocation3 = new byte[2 * _1MB];
        allocation1 = null;
        byte[] allocation4 = new byte[2 * _1MB];
        byte[] allocation5 = new byte[2 * _1MB];
        byte[] allocation6 = new byte[2 * _1MB];
        allocation4 = null;
        allocation5 = null;
        allocation6 = null;
        byte[] allocation7 = new byte[2 * _1MB];
    }

    public static void main(String []args) {
        System.out.println("-------------- args[0]=" + args[0] + " ---------------");
        String type = args[0];
        if (type.equals("alloc")) {
            testAllocation();
        } else if (type.equals("pretenureSizeThreshold")) {
            testPretenureSizeThreshold();
        } else if (args[0].equals("tenuringThreshold")) {
            testTenuringThreshold();
        } else if (args[0].equals("tenuringThreshold2")) {
            testTenuringThreshold2();
        } else if (args[0].equals("handlePromotion")) {
            testHandlePromotion();
        } else {
            System.out.println("java MinorGC alloc|pretenureSizeThreshold|tenuringThreshold");
        }
    }
}
