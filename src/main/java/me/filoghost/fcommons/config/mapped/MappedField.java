/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.modifier.ChatColorsModifier;
import me.filoghost.fcommons.config.mapped.modifier.ValueModifier;
import me.filoghost.fcommons.reflection.ReflectField;
import me.filoghost.fcommons.reflection.TypeInfo;
import me.filoghost.fcommons.reflection.UnexpectedActualClassException;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappedField<T> {

    private static final List<ValueModifier<?, ?>> VALUE_MODIFIERS = ImmutableList.of(
            new ChatColorsModifier()
    );
    
    private final ReflectField<T> field;
    private final Converter<T, ?> converter;
    private final String configPath;
    private final List<Annotation> annotations;
    
    public MappedField(ReflectField<T> field) throws ReflectiveOperationException, ConfigMappingException {
        this.field = field;
        this.converter = ConverterRegistry.fromObjectType(getFieldTypeInfo(field));
        this.configPath = field.getName().replace("__", ".").replace("_", "-");
        this.annotations = Stream.concat(
                Arrays.stream(field.getAnnotations()),
                Arrays.stream(field.getDeclaringClass().getDeclaredAnnotations()))
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings("unchecked")
    private TypeInfo<T> getFieldTypeInfo(ReflectField<T> field) throws ReflectiveOperationException {
        TypeInfo<?> typeInfo = TypeInfo.of(field);
        if (typeInfo.getTypeClass() == field.getExpectedClass()) {
            return (TypeInfo<T>) typeInfo;
        } else {
            throw new UnexpectedActualClassException("actual field type \"" + typeInfo + "\""
                    + " doesn't have expected class \"" + field.getExpectedClass() + "\"");
        }
    }

    public boolean equalsConfigValue(Object mappedObject, ConfigSection config) throws ConfigMappingException {
        T fieldValue = readFromObject(mappedObject);
        ConfigValue configValue = config.get(configPath);

        return converter.equalsConfig(fieldValue, configValue);
    }

    public ConfigValue readConfigValueFromObject(Object mappedObject) throws ConfigMappingException {
        try {
            T fieldValue = readFromObject(mappedObject);

            if (fieldValue != null) {
                return converter.toConfigValue(fieldValue);
            } else {
                return ConfigValue.NULL;
            }

        } catch (ConfigMappingException e) {
            // Display field information in exception
            throw new ConfigMappingException(ConfigErrors.conversionFailed(this), e);
        }
    }

    public void setFieldValueFromConfig(Object mappedObject, ConfigSection config, Object context)
            throws ConfigMappingException, ConfigPostLoadException {
        ConfigValue configValue = config.get(configPath);
        if (configValue == null) {
            return;
        }

        try {
            T fieldValue = converter.toFieldValue(configValue, context);
            if (fieldValue == null) {
                return;
            }

            for (Annotation annotation : annotations) {
                fieldValue = applyValueModifiers(fieldValue, annotation);
            }

            // Field is written only if new value is not null (default field value is kept)
            writeToObject(mappedObject, fieldValue);

        } catch (ConfigMappingException e) {
            // Display field information in exception
            throw new ConfigMappingException(ConfigErrors.conversionFailed(this), e);
        }
    }

    private T readFromObject(Object mappedObject) throws ConfigMappingException {
        try {
            return field.get(mappedObject);
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.fieldReadError(this), e);
        }
    }

    private void writeToObject(Object mappedObject, T fieldValue) throws ConfigMappingException {
        try {
            field.set(mappedObject, fieldValue);
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.fieldWriteError(this), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <F, A extends Annotation> F applyValueModifiers(F fieldValue, A annotation) {
        for (ValueModifier<?, ?> modifier : VALUE_MODIFIERS) {
            if (modifier.isApplicable(annotation, fieldValue)) {
                fieldValue = ((ValueModifier<F, A>) modifier).transform(annotation, fieldValue);
            }
        }
        return fieldValue;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public String getConfigPath() {
        return configPath;
    }

}
