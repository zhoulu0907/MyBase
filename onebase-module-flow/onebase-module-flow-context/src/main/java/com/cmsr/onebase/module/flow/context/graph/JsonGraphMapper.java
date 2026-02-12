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
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 *                  @Date：2025/12/14 19:05
 */
@Slf4j
public class JsonGraphMapper {

    private static final Map<String, Class<? extends NodeData>> TYPE_CLASS_MAP = new HashMap<>();

    static {
        initializeTypeClassMap();
    }

    private static void initializeTypeClassMap() {
        try {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                    false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(NodeType.class));
            scanner.addIncludeFilter(new AssignableTypeFilter(NodeData.class));
            Set<BeanDefinition> annotatedClasses = scanner.findCandidateComponents("com.cmsr.onebase.module.flow");

            for (BeanDefinition beanDefinition : annotatedClasses) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (NodeData.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(NodeType.class)) {
                    NodeType annotation = clazz.getAnnotation(NodeType.class);
                    if (annotation != null) {
                        Class<? extends NodeData> nodeDataClass = (Class<? extends NodeData>) clazz;
                        TYPE_CLASS_MAP.put(annotation.value(), nodeDataClass);
                        log.debug("加载节点类型[{}] @{}", annotation.value(), nodeDataClass.getName());
                    }
                }
            }
            // 【DEBUG】打印所有注册的节点类型
            log.info("【DEBUG JsonGraphMapper】初始化完成，注册的节点类型: {}", TYPE_CLASS_MAP.keySet());
            log.debug("初始化节点数据类型映射成功");
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

        private NodeData getNodeDataByType(JsonNode dataNode, String type, ObjectMapper mapper)
                throws com.fasterxml.jackson.core.JsonProcessingException {
            Class<? extends NodeData> dataClass = TYPE_CLASS_MAP.get(type);
            // 【DEBUG】打印原始 JSON 数据，用于诊断字段映射问题
            log.info("【DEBUG JsonGraphMapper】节点类型:{}, 原始JSON数据:\n{}", type, dataNode.toPrettyString());
            log.info("【DEBUG JsonGraphMapper】映射到类:{}", type,
                    dataClass != null ? dataClass.getSimpleName() : "null");
            log.info("【DEBUG JsonGraphMapper】TYPE_CLASS_MAP包含的类型: {}", TYPE_CLASS_MAP.keySet());
            log.debug("获取到节点类型:{}, 节点内容: {}", type, dataNode.toString());

            if (dataClass != null) {
                NodeData result = mapper.treeToValue(dataNode, dataClass);
                log.info("【DEBUG JsonGraphMapper】反序列化成功: type={}, resultClass={}, result={}",
                        type, result.getClass().getSimpleName(), result);
                return result;
            }
            // 兼容性处理：connectr 类型映射到 CommonNodeData
            if ("connector".equals(type)) {
                log.info("【DEBUG JsonGraphMapper】connector类型映射到CommonNodeData");
                return mapper.treeToValue(dataNode,
                        com.cmsr.onebase.module.flow.context.graph.nodes.CommonNodeData.class);
            }
            log.warn("【DEBUG JsonGraphMapper】未知节点类型:{}, 使用基类NodeData", type);
            return mapper.treeToValue(dataNode, NodeData.class);
        }
    }

}
