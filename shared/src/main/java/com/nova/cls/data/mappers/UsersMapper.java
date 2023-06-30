package com.nova.cls.data.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nova.cls.data.View;
import com.nova.cls.data.models.User;
import com.nova.cls.exceptions.MapperException;

import java.io.IOException;

public class UsersMapper extends ModelMapper<User> {
    private final ObjectReader loginReader;
    private final ObjectWriter loginWriter;
    public UsersMapper() {
        super(User.class);
        this.loginReader = mapper.readerWithView(View.Login.class);
        this.loginWriter = mapper.writerWithView(View.Login.class);
    }

    public User fromLoginJson(String json) throws MapperException {
        try {
            return loginReader.readValue(json, User.class);
        } catch (IOException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public String toLoginJson(User model) {
        try {
            return loginWriter.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            // properly written models are expected to be transformable to json
            // so this exception would be a programming failure
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
