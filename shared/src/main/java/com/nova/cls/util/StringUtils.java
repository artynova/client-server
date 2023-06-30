package com.nova.cls.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static String wrap(String string) {
        if (string == null) {
            return string;
        }
        return '\'' + string + '\'';
    }

    public static String camelCaseToHumanCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);
            if (Character.isUpperCase(currentChar) && i != 0) {
                result.append(" ");
            }
            result.append(Character.toLowerCase(currentChar));
        }
        return result.toString();
    }
}
