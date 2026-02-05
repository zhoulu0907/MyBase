package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FlowNodeConfigServiceImpl 单元测试
 * <p>
 * 测试连接器类型动作配置模板查询功能
 *
 * @author kanten
 * @since 2026-01-31
 */
class FlowNodeConfigServiceImplTest {

    @Mock
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @InjectMocks
    private FlowNodeConfigServiceImpl flowNodeConfigService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== getActionSchemaTemplate 测试用例 ====================

    @Test
    void testGetActionSchemaTemplate_Success() {
        // 准备测试数据
        String typeCode = "HTTP";
        String mockActionConfig = "{\"properties\": {\"基础信息\": {\"type\": \"object\"}}}";
        FlowNodeConfigDO mockConfig = new FlowNodeConfigDO();
        mockConfig.setNodeCode(typeCode);
        mockConfig.setActionConfig(mockActionConfig);

        // Mock repository 行为
        when(flowNodeConfigRepository.findByNodeCode(typeCode)).thenReturn(mockConfig);

        // 执行测试
        JsonNode result = flowNodeConfigService.getActionSchemaTemplate(typeCode);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.has("properties"));
        verify(flowNodeConfigRepository).findByNodeCode(typeCode);
    }

    @Test
    void testGetActionSchemaTemplate_TypeCodeNotFound() {
        // Mock repository 返回 null
        String typeCode = "NOT_EXIST";
        when(flowNodeConfigRepository.findByNodeCode(typeCode)).thenReturn(null);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowNodeConfigService.getActionSchemaTemplate(typeCode);
        });

        assertEquals(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXIST.getCode(), exception.getCode());
    }

    @Test
    void testGetActionSchemaTemplate_ConfigEmpty() {
        // 准备测试数据：actionConfig 为空
        String typeCode = "EMPTY_CONFIG";
        FlowNodeConfigDO mockConfig = new FlowNodeConfigDO();
        mockConfig.setNodeCode(typeCode);
        mockConfig.setActionConfig("");

        // Mock repository 行为
        when(flowNodeConfigRepository.findByNodeCode(typeCode)).thenReturn(mockConfig);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            flowNodeConfigService.getActionSchemaTemplate(typeCode);
        });

        assertEquals(FlowErrorCodeConstants.ACTION_CONFIG_EMPTY.getCode(), exception.getCode());
    }
}
