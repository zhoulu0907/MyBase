package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import jakarta.annotation.Resource;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Repository;

/**
 * 历史任务 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends DataRepository<FlowHisTask> {
    @Resource
    private AnylineService<?> service;
    public FlowHisTaskRepository() {
        super(FlowHisTask.class);
    }
    public PageResult<FlowHisTask> getProcessedTaskPage(BpmFlowDoneTaskPageReqVO reqVO, Long userId) {
        StringBuilder sql = new StringBuilder();
        buildQuerySql(sql, reqVO, userId);
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        DataSet dataSet = service.querys(sql.toString(), navi);
        return new PageResult<>(
                dataSet.entitys(FlowHisTask.class).stream().toList(),
                dataSet.total()
        );
    }
    private void buildQuerySql(StringBuilder sql, BpmFlowDoneTaskPageReqVO reqVO, Long userId) {
        sql.append("SELECT ")
                .append("    ht.id, ")
                .append("    ht.update_time AS update_time, ")
                .append("    i.ext, ")
                .append("    CASE ")
                .append("        WHEN ht.skip_type = 'PASS' THEN '已同意' ")
                .append("        WHEN ht.skip_type = 'REJECT' THEN '已拒绝' ")
                .append("        WHEN ht.skip_type = 'NONE' THEN '已退回' ")
                .append("        ELSE '' ")
                .append("    END AS skipType, ")
                .append("    ht.message AS message, ")
                .append("    ht.node_name AS nodeName ")
                .append("FROM ")
                .append("    bpm_flow_his_task ht ")
                .append("    INNER JOIN bpm_flow_instance i ON ht.instance_id = i.id AND i.deleted = 0 ")
                .append("WHERE ")
                .append("    ht.deleted = 0 ")
                .append("    AND ( ")
                .append("        ht.approver = '").append(userId).append("' ")
                .append("        OR (ht.collaborator IS NOT NULL AND ht.collaborator LIKE '%").append(userId).append("%') ")
                .append("        OR EXISTS ( ")
                .append("            SELECT 1 FROM bpm_flow_user u ")
                .append("            WHERE u.associated = ht.task_id ")
                .append("            AND u.processed_by = '").append(userId).append("' ")
                .append("            AND u.deleted = 0 ")
                .append("        ) ")
                .append("    ) ");

        // 动态添加查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'processTitle' LIKE '%").append(reqVO.getProcessTitle()).append("%' ");
        }

        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'initiator' LIKE '%").append(reqVO.getInitiator()).append("%' ");
        }

        if (reqVO.getHandleOperation() != null && !reqVO.getHandleOperation().isEmpty()) {
            // 根据处理操作类型反向映射查询条件
            sql.append(" AND ( ");
            switch (reqVO.getHandleOperation()) {
                case "已同意":
                    sql.append(" ht.skip_type = 'PASS' ");
                    break;
                case "已拒绝":
                    sql.append(" ht.skip_type = 'REJECT' ");
                    break;
                case "已退回":
                    sql.append(" ht.skip_type = 'NONE' ");
                    break;
                case "已转交":
                    sql.append(" ht.cooperate_type = 2 ");
                    break;
                case "已委派":
                    sql.append(" ht.cooperate_type = 3 ");
                    break;
                default:
                    sql.append(" 1=1 ");
            }
            sql.append(" ) ");
        }

        if (reqVO.getHandleTime() != null && reqVO.getHandleTime().length == 2) {
            sql.append(" AND ht.update_time >= '").append(reqVO.getHandleTime()[0]).append("' ");
            sql.append(" AND ht.update_time <= '").append(reqVO.getHandleTime()[1]).append("' ");
        }
        // 排序逻辑
        sql.append(" ORDER BY ");
        if (reqVO.getSortType() != null) {
            switch (reqVO.getSortType()) {
                case "asc":
                    sql.append(" ht.update_time ASC "); // 最早处理的
                    break;
                case "desc":
                default:
                    sql.append(" ht.update_time DESC "); // 最新处理的（默认）
            }
        } else {
            sql.append(" ht.update_time DESC "); // 默认最新处理的
        }
    }
}


