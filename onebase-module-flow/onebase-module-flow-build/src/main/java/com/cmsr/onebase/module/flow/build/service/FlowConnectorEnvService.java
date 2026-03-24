package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 连接器环境配置Service接口
 *
 * @author onebase
 * @since 2026-03-20
 */
public interface FlowConnectorEnvService {

    /**
     * 查询连接器的环境配置列表
     * <p>
     * 从 flow_connector_env 表查询该连接器类型下的所有环境配置
     *
     * @param connectorId 连接器ID
     * @return 环境配置列表
     */
    List<FlowConnectorEnvLiteVO> getEnvironments(Long connectorId);

    /**
     * 查询连接器的指定环境配置信息
     * <p>
     * 从 flow_connector_env 表查询指定环境编码的配置详情
     *
     * @param connectorId 连接器实例ID（主键）
     * @param envCode     环境编码（如 DEV）
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
     * 保存连接器环境配置
     * <p>
     * 将新的环境配置保存到 flow_connector_env 表
     * 如果环境名称已存在则拒绝保存
     *
     * @param connectorId 连接器实例ID
     * @param reqVO       环境配置请求
     * @return 保存是否成功
     */
    Boolean saveEnvironmentConfig(Long connectorId, @Valid SaveEnvironmentConfigReqVO reqVO);

    /**
     * 更新连接器环境配置
     * <p>
     * 更新 flow_connector_env 表中已存在的环境配置
     * 如果环境不存在则拒绝更新
     *
     * @param connectorId 连接器实例ID
     * @param reqVO       环境配置请求
     * @return 更新是否成功
     */
    Boolean updateEnvironmentConfig(Long connectorId, @Valid SaveEnvironmentConfigReqVO reqVO);

    /**
     * 设置启用环境
     * <p>
     * 在 flow_connector 表设置当前启用的环境（通过 env_uuid 关联）
     * 环境必须存在才能启用
     *
     * @param connectorId 连接器实例ID
     * @param envCode     环境编码（传空或null表示取消启用）
     * @return 设置是否成功
     */
    Boolean enableEnvironment(Long connectorId, String envCode);

    /**
     * 获取启用环境
     * <p>
     * 从 flow_connector 表读取当前启用的环境完整信息
     *
     * @param connectorId 连接器实例ID
     * @return 启用的环境完整信息，未设置则返回null
     */
    FlowConnectorEnvLiteVO getEnabledEnv(Long connectorId);
}
