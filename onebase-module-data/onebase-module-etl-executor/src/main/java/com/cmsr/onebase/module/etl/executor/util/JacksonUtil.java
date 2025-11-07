package com.cmsr.onebase.module.etl.executor.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {

    public static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.findAndRegisterModules();
    }

    public static String toJson(Object o) {
        try {
            OBJECT_MAPPER.
        }
    }

}
