package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorEnvReqVO;

import java.util.List;

/**
 * 连接器环境配置 Service 接口
 * <p>
 * 提供环境配置的业务操作接口
 *
 * @author zhoulu
 * @since 2026-01-23
 */
public interface FlowConnectorEnvService {

    /**
     * 分页查询环境配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<FlowConnectorEnvVO> pageEnvs(PageConnectorEnvReqVO pageReqVO);

    /**
     * 根据ID查询环境配置详情
     *
     * @param id 主键ID
     * @return 环境配置详情
     */
    FlowConnectorEnvVO getEnvDetail(Long id);

    /**
     * 根据UUID查询环境配置详情
     *
     * @param envUuid 环境配置UUID
     * @return 环境配置详情
     */
    FlowConnectorEnvVO getEnvDetailByUuid(String envUuid);

    /**
     * 根据连接器类型查询环境配置列表
     *
     * @param typeCode 连接器类型编号
     * @return 环境配置列表
     */
    List<FlowConnectorEnvVO> listByTypeCode(String typeCode);

    /**
     * 创建环境配置
     *
     * @param createVO 创建请求
     * @return 创建的环境配置
     */
    FlowConnectorEnvVO createEnv(CreateFlowConnectorEnvReqVO createVO);

    /**
     * 更新环境配置
     *
     * @param updateVO 更新请求
     */
    void updateEnv(UpdateFlowConnectorEnvReqVO updateVO);

    /**
     * 删除环境配置
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 启用/禁用环境配置
     *
     * @param id           主键ID
     * @param activeStatus 启用状态（0-禁用，1-启用）
     */
    void updateActiveStatus(Long id, Integer activeStatus);

    /**
     * 列出所有环境配置（精简版）
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    PageResult<FlowConnectorEnvLiteVO> listAll(PageParam pageParam);
}
