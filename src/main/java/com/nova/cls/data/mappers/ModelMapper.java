package com.nova.cls.data.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nova.cls.data.Views;
import com.nova.cls.data.services.criteria.Criterion;
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
        readReader = mapper.readerWithView(Views.ReadView.class);
        readWriter = mapper.writerWithView(Views.ReadView.class);
        // for create input
        createReader = mapper.readerWithView(Views.CreateView.class);
        createWriter = mapper.writerWithView(Views.CreateView.class);
        // for update input
        updateReader = mapper.readerWithView(Views.UpdateView.class);
        updateWriter = mapper.writerWithView(Views.UpdateView.class);
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

    public String toReadJson(Model model) throws MapperException {
        try {
            return readWriter.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new MapperException(e.getMessage(), e);
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

    public String toReadManyJson(List<Model> models) throws MapperException {
        try {
            return readWriter.writeValueAsString(models);
        } catch (JsonProcessingException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    /**
     * Default implementation is a non-op.
     */
    public List<? extends Criterion<Model, ?>> fromCriteriaParams(Map<String, String> params) throws MapperException {
        return params.keySet()
            .stream()
            .filter(criteriaParsers::containsKey)
            .map(key -> criteriaParsers.get(key).apply(params.get(key)))
            .toList();
    }

    /**
     * Default implementation is a non-op.
     */
    public String toCriteriaParams(List<Criterion<Model, ?>> criteria) throws JsonProcessingException {
        return criteria.stream()
            .map(criterion -> criterion.getQueryParamName() + "=" + criterion.getValue().toString())
            .collect(Collectors.joining("&")); // similarly to the mirror method, assumes
    }

    public Model fromCreateJson(String json) throws IOException {
        return createReader.readValue(json, modelClass);
    }

    public String toCreateJson(Model model) throws JsonProcessingException {
        return createWriter.writeValueAsString(model);
    }

    public Model fromUpdateJson(String json) throws IOException {
        return updateReader.readValue(json, modelClass);
    }

    public String toUpdateJson(Model model) throws JsonProcessingException {
        return updateWriter.writeValueAsString(model);
    }
}
