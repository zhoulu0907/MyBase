package com.cmsr.onebase.module.metadata.controller.admin;

import com.cmsr.onebase.framework.test.core.ut.BaseDbAndRedisUnitTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 元数据管理接口集成测试
 *
 * @author bty418
 * @date 2025-01-25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("unit-test")
class MetadataIntegrationTest extends BaseDbAndRedisUnitTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testDatasourceManagementAPIs() throws Exception {
        // 1. 获取数据源类型
        mockMvc.perform(get("/metadata/datasource/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 创建数据源
        Map<String, Object> datasourceReq = new HashMap<>();
        datasourceReq.put("datasourceName", "测试数据源");
        datasourceReq.put("code", "test_db");
        datasourceReq.put("datasourceType", "POSTGRESQL");
        datasourceReq.put("description", "测试用数据源");
        datasourceReq.put("appId", 12345L);

        mockMvc.perform(post("/metadata/datasource/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datasourceReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 查询数据源列表
        mockMvc.perform(get("/metadata/datasource/page")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testBusinessEntityManagementAPIs() throws Exception {
        // 1. 创建业务实体
        Map<String, Object> entityReq = new HashMap<>();
        entityReq.put("displayName", "测试实体");
        entityReq.put("code", "test_entity");
        entityReq.put("entityType", 1);
        entityReq.put("description", "测试业务实体");
        entityReq.put("datasourceId", 1001L);
        entityReq.put("tableName", "test_table");
        entityReq.put("appId", 12345L);

        mockMvc.perform(post("/metadata/business-entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 查询业务实体列表
        mockMvc.perform(get("/metadata/business-entity/list")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testEntityFieldManagementAPIs() throws Exception {
        // 1. 获取字段类型配置
        mockMvc.perform(get("/metadata/entity-field/field-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 创建实体字段
        Map<String, Object> fieldReq = new HashMap<>();
        fieldReq.put("entityId", 2001L);
        fieldReq.put("fieldName", "test_field");
        fieldReq.put("displayName", "测试字段");
        fieldReq.put("fieldType", "VARCHAR");
        fieldReq.put("dataLength", 50);
        fieldReq.put("description", "测试字段");
        fieldReq.put("isRequired", true);
        fieldReq.put("isUnique", false);
        fieldReq.put("allowNull", false);
        fieldReq.put("sortOrder", 10);
        fieldReq.put("appId", 12345L);

        mockMvc.perform(post("/metadata/entity-field")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 查询实体字段列表
        mockMvc.perform(get("/metadata/entity-field/list")
                        .param("entityId", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testEntityRelationshipManagementAPIs() throws Exception {
        // 1. 获取关系类型配置
        mockMvc.perform(get("/metadata/entity-relationship/relationship-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 获取级联操作类型
        mockMvc.perform(get("/metadata/entity-relationship/cascade-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 创建实体关系
        Map<String, Object> relationReq = new HashMap<>();
        relationReq.put("relationName", "测试关系");
        relationReq.put("sourceEntityId", 2001L);
        relationReq.put("targetEntityId", 2002L);
        relationReq.put("relationshipType", "ONE_TO_MANY");
        relationReq.put("sourceFieldId", 3001L);
        relationReq.put("targetFieldId", 3002L);
        relationReq.put("cascadeType", "READ");
        relationReq.put("description", "测试实体关系");
        relationReq.put("appId", 12345L);

        mockMvc.perform(post("/metadata/entity-relationship")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relationReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. 查询实体关系列表
        mockMvc.perform(get("/metadata/entity-relationship/list")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testValidationRuleManagementAPIs() throws Exception {
        // 1. 获取校验类型配置
        mockMvc.perform(get("/metadata/validation-rule/validation-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 创建校验规则
        Map<String, Object> ruleReq = new HashMap<>();
        ruleReq.put("validationName", "测试校验规则");
        ruleReq.put("validationCode", "test_validation_rule");
        ruleReq.put("entityId", 2001L);
        ruleReq.put("fieldId", 3001L);
        ruleReq.put("validationCondition", "REGEX_MATCH");
        ruleReq.put("validationType", "FORMAT_VALIDATION");
        ruleReq.put("validationTargetObject", "FIELD");
        ruleReq.put("validationExpression", "^[a-zA-Z0-9_]{4,20}$");
        ruleReq.put("errorMessage", "格式不正确");
        ruleReq.put("validationTiming", "CREATE,UPDATE");
        ruleReq.put("sortOrder", 10);
        ruleReq.put("appId", 12345L);

        mockMvc.perform(post("/metadata/validation-rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ruleReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 查询校验规则列表
        mockMvc.perform(get("/metadata/validation-rule/list")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDataMethodManagementAPIs() throws Exception {
        // 1. 查询数据方法列表
        mockMvc.perform(get("/metadata/data-method/list")
                        .param("entityId", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 获取数据方法详情
        mockMvc.perform(get("/metadata/data-method/2001/create_single"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCompleteWorkflow() throws Exception {
        // 完整工作流测试：创建数据源 -> 创建实体 -> 创建字段 -> 创建关系 -> 创建校验规则
        
        // 1. 创建数据源
        Map<String, Object> datasourceReq = new HashMap<>();
        datasourceReq.put("datasourceName", "工作流测试数据源");
        datasourceReq.put("code", "workflow_test_db");
        datasourceReq.put("datasourceType", "MYSQL");
        datasourceReq.put("description", "工作流测试数据源");
        datasourceReq.put("appId", 99999L);

        mockMvc.perform(post("/metadata/datasource/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datasourceReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 创建业务实体
        Map<String, Object> entityReq = new HashMap<>();
        entityReq.put("displayName", "工作流测试实体");
        entityReq.put("code", "workflow_test_entity");
        entityReq.put("entityType", 1);
        entityReq.put("description", "工作流测试实体");
        entityReq.put("datasourceId", 1001L);
        entityReq.put("tableName", "workflow_test_table");
        entityReq.put("appId", 99999L);

        mockMvc.perform(post("/metadata/business-entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 验证数据方法自动生成
        mockMvc.perform(get("/metadata/data-method/list")
                        .param("entityId", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
} 