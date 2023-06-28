package com.nova.cls.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nova.cls.exceptions.HttpException;
import com.nova.cls.exceptions.request.BadRequestException;
import com.nova.cls.network.constants.Codes;
import com.nova.cls.network.constants.Method;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Endpoint {
    private final Method method;
    private final RouteSegment[] segments;
    private final Set<String> mandatoryQueryParams;
    private final Set<String> possibleQueryParams;

    /**
     * Is meant to be used in conjunction with {@link Router}: it defines the common functional prefix,
     * e.g. "/api/good", and {@link Endpoint Endpoints} define the specific sub-routes with methods, e.g.
     * "/{id}" with "GET" method.
     *
     * @param method HTTP method.
     * @param route Route (may include route parameters, e.g. "/{id}").
     * @param mandatoryQueryParams Query parameters that must be present, or else an exception will be thrown.
     * @param possibleQueryParams Query parameters whose presence does NOT cause an exception. Set is considered a
     * superset of the mandatory set, and so any mandatory parameters not in the possible set are added automatically.
     */
    public Endpoint(Method method, String route, Set<String> mandatoryQueryParams, Set<String> possibleQueryParams) {
        this.method = method;
        // with correct URI format, array has a leading empty element
        String[] stringSegments = route.split("/");
        segments = new RouteSegment[stringSegments.length];
        for (int i = 0; i < stringSegments.length; i++) {
            // parametrized segment: stores the name with which it parses the parameters
            if (stringSegments[i].matches("\\{[^{}]+}")) {
                segments[i] = new RouteSegment(stringSegments[i].substring(1, stringSegments[i].length() - 1), true);
            }
            // normal segment: will be used to check for exact match
            else {
                segments[i] = new RouteSegment(stringSegments[i], false);
            }
        }
        this.mandatoryQueryParams = new HashSet<>(mandatoryQueryParams);
        this.possibleQueryParams = new HashSet<>(possibleQueryParams);
        this.possibleQueryParams.addAll(mandatoryQueryParams);
    }

    /**
     * Constructs an endpoint with exactly specified mandatory query parameters and implied possible parameters.
     */
    public Endpoint(Method method, String route, Set<String> mandatoryQueryParams) {
        this(method, route, mandatoryQueryParams, Set.of()); // mandatory params will be automatically possible
    }

    /**
     * Constructs an endpoint without any query parameters.
     */
    public Endpoint(Method method, String route) {
        this(method, route, Set.of());
    }


    /**
     * Returning null signals that this endpoint did not process the request.
     */
    public Response tryProcess(String[] subrouteSegments, String[] queryParamsValues, HttpExchange exchange) {
        try {
            if (!method.name().equalsIgnoreCase(exchange.getRequestMethod())) {
                return null; // wrong method
            }
            // incoming segments array also has a leading ""
            if (segments.length != subrouteSegments.length) {
                return null; // routes immediately do not match
            }

            Map<String, String> routeParams = new HashMap<>();
            for (int i = 0; i < segments.length; i++) {
                if (!segments[i].processSegment(subrouteSegments[i], routeParams)) {
                    return null; // routes do not match
                }
            }

            Map<String, String> queryParams = getQueryParams(queryParamsValues);

            return process(routeParams,
                queryParams,
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        } catch (HttpException e) {
            return new Response(e.getCode(), e.getMessage());
        } catch (JsonProcessingException | NumberFormatException e) {
            return new Response(Codes.BAD_REQUEST, "Invalid payload: " + e.getMessage());
        } catch (Exception e) {
            return new Response(Codes.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    private Map<String, String> getQueryParams(String[] queryParamsValues) {
        Map<String, String> queryParams = new HashMap<>();
        for (String paramPair : queryParamsValues) {
            String[] keyValue = paramPair.split("=");
            if (keyValue.length != 2) {
                throw new BadRequestException("Malformed query parameters");
            }
            if (!possibleQueryParams.contains(keyValue[0])) {
                throw new BadRequestException("Unknown query parameter " + keyValue[0]);
            }
            queryParams.put(keyValue[0], keyValue[1]);
        }
        for (String mandatoryParam : mandatoryQueryParams) {
            if (!queryParams.containsKey(mandatoryParam)) {
                throw new BadRequestException("Missing mandatory query parameter " + mandatoryParam);
            }
        }
        return queryParams;
    }

    protected abstract Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception;

    private static class RouteSegment {
        private final String segment;
        private final boolean parametrized;

        public RouteSegment(String segment, boolean parametrized) {
            this.segment = segment;
            this.parametrized = parametrized;
        }

        public boolean processSegment(String incomingSegment, Map<String, String> routeParams) {
            if (parametrized) {
                routeParams.put(segment, incomingSegment);
                return true;
            }
            return segment.equals(incomingSegment);
        }
    }
}
