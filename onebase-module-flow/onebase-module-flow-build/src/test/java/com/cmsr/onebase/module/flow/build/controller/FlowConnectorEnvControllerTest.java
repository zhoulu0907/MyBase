package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorEnvService;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 连接器环境配置 Controller 测试
 * <p>
 * 注意：由于 PageConnectorEnvReqVO 在 core 模块中，
 * 完整的集成测试需要在运行时环境中进行
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@ExtendWith(MockitoExtension.class)
class FlowConnectorEnvControllerTest {

    @Mock
    private FlowConnectorEnvService service;

    @InjectMocks
    private FlowConnectorEnvController controller;

    private MockMvc mockMvc;

    private FlowConnectorEnvVO envVO;

    /**
     * 初始化测试数据和MockMvc
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 创建测试数据
        envVO = new FlowConnectorEnvVO();
        envVO.setId(1L);
        envVO.setEnvUuid("env-mysql-dev-001");
        envVO.setEnvName("开发数据库");
        envVO.setEnvCode("DEV");
        envVO.setTypeCode("DATABASE_MYSQL");
        envVO.setEnvUrl("jdbc:mysql://192.168.1.10:3306");
        envVO.setAuthType("BASIC");
        envVO.setDescription("开发环境MySQL数据库配置");
        envVO.setActiveStatus(1);
        envVO.setApplicationId(1L);
    }

    /**
     * 测试根据连接器类型查询环境配置列表
     */
    @Test
    void testListByTypeCode() throws Exception {
        // Given
        when(service.listByTypeCode(eq("DATABASE_MYSQL")))
                .thenReturn(Arrays.asList(envVO));

        // When & Then
        mockMvc.perform(get("/flow/connector-env/by-type/DATABASE_MYSQL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].envUuid").value("env-mysql-dev-001"))
                .andExpect(jsonPath("$.data[0].envName").value("开发数据库"))
                .andExpect(jsonPath("$.data[0].envCode").value("DEV"))
                .andExpect(jsonPath("$.data[0].typeCode").value("DATABASE_MYSQL"));
    }

    /**
     * 测试根据连接器类型查询环境配置列表 - 空结果
     */
    @Test
    void testListByTypeCode_EmptyResult() throws Exception {
        // Given
        when(service.listByTypeCode(eq("DATABASE_MYSQL")))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/flow/connector-env/by-type/DATABASE_MYSQL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    /**
     * 测试根据ID查询环境配置详情
     */
    @Test
    void testGetEnvDetail() throws Exception {
        // Given
        when(service.getEnvDetail(eq(1L))).thenReturn(envVO);

        // When & Then
        mockMvc.perform(get("/flow/connector-env/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.envUuid").value("env-mysql-dev-001"))
                .andExpect(jsonPath("$.data.envName").value("开发数据库"))
                .andExpect(jsonPath("$.data.typeCode").value("DATABASE_MYSQL"));
    }

    /**
     * 测试创建环境配置
     */
    @Test
    void testCreateEnv() throws Exception {
        // Given
        when(service.createEnv(any())).thenReturn(envVO);

        // When & Then
        mockMvc.perform(post("/flow/connector-env/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "applicationId": 1,
                                    "envName": "开发数据库",
                                    "envCode": "DEV",
                                    "typeCode": "DATABASE_MYSQL",
                                    "envUrl": "jdbc:mysql://192.168.1.10:3306",
                                    "authType": "BASIC",
                                    "description": "开发环境MySQL数据库配置"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.envUuid").value("env-mysql-dev-001"))
                .andExpect(jsonPath("$.data.envName").value("开发数据库"));
    }

    /**
     * 测试创建环境配置 - 缺少必填字段
     */
    @Test
    void testCreateEnv_MissingRequiredFields() throws Exception {
        // When & Then - 缺少 envName
        mockMvc.perform(post("/flow/connector-env/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "applicationId": 1,
                                    "envCode": "DEV",
                                    "typeCode": "DATABASE_MYSQL"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
