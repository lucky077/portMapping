package com.dota2imca.portMapping.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanUtils {

    /**
     * 浅克隆
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T clone(T obj) {
        T target = null;
        try {
            target = (T) obj.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        copy(target, obj);
        return target;
    }

    public static void copy(Object target, Object src) {

        Field[] declaredFields = getAllFields(src);
        for (Field field : declaredFields) {

            field.setAccessible(true);

            try {
                Object value = field.get(src);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            }

        }
    }

    public static Field[] getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;

    }
}