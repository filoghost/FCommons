/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReflectFieldTest {

    @Test
    void notExisting() {
        ReflectField<Object> field = ReflectField.lookup(ClassWithFields.class, "notExisting");
        
        assertThatExceptionOfType(NoSuchFieldException.class).isThrownBy(() -> {
            assertThat(field.get(new ClassWithFields()));
        });
    }

    @Test
    void getPrivate() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(ClassWithFields.class, "privateObject");

        ClassWithFields instance = new ClassWithFields();
        instance.privateObject = "string";

        assertThat(field.get(instance)).isEqualTo("string");
    }

    @Test
    void setPrivate() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(ClassWithFields.class, "privateObject");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, true);
        
        assertThat(instance.privateObject).isEqualTo(true);
    }

    @Test
    void getStatic() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(ClassWithFields.class, "staticObject");

        ClassWithFields.staticObject = "string";

        assertThat(field.get(null)).isEqualTo("string");
        assertThat(field.getStatic()).isEqualTo("string");
    }

    @Test
    void setStatic() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(ClassWithFields.class, "staticObject");
        
        field.set(null, 1);
        assertThat(ClassWithFields.staticObject).isEqualTo(1);
        
        field.setStatic(2);
        assertThat(ClassWithFields.staticObject).isEqualTo(2);
    }

    @Test
    void getNonStaticAsStatic() {
        ReflectField<String> field = ReflectField.lookup(ClassWithFields.class, "string");

        assertThatExceptionOfType(InvalidInstanceException.class).isThrownBy(() -> {
            field.getStatic();
        });
    }

    @Test
    void setNonStaticAsStatic() {
        ReflectField<String> field = ReflectField.lookup(ClassWithFields.class, "string");

        assertThatExceptionOfType(InvalidInstanceException.class).isThrownBy(() -> {
            field.setStatic("abc");
        });
    }

    @Test
    void getNull() throws ReflectiveOperationException {
        ReflectField<String> field = ReflectField.lookup(ClassWithFields.class, "string");

        ClassWithFields instance = new ClassWithFields();
        instance.string = null;

        assertThat(field.get(instance)).isNull();
    }

    @Test
    void setNull() throws ReflectiveOperationException {
        ReflectField<String> field = ReflectField.lookup(ClassWithFields.class, "string");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, null);

        assertThat(instance.string).isNull();
    }

    @Test
    void getPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        instance.primitiveInt = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void setPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1);

        assertThat(instance.primitiveInt).isEqualTo(1);
    }

    @Test
    void setPrimitiveAsBoxed() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1);

        assertThat(instance.primitiveInt).isEqualTo(1);
    }

    @Test
    void setPrimitiveAsNull() {
        ReflectField<Integer> field = ReflectField.lookup(ClassWithFields.class, "primitiveInt");
        
        assertThatExceptionOfType(ReflectiveOperationException.class).isThrownBy(() -> {
            field.set(new ClassWithFields(), null);
        });
    }

    @Test
    void getPrimitiveAsBoxed() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        instance.primitiveInt = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void getWrongType() {
        ReflectField<Boolean> field = ReflectField.lookup(ClassWithFields.class, "string");

        ClassWithFields instance = new ClassWithFields();
        instance.string = "abc";
        
        assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> {
            Boolean x = field.get(instance);
        });
    }

    @Test
    void setWrongType() {
        ReflectField<Boolean> field = ReflectField.lookup(ClassWithFields.class, "string");

        assertThatExceptionOfType(ReflectiveOperationException.class).isThrownBy(() -> {
            field.set(new ClassWithFields(), true);
        });
    }
    
    
    private static class ClassWithFields {

        private Object privateObject;
        private static Object staticObject;
        public int primitiveInt;
        public String string;
        
    }

}
