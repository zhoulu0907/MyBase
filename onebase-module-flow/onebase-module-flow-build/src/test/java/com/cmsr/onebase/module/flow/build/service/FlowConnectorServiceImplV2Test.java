package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeActionRefDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeActionRefRepository;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * FlowConnectorService v2.0 方法单元测试
 * <p>
 * 测试动作配置管理 v2.0 版本的业务逻辑
 *
 * @author kanten
 * @since 2026-01-29
 */
@ExtendWith(MockitoExtension.class)
class FlowConnectorServiceImplV2Test {

    @Mock
    private FlowConnectorRepository connectorRepository;

    @Mock
    private FlowNodeActionRefRepository actionRefRepository;

    @Mock
    private ActionConfigHelper actionConfigHelper;

    @InjectMocks
    private FlowConnectorServiceImpl service;

    private ObjectMapper objectMapper;
    private FlowConnectorDO testConnector;
    private String testConfig;
    private SaveActionReqVO saveActionReqVO;
    private UpdateActionReqVO updateActionReqVO;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // 创建测试配置
        ObjectNode configRoot = objectMapper.createObjectNode();
        configRoot.put("version", "2.0");
        configRoot.set("actions", objectMapper.createArrayNode());
        testConfig = JsonUtils.toJsonString(configRoot);

        // 创建测试连接器
        testConnector = new FlowConnectorDO();
        testConnector.setId(1L);
        testConnector.setConnectorName("测试连接器");
        testConnector.setConfig(testConfig);
        testConnector.setUpdateTime(LocalDateTime.now());

        // 创建保存动作请求VO
        saveActionReqVO = SaveActionReqVO.builder()
                .actionName("测试动作")
                .actionCode("TEST_ACTION")
                .description("测试动作描述")
                .basicInfo(createBasicInfo())
                .inputConfig(createInputConfig())
                .outputConfig(createOutputConfig())
                .debugConfig(createDebugConfig())
                .build();

