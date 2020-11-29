/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ValidReflectField<T> implements ReflectField<T> {
    
    private final Field field;

    private TypeInfo<T> fieldTypeInfo;
    private boolean initialized;

    protected ValidReflectField(Field field) {
        Preconditions.notNull(field, "field");
        this.field = field;
    }
    
    @SuppressWarnings("unchecked")
    private void init() throws ReflectiveOperationException {
        if (initialized) {
            return;
        }
        
        try {
            field.setAccessible(true);
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }

        fieldTypeInfo = (TypeInfo<T>) TypeInfo.of(field);
        
        initialized = true;
    }

    @Override
    public TypeInfo<T> getTypeInfo() throws ReflectiveOperationException {
        init();
        return fieldTypeInfo;
    }

    @Override
    public T getStatic() throws ReflectiveOperationException {
        return get(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Object instance) throws ReflectiveOperationException {
        init();
        checkInstance(instance);
        
        try {
            return (T) field.get(instance);
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public void setStatic(T value) throws ReflectiveOperationException {
        set(null, value);
    }

    @Override
    public void set(Object instance, T value) throws ReflectiveOperationException {
        init();
        checkInstance(instance);
        
        try {
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    private void checkInstance(Object instance) throws InvalidInstanceException {
        if (!Modifier.isStatic(getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when field is not static");
        }
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return field.getAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return field.getAnnotations();
    }

}
