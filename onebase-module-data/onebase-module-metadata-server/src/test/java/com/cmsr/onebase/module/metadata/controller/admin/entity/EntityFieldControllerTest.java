package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.test.core.ut.BaseDbAndRedisUnitTest;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 实体字段管理接口单元测试
 *
 * @author bty418
 * @date 2025-01-25
 */
@WebMvcTest(EntityFieldController.class)
class EntityFieldControllerTest extends BaseDbAndRedisUnitTest {

    private MockMvc mockMvc;

    @MockBean
    private MetadataEntityFieldService entityFieldService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetFieldTypes() throws Exception {
        // 准备测试数据
        List<FieldTypeConfigRespVO> mockTypes = Arrays.asList(
            createFieldTypeConfigRespVO("VARCHAR", "短文本", "TEXT", true, false, 255, 4000, null),
            createFieldTypeConfigRespVO("INTEGER", "整数", "NUMBER", true, false, 11, null, null)
        );

        // Mock服务方法
        when(entityFieldService.getFieldTypes()).thenReturn(mockTypes);

        // 执行测试
        mockMvc.perform(get("/metadata/entity-field/field-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].fieldType").value("VARCHAR"))
                .andExpect(jsonPath("$.data[0].displayName").value("短文本"))
                .andExpect(jsonPath("$.data[1].fieldType").value("INTEGER"))
                .andExpect(jsonPath("$.data[1].displayName").value("整数"));
    }

    @Test
    void testBatchCreateEntityFields() throws Exception {
        // 准备测试数据
        EntityFieldBatchCreateReqVO reqVO = createEntityFieldBatchCreateReqVO();
        EntityFieldBatchCreateRespVO mockResp = new EntityFieldBatchCreateRespVO();
        mockResp.setSuccessCount(2);
        mockResp.setFailureCount(0);
        mockResp.setFieldIds(Arrays.asList(3001L, 3002L));

        // Mock服务方法
        when(entityFieldService.batchCreateEntityFields(any(EntityFieldBatchCreateReqVO.class))).thenReturn(mockResp);

        // 执行测试
        mockMvc.perform(post("/metadata/entity-field/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failureCount").value(0))
                .andExpect(jsonPath("$.data.fieldIds.length()").value(2));
    }

    @Test
    void testCreateEntityField() throws Exception {
        // 准备测试数据
        EntityFieldSaveReqVO reqVO = createEntityFieldSaveReqVO();
        EntityFieldRespVO mockResp = createEntityFieldRespVO();

        // Mock服务方法
        when(entityFieldService.createEntityField(any(EntityFieldSaveReqVO.class))).thenReturn(3001L);
        when(entityFieldService.getEntityField(3001L)).thenReturn(mockResp);

        // 执行测试
        mockMvc.perform(post("/metadata/entity-field")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(3001))
                .andExpect(jsonPath("$.data.fieldName").value("username"))
                .andExpect(jsonPath("$.data.displayName").value("用户名"));
    }

