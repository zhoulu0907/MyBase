package com.cmsr.onebase.module.etl.executor.util;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.node.*;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:11
 */
public class GsonUtil {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Node.class, new NodeDeserializer())
            .create();

    public static class NodeDeserializer implements JsonDeserializer<Node> {
        @Override
        public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            switch (type) {
                case "jdbc_input":
                    return context.deserialize(jsonObject, JdbcInputNode.class);
                case "jdbc_output":
                    return context.deserialize(jsonObject, JdbcOutputNode.class);
                case "join":
                    return context.deserialize(jsonObject, JoinNode.class);
                case "union":
                    return context.deserialize(jsonObject, UnionNode.class);
                default:
                    return context.deserialize(jsonObject, DefaultNode.class);
            }
        }
    }
}
