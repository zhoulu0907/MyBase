package com.cmsr.onebase.module.flow.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 动作配置 OpenAPI 格式转换器
 * <p>
 * 用于在 OpenAPI 格式和 FlowConnectorHttpDO 之间进行转换
 * <p>
 * 核心功能：
 * - 判断是否为 OpenAPI 格式
 * - OpenAPI 格式转换为实体字段
 * - 实体字段转换为 OpenAPI 格式
 *
 * @author onebase
 * @since 2026-03-18
 */
@Slf4j
@Component
public class HttpActionOpenApiConverter {

    /**
     * 判断是否为 OpenAPI 格式
     * <p>
     * OpenAPI 格式必须包含 path 和 method 字段
     *
     * @param config 配置 JSON
     * @return 是否为 OpenAPI 格式
     */
    public boolean isOpenApiFormat(JsonNode config) {
        return config != null
            && config.has("path")
            && config.has("method");
    }

    /**
     * 将 OpenAPI 格式应用到 FlowConnectorHttpDO
     * <p>
     * 解析 OpenAPI 格式的配置，并将数据映射到实体字段
     *
     * @param openApiConfig OpenAPI 格式配置
     * @param entity        目标实体
     */
    public void applyOpenApiConfig(JsonNode openApiConfig, FlowConnectorHttpDO entity) {
        if (openApiConfig == null || entity == null) {
            return;
        }

        // 基础字段映射
        entity.setRequestPath(getText(openApiConfig, "path"));
        entity.setRequestMethod(getText(openApiConfig, "method"));
        entity.setHttpName(getText(openApiConfig, "summary"));
        entity.setDescription(getText(openApiConfig, "description"));

        // 处理 x-onebase 扩展
        JsonNode xOnebase = openApiConfig.get("x-onebase");
        if (xOnebase != null) {
            // x-onebase 中的 actionName 优先级更高
            String actionName = getText(xOnebase, "actionName");
            if (actionName != null && !actionName.isEmpty()) {
                entity.setHttpName(actionName);
            }
            String actionDescription = getText(xOnebase, "actionDescription");
            if (actionDescription != null && !actionDescription.isEmpty()) {
                entity.setDescription(actionDescription);
            }
            entity.setSuccessCondition(toJsonString(xOnebase.get("successCondition")));
            entity.setInputSchema(toJsonString(xOnebase.get("inputs")));
            entity.setOutputSchema(toJsonString(xOnebase.get("outputs")));
        }

        // 处理 parameters - 拆分为 query、headers、path
        JsonNode parameters = openApiConfig.get("parameters");
        if (parameters != null && parameters.isArray()) {
            List<Map<String, Object>> queryParams = new ArrayList<>();
            List<Map<String, Object>> headerParams = new ArrayList<>();
            List<Map<String, Object>> pathParams = new ArrayList<>();

            for (JsonNode param : parameters) {
                String in = getText(param, "in");
                Map<String, Object> paramMap = convertParameterToMap(param);
                if ("query".equals(in)) {
                    queryParams.add(paramMap);
                } else if ("header".equals(in)) {
                    headerParams.add(paramMap);
                } else if ("path".equals(in)) {
                    pathParams.add(paramMap);
                }
            }

            // 合并 pathParams 到 queryParams（如果没有单独的字段存储）
            queryParams.addAll(pathParams);

            entity.setRequestQuery(toJsonString(queryParams));
            entity.setRequestHeaders(toJsonString(headerParams));
        }

        // 处理 requestBody
        JsonNode requestBody = openApiConfig.get("requestBody");
        if (requestBody != null) {
            JsonNode content = requestBody.get("content");
            if (content != null) {
                // 处理 application/json
                if (content.has("application/json")) {
                    JsonNode jsonContent = content.get("application/json");
                    JsonNode schema = jsonContent.get("schema");
                    if (schema != null) {
                        entity.setRequestBodyTemplate(toJsonString(schema));
                        entity.setRequestBodyType("JSON");
                    }
                }
                // 处理 application/x-www-form-urlencoded
                else if (content.has("application/x-www-form-urlencoded")) {
                    JsonNode formContent = content.get("application/x-www-form-urlencoded");
                    JsonNode schema = formContent.get("schema");
                    if (schema != null) {
                        entity.setRequestBodyTemplate(toJsonString(schema));
                        entity.setRequestBodyType("FORM");
                    }
                }
            }
        }

        // 处理 responses（可选，存储到 response_mapping）
        JsonNode responses = openApiConfig.get("responses");
        if (responses != null) {
            entity.setResponseMapping(toJsonString(responses));
        }
    }

