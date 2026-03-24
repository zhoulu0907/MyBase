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
     * 从flow_connector.config字段解析环境配置信息
     *
     * @param connectorId 连接器ID
     * @return 环境配置列表
     */
    List<FlowConnectorEnvLiteVO> getEnvironments(Long connectorId);

    /**
     * 查询连接器的指定环境配置信息
     * <p>
     * 从flow_connector.config的properties中解析指定环境的Formily Schema
     *
     * @param connectorId 连接器实例ID（主键）
     * @param envName     环境名称（如DEV环境配置）
     * @return 环境配置 VO
     */
    EnvironmentConfigVO getEnvironmentConfig(Long connectorId, String envName);

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
     * 将新的环境配置添加到 flow_connector.config.properties 中
     * 如果环境已存在则拒绝保存
     *
     * @param connectorId 连接器实例ID
     * @param reqVO       环境配置请求
     * @return 保存是否成功
     */
    Boolean saveEnvironmentConfig(Long connectorId, @Valid SaveEnvironmentConfigReqVO reqVO);

    /**
     * 更新连接器环境配置
     * <p>
     * 更新已存在的环境配置，环境必须存在
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
     * 在连接器配置中设置当前启用的环境名称
     * 环境必须存在才能启用
     *
     * @param connectorId 连接器实例ID
     * @param envName     环境名称（传空或null表示取消启用）
     * @return 设置是否成功
     */
    Boolean enableEnvironment(Long connectorId, String envName);

    /**
     * 获取启用环境名称
     * <p>
     * 从连接器配置中读取当前启用的环境名称
     *
     * @param connectorId 连接器实例ID
     * @return 启用的环境名称，未设置则返回null
     */
    String getEnabledEnvName(Long connectorId);
}