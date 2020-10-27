/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.Material;

public final class Preconditions {


    public static void notNull(Object object, String objectName) {
        if (object == null) {
            throw new NullPointerException(objectName + " cannot be null");
        }
    }

    public static void notEmpty(String string, String objectName) {
        notNull(string, objectName);
        if (string.isEmpty()) {
            throw new IllegalArgumentException(objectName + " cannot be empty");
        }
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }

    public static void checkIndex(int index, int size, String objectName) {
        checkArgument(size >= 0, "size cannot be negative");

        if (index < 0) {
            throw new IndexOutOfBoundsException(objectName + " (" + index + ") cannot be negative");
        }
        if (index >= size) {
            throw new IndexOutOfBoundsException(objectName + " (" + index + ") must be less than size (" + size + ")");
        }
    }

    public static void checkArgumentNotAir(Material material, String objectName) {
        notNull(material, objectName);
        if (MaterialsHelper.isAir(material)) {
            throw new IllegalArgumentException(objectName + " cannot be " + material);
        }
    }

}
