package com.nova.cls.data.models;

/**
 * Grouping of different JSON views on models in the application.
 * Views are used:
 * <ol>
 *     <li>To validate that incoming messages' JSON payloads are structured exactly as expected.</li>
 *     <li>To instruct the object-JSON mapper which properties to serialize in JSON.</li>
 * </ol>
 */
public class Views {
    public static class ReadView {
    }

    public static class CreateView {
    }

    public static class UpdateView {
    }
}
