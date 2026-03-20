package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.CreateActionReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateActionReqVO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorActionDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorActionRepository;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorActionTableDef.FLOW_CONNECTOR_ACTION;

/**
 * 统一动作配置Service实现
 *
 * @author onebase
 * @since 2026-03-19
 */
@Service
@Validated
public class FlowConnectorActionServiceImpl implements FlowConnectorActionService {

    @Autowired
    private FlowConnectorActionRepository actionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAction(@Valid CreateActionReqVO createReqVO) {
        // 转换为DO
        FlowConnectorActionDO actionDO = BeanUtils.toBean(createReqVO, FlowConnectorActionDO.class);

        // 生成UUID
        actionDO.setActionUuid("action-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));

        // 设置默认值
        if (actionDO.getActiveStatus() == null) {
            actionDO.setActiveStatus(1);
        }
        if (actionDO.getSortOrder() == null) {
            actionDO.setSortOrder(0);
        }

        // 保存
        actionRepository.save(actionDO);

        return actionDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAction(@Valid UpdateActionReqVO updateReqVO) {
        // 查询是否存在
        FlowConnectorActionDO existDO = actionRepository.getById(updateReqVO.getId());
        if (existDO == null) {
            throw new IllegalArgumentException("动作不存在: " + updateReqVO.getId());
        }

        // 转换为DO并更新
        FlowConnectorActionDO actionDO = BeanUtils.toBean(updateReqVO, FlowConnectorActionDO.class);
        actionRepository.updateById(actionDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAction(Long id) {
        FlowConnectorActionDO actionDO = actionRepository.getById(id);
        if (actionDO == null) {
            throw new IllegalArgumentException("动作不存在: " + id);
        }

        // 软删除
        actionDO.setActiveStatus(0);
        actionRepository.updateById(actionDO);
    }

    @Override
    public FlowConnectorActionDO getAction(Long id) {
        return actionRepository.getById(id);
    }

    @Override
    public FlowConnectorActionDO getActionByUuid(String actionUuid) {
        return actionRepository.getOne(
                QueryWrapper.create()
                        .where(FLOW_CONNECTOR_ACTION.ACTION_UUID.eq(actionUuid))
                        .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1))
        );
    }

    @Override
    public List<FlowConnectorActionDO> getActionsByConnectorUuid(String connectorUuid) {
        return actionRepository.findByConnectorUuid(connectorUuid);
    }

    @Override
    public FlowConnectorActionDO getActionByConnectorUuidAndCode(String connectorUuid, String actionCode) {
        return actionRepository.findByConnectorUuidAndCode(connectorUuid, actionCode);
    }

    @Override
    public PageResult<FlowConnectorActionDO> getActionPage(String connectorUuid, int pageNo, int pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_ACTION.SORT_ORDER.asc(), FLOW_CONNECTOR_ACTION.CREATE_TIME.desc());

        if (connectorUuid != null && !connectorUuid.isEmpty()) {
            queryWrapper.and(FLOW_CONNECTOR_ACTION.CONNECTOR_UUID.eq(connectorUuid));
        }

        Page<FlowConnectorActionDO> page = actionRepository.page(
                Page.of(pageNo, pageSize), queryWrapper
        );

        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}