package com.cmsr.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationProcessor {

    public static void main(String[] args) throws Exception {
        BusinessService service = new BusinessService();
        process(service);
    }

    private static final String CURRENT_USER_ROLE = "USER";

    public static void process(Object target) throws InvocationTargetException, IllegalAccessException {

        Class<?> clazz = target.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequiresRole.class)) {
                checkPersion(method);
            }
            if (method.isAnnotationPresent(Loggable.class)) {
                logAndInvoke(target, method);
            }
        }
    }

    private static void checkPersion(Method method) {
        RequiresRole requiresRoleAnnotation = method.getAnnotation(RequiresRole.class);
        String[] requiredRoles = requiresRoleAnnotation.value();
        for (String role : requiredRoles) {
            if (role.equals(CURRENT_USER_ROLE)) {
                return;
            }
        }
        throw new SecurityException("Acess denied! Required role: "
                + String.join(",", requiredRoles));
    }

    private static void logAndInvoke(Object target, Method method)
            throws IllegalAccessException, InvocationTargetException {
        Loggable logAnnotation = method.getAnnotation(Loggable.class);
        System.out.println(logAnnotation.value()+"Method called: "+ method.getName());
        long start = System.currentTimeMillis();
        method.invoke(target);
        if (logAnnotation.trackTime()) {
            long duration = System.currentTimeMillis() - start;
            System.out.println("Execution time: " + duration + "ms");
        }
    }
}
