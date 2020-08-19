/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigValueType<T> {

	public static final ConfigValueType<String> STRING = new ConfigValueType<>(
			(Object value) -> value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character,
			(Object value) -> value.toString(),
			(String value) -> value,
			ConfigErrors.valueNotString
	);

	public static final ConfigValueType<Boolean> BOOLEAN = new ConfigValueType<>(
			(Object value) -> value instanceof Boolean,
			(Object value) -> (Boolean) value,
			(Boolean value) -> value,
			ConfigErrors.valueNotBoolean
	);

	public static final ConfigValueType<Long> LONG = newNumberType(Number::longValue);
	public static final ConfigValueType<Integer> INTEGER = newNumberType(Number::intValue);
	public static final ConfigValueType<Short> SHORT = newNumberType(Number::shortValue);
	public static final ConfigValueType<Byte> BYTE = newNumberType(Number::byteValue);
	public static final ConfigValueType<Double> DOUBLE = newNumberType(Number::doubleValue);
	public static final ConfigValueType<Float> FLOAT = newNumberType(Number::floatValue);

	public static final ConfigValueType<List<String>> STRING_LIST = newListType(STRING);
	public static final ConfigValueType<List<Integer>> INTEGER_LIST = newListType(INTEGER);

	public static final ConfigValueType<List<?>> LIST = new ConfigValueType<>(
			(Object value) -> value instanceof List,
			(Object value) -> (List<?>) value,
			(List<?> value) -> value,
			ConfigErrors.valueNotList
	);

	public static final ConfigValueType<ConfigSection> CONFIG_SECTION = new ConfigValueType<>(
			(Object value) -> value instanceof ConfigurationSection,
			(Object value) -> new ConfigSection((ConfigurationSection) value),
			(ConfigSection value) -> value.getInternalYamlSection(),
			ConfigErrors.valueNotSection
	);

	private final Predicate<Object> isValidConfigValueFunction;
	private final Function<Object, T> fromConfigValueFunction;
	private final Function<T, Object> toConfigValueFunction;
	private final String notConvertibleErrorMessage;

	public ConfigValueType(
			Predicate<Object> isConvertibleFunction,
			Function<Object, T> fromConfigValueFunction,
			Function<T, Object> toConfigValueFunction,
			String notConvertibleErrorMessage) {
		this.isValidConfigValueFunction = isConvertibleFunction;
		this.fromConfigValueFunction = fromConfigValueFunction;
		this.toConfigValueFunction = toConfigValueFunction;
		this.notConvertibleErrorMessage = notConvertibleErrorMessage;
	}

	protected boolean isValidConfigValue(Object rawConfigValue) {
		return rawConfigValue != null && isValidConfigValueFunction.test(rawConfigValue);
	}

	protected T fromConfigValueRequired(Object rawConfigValue) throws MissingConfigValueException, InvalidConfigValueException {
		if (rawConfigValue == null) {
			throw new MissingConfigValueException(ConfigErrors.valueNotSet);
		}

		if (isValidConfigValueFunction.test(rawConfigValue)) {
			return fromConfigValueFunction.apply(rawConfigValue);
		} else {
			throw new InvalidConfigValueException(notConvertibleErrorMessage);
		}
	}

	protected T fromConfigValueOrDefault(Object rawConfigValue, T defaultValue) {
		if (rawConfigValue == null) {
			return defaultValue;
		}

		if (isValidConfigValueFunction.test(rawConfigValue)) {
			return fromConfigValueFunction.apply(rawConfigValue);
		} else {
			return defaultValue;
		}
	}

	protected Object toConfigValue(T value) {
		if (value != null) {
			return toConfigValueFunction.apply(value);
		} else {
			return null;
		}
	}


	private static <T extends Number> ConfigValueType<T> newNumberType(Function<Number, T> toTypeFunction) {
		return new ConfigValueType<>(
				(Object value) -> value instanceof Number,
				(Object value) -> toTypeFunction.apply((Number) value),
				(T value) -> value,
				ConfigErrors.valueNotNumber
		);
	}

	private static <T> ConfigValueType<List<T>> newListType(ConfigValueType<T> elementType) {
		return new ConfigValueType<>(
				(Object value) -> value instanceof List,
				(Object value) -> {
					List<T> result = new ArrayList<>();

					for (Object element : ((List<?>) value)) {
						if (elementType.isValidConfigValueFunction.test(element)) {
							result.add(elementType.fromConfigValueFunction.apply(element));
						}
					}

					return result;
				},
				(List<T> value) -> value,
				ConfigErrors.valueNotList
		);
	}

}
