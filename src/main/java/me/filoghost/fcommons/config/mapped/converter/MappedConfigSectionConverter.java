/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;
import me.filoghost.fcommons.config.mapped.ConfigMapper;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;
import me.filoghost.fcommons.reflection.TypeInfo;

public class MappedConfigSectionConverter<T extends MappedConfigSection> extends Converter<T, ConfigSection> {

    private final ConfigMapper<T> configMapper;

    public MappedConfigSectionConverter(TypeInfo<T> fieldTypeInfo) throws ConfigMappingException {
        super(ConfigType.SECTION);
        this.configMapper = new ConfigMapper<>(fieldTypeInfo);
    }

    @Override
    protected ConfigSection toConfigValue0(T mappedObject) throws ConfigMappingException {
        ConfigSection configSection = new ConfigSection();
        configMapper.setConfigFromFields(mappedObject, configSection);
        return configSection;
    }

    @Override
    protected T toFieldValue0(ConfigSection configSection) throws ConfigMappingException, ConfigValidateException {
        T mappedObject = configMapper.newMappedObjectInstance();
        configMapper.setFieldsFromConfig(mappedObject, configSection);
        return mappedObject;
    }

    @Override
    protected boolean equalsConfig0(T fieldValue, ConfigSection configSection) throws ConfigMappingException {
        if (fieldValue == null && configSection == null) {
            return true;
        } else if (fieldValue == null || configSection == null) {
            return false;
        }

        return configMapper.equalsConfig(fieldValue, configSection);
    }

    public static boolean supports(Class<?> typeClass) {
        return MappedConfigSection.class.isAssignableFrom(typeClass);
    }

}