    /**
     * 将 FlowConnectorHttpDO 转换为 OpenAPI 格式
     *
     * @param entity 实体
     * @param mapper ObjectMapper
     * @return OpenAPI 格式的 JSON
     */
    public ObjectNode toOpenApiFormat(FlowConnectorHttpDO entity, ObjectMapper mapper) {
        ObjectNode openApi = mapper.createObjectNode();

        // 基础字段
        openApi.put("path", entity.getRequestPath());
        openApi.put("method", entity.getRequestMethod());
        openApi.put("summary", entity.getHttpName());
        openApi.put("operationId", entity.getHttpCode());
        openApi.put("description", entity.getDescription());

        // 构建 parameters 数组
        ArrayNode parameters = mapper.createArrayNode();
        addParametersFromJson(parameters, entity.getRequestQuery(), "query", mapper);
        addParametersFromJson(parameters, entity.getRequestHeaders(), "header", mapper);
        openApi.set("parameters", parameters);

        // 构建 requestBody
        if (entity.getRequestBodyTemplate() != null) {
            ObjectNode requestBody = mapper.createObjectNode();
            ObjectNode content = mapper.createObjectNode();

            String bodyType = entity.getRequestBodyType();
            String contentType = "application/json";
            if ("FORM".equals(bodyType)) {
                contentType = "application/x-www-form-urlencoded";
            }

            ObjectNode contentTypeNode = mapper.createObjectNode();
            try {
                JsonNode schema = mapper.readTree(entity.getRequestBodyTemplate());
                contentTypeNode.set("schema", schema);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse requestBodyTemplate as JSON", e);
                ObjectNode schema = mapper.createObjectNode();
                schema.put("type", "object");
                contentTypeNode.set("schema", schema);
            }
            content.set(contentType, contentTypeNode);
            requestBody.set("content", content);
            openApi.set("requestBody", requestBody);
        }

        // 构建 x-onebase
        ObjectNode xOnebase = mapper.createObjectNode();
        xOnebase.put("actionName", entity.getHttpName());
        xOnebase.put("actionDescription", entity.getDescription());

        if (entity.getSuccessCondition() != null) {
            xOnebase.set("successCondition", parseJson(entity.getSuccessCondition(), mapper));
        }
        if (entity.getInputSchema() != null) {
            xOnebase.set("inputs", parseJson(entity.getInputSchema(), mapper));
        }
        if (entity.getOutputSchema() != null) {
            xOnebase.set("outputs", parseJson(entity.getOutputSchema(), mapper));
        }
        openApi.set("x-onebase", xOnebase);

        // 添加 responses（如果有）
        if (entity.getResponseMapping() != null) {
            openApi.set("responses", parseJson(entity.getResponseMapping(), mapper));
        }

        return openApi;
    }

    /**
     * 从 JSON 字符串添加参数到数组
     */
    private void addParametersFromJson(ArrayNode parameters, String jsonString, String in, ObjectMapper mapper) {
        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }

        try {
            JsonNode paramsArray = mapper.readTree(jsonString);
            if (paramsArray.isArray()) {
                for (JsonNode param : paramsArray) {
                    ObjectNode paramNode = mapper.createObjectNode();
                    paramNode.put("name", getText(param, "name"));
                    paramNode.put("in", in);
                    paramNode.put("required", getBoolean(param, "required", false));
                    paramNode.put("description", getText(param, "description"));

                    // 添加 schema
                    JsonNode schema = param.get("schema");
                    if (schema != null) {
                        paramNode.set("schema", schema);
                    } else {
                        ObjectNode defaultSchema = mapper.createObjectNode();
                        defaultSchema.put("type", "string");
                        paramNode.set("schema", defaultSchema);
                    }

                    parameters.add(paramNode);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse parameters JSON: {}", jsonString, e);
        }
    }

    /**
     * 将参数节点转换为 Map
     */
    private Map<String, Object> convertParameterToMap(JsonNode param) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getText(param, "name"));
        map.put("in", getText(param, "in"));
        map.put("required", getBoolean(param, "required", false));
        map.put("description", getText(param, "description"));

        JsonNode schema = param.get("schema");
        if (schema != null) {
            map.put("schema", convertJsonNodeToMap(schema));
        }

        return map;
    }

    /**
     * 将 JsonNode 转换为 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertJsonNodeToMap(JsonNode node) {
        if (node == null) {
            return new HashMap<>();
        }
        try {
            return new ObjectMapper().treeToValue(node, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert JsonNode to Map", e);
            return new HashMap<>();
        }
    }

    /**
     * 获取文本值
     */
    private String getText(JsonNode node, String field) {
        if (node == null || !node.has(field)) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode.isNull() ? null : fieldNode.asText();
    }

    /**
     * 获取文本值，带默认值
     */
    private String getText(JsonNode node, String field, String defaultValue) {
        String value = getText(node, field);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取布尔值
     */
    private boolean getBoolean(JsonNode node, String field, boolean defaultValue) {
        if (node == null || !node.has(field)) {
            return defaultValue;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode.isNull() ? defaultValue : fieldNode.asBoolean(defaultValue);
    }

    /**
     * 将 JsonNode 转换为 JSON 字符串
     */
    private String toJsonString(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert JsonNode to string", e);
            return null;
        }
    }

    /**
     * 将 List 转换为 JSON 字符串
     */
    private String toJsonString(List<?> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert List to string", e);
            return null;
        }
    }

    /**
     * 解析 JSON 字符串为 JsonNode
     */
    private JsonNode parseJson(String jsonString, ObjectMapper mapper) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON string: {}", jsonString, e);
            return null;
        }
    }
}