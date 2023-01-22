package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static String objectToJson(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
