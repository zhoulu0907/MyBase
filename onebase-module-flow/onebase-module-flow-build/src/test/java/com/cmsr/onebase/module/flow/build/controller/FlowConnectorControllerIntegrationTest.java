package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorService;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FlowConnectorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testConnectorId;

    @BeforeEach
    void setUp() {
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setConnectorUuid("test-integration-connector-" + System.currentTimeMillis());
        connector.setConnectorName("集成测试连接器");
        connector.setTypeCode("HTTP");
        connector.setDescription("用于集成测试的连接器");
        connector.setConfig("{\"type\":\"HTTP\",\"properties\":{}}");
        connectorRepository.save(connector);
        testConnectorId = connector.getId();
    }

    @Test
    void testSaveEnvironmentConfig_Integration_Success() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode devConfig = mapper.createObjectNode();
        devConfig.put("type", "object");
        devConfig.put("title", "DEV环境配置");
        ObjectNode devProperties = mapper.createObjectNode();
        devProperties.put("host", mapper.createObjectNode().put("type", "string").put("title", "主机地址"));
        devConfig.set("properties", devProperties);
        configMap.put("DEV", devConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        FlowConnectorDO updatedConnector = connectorRepository.getById(testConnectorId);
        org.junit.jupiter.api.Assertions.assertNotNull(updatedConnector.getConfig());
        org.junit.jupiter.api.Assertions.assertTrue(updatedConnector.getConfig().contains("DEV"));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_ConnectorNotFound() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode devConfig = mapper.createObjectNode();
        devConfig.put("type", "object");
        devConfig.put("title", "DEV环境配置");
        ObjectNode devProperties = mapper.createObjectNode();
        devProperties.put("host", mapper.createObjectNode().put("type", "string"));
        devConfig.set("properties", devProperties);
        configMap.put("DEV", devConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1123784))
                .andExpect(jsonPath("$.msg", containsString("连接器不存在")));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_EnvAlreadyExists() throws Exception {
        FlowConnectorDO connector = connectorRepository.getById(testConnectorId);
        connector.setConfig("{\"properties\":{\"DEV\":{\"type\":\"object\"}},\"_metadata\":{\"version\":1}}");
        connectorRepository.updateById(connector);

        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode devConfig = mapper.createObjectNode();
        devConfig.put("type", "object");
        ObjectNode devProperties = mapper.createObjectNode();
        devProperties.put("host", mapper.createObjectNode().put("type", "string"));
        devConfig.set("properties", devProperties);
        configMap.put("DEV", devConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1123793))
                .andExpect(jsonPath("$.msg", containsString("环境配置已存在")));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_MultipleEnvironments() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode devConfig = mapper.createObjectNode();
        devConfig.put("type", "object");
        devConfig.put("title", "DEV环境");
        ObjectNode devProperties = mapper.createObjectNode();
        devProperties.put("host", mapper.createObjectNode().put("type", "string"));
        devConfig.set("properties", devProperties);
        configMap.put("DEV", devConfig);

        ObjectNode testConfig = mapper.createObjectNode();
        testConfig.put("type", "object");
        testConfig.put("title", "TEST环境");
        ObjectNode testProperties = mapper.createObjectNode();
        testProperties.put("host", mapper.createObjectNode().put("type", "string"));
        testConfig.set("properties", testProperties);
        configMap.put("TEST", testConfig);

        ObjectNode prodConfig = mapper.createObjectNode();
        prodConfig.put("type", "object");
        prodConfig.put("title", "PROD环境");
        ObjectNode prodProperties = mapper.createObjectNode();
        prodProperties.put("apiUrl", mapper.createObjectNode().put("type", "string"));
        prodConfig.set("properties", prodProperties);
        configMap.put("PROD", prodConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        FlowConnectorDO updatedConnector = connectorRepository.getById(testConnectorId);
        String config = updatedConnector.getConfig();
        org.junit.jupiter.api.Assertions.assertTrue(config.contains("DEV"));
        org.junit.jupiter.api.Assertions.assertTrue(config.contains("TEST"));
        org.junit.jupiter.api.Assertions.assertTrue(config.contains("PROD"));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_InvalidConfigFormat() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode invalidConfig = mapper.createObjectNode();
        invalidConfig.put("type", "string");
        invalidConfig.put("title", "无效配置");
        configMap.put("DEV", invalidConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1123794));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_EmptyConfig() throws Exception {
        String requestBody = "{\"config\":{}}";

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveEnvironmentConfig_Integration_NullConfig() throws Exception {
        String requestBody = "{}";

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveEnvironmentConfig_Integration_ProductionEnv() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode prodConfig = mapper.createObjectNode();
        prodConfig.put("type", "object");
        prodConfig.put("title", "生产环境配置");
        ObjectNode prodProperties = mapper.createObjectNode();
        prodProperties.put("apiUrl", mapper.createObjectNode().put("type", "string").put("title", "API地址"));
        prodProperties.put("timeout", mapper.createObjectNode().put("type", "number").put("title", "超时时间"));
        prodConfig.set("properties", prodProperties);
        configMap.put("PROD", prodConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        FlowConnectorDO updatedConnector = connectorRepository.getById(testConnectorId);
        org.junit.jupiter.api.Assertions.assertNotNull(updatedConnector.getConfig());
        org.junit.jupiter.api.Assertions.assertTrue(updatedConnector.getConfig().contains("PROD"));
    }

    @Test
    void testSaveEnvironmentConfig_Integration_ValidateResponseStructure() throws Exception {
        Map<String, JsonNode> configMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode devConfig = mapper.createObjectNode();
        devConfig.put("type", "object");
        ObjectNode devProperties = mapper.createObjectNode();
        devProperties.put("host", mapper.createObjectNode().put("type", "string"));
        devConfig.set("properties", devProperties);
        configMap.put("DEV", devConfig);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(configMap);

        mockMvc.perform(put("/flow/connector/{id}/environments", testConnectorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());
    }
}
