package com.github.tonbei.archangelsbow.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class NMSUtil {

    @Nullable
    public static String getVersion(){
        String[] packageParts = Bukkit.getServer().getClass().getPackage().getName().split("\\.");

        if (packageParts.length > 0 && packageParts[packageParts.length - 1].startsWith("v"))
            return packageParts[packageParts.length - 1];

        return null;
    }

    @Nullable
    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static Method getMethod(@NotNull Class<?> clazz, String methodName, Class<?>... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if (params.length > 0) {
                    if (Arrays.equals(method.getParameterTypes(), params)) {
                        method.setAccessible(true);
                        return method;
                    }
                } else {
                    method.setAccessible(true);
                    return method;
                }
            }
        }

        return null;
    }

    @Nullable
    public static Field getField(@NotNull Class<?> clazz, String fieldName) {
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }

        return null;
    }
}
