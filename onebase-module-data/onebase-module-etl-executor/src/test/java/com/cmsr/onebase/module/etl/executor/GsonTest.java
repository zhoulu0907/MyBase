package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.graph.conf.OutputField;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import org.junit.jupiter.api.Test;

public class GsonTest {

    @Test
    public void testObjectParseTest() {
        String json = """
                {
                    "targetFieldId": "ejc10280neu198ve9197ry37ney197v18"
                }
                """;

        OutputField outputField = GsonUtil.GSON.fromJson(json, OutputField.class);

    }

}
