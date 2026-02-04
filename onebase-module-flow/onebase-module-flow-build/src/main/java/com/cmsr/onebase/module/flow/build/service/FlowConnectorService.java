package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
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

    /**
     * 更新连接器基本信息
     * <p>
     * 只更新描述信息，自动检测变化。如果未发生变化则不执行数据库更新。
     *
     * @param connectorId 连接器ID
     * @param updateVO    更新请求VO
     * @return true=实际更新了，false=没有变化
     */
    Boolean updateBaseInfo(Long connectorId, UpdateFlowConnectorReqVO updateVO);

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
     *
     * @param id           连接器实例ID
     * @param activeStatus 启用状态（0-禁用，1-启用）
     */
    void updateActiveStatus(Long id, Integer activeStatus);

    /**
     * 查询连接器的环境配置列表
     * <p>
     * 从 flow_connector.config 字段解析环境配置信息
     *
     * @param connectorId 连接器ID
     * @return 环境配置列表
     */
    List<FlowConnectorEnvLiteVO> getEnvironments(Long connectorId);

    /**
     * 查询连接器的指定环境配置信息
     * <p>
     * 从 flow_connector.config 的 properties 中解析指定环境的 Formily Schema
     *
     * @param connectorId 连接器实例ID（主键）
     * @param envCode     环境编码（如 DEV、TEST、PROD）
     * @return 环境配置 VO
     */
    EnvironmentConfigVO getEnvironmentConfig(Long connectorId, String envCode);

    /**
     * 获取环境配置模板
     * <p>
     * 根据连接器实例获取其类型对应的环境配置 Formily Schema 模板
     *
     * @param connectorId 连接器实例ID
     * @return 环境配置模板 VO
     */
    EnvConfigTemplateVO getEnvConfigTemplate(Long connectorId);

    /**
     * 获取动作配置模板
     * <p>
     * 根据连接器实例获取其类型对应的动作配置 Formily Schema 模板
     *
     * @param connectorId 连接器实例ID
     * @return 动作配置模板 VO
     */
    ActionConfigTemplateVO getActionConfigTemplate(Long connectorId);

    /**
     * 保存连接器环境配置
     * <p>
     * 将新的环境配置添加到 flow_connector.config.properties 中
     * 如果环境已存在则拒绝保存
     *
     * @param connectorId 连接器实例ID
     * @param reqVO 环境配置请求
     * @return 保存是否成功
     */
    Boolean saveEnvironmentConfig(Long connectorId, SaveEnvironmentConfigReqVO reqVO);

    // ==================== 动作管理接口 ====================

    /**
     * 获取连接器的动作列表
     *
     * @param connectorId 连接器ID
     * @return 动作列表
     */
    List<ConnectorActionVO> getActionList(Long connectorId);

    /**
     * 获取连接器的动作配置列表
     *
     * @param connectorId 连接器ID
     * @return 动作配置列表
     */
    List<ConnectorActionVO> getActionInfos(Long id);

    /**
     * 获取动作详情
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     * @return 动作详情
     */
    ConnectorActionVO getActionDetail(Long connectorId, String actionCode);

    /**
     * 获取动作的 Formily Schema
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     * @return Formily Schema JsonNode
     */
    JsonNode getActionSchema(Long connectorId, String actionCode);

    /**
     * 保存动作草稿
     *
     * @param connectorId 连接器ID
     * @param createVO    创建请求
     * @return 创建的动作编码
     */
    String saveActionDraft(Long connectorId, CreateConnectorActionReqVO createVO);

    /**
     * 更新动作草稿
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     * @param updateVO    更新请求
     */
    void updateActionDraft(Long connectorId, String actionCode, UpdateConnectorActionReqVO updateVO);

    /**
     * 发布动作
     * <p>
     * 业务规则：
     * - 校验所有步骤配置是否完整
     * - 完整性校验通过后更新状态为 published，版本号+1
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     */
    void publishAction(Long connectorId, String actionCode);

    /**
     * 下架动作
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     */
    void offlineAction(Long connectorId, String actionCode);

    /**
     * 重新上线动作
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     */
    void republishAction(Long connectorId, String actionCode);

    /**
     * 复制动作
     * <p>
     * 业务规则：
     * - 自动生成唯一名称：原名称_copy序号
     * - 复制的动作状态为 draft
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     * @return 新复制的动作编码
     */
    String copyAction(Long connectorId, String actionCode);

    /**
     * 删除动作
     * <p>
     * 业务规则：
     * - 检查是否被逻辑流引用
     * - 如被引用则抛出异常并返回引用信息
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     */
    void deleteAction(Long connectorId, String actionCode);

    /**
     * 校验动作是否可发布
     *
     * @param connectorId 连接器ID
     * @param actionCode  动作编码
     * @return 校验结果
     */
    ActionConfigHelper.ValidationResult validateActionForPublish(Long connectorId, String actionCode);
}