        // 创建更新动作请求VO
        updateActionReqVO = UpdateActionReqVO.builder()
                .actionName("更新后的动作名称")
                .description("更新后的描述")
                .basicInfo(createBasicInfo())
                .inputConfig(createInputConfig())
                .outputConfig(createOutputConfig())
                .debugConfig(createDebugConfig())
                .build();
    }

    /**
     * 创建基础信息配置
     */
    private JsonNode createBasicInfo() {
        ObjectNode basicInfo = objectMapper.createObjectNode();
        basicInfo.put("name", "基础信息");
        return basicInfo;
    }

    /**
     * 创建入参配置
     */
    private JsonNode createInputConfig() {
        ObjectNode inputConfig = objectMapper.createObjectNode();
        inputConfig.put("type", "object");
        return inputConfig;
    }

    /**
     * 创建出参配置
     */
    private JsonNode createOutputConfig() {
        ObjectNode outputConfig = objectMapper.createObjectNode();
        outputConfig.put("type", "object");
        return outputConfig;
    }

    /**
     * 创建调试配置
     */
    private JsonNode createDebugConfig() {
        ObjectNode debugConfig = objectMapper.createObjectNode();
        debugConfig.put("timeout", 5000);
        return debugConfig;
    }

    /**
     * 创建测试动作节点
     */
    private JsonNode createTestAction(String actionId) {
        ObjectNode action = objectMapper.createObjectNode();
        action.put("actionId", actionId);
        action.put("actionName", "测试动作");
        action.put("actionCode", "TEST_ACTION");
        action.put("description", "测试动作描述");
        action.put("status", "draft");
        action.put("version", 1);
        action.set("基础信息", createBasicInfo());
        action.set("入参配置", createInputConfig());
        action.set("出参配置", createOutputConfig());
        action.set("调试配置", createDebugConfig());
        return action;
    }

    // ==================== getActionInfosV2 测试 ====================

    /**
     * 测试获取动作列表 - 空配置
     */
    @Test
    void testGetActionInfosV2_withEmptyConfig() {
        // Given
        testConnector.setConfig(null);
        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.getActions(null)).thenReturn(new ArrayList<>());

        // When
        List<ActionInfoVO> result = service.getActionInfosV2(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).getActions(null);
    }

    /**
     * 测试获取动作列表 - 有动作
     */
    @Test
    void testGetActionInfosV2_withActions() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.getActions(testConfig)).thenReturn(List.of(testAction));
        when(actionRefRepository.findByConnectorIdAndActionIds(eq(1L), any()))
                .thenReturn(new ArrayList<>());

        // When
        List<ActionInfoVO> result = service.getActionInfosV2(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActionId()).isEqualTo(actionId);
        assertThat(result.get(0).getActionName()).isEqualTo("测试动作");
        assertThat(result.get(0).getActionCode()).isEqualTo("TEST_ACTION");
        assertThat(result.get(0).getStatus()).isEqualTo("draft");
        assertThat(result.get(0).getVersion()).isEqualTo(1);
        assertThat(result.get(0).getUsedCount()).isEqualTo(0);

        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).getActions(testConfig);
        verify(actionRefRepository).findByConnectorIdAndActionIds(eq(1L), any());
    }

    /**
     * 测试获取动作列表 - 连接器不存在
     */
    @Test
    void testGetActionInfosV2_connectorNotFound() {
        // Given
        when(connectorRepository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.getActionInfosV2(999L))
                .isInstanceOf(RuntimeException.class);

        verify(connectorRepository).getById(999L);
        verify(actionConfigHelper, never()).getActions(any());
    }

    // ==================== saveActionDraftV2 测试 ====================

    /**
     * 测试保存动作草稿 - 成功
     */
    @Test
    void testSaveActionDraftV2_success() {
        // Given
        String actionId = "action-new-001";
        String updatedConfig = "{\"updated\":true}";

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.generateActionId()).thenReturn(actionId);
        when(actionConfigHelper.addAction(eq(testConfig), any(JsonNode.class))).thenReturn(updatedConfig);
        when(connectorRepository.updateById(any(FlowConnectorDO.class))).thenReturn(true);

        // When
        String result = service.saveActionDraftV2(1L, saveActionReqVO);

        // Then
        assertThat(result).isEqualTo(actionId);
        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).generateActionId();
        verify(actionConfigHelper).addAction(eq(testConfig), any(JsonNode.class));
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    /**
     * 测试保存动作草稿 - 连接器不存在
     */
    @Test
    void testSaveActionDraftV2_connectorNotFound() {
        // Given
        when(connectorRepository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.saveActionDraftV2(999L, saveActionReqVO))
                .isInstanceOf(RuntimeException.class);

        verify(connectorRepository).getById(999L);
        verify(actionConfigHelper, never()).generateActionId();
        verify(actionConfigHelper, never()).addAction(any(), any());
    }

    // ==================== publishActionV2 测试 ====================

    /**
     * 测试发布动作 - 成功
     */
    @Test
    void testPublishActionV2_success() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        String updatedConfig = "{\"updated\":true}";

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);
        when(actionConfigHelper.updateAction(eq(testConfig), eq(actionId), any(JsonNode.class)))
                .thenReturn(updatedConfig);
        when(connectorRepository.updateById(any(FlowConnectorDO.class))).thenReturn(true);

        // When
        service.publishActionV2(1L, actionId);

        // Then
        verify(connectorRepository, times(2)).getById(1L);
        verify(actionConfigHelper, times(2)).findAction(testConfig, actionId);
        verify(actionConfigHelper).updateAction(eq(testConfig), eq(actionId), any(JsonNode.class));
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    /**
     * 测试发布动作 - 连接器不存在
     */
    @Test
    void testPublishActionV2_connectorNotFound() {
        // Given
        String actionId = "action-test-001";
        when(connectorRepository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.publishActionV2(999L, actionId))
                .isInstanceOf(RuntimeException.class);

        verify(connectorRepository).getById(999L);
        verify(actionConfigHelper, never()).findAction(any(), any());
    }

    /**
     * 测试发布动作 - 动作不存在
     */
    @Test
    void testPublishActionV2_actionNotFound() {
        // Given
        String actionId = "action-not-exist";
        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.publishActionV2(1L, actionId))
                .isInstanceOf(RuntimeException.class);

        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).findAction(testConfig, actionId);
        verify(actionConfigHelper, never()).updateAction(any(), any(), any());
    }

    /**
     * 测试发布动作 - 状态不允许
     */
    @Test
    void testPublishActionV2_invalidStatus() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).put("status", "published");

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When & Then
        assertThatThrownBy(() -> service.publishActionV2(1L, actionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("只有草稿或下架状态的动作才能发布");

        verify(actionConfigHelper, never()).updateAction(any(), any(), any());
    }

    /**
     * 测试发布动作 - 校验失败
     */
    @Test
    void testPublishActionV2_validationFailed() {
        // Given
        String actionId = "action-test-001";
        ObjectNode testAction = objectMapper.createObjectNode();
        testAction.put("actionId", actionId);
        testAction.put("actionName", "测试动作");
        testAction.put("status", "draft");
        testAction.put("version", 1);
        // 缺少必需配置

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When & Then
        assertThatThrownBy(() -> service.publishActionV2(1L, actionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("请完善动作信息后再进行发布");

        verify(actionConfigHelper, never()).updateAction(any(), any(), any());
    }

    // ==================== deleteActionV2 测试 ====================

    /**
     * 测试删除动作 - 成功
     */
    @Test
    void testDeleteActionV2_success() {
        // Given
        String actionId = "action-test-001";
        String updatedConfig = "{\"updated\":true}";

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionRefRepository.findByConnectorAndAction(1L, actionId))
                .thenReturn(new ArrayList<>());
        when(actionConfigHelper.removeAction(testConfig, actionId)).thenReturn(updatedConfig);
        when(connectorRepository.updateById(any(FlowConnectorDO.class))).thenReturn(true);

        // When
        service.deleteActionV2(1L, actionId);

        // Then
        verify(connectorRepository).getById(1L);
        verify(actionRefRepository).findByConnectorAndAction(1L, actionId);
        verify(actionConfigHelper).removeAction(testConfig, actionId);
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    /**
     * 测试删除动作 - 连接器不存在
     */
    @Test
    void testDeleteActionV2_connectorNotFound() {
        // Given
        String actionId = "action-test-001";
        when(connectorRepository.getById(999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.deleteActionV2(999L, actionId))
                .isInstanceOf(RuntimeException.class);

        verify(connectorRepository).getById(999L);
        verify(actionRefRepository, never()).findByConnectorAndAction(any(), any());
    }

    /**
     * 测试删除动作 - 存在引用关系
     */
    @Test
    void testDeleteActionV2_hasReferences() {
        // Given
        String actionId = "action-test-001";
        FlowNodeActionRefDO ref = new FlowNodeActionRefDO();
        ref.setFlowVersion("1");
        ref.setNodeId(1L);

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionRefRepository.findByConnectorAndAction(1L, actionId))
                .thenReturn(List.of(ref));

        // When & Then
        assertThatThrownBy(() -> service.deleteActionV2(1L, actionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("存在关联逻辑流");

        verify(actionConfigHelper, never()).removeAction(any(), any());
    }

    // ==================== getActionDetailV2 测试 ====================

    /**
     * 测试获取动作详情 - 成功
     */
    @Test
    void testGetActionDetailV2_success() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);
        when(actionRefRepository.findByConnectorAndAction(1L, actionId))
                .thenReturn(new ArrayList<>());

        // When
        ActionDetailVO result = service.getActionDetailV2(1L, actionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getActionId()).isEqualTo(actionId);
        assertThat(result.getActionName()).isEqualTo("测试动作");
        assertThat(result.getActionCode()).isEqualTo("TEST_ACTION");
        assertThat(result.getStatus()).isEqualTo("draft");
        assertThat(result.getVersion()).isEqualTo(1);
        assertThat(result.getUsedCount()).isEqualTo(0);
        assertThat(result.getBasicInfo()).isNotNull();
        assertThat(result.getInputConfig()).isNotNull();
        assertThat(result.getOutputConfig()).isNotNull();
        assertThat(result.getDebugConfig()).isNotNull();

        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).findAction(testConfig, actionId);
        verify(actionRefRepository).findByConnectorAndAction(1L, actionId);
    }

    /**
     * 测试获取动作详情 - 动作不存在
     */
    @Test
    void testGetActionDetailV2_actionNotFound() {
        // Given
        String actionId = "action-not-exist";
        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.getActionDetailV2(1L, actionId))
                .isInstanceOf(RuntimeException.class);

        verify(actionConfigHelper).findAction(testConfig, actionId);
    }

    // ==================== offlineActionV2 测试 ====================

    /**
     * 测试下架动作 - 成功
     */
    @Test
    void testOfflineActionV2_success() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).put("status", "published");
        String updatedConfig = "{\"updated\":true}";

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);
        when(actionConfigHelper.updateAction(eq(testConfig), eq(actionId), any(JsonNode.class)))
                .thenReturn(updatedConfig);
        when(connectorRepository.updateById(any(FlowConnectorDO.class))).thenReturn(true);

        // When
        service.offlineActionV2(1L, actionId);

        // Then
        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).findAction(testConfig, actionId);
        verify(actionConfigHelper).updateAction(eq(testConfig), eq(actionId), any(JsonNode.class));
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    /**
     * 测试下架动作 - 状态不允许
     */
    @Test
    void testOfflineActionV2_invalidStatus() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).put("status", "draft");

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When & Then
        assertThatThrownBy(() -> service.offlineActionV2(1L, actionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("只有已发布的动作才能下架");

        verify(actionConfigHelper, never()).updateAction(any(), any(), any());
    }

    // ==================== validateActionForPublishV2 测试 ====================

    /**
     * 测试校验动作 - 成功
     */
    @Test
    void testValidateActionForPublishV2_success() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When
        ValidationResultVO result = service.validateActionForPublishV2(1L, actionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();

        verify(connectorRepository).getById(1L);
        verify(actionConfigHelper).findAction(testConfig, actionId);
    }

    /**
     * 测试校验动作 - 缺少基础信息
     */
    @Test
    void testValidateActionForPublishV2_missingBasicInfo() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).remove("基础信息");

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When
        ValidationResultVO result = service.validateActionForPublishV2(1L, actionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValid()).isFalse();
        assertThat(result.getErrors()).contains("基础信息不能为空");
    }

    /**
     * 测试校验动作 - 缺少入参配置
     */
    @Test
    void testValidateActionForPublishV2_missingInputConfig() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).remove("入参配置");

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When
        ValidationResultVO result = service.validateActionForPublishV2(1L, actionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValid()).isFalse();
        assertThat(result.getErrors()).contains("入参配置不能为空");
    }

    /**
     * 测试校验动作 - 缺少出参配置
     */
    @Test
    void testValidateActionForPublishV2_missingOutputConfig() {
        // Given
        String actionId = "action-test-001";
        JsonNode testAction = createTestAction(actionId);
        ((ObjectNode) testAction).remove("出参配置");

        when(connectorRepository.getById(1L)).thenReturn(testConnector);
        when(actionConfigHelper.findAction(testConfig, actionId)).thenReturn(testAction);

        // When
        ValidationResultVO result = service.validateActionForPublishV2(1L, actionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValid()).isFalse();
        assertThat(result.getErrors()).contains("出参配置不能为空");
    }
}
