package com.nova.cls.data.services;

import java.sql.SQLException;

/**
 * Adapter for SQL constraint exceptions that may be commonly encountered by non-technical operators during regular work.
 */
public class ConstraintExceptionAdapter {
    public static final int CONSTRAINT_ERROR_CODE = 19;

    public static boolean isConstraintException(SQLException e) {
        return e.getErrorCode() == CONSTRAINT_ERROR_CODE;
    }

    public static String getBetterMessage(SQLException e) {
        String message = e.getMessage();
        if (isMessageForConstraint(message, "UNIQUE")) {
            // initially it comes as TableName.fieldName, extracting fieldName
            String column = getProblem(message, "UNIQUE").split("\\.")[1];
            // columns in the database are in camelCase by project's convention
            String humanCase = camelCaseToHumanCase(column);
            return "supplied value for " + humanCase + " is not unique";
        }
        if (isMessageForConstraint(message, "NOTNULL")) {
            String column = getProblem(message, "NOTNULL").split("\\.")[1];
            String humanCase = camelCaseToHumanCase(column);
            return "supplied value for " + humanCase + " is null";
        }
        if (isMessageForConstraint(message, "FOREIGNKEY")) {
            return "reference to an entity that no longer exists";
        }
        if (isMessageForConstraint(message, "CHECK")) {
            return "condition " + getProblem(message, "CHECK") + " is not satisfied";
        }
        return message;
    }

    private static boolean isMessageForConstraint(String message, String constraintName) {
        return message.startsWith("[SQLITE_CONSTRAINT_" + constraintName + "]");
    }

    private static String getProblem(String message, String constraintName) {
        String backTrimmed = message.substring(
            ("[SQLITE_CONSTRAINT_" + constraintName + "] A " + constraintName + " constraint failed (" + constraintName
                + " constraint failed: ").length()); // remove not very readable prefix
        return backTrimmed.substring(0, backTrimmed.length() - 1); // remove trailing ")"
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
