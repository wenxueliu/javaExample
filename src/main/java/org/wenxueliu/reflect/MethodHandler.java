package org.wenxueliu.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class MethodHandler {
    void test() {
        System.out.println("test method");
    }

    public static void main(String []args) {
        String methodName = "test";
        MethodHandler t = new MethodHandler();
        Class<?> paramTypes[];
        paramTypes = null;
        Method method = null;
        try {
            method = t.getClass().getMethod(methodName, paramTypes);
            method.invoke(t, (Object[])null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
