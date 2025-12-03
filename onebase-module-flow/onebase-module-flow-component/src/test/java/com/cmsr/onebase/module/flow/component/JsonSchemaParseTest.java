package com.cmsr.onebase.module.flow.component;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.component.utils.PropertyDefine;
import com.cmsr.onebase.module.flow.component.utils.SchemaParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JsonSchemaParseTest {
    @Test
    public void testParseJson() {
        String json = """
                {"hello": "1ued891h2v1877e2"}
                """;

        String jsonSchema = """
                {
                    "type": "STRUCTURE",
                    "properties": {
                        "hello": {
                            "type": "TEXT"
                        }
                    }
                }
                """;
        PropertyDefine jsonSchemaDef = JsonUtils.parseObject(jsonSchema, PropertyDefine.class);

        Map<String, Object> parseResult = SchemaParser.parseBySchemaDef(null, jsonSchemaDef);

        System.out.println(parseResult);
    }
}
