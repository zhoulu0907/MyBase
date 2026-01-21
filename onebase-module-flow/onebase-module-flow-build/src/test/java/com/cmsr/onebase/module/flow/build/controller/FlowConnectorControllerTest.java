package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorService;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FlowConnectorController 单元测试
 * <p>
 * 测试连接器管理 Controller 的 REST API 接口
 *
 * @author zhoulu
 * @since 2026-01-21
 */
@ExtendWith(MockitoExtension.class)
class FlowConnectorControllerTest {

    @Mock
    private FlowConnectorService service;

    @InjectMocks
    private FlowConnectorController controller;

    private MockMvc mockMvc;

    private FlowConnectorVO connectorVO;

    /**
     * 初始化测试数据和MockMvc
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 创建测试数据 - script类型连接器
        connectorVO = new FlowConnectorVO();
        connectorVO.setId(1L);
        connectorVO.setConnectorUuid("script-connector-uuid-001");
        connectorVO.setConnectorName("测试脚本连接器");
        connectorVO.setTypeCode("script");
        connectorVO.setDescription("用于测试的脚本连接器");

        // 创建 JsonNode 类型的 config
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode configNode = factory.objectNode();
        configNode.put("test", "value");
        connectorVO.setConfig(configNode);

        connectorVO.setApplicationId(158284491560812544L);
        connectorVO.setConnectorVersion("1.0.0");
    }

    /**
     * 测试根据类型查询连接器实例列表 - 成功（有数据）
     */
    @Test
    void testListByType_Success_WithData() throws Exception {
        // Given
        List<FlowConnectorVO> connectorList = Arrays.asList(connectorVO);
        when(service.listByType(eq("script"))).thenReturn(connectorList);

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].connectorUuid").value("script-connector-uuid-001"))
                .andExpect(jsonPath("$.data[0].connectorName").value("测试脚本连接器"))
                .andExpect(jsonPath("$.data[0].typeCode").value("script"));

