package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateActionReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateActionReqVO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorActionDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 统一动作配置Service接口
 *
 * @author onebase
 * @since 2026-03-19
 */
public interface FlowConnectorActionService {

    /**
     * 创建动作
     *
     * @param createReqVO 创建请求
     * @return 动作ID
     */
    Long createAction(@Valid CreateActionReqVO createReqVO);

    /**
     * 更新动作
     *
     * @param updateReqVO 更新请求
     */
    void updateAction(@Valid UpdateActionReqVO updateReqVO);

    /**
     * 删除动作
     *
     * @param id 动作ID
     */
    void deleteAction(Long id);

    /**
     * 获取动作详情
     *
     * @param id 动作ID
     * @return 动作DO
     */
    FlowConnectorActionDO getAction(Long id);

    /**
     * 根据动作UUID获取动作
     *
     * @param actionUuid 动作UUID
     * @return 动作DO
     */
    FlowConnectorActionDO getActionByUuid(String actionUuid);

    /**
     * 根据连接器UUID获取动作列表
     *
     * @param connectorUuid 连接器UUID
     * @return 动作列表
     */
    List<FlowConnectorActionDO> getActionsByConnectorUuid(String connectorUuid);

    /**
     * 根据连接器UUID和动作编码获取动作
     *
     * @param connectorUuid 连接器UUID
     * @param actionCode    动作编码
     * @return 动作DO
     */
    FlowConnectorActionDO getActionByConnectorUuidAndCode(String connectorUuid, String actionCode);

    /**
     * 分页查询动作列表
     *
     * @param connectorUuid 连接器UUID
     * @param pageNo        页码
     * @param pageSize      每页大小
     * @return 分页结果
     */
    PageResult<FlowConnectorActionDO> getActionPage(String connectorUuid, int pageNo, int pageSize);
}