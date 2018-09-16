public class Parent {
    public static int A = 1;

    static {
        System.out.println("static init block");
        A = 2;
        B = 1;
    }

    public static int B = 2;

    public int D = 1;

    {
        System.out.println("init block");
        D = 2;
        E = 2;
    }

    public int E = 1;

    public Parent() {
        System.out.println("init");
    }

    public int getD() {
        return D;
    }

    public int getE() {
        return E;
    }
}
