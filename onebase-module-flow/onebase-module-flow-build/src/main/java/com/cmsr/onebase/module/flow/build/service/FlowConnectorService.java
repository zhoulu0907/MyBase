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

    PageResult<FlowConnectorVO> pageConnectors(PageConnectorReqVO pageReqVO);

    FlowConnectorVO getConnectorDetail(Long connectorId);

    /**
     * Get connector detail by connector UUID
     */
    FlowConnectorVO getConnectorDetailByUuid(String connectorUuid);

    CreateFlowConnectorRespVO createConnector(CreateFlowConnectorReqVO createVO);

    void updateConnector(UpdateFlowConnectorReqVO updateVO);

    void deleteById(Long connectorId);

    /**
     * List connector instances by type code
     */
    List<FlowConnectorVO> listByType(String typeCode);

    /**
     * Get action list by connector UUID
     *
     * @param connectorUuid the connector UUID
     * @return list of action keys
     */
    List<String> getActionsByConnectorUuid(String connectorUuid);

    /**
     * Get action value by connector UUID and action name
     *
     * @param connectorUuid the connector UUID
     * @param actionName the action name
     * @return action value as JsonNode
     */
    JsonNode getActionValueByConnectorUuid(String connectorUuid, String actionName);

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
