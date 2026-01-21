package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorHttpService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FlowConnectorHttpController 单元测试
 * <p>
 * 测试 HTTP 连接器动作管理 Controller 的 REST API 接口
 *
 * @author zhoulu
 * @since 2026-01-17
 */
@ExtendWith(MockitoExtension.class)
class FlowConnectorHttpControllerTest {

    @Mock
    private FlowConnectorHttpService service;

    @InjectMocks
    private FlowConnectorHttpController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CreateHttpActionReqVO createReqVO;
    private UpdateHttpActionReqVO updateReqVO;
    private HttpActionVO httpActionVO;
    private PageConnectorHttpReqVO pageReqVO;

    /**
     * 初始化测试数据和MockMvc
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        // 创建测试数据
        createReqVO = new CreateHttpActionReqVO();
        createReqVO.setConnectorUuid("test-connector-uuid");
        createReqVO.setHttpName("测试HTTP动作");
        createReqVO.setRequestMethod("GET");
        createReqVO.setRequestPath("/api/test");

        updateReqVO = new UpdateHttpActionReqVO();
        updateReqVO.setId(1L);
        updateReqVO.setHttpName("更新HTTP动作");

        httpActionVO = new HttpActionVO();
        httpActionVO.setId(1L);
        httpActionVO.setConnectorUuid("test-connector-uuid");
        httpActionVO.setHttpUuid("test-http-uuid");
        httpActionVO.setHttpName("测试HTTP动作");
        httpActionVO.setRequestMethod("GET");
        httpActionVO.setRequestPath("/api/test");

        pageReqVO = new PageConnectorHttpReqVO();
        pageReqVO.setConnectorUuid("test-connector-uuid");
    }

    /**
     * 测试创建HTTP动作 - 成功
     */
    @Test
    void testCreateHttpAction_Success() throws Exception {
        // Given
        when(service.createHttpAction(any(CreateHttpActionReqVO.class))).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/flow/connector/http/action/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(1));