    @Test
    void testGetEntityFieldList() throws Exception {
        // 准备测试数据
        List<EntityFieldRespVO> mockList = Arrays.asList(createEntityFieldRespVO());

        // Mock服务方法
        when(entityFieldService.getEntityFieldListByConditions(eq(2001L), any(), any())).thenReturn(mockList);

        // 执行测试
        mockMvc.perform(get("/metadata/entity-field/list")
                        .param("entityId", "2001")
                        .param("isSystemField", "false")
                        .param("keyword", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].fieldName").value("username"));
    }

    @Test
    void testGetEntityField() throws Exception {
        // 准备测试数据
        EntityFieldDetailRespVO mockDetail = createEntityFieldDetailRespVO();

        // Mock服务方法
        when(entityFieldService.getEntityFieldDetail(3001L)).thenReturn(mockDetail);

        // 执行测试
        mockMvc.perform(get("/metadata/entity-field/3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(3001))
                .andExpect(jsonPath("$.data.fieldName").value("username"))
                .andExpect(jsonPath("$.data.entityName").value("用户信息"));
    }

    @Test
    void testBatchUpdateEntityFields() throws Exception {
        // 准备测试数据
        EntityFieldBatchUpdateReqVO reqVO = createEntityFieldBatchUpdateReqVO();
        EntityFieldBatchUpdateRespVO mockResp = new EntityFieldBatchUpdateRespVO();
        mockResp.setSuccessCount(2);
        mockResp.setFailureCount(0);

        // Mock服务方法
        when(entityFieldService.batchUpdateEntityFields(any(EntityFieldBatchUpdateReqVO.class))).thenReturn(mockResp);

        // 执行测试
        mockMvc.perform(put("/metadata/entity-field/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failureCount").value(0));
    }

    @Test
    void testUpdateEntityField() throws Exception {
        // 准备测试数据
        EntityFieldSaveReqVO reqVO = createEntityFieldSaveReqVO();
        reqVO.setId(3001L);
        reqVO.setDisplayName("登录用户名");

        // 执行测试
        mockMvc.perform(put("/metadata/entity-field/3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDeleteEntityField() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/metadata/entity-field/3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testBatchSortEntityFields() throws Exception {
        // 准备测试数据
        EntityFieldBatchSortReqVO reqVO = createEntityFieldBatchSortReqVO();

        // 执行测试
        mockMvc.perform(put("/metadata/entity-field/batch-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    // 辅助方法 - 创建测试数据
    private FieldTypeConfigRespVO createFieldTypeConfigRespVO(String fieldType, String displayName, String category,
                                                              Boolean supportLength, Boolean supportDecimal,
                                                              Integer defaultLength, Integer maxLength, Integer defaultDecimal) {
        FieldTypeConfigRespVO vo = new FieldTypeConfigRespVO();
        vo.setFieldType(fieldType);
        vo.setDisplayName(displayName);
        vo.setCategory(category);
        vo.setSupportLength(supportLength);
        vo.setSupportDecimal(supportDecimal);
        vo.setDefaultLength(defaultLength);
        vo.setMaxLength(maxLength);
        vo.setDefaultDecimal(defaultDecimal);
        return vo;
    }

    private EntityFieldBatchCreateReqVO createEntityFieldBatchCreateReqVO() {
        EntityFieldBatchCreateReqVO vo = new EntityFieldBatchCreateReqVO();
        vo.setEntityId(2001L);
        vo.setAppId(12345L);

        EntityFieldCreateItemVO field1 = new EntityFieldCreateItemVO();
        field1.setFieldName("username");
        field1.setDisplayName("用户名");
        field1.setFieldType("VARCHAR");
        field1.setDataLength(50);
        field1.setDescription("系统登录用户名");
        field1.setIsRequired(true);
        field1.setIsUnique(true);
        field1.setAllowNull(false);
        field1.setDefaultValue("");
        field1.setSortOrder(10);

        EntityFieldCreateItemVO field2 = new EntityFieldCreateItemVO();
        field2.setFieldName("email");
        field2.setDisplayName("邮箱");
        field2.setFieldType("VARCHAR");
        field2.setDataLength(100);
        field2.setDescription("用户邮箱地址");
        field2.setIsRequired(false);
        field2.setIsUnique(false);
        field2.setAllowNull(true);
        field2.setSortOrder(20);

        vo.setFields(Arrays.asList(field1, field2));
        return vo;
    }

    private EntityFieldSaveReqVO createEntityFieldSaveReqVO() {
        EntityFieldSaveReqVO vo = new EntityFieldSaveReqVO();
        vo.setEntityId(2001L);
        vo.setFieldName("username");
        vo.setDisplayName("用户名");
        vo.setFieldType("VARCHAR");
        vo.setDataLength(50);
        vo.setDescription("系统登录用户名");
        vo.setIsRequired(true);
        vo.setIsUnique(true);
        vo.setAllowNull(false);
        vo.setDefaultValue("");
        vo.setSortOrder(10);
        vo.setAppId(12345L);
        return vo;
    }

    private EntityFieldRespVO createEntityFieldRespVO() {
        EntityFieldRespVO vo = new EntityFieldRespVO();
        vo.setId(3001L);
        vo.setEntityId(2001L);
        vo.setFieldName("username");
        vo.setDisplayName("用户名");
        vo.setFieldType("VARCHAR");
        vo.setDataLength(50);
        vo.setDescription("系统登录用户名");
        vo.setIsRequired(true);
        vo.setIsUnique(true);
        vo.setAllowNull(false);
        vo.setIsSystemField(false);
        vo.setIsPrimaryKey(false);
        vo.setSortOrder(10);
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    private EntityFieldDetailRespVO createEntityFieldDetailRespVO() {
        EntityFieldDetailRespVO vo = new EntityFieldDetailRespVO();
        vo.setId(3001L);
        vo.setEntityId(2001L);
        vo.setEntityName("用户信息");
        vo.setFieldName("username");
        vo.setDisplayName("用户名");
        vo.setFieldType("VARCHAR");
        vo.setDataLength(50);
        vo.setDescription("系统登录用户名");
        vo.setIsRequired(true);
        vo.setIsUnique(true);
        vo.setAllowNull(false);
        vo.setIsSystemField(false);
        vo.setIsPrimaryKey(false);
        vo.setSortOrder(10);
        vo.setCreateTime(LocalDateTime.now());

        // 设置校验规则列表
        ValidationRuleItemVO rule = new ValidationRuleItemVO();
        rule.setId(4001L);
        rule.setValidationType("FORMAT_VALIDATION");
        rule.setValidationExpression("^[a-zA-Z0-9_]{4,20}$");
        rule.setErrorMessage("用户名只能包含字母、数字和下划线，长度4-20位");
        vo.setValidationRules(Arrays.asList(rule));

        return vo;
    }

    private EntityFieldBatchUpdateReqVO createEntityFieldBatchUpdateReqVO() {
        EntityFieldBatchUpdateReqVO vo = new EntityFieldBatchUpdateReqVO();
        vo.setEntityId(2001L);

        EntityFieldUpdateItemVO field1 = new EntityFieldUpdateItemVO();
        field1.setId(3001L);
        field1.setDisplayName("登录用户名");
        field1.setDescription("系统登录使用的用户名");
        field1.setIsRequired(true);
        field1.setDataLength(60);

        EntityFieldUpdateItemVO field2 = new EntityFieldUpdateItemVO();
        field2.setId(3002L);
        field2.setDisplayName("用户邮箱");
        field2.setDescription("用户的邮箱地址");
        field2.setIsRequired(false);
        field2.setDataLength(120);

        vo.setFields(Arrays.asList(field1, field2));
        return vo;
    }

    private EntityFieldBatchSortReqVO createEntityFieldBatchSortReqVO() {
        EntityFieldBatchSortReqVO vo = new EntityFieldBatchSortReqVO();
        vo.setEntityId(2001L);

        EntityFieldSortItemVO sort1 = new EntityFieldSortItemVO();
        sort1.setFieldId(3001L);
        sort1.setSortOrder(10);

        EntityFieldSortItemVO sort2 = new EntityFieldSortItemVO();
        sort2.setFieldId(3002L);
        sort2.setSortOrder(20);

        vo.setFieldSorts(Arrays.asList(sort1, sort2));
        return vo;
    }
} 