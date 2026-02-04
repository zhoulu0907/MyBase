package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeActionRefDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeActionRefMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowNodeActionRefTableDef.FLOW_NODE_ACTION_REF;

/**
 * 流程节点动作引用 Repository
 *
 * @author onebase
 * @since 2026-01-26
 */
@Repository
public class FlowNodeActionRefRepository extends ServiceImpl<FlowNodeActionRefMapper, FlowNodeActionRefDO> {

    /**
     * 查询指定连接器动作的所有引用
     *
     * @param connectorId 连接器ID
     * @param actionId    动作ID
     * @return 引用列表
     */
    public List<FlowNodeActionRefDO> findByConnectorAndAction(Long connectorId, String actionId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.CONNECTOR_ID.eq(connectorId))
                .and(FLOW_NODE_ACTION_REF.ACTION_ID.eq(actionId))
                .and(FLOW_NODE_ACTION_REF.DELETED.eq(0));
        return list(query);
    }

    /**
     * 查询指定连接器的多个动作的所有引用
     *
     * @param connectorId 连接器ID
     * @param actionIds   动作ID列表
     * @return 引用列表
     */
    public List<FlowNodeActionRefDO> findByConnectorIdAndActionIds(Long connectorId, List<String> actionIds) {
        if (actionIds == null || actionIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.CONNECTOR_ID.eq(connectorId))
                .and(FLOW_NODE_ACTION_REF.ACTION_ID.in(actionIds))
                .and(FLOW_NODE_ACTION_REF.DELETED.eq(0));
        return list(query);
    }

    /**
     * 查询指定连接器的所有动作引用
     *
     * @param connectorId 连接器ID
     * @return 引用列表
     */
    public List<FlowNodeActionRefDO> findByConnector(Long connectorId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.CONNECTOR_ID.eq(connectorId))
                .and(FLOW_NODE_ACTION_REF.DELETED.eq(0));
        return list(query);
    }

    /**
     * 查询指定节点的动作引用
     *
     * @param nodeId 节点ID
     * @return 引用列表
     */
    public List<FlowNodeActionRefDO> findByNode(Long nodeId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.NODE_ID.eq(nodeId))
                .and(FLOW_NODE_ACTION_REF.DELETED.eq(0));
        return list(query);
    }

    /**
     * 删除指定连接器的所有引用
     *
     * @param connectorId 连接器ID
     */
    public void deleteByConnector(Long connectorId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.CONNECTOR_ID.eq(connectorId));
        remove(query);
    }

    /**
     * 删除指定节点的引用
     *
     * @param nodeId 节点ID
     */
    public void deleteByNode(Long nodeId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_NODE_ACTION_REF.NODE_ID.eq(nodeId));
        remove(query);
    }
}
