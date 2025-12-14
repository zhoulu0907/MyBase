package com.cmsr.onebase.module.flow.context.graph;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/12/14 19:05
 */
@Slf4j
public class JsonGraphMapper {


    private static final Map<String, Class<? extends NodeData>> TYPE_CLASS_MAP = new HashMap<>();

    static {
        initializeTypeClassMap();
    }

    private static void initializeTypeClassMap() {
        try {
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(NodeType.class));
            Set<BeanDefinition> annotatedClasses = scanner.findCandidateComponents(
                    "com.cmsr.onebase.module.flow.context.graph.nodes");

            for (BeanDefinition beanDefinition : annotatedClasses) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (NodeData.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(NodeType.class)) {                    NodeType annotation = clazz.getAnnotation(NodeType.class);
                    if (annotation != null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends NodeData> nodeDataClass = (Class<? extends NodeData>) clazz;
                        TYPE_CLASS_MAP.put(annotation.value(), nodeDataClass);
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化节点数据类型映射失败", e);
        }
    }

    public static class JsonGraphNodeDeserializer extends JsonDeserializer<JsonGraphNode> {
        @Override
        public JsonGraphNode deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = mapper.readTree(p);

            JsonGraphNode graphNode = new JsonGraphNode();
            graphNode.setId(node.get("id") != null ? node.get("id").asText() : null);
            graphNode.setType(node.get("type") != null ? node.get("type").asText() : null);
            graphNode.setBlocks(mapper.convertValue(node.get("blocks"),
                    mapper.getTypeFactory().constructCollectionType(List.class, JsonGraphNode.class)));

            // 根据类型动态解析 data 字段
            JsonNode dataNode = node.get("data");
            if (dataNode != null && graphNode.getType() != null) {
                NodeData nodeData = getNodeDataByType(dataNode, graphNode.getType(), mapper);
                graphNode.setData(nodeData);
            }

            return graphNode;
        }

        private NodeData getNodeDataByType(JsonNode dataNode, String type, ObjectMapper mapper) throws com.fasterxml.jackson.core.JsonProcessingException {
            Class<? extends NodeData> dataClass = TYPE_CLASS_MAP.get(type);
            if (dataClass != null) {
                return mapper.treeToValue(dataNode, dataClass);
            }
            return mapper.treeToValue(dataNode, NodeData.class);
        }
    }

}
