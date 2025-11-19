package com.cmsr.onebase.module.etl.executor.util;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.JdbcInputNode;
import com.cmsr.onebase.module.etl.executor.graph.JdbcOutputNode;
import com.cmsr.onebase.module.etl.executor.graph.PairJoinNode;
import com.cmsr.onebase.module.etl.executor.graph.UnionNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:11
 */
public class JacksonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Node.class, new NodeDeserializer());
        //module.addDeserializer(NodeConfig.class, new NodeConfigDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    public static class NodeDeserializer extends JsonDeserializer<Node> {
        @Override
        public Node deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);

            String type = node.get("type").asText();
            switch (type) {
                case "jdbc_input":
                    return codec.treeToValue(node, JdbcInputNode.class);
                case "jdbc_output":
                    return codec.treeToValue(node, JdbcOutputNode.class);
                case "pair_join":
                    return codec.treeToValue(node, PairJoinNode.class);
                case "union":
                    return codec.treeToValue(node, UnionNode.class);
                default:
                    throw new RuntimeException("unknown node type: " + type);
            }
        }
    }

//    public static class NodeConfigDeserializer extends JsonDeserializer<NodeConfig> {
//        @Override
//        public NodeConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//            ObjectCodec codec = p.getCodec();
//            JsonNode node = codec.readTree(p);
//
//            // 从父节点获取 type 信息
//            Object parentNode = p.getCurrentValue();
//            if (parentNode instanceof Node) {
//                String type = ((Node) parentNode).getType();
//                switch (type) {
//                    case "jdbc_input":
//                        return codec.treeToValue(node, JdbcInputConfig.class);
//                    case "jdbc_output":
//                        return codec.treeToValue(node, JdbcOutputConfig.class);
//                    default:
//                        return codec.treeToValue(node, NodeConfig.class);
//                }
//            }
//
//            return codec.treeToValue(node, NodeConfig.class);
//        }
//    }

    public static <T> T fromJson(String inputJson, Class<T> valueType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(inputJson, valueType);
    }

    public static <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(content, valueType);
    }

}
