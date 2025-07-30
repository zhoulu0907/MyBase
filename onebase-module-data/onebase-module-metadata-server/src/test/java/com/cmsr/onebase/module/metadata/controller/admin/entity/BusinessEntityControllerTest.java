package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.test.core.ut.BaseDbAndRedisUnitTest;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
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
 * 业务实体管理接口单元测试
 *
 * @author bty418
 * @date 2025-01-25
 */
@WebMvcTest(BusinessEntityController.class)
class BusinessEntityControllerTest extends BaseDbAndRedisUnitTest {

    private MockMvc mockMvc;

    @MockBean
    private MetadataBusinessEntityService businessEntityService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCreateBusinessEntity() throws Exception {
        // 准备测试数据
        BusinessEntitySaveReqVO reqVO = createBusinessEntitySaveReqVO();
        BusinessEntityRespVO mockResp = createBusinessEntityRespVO();

        // Mock服务方法
        when(businessEntityService.createBusinessEntity(any(BusinessEntitySaveReqVO.class))).thenReturn(2001L);
        when(businessEntityService.getBusinessEntityDetail(2001L)).thenReturn(mockResp);

        // 执行测试
        mockMvc.perform(post("/metadata/business-entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(2001))
                .andExpect(jsonPath("$.data.displayName").value("用户信息"))
                .andExpect(jsonPath("$.data.code").value("user_info"));
    }

    @Test
    void testUpdateBusinessEntity() throws Exception {
        // 准备测试数据
        BusinessEntitySaveReqVO reqVO = createBusinessEntitySaveReqVO();
        reqVO.setId(2001L);
        reqVO.setDisplayName("用户信息_更新");

        // 执行测试
        mockMvc.perform(put("/metadata/business-entity/2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDeleteBusinessEntity() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/metadata/business-entity/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testGetBusinessEntity() throws Exception {
        // 准备测试数据
        BusinessEntityDetailRespVO mockDetail = createBusinessEntityDetailRespVO();

        // Mock服务方法
        when(businessEntityService.getBusinessEntityDetail(2001L)).thenReturn(mockDetail);

        // 执行测试
        mockMvc.perform(get("/metadata/business-entity/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(2001))
                .andExpect(jsonPath("$.data.displayName").value("用户信息"))
                .andExpect(jsonPath("$.data.code").value("user_info"))
                .andExpect(jsonPath("$.data.datasourceName").value("用户数据库"));
    }

    @Test
    void testGetBusinessEntityList() throws Exception {
        // 准备测试数据
        PageResult<BusinessEntityRespVO> mockPageResult = new PageResult<>();
        mockPageResult.setTotal(5L);
        mockPageResult.setList(Arrays.asList(createBusinessEntityRespVO()));

        // Mock服务方法
        when(businessEntityService.getBusinessEntityPage(any(BusinessEntityPageReqVO.class))).thenReturn(mockPageResult);

        // 执行测试
        mockMvc.perform(get("/metadata/business-entity/list")
                        .param("appId", "12345")
                        .param("pageNo", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(5))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].displayName").value("用户信息"));
    }

    @Test
    void testGetBusinessEntityListByDatasource() throws Exception {
        // 准备测试数据
        List<BusinessEntityRespVO> mockList = Arrays.asList(createBusinessEntityRespVO());

        // Mock服务方法
        when(businessEntityService.getBusinessEntityListByDatasource(1001L)).thenReturn(mockList);

        // 执行测试
        mockMvc.perform(get("/metadata/business-entity/list-by-datasource")
                        .param("datasourceId", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].displayName").value("用户信息"));
    }

    // 辅助方法 - 创建测试数据
    private BusinessEntitySaveReqVO createBusinessEntitySaveReqVO() {
        BusinessEntitySaveReqVO vo = new BusinessEntitySaveReqVO();
        vo.setDisplayName("用户信息");
        vo.setCode("user_info");
        vo.setEntityType(1);
        vo.setDescription("系统用户基础信息实体");
        vo.setDatasourceId(1001L);
        vo.setTableName("users");
        vo.setAppId(12345L);
        return vo;
    }

    private BusinessEntityRespVO createBusinessEntityRespVO() {
        BusinessEntityRespVO vo = new BusinessEntityRespVO();
        vo.setId(2001L);
        vo.setDisplayName("用户信息");
        vo.setCode("user_info");
        vo.setEntityType(1);
        vo.setDescription("系统用户基础信息实体");
        vo.setDatasourceId(1001L);
        vo.setDatasourceName("用户数据库");
        vo.setTableName("users");
        vo.setFieldCount(8);
        vo.setAppId(12345L);
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    private BusinessEntityDetailRespVO createBusinessEntityDetailRespVO() {
        BusinessEntityDetailRespVO vo = new BusinessEntityDetailRespVO();
        vo.setId(2001L);
        vo.setDisplayName("用户信息");
        vo.setCode("user_info");
        vo.setEntityType(1);
        vo.setDescription("系统用户基础信息实体");
        vo.setDatasourceId(1001L);
        vo.setDatasourceName("用户数据库");
        vo.setTableName("users");
        vo.setAppId(12345L);
        vo.setCreateTime(LocalDateTime.now());
        
        // 设置字段列表
        EntityFieldSimpleRespVO field = new EntityFieldSimpleRespVO();
        field.setId(3001L);
        field.setFieldName("id");
        field.setDisplayName("主键ID");
        field.setFieldType("BIGINT");
        field.setIsPrimaryKey(true);
        field.setIsRequired(true);
        field.setIsSystemField(true);
        vo.setFields(Arrays.asList(field));
        
        return vo;
    }
} 