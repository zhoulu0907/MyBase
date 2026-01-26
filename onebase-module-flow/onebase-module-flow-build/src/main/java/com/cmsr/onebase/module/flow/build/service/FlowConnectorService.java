package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorRespVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorLiteVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface FlowConnectorService {

    /**
     * 分页查询连接器实例（精简版，用于列表展示）
     * <p>
     * 返回 FlowConnectorLiteVO，不包含完整的 config 配置信息
     * 包含：实例名称、类型、环境信息、配置状态、启用状态、创建时间等
     *
     * @param pageReqVO 分页查询参数（支持按名称、类型、状态筛选）
     * @return 分页结果（FlowConnectorLiteVO）
     */
    PageResult<FlowConnectorLiteVO> pageConnectors(PageConnectorReqVO pageReqVO);

    FlowConnectorVO getConnectorDetail(Long connectorId);

    CreateFlowConnectorRespVO createConnector(CreateFlowConnectorReqVO createVO);

    void updateConnector(UpdateFlowConnectorReqVO updateVO);

    void deleteById(Long connectorId);

    /**
     * List connector instances by type code
     */
    List<FlowConnectorVO> listByType(String typeCode);

    /**
     * List all connector instances with pagination (lite version without config)
     */
    PageResult<FlowConnectorLiteVO> listAll(PageParam pageParam);

    /**
     * Get action list by connector ID
     *
     * @param id the connector ID
     * @return list of action keys
     */
    List<String> getActionsById(Long id);

    /**
     * Get action value by connector ID and action name
     *
     * @param id the connector ID
     * @param actionName the action name
     * @return action value as JsonNode
     */
    JsonNode getActionValueById(Long id, String actionName);

    /**
     * 启用/禁用连接器实例
     * <p>
     * 启用前会检查实例是否已配置环境信息（envUuid不为空）
     * <p>
     * 业务规则：
     * - 禁用操作：允许任何实例禁用
     * - 启用操作：必须先配置环境信息，否则抛出 CONNECTOR_ENV_NOT_CONFIGURED 异常
     *
     * @param id           连接器实例ID
     * @param activeStatus 启用状态（0-禁用，1-启用）
     * @throws ServiceException CONNECTOR_ENV_NOT_CONFIGURED - 启用时未配置环境
     */
    void updateActiveStatus(Long id, Integer activeStatus);
}