        verify(service).listByType(eq("script"));
    }

    /**
     * 测试根据类型查询连接器实例列表 - 空结果
     */
    @Test
    void testListByType_EmptyResult() throws Exception {
        // Given
        when(service.listByType(eq("script"))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(service).listByType(eq("script"));
    }

    /**
     * 测试根据类型查询连接器实例列表 - 缺少typeCode参数
     */
    @Test
    void testListByType_MissingTypeCode() throws Exception {
        // When & Then - 不传 typeCode 参数
        mockMvc.perform(get("/flow/connector/list-by-type"))
                .andExpect(status().isBadRequest());

        verify(service, never()).listByType(any());
    }

    /**
     * 测试根据类型查询连接器实例列表 - 多条数据
     */
    @Test
    void testListByType_MultipleResults() throws Exception {
        // Given
        FlowConnectorVO connectorVO2 = new FlowConnectorVO();
        connectorVO2.setId(2L);
        connectorVO2.setConnectorUuid("script-connector-uuid-002");
        connectorVO2.setConnectorName("脚本连接器2");
        connectorVO2.setTypeCode("script");

        List<FlowConnectorVO> connectorList = Arrays.asList(connectorVO, connectorVO2);
        when(service.listByType(eq("script"))).thenReturn(connectorList);

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].typeCode").value("script"))
                .andExpect(jsonPath("$.data[1].typeCode").value("script"));

        verify(service).listByType(eq("script"));
    }

    /**
     * 测试根据类型查询连接器实例列表 - 不同类型
     */
    @Test
    void testListByType_DifferentType() throws Exception {
        // Given - 测试 EMAIL 类型
        FlowConnectorVO emailConnector = new FlowConnectorVO();
        emailConnector.setId(3L);
        emailConnector.setConnectorUuid("email-connector-uuid");
        emailConnector.setConnectorName("邮件连接器");
        emailConnector.setTypeCode("EMAIL_163");

        when(service.listByType(eq("EMAIL_163"))).thenReturn(Arrays.asList(emailConnector));

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "EMAIL_163"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].typeCode").value("EMAIL_163"));

        verify(service).listByType(eq("EMAIL_163"));
    }

    /**
     * 测试返回的CommonResult结构
     */
    @Test
    void testCommonResultStructure() throws Exception {
        // Given
        when(service.listByType(eq("script"))).thenReturn(Arrays.asList(connectorVO));

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "script"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.msg").exists());

        verify(service).listByType(eq("script"));
    }

    /**
     * 测试JSON序列化
     */
    @Test
    void testJsonSerialization() throws Exception {
        // Given
        when(service.listByType(eq("script"))).thenReturn(Arrays.asList(connectorVO));

        // When & Then
        mockMvc.perform(get("/flow/connector/list-by-type")
                        .param("typeCode", "script")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].connectorName").value("测试脚本连接器"));

        verify(service).listByType(eq("script"));
    }

    // ==================== getActions 测试用例 ====================

    /**
     * 测试查询连接器动作清单 - 成功（有数据）
     */
    @Test
    void testGetActions_Success_WithData() throws Exception {
        // Given
        List<String> actions = Arrays.asList("getCustomerList", "getCustomerDetail", "getCustomerOrders");
        when(service.getActionsByConnectorUuid(eq("test-connector-uuid"))).thenReturn(actions);

        // When & Then
        mockMvc.perform(get("/flow/connector/actions")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("getCustomerList"))
                .andExpect(jsonPath("$.data[1]").value("getCustomerDetail"))
                .andExpect(jsonPath("$.data[2]").value("getCustomerOrders"));

        verify(service).getActionsByConnectorUuid(eq("test-connector-uuid"));
    }

    /**
     * 测试查询连接器动作清单 - 空结果
     */
    @Test
    void testGetActions_EmptyResult() throws Exception {
        // Given
        when(service.getActionsByConnectorUuid(eq("test-connector-uuid"))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/flow/connector/actions")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(service).getActionsByConnectorUuid(eq("test-connector-uuid"));
    }

    /**
     * 测试查询连接器动作清单 - 缺少connectorUuid参数
     */
    @Test
    void testGetActions_MissingConnectorUuid() throws Exception {
        // When & Then - 不传 connectorUuid 参数
        mockMvc.perform(get("/flow/connector/actions"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getActionsByConnectorUuid(any());
    }

    /**
     * 测试查询连接器动作清单 - 单个动作
     */
    @Test
    void testGetActions_SingleAction() throws Exception {
        // Given
        List<String> actions = Arrays.asList("getCustomerList");
        when(service.getActionsByConnectorUuid(eq("test-connector-uuid"))).thenReturn(actions);

        // When & Then
        mockMvc.perform(get("/flow/connector/actions")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0]").value("getCustomerList"));

        verify(service).getActionsByConnectorUuid(eq("test-connector-uuid"));
    }

    /**
     * 测试查询连接器动作清单 - 验证顺序
     */
    @Test
    void testGetActions_OrderPreserved() throws Exception {
        // Given - 确保动作按特定顺序返回
        List<String> actions = Arrays.asList("action1", "action2", "action3");
        when(service.getActionsByConnectorUuid(eq("test-connector-uuid"))).thenReturn(actions);

        // When & Then
        mockMvc.perform(get("/flow/connector/actions")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0]").value("action1"))
                .andExpect(jsonPath("$.data[1]").value("action2"))
                .andExpect(jsonPath("$.data[2]").value("action3"));

        verify(service).getActionsByConnectorUuid(eq("test-connector-uuid"));
    }

    // ==================== getActionValue 测试用例 ====================

    /**
     * 测试查询指定动作配置内容 - 成功
     */
    @Test
    void testGetActionValue_Success() throws Exception {
        // Given
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode actionValue = factory.objectNode();
        actionValue.put("type", "object");
        actionValue.put("title", "获取客户列表");
        actionValue.put("x-component", "FormDataGrid");

        when(service.getActionValueByConnectorUuid(eq("test-connector-uuid"), eq("getCustomerList")))
                .thenReturn(actionValue);

        // When & Then
        mockMvc.perform(get("/flow/connector/action-value")
                        .param("connectorUuid", "test-connector-uuid")
                        .param("actionName", "getCustomerList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.type").value("object"))
                .andExpect(jsonPath("$.data.title").value("获取客户列表"))
                .andExpect(jsonPath("$.data.x-component").value("FormDataGrid"));

        verify(service).getActionValueByConnectorUuid(eq("test-connector-uuid"), eq("getCustomerList"));
    }

    /**
     * 测试查询指定动作配置内容 - 缺少connectorUuid参数
     */
    @Test
    void testGetActionValue_MissingConnectorUuid() throws Exception {
        // When & Then
        mockMvc.perform(get("/flow/connector/action-value")
                        .param("actionName", "getCustomerList"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getActionValueByConnectorUuid(any(), any());
    }

    /**
     * 测试查询指定动作配置内容 - 缺少actionName参数
     */
    @Test
    void testGetActionValue_MissingActionName() throws Exception {
        // When & Then
        mockMvc.perform(get("/flow/connector/action-value")
                        .param("connectorUuid", "test-connector-uuid"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getActionValueByConnectorUuid(any(), any());
    }

    /**
     * 测试查询指定动作配置内容 - 缺少所有参数
     */
    @Test
    void testGetActionValue_MissingAllParams() throws Exception {
        // When & Then
        mockMvc.perform(get("/flow/connector/action-value"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getActionValueByConnectorUuid(any(), any());
    }

    /**
     * 测试查询指定动作配置内容 - 复杂JSON结构
     */
    @Test
    void testGetActionValue_ComplexJsonStructure() throws Exception {
        // Given
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode actionValue = factory.objectNode();
        actionValue.put("type", "object");
        actionValue.put("title", "获取客户详情");

        // 添加嵌套对象
        ObjectNode apiMeta = factory.objectNode();
        apiMeta.put("method", "GET");
        apiMeta.put("path", "/api/customers/{id}");
        actionValue.set("x-api-meta", apiMeta);

        // 添加数组
        ObjectNode componentProps = factory.objectNode();
        componentProps.put("label", "客户详情");
        componentProps.set("required", factory.arrayNode().add("id").add("name"));
        actionValue.set("x-component-props", componentProps);

        when(service.getActionValueByConnectorUuid(eq("test-connector-uuid"), eq("getCustomerDetail")))
                .thenReturn(actionValue);

        // When & Then
        mockMvc.perform(get("/flow/connector/action-value")
                        .param("connectorUuid", "test-connector-uuid")
                        .param("actionName", "getCustomerDetail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.type").value("object"))
                .andExpect(jsonPath("$.data.title").value("获取客户详情"))
                .andExpect(jsonPath("$.data.x-api-meta.method").value("GET"))
                .andExpect(jsonPath("$.data.x-api-meta.path").value("/api/customers/{id}"))
                .andExpect(jsonPath("$.data.x-component-props.label").value("客户详情"));

        verify(service).getActionValueByConnectorUuid(eq("test-connector-uuid"), eq("getCustomerDetail"));
    }
}
