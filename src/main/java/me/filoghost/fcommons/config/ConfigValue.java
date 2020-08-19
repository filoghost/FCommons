/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;

public class ConfigValue {

	private static final ConfigValue EMPTY = new ConfigValue(null);

	private final Object rawConfigValue;

	public static <T> ConfigValue of(ConfigValueType<T> valueType, T value) {
		Preconditions.notNull(valueType, "valueType");
		Preconditions.notNull(value, "value");
		return new ConfigValue(valueType.toConfigValue(value));
	}

	protected static ConfigValue fromRawConfigValue(Object rawConfigValue) {
		if (rawConfigValue != null) {
			return new ConfigValue(rawConfigValue);
		} else {
			return EMPTY;
		}
	}

	private ConfigValue(Object rawConfigValue) {
		this.rawConfigValue = rawConfigValue;
	}

	protected Object getRawConfigValue() {
		return rawConfigValue;
	}

	public <T> T as(ConfigValueType<T> valueType) {
		return valueType.fromConfigValueOrDefault(rawConfigValue, null);
	}

	public <T> T asRequired(ConfigValueType<T> valueType) throws MissingConfigValueException, InvalidConfigValueException {
		return valueType.fromConfigValueRequired(rawConfigValue);
	}

	public <T> T asOrDefault(ConfigValueType<T> valueType, T defaultValue) {
		return valueType.fromConfigValueOrDefault(rawConfigValue, defaultValue);
	}

	public boolean isValidAs(ConfigValueType<?> configValueType) {
		return configValueType.isValidConfigValue(rawConfigValue);
	}

}
