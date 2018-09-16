

public class ClassLoad {

    public static void main(String []args) {
        String testType = args[0];
        if (testType.equals("case1")) {
            System.out.println(SubClass.value);
        } else if (testType.equals("case2")) {
            SuperClass[] sca = new SuperClass[10];
        } else if (testType.equals("case3")) {
            System.out.println(ConstClass.HELLOWORLD);
        } else if (testType.equals("case4")) {
            System.out.println(Sub.A);
            System.out.println(Sub.B);
            System.out.println(Sub.C);
            Sub sub = new Sub();
            System.out.println(sub.getD());
            System.out.println(sub.getE());
        }

    }
}
