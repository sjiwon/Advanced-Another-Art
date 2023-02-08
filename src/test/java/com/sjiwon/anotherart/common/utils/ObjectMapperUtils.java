package com.sjiwon.anotherart.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static String objectToJson(Object object) throws Exception {
        return MAPPER.writeValueAsString(object);
    }
}
