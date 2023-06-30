package com.nova.cls.data.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nova.cls.data.View;
import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.exceptions.MapperException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base of the mapper between objects and their JSON representations using different JSON views.
 *
 * @param <Model> Model class.
 */
public abstract class ModelMapper<Model> {
    protected final ObjectMapper mapper;
    protected final ObjectReader readReader;
    protected final ObjectWriter readWriter;
    protected final ObjectReader createReader;
    protected final ObjectWriter createWriter;
    protected final ObjectReader updateReader;
    protected final ObjectWriter updateWriter;
    private final Class<Model> modelClass;
    private final Map<String, Function<String, ? extends Criterion<Model, ?>>> criteriaParsers;

    public ModelMapper(Class<Model> modelClass,
        Map<String, Function<String, ? extends Criterion<Model, ?>>> criteriaParsers) {
        mapper = new JsonMapper();
        this.modelClass = modelClass;
        this.criteriaParsers = criteriaParsers;

        // for server output
        readReader = mapper.readerWithView(View.Read.class);
        readWriter = mapper.writerWithView(View.Read.class);
        // for create input
        createReader = mapper.readerWithView(View.Create.class);
        createWriter = mapper.writerWithView(View.Create.class);
        // for update input
        updateReader = mapper.readerWithView(View.Update.class);
        updateWriter = mapper.writerWithView(View.Update.class);
    }

    public ModelMapper(Class<Model> modelClass) {
        this(modelClass, Map.of());
    }

    public Model fromReadJson(String json) throws MapperException {
        try {
            return readReader.readValue(json, modelClass);
        } catch (IOException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public String toReadJson(Model model) {
        try {
            return readWriter.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Model> fromReadManyJson(String json) throws MapperException {
        try {
            // array type of Model class (modelClass) is definitely Model[]
            return List.of(readReader.readValue(json, (Class<Model[]>) modelClass.arrayType()));
        } catch (IOException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public String toReadManyJson(List<Model> models) {
        try {
            return readWriter.writeValueAsString(models);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<? extends Criterion<Model, ?>> fromCriteriaParams(Map<String, String> params) {
        return params.keySet()
            .stream()
            .filter(criteriaParsers::containsKey)
            .map(key -> criteriaParsers.get(key).apply(params.get(key)))
            .toList();
    }

    public String toCriteriaParams(List<? extends Criterion<Model, ?>> criteria) {
        return criteria.stream()
            .map(criterion -> criterion.getQueryParamName() + "=" + criterion.getValue().toString())
            .collect(Collectors.joining("&")); // similarly to the mirror method, assumes
    }

    public String toCriteriaParams(Map<String, String> criteria) {
        return criteria.keySet()
            .stream()
            .map(criterion -> criterion + "=" + criteria.get(criterion))
            .collect(Collectors.joining("&"));
    }

    public Model fromCreateJson(String json) throws MapperException {
        try {
            return createReader.readValue(json, modelClass);
        } catch (IOException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public String toCreateJson(Model model) {
        try {
            return createWriter.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Model fromUpdateJson(String json) throws MapperException {
        try {
            return updateReader.readValue(json, modelClass);
        } catch (IOException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public String toUpdateJson(Model model) {
        try {
            return updateWriter.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
