package com.nova.cls.data.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nova.cls.data.services.criteria.CriteriaAggregate;

import java.io.IOException;
import java.util.List;

/**
 * Base of the mapper between objects and their JSON representations using different JSON views.
 *
 * @param <Model> Model class.
 */
public abstract class ModelJsonMapper<Model> {
    protected final ObjectMapper mapper;
    protected final ObjectReader readReader;
    protected final ObjectWriter readWriter;
    protected final ObjectReader createReader;
    protected final ObjectWriter createWriter;
    protected final ObjectReader updateReader;
    protected final ObjectWriter updateWriter;
    private final Class<Model> modelClass;
    private final Class<? extends CriteriaAggregate<Model>> criteriaAggregateClass;

    public ModelJsonMapper(Class<Model> modelClass, Class<? extends CriteriaAggregate<Model>> criteriaAggregateClass) {
        mapper = new JsonMapper();
        this.modelClass = modelClass;
        this.criteriaAggregateClass = criteriaAggregateClass;

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

    public Model fromReadJson(String json) throws IOException {
        return readReader.readValue(json, modelClass);
    }

    public String toReadJson(Model model) throws JsonProcessingException {
        return readWriter.writeValueAsString(model);
    }

    @SuppressWarnings("unchecked")
    public List<Model> fromReadManyJson(String json) throws IOException {
        return List.of(readReader.readValue(json,
            (Class<Model[]>) modelClass.arrayType())); // array type of Model class (modelClass) is definitely Model[]
    }

    public String toReadManyJson(List<Model> models) throws JsonProcessingException {
        return readWriter.writeValueAsString(models);
    }

    public CriteriaAggregate<Model> fromCriteriaJson(String json) throws IOException {
        return readReader.readValue(json, criteriaAggregateClass);
    }

    public String toCriteriaJson(CriteriaAggregate<Model> criteria) throws JsonProcessingException {
        return readWriter.writeValueAsString(criteria);
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
