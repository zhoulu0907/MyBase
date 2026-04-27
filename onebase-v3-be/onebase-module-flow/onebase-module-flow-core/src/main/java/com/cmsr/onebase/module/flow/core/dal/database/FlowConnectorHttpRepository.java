package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorHttpMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef.FLOW_CONNECTOR_HTTP;

/**
 * HTTP连接器动作配置Repository
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Repository
public class FlowConnectorHttpRepository extends ServiceImpl<FlowConnectorHttpMapper, FlowConnectorHttpDO> {

    /**
     * 根据应用ID和UUID查询HTTP动作配置
     *
     * @param applicationId 应用ID
     * @param httpUuid      HTTP动作UUID
     * @return HTTP动作配置
     */
    public FlowConnectorHttpDO findByApplicationAndUuid(Long applicationId, String httpUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.HTTP_UUID.eq(httpUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1));
        return this.getOne(query);
    }

    /**
     * 根据连接器UUID查询HTTP动作列表
     *
     * @param connectorUuid 连接器UUID
     * @return HTTP动作列表
     */
    public List<FlowConnectorHttpDO> findByConnectorUuid(String connectorUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc());
        return this.list(query);
    }

    /**
     * 根据应用ID和连接器UUID查询HTTP动作列表
     *
     * @param applicationId 应用ID
     * @param connectorUuid 连接器UUID
     * @return HTTP动作列表
     */
    public List<FlowConnectorHttpDO> findByApplicationAndConnectorUuid(
            Long applicationId, String connectorUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc());
        return this.list(query);
    }

    /**
     * 根据应用ID和HTTP动作编码查询
     *
     * @param applicationId 应用ID
     * @param httpCode      HTTP动作编码
     * @return HTTP动作配置
     */
    public FlowConnectorHttpDO findByApplicationAndCode(Long applicationId, String httpCode) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.HTTP_CODE.eq(httpCode))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1));
        return this.getOne(query);
    }
}