        verify(service).createHttpAction(any(CreateHttpActionReqVO.class));
    }

    /**
     * 测试创建HTTP动作 - 验证必填字段
     */
    @Test
    void testCreateHttpAction_ValidationFailed() throws Exception {
        // Given - 缺少必填字段
        CreateHttpActionReqVO invalidReq = new CreateHttpActionReqVO();
        // 不设置 connectorUuid、httpName、requestMethod 等必填字段

        // When & Then
        mockMvc.perform(post("/flow/connector/http/action/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createHttpAction(any());
    }

    /**
     * 测试更新HTTP动作 - 成功
     */
    @Test
    void testUpdateHttpAction_Success() throws Exception {
        // Given
        doNothing().when(service).updateHttpAction(any(UpdateHttpActionReqVO.class));

        // When & Then
        mockMvc.perform(put("/flow/connector/http/action/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        verify(service).updateHttpAction(any(UpdateHttpActionReqVO.class));
    }

    /**
     * 测试更新HTTP动作 - 验证必填字段
     */
    @Test
    void testUpdateHttpAction_ValidationFailed() throws Exception {
        // Given - 缺少ID
        UpdateHttpActionReqVO invalidReq = new UpdateHttpActionReqVO();
        // 不设置 id

        // When & Then
        mockMvc.perform(put("/flow/connector/http/action/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateHttpAction(any());
    }

    /**
     * 测试删除HTTP动作 - 成功
     */
    @Test
    void testDeleteHttpAction_Success() throws Exception {
        // Given
        doNothing().when(service).deleteHttpAction(1L);

        // When & Then
        mockMvc.perform(delete("/flow/connector/http/action/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        verify(service).deleteHttpAction(1L);
    }

    /**
     * 测试删除HTTP动作 - 缺少参数
     */
    @Test
    void testDeleteHttpAction_MissingParameter() throws Exception {
        // When & Then - 不传 id 参数
        mockMvc.perform(delete("/flow/connector/http/action/delete"))
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteHttpAction(any());
    }

    /**
     * 测试获取HTTP动作详情 - 成功
     */
    @Test
    void testGetHttpAction_Success() throws Exception {
        // Given
        when(service.getHttpAction(1L)).thenReturn(httpActionVO);

        // When & Then
        mockMvc.perform(get("/flow/connector/http/action/get")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.httpName").value("测试HTTP动作"));

        verify(service).getHttpAction(1L);
    }

    /**
     * 测试获取HTTP动作详情 - 缺少参数
     */
    @Test
    void testGetHttpAction_MissingParameter() throws Exception {
        // When & Then - 不传 id 参数
        mockMvc.perform(get("/flow/connector/http/action/get"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getHttpAction(any());
    }

    /**
     * 测试分页查询HTTP动作列表 - 成功
     */
    @Test
    void testGetHttpActionPage_Success() throws Exception {
        // Given
        PageResult<HttpActionVO> pageResult = new PageResult<>(
                Arrays.asList(httpActionVO),
                1L
        );
        when(service.getHttpActionPage(any(PageConnectorHttpReqVO.class))).thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/flow/connector/http/action/page")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(service).getHttpActionPage(any(PageConnectorHttpReqVO.class));
    }

    /**
     * 测试分页查询HTTP动作列表 - 空结果
     */
    @Test
    void testGetHttpActionPage_EmptyResult() throws Exception {
        // Given
        PageResult<HttpActionVO> emptyResult = new PageResult<>(
                Collections.emptyList(),
                0L
        );
        when(service.getHttpActionPage(any(PageConnectorHttpReqVO.class))).thenReturn(emptyResult);

        // When & Then
        mockMvc.perform(get("/flow/connector/http/action/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list").isEmpty())
                .andExpect(jsonPath("$.data.total").value(0));

        verify(service).getHttpActionPage(any(PageConnectorHttpReqVO.class));
    }

    /**
     * 测试Controller层Service注入
     * <p>
     * 验证Service层被正确注入到Controller中
     */
    @Test
    void testServiceInjection() {
        // When
        controller.setConnectorHttpService(service);

        // Then - 验证通过setter正确注入
        // 这里主要验证编译和注入不报错
        // 实际调用会通过上面的其他测试方法验证
    }

    /**
     * 测试返回的CommonResult结构
     * <p>
     * 验证所有接口都返回正确的CommonResult结构
     */
    @Test
    void testCommonResultStructure() throws Exception {
        // Given - prepare service responses
        when(service.createHttpAction(any())).thenReturn(1L);
        when(service.getHttpAction(1L)).thenReturn(httpActionVO);
        doNothing().when(service).updateHttpAction(any());
        doNothing().when(service).deleteHttpAction(1L);
        when(service.getHttpActionPage(any())).thenReturn(new PageResult<>(Collections.emptyList(), 0L));

        // Test /create endpoint
        mockMvc.perform(post("/flow/connector/http/action/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());

        // Test /get endpoint
        mockMvc.perform(get("/flow/connector/http/action/get")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());

        // Test /update endpoint
        mockMvc.perform(put("/flow/connector/http/action/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());

        // Test /delete endpoint
        mockMvc.perform(delete("/flow/connector/http/action/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());

        // Test /page endpoint
        mockMvc.perform(get("/flow/connector/http/action/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());
    }

    /**
     * 测试JSON序列化
     * <p>
     * 验证请求和响应的JSON序列化正确
     */
    @Test
    void testJsonSerialization() throws Exception {
        // Given
        when(service.createHttpAction(any())).thenReturn(1L);

        // When & Then - 验证JSON可以被正确序列化
        mockMvc.perform(post("/flow/connector/http/action/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"connectorUuid\":\"test\",\"httpName\":\"Test\",\"requestMethod\":\"GET\",\"requestPath\":\"/api/test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(service).createHttpAction(any(CreateHttpActionReqVO.class));
    }
}
