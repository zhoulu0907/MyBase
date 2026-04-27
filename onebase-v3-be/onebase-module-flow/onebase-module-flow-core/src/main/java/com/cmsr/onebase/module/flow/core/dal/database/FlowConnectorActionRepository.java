package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorActionDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorActionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorActionTableDef.FLOW_CONNECTOR_ACTION;

/**
 * 统一动作配置Repository
 *
 * @author onebase
 * @since 2026-03-19
 */
@Repository
public class FlowConnectorActionRepository extends ServiceImpl<FlowConnectorActionMapper, FlowConnectorActionDO> {

    /**
     * 根据应用ID和动作UUID查询
     *
     * @param applicationId 应用ID
     * @param actionUuid    动作UUID
     * @return 动作配置
     */
    public FlowConnectorActionDO findByApplicationAndUuid(Long applicationId, String actionUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_ACTION.ACTION_UUID.eq(actionUuid))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1));
        return this.getOne(query);
    }

    /**
     * 根据连接器UUID查询动作列表
     *
     * @param connectorUuid 连接器UUID
     * @return 动作列表
     */
    public List<FlowConnectorActionDO> findByConnectorUuid(String connectorUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_ACTION.SORT_ORDER.asc());
        return this.list(query);
    }

    /**
     * 根据应用ID和连接器UUID查询动作列表
     *
     * @param applicationId 应用ID
     * @param connectorUuid 连接器UUID
     * @return 动作列表
     */
    public List<FlowConnectorActionDO> findByApplicationAndConnectorUuid(
            Long applicationId, String connectorUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_ACTION.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_ACTION.SORT_ORDER.asc());
        return this.list(query);
    }

    /**
     * 根据应用ID和动作编码查询
     *
     * @param applicationId 应用ID
     * @param actionCode    动作编码
     * @return 动作配置
     */
    public FlowConnectorActionDO findByApplicationAndCode(Long applicationId, String actionCode) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_ACTION.ACTION_CODE.eq(actionCode))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1));
        return this.getOne(query);
    }

    /**
     * 根据连接器UUID和动作编码查询
     *
     * @param connectorUuid 连接器UUID
     * @param actionCode    动作编码
     * @return 动作配置
     */
    public FlowConnectorActionDO findByConnectorUuidAndCode(String connectorUuid, String actionCode) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_ACTION.ACTION_CODE.eq(actionCode))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1));
        return this.getOne(query);
    }

    /**
     * 根据应用ID和连接器类型查询动作列表
     *
     * @param applicationId 应用ID
     * @param connectorType 连接器类型
     * @return 动作列表
     */
    public List<FlowConnectorActionDO> findByApplicationAndType(Long applicationId, String connectorType) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ACTION.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_ACTION.CONNECTOR_TYPE.eq(connectorType))
                .and(FLOW_CONNECTOR_ACTION.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_ACTION.SORT_ORDER.asc());
        return this.list(query);
    }
}