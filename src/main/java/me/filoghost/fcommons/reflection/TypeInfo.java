/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeInfo<T> {

    private final Type type;
    private final Class<T> typeClass;

    private TypeInfo(Type type, Class<T> typeClass) {
        this.type = type;
        this.typeClass = typeClass;
    }

    public Type getType() {
        return type;
    }
    
    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Type[] getTypeArguments() {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        } else {
            return null;
        }
    }

    public ReflectField<?>[] getDeclaredFields() throws ReflectiveOperationException {
        if (typeClass == null) {
            throw new ReflectiveOperationException("cannot read fields of type without class: " + type);
        }
        
        Field[] declaredFields;
        try {
            declaredFields = typeClass.getDeclaredFields();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        
        ReflectField<?>[] output = new ReflectField[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            output[i] = ReflectField.wrap(declaredFields[i]);
        }
        return output;
    }

    public T newInstance() throws ReflectiveOperationException {
        if (typeClass == null) {
            throw new ReflectiveOperationException("cannot create instance of type without class: " + type);
        }

        try {
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public static TypeInfo<?> of(Field field) throws ReflectiveOperationException {
        Preconditions.notNull(field, "field");

        Type genericType;
        try {
            genericType = field.getGenericType();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        return of(genericType);
    }

    public static <T> TypeInfo<T> of(Class<T> typeClass) {
        Preconditions.notNull(typeClass, "type");
        return new TypeInfo<>(typeClass, typeClass);
    }

    public static TypeInfo<?> of(Type type) {
        Preconditions.notNull(type, "type");
        return new TypeInfo<>(type, getClassFromType(type));
    }

    private static Class<?> getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class<?>) parameterizedType.getRawType();
            }
        }
        return null;
    }

}
