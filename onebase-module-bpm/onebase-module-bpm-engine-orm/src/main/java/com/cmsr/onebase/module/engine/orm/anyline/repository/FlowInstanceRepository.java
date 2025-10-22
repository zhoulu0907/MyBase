package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.FlowInstanceDO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import jakarta.annotation.Resource;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WarmFlow 流程实例 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowInstanceRepository extends DataRepository<FlowInstance> {
    @Resource
    private AnylineService<?> service;

    public FlowInstanceRepository() {
        super(FlowInstance.class);
    }

    public PageResult<FlowInstance> getTodoTaskPage(BpmFlowTodoTaskPageReqVO reqVO, Long userId) {
        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        buildQuerySql(sql, reqVO, userId);
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());

        // 执行查询
        DataSet dataSet = service.querys(sql.toString(), navi);

        return new PageResult<>(
                dataSet.entitys(FlowInstance.class).stream().toList(),
                dataSet.total()
        );
    }
    private void buildQuerySql(StringBuilder sql, BpmFlowTodoTaskPageReqVO reqVO,Long userId) {
        sql.append("SELECT ")
                .append("    i.* ")
                .append("FROM ")
                .append("    bpm_flow_instance i ")
                .append("    INNER JOIN bpm_flow_task t ON i.id = t.instance_id AND t.deleted = 0 ")
                .append("WHERE ")
                .append("    i.deleted = 0 ")
                .append("    AND i.flow_status = '1' ")  // 审批中
                .append("    AND ( ")
                .append("        t.creator = ").append(userId).append(" ")
                .append("        OR ")
                .append("        EXISTS ( ")
                .append("            SELECT 1 FROM bpm_flow_user u ")
                .append("            WHERE u.associated = t.id ")
                .append("            AND u.type = '1' ")
                .append("            AND u.processed_by = '").append(userId).append("' ")
                .append("            AND u.deleted = 0 ")
                .append("        ) ")
                .append("        OR ")
                .append("        EXISTS ( ")
                .append("            SELECT 1 FROM bpm_flow_user u ")
                .append("            WHERE u.associated = t.id ")
                .append("            AND u.type = '1' ")
                .append("            AND u.processed_by IN ( ")
                .append("                select distinct t2.id::varchar  from system_users t \n" +
                                        "inner join  system_user_role t1 on t.id = t1.user_id  \n" +
                                        "inner join system_role t2 on t1.role_id  = t2.id\n" +
                                        "where t.deleted  = 0 and t1.deleted =0  and t2.deleted =0\n" +
                                        "and t.id =  ").append(userId).append(" ")
                .append("            ) ")
                .append("            AND u.deleted = 0 ")
                .append("        ) ")
                .append("    ) ");
        // 动态添加其他查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'processTitle' LIKE '%").append(reqVO.getProcessTitle()).append("%' ");
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'initiator' LIKE '%").append(reqVO.getInitiator()).append("%' ");
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'formSummary' LIKE '%").append(reqVO.getFormSummary()).append("%' ");
        }
        if (reqVO.getStartTime() != null && reqVO.getStartTime().length == 2) {
            sql.append(" AND i.create_time >= '").append(reqVO.getStartTime()[0]).append("' ");
            sql.append(" AND i.create_time <= '").append(reqVO.getStartTime()[1]).append("' ");
        }
        // 排序逻辑
        sql.append(" ORDER BY ");
        if (reqVO.getSortType() != null) {
            switch (reqVO.getSortType()) {
                case "asc":
                    sql.append(" i.create_time ASC "); // 最早处理的
                    break;
                case "desc":
                default:
                    sql.append(" i.create_time DESC "); // 最新处理的（默认）
            }
        } else {
            sql.append(" i.create_time DESC "); // 默认最新处理的
        }
    }

    public PageResult<FlowInstanceDO> getMyCreatedPage(BpmMyCreatedPageReqVO reqVO, Long userId) {
        StringBuilder sql = new StringBuilder();
        querySql(sql, reqVO, userId);

        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        DataSet dataSet = service.querys(sql.toString(), navi);
        return new PageResult<>(
                dataSet.entitys(FlowInstanceDO.class).stream().toList(),
                dataSet.total()
        );
    }
    private void querySql(StringBuilder sql, BpmMyCreatedPageReqVO reqVO, Long userId) {
        sql.append("SELECT ")
                .append("    i.id , ")
                .append("    i.ext, ")
                .append("    CASE ")
                .append("        WHEN i.flow_status = '0' THEN '草稿' ")
                .append("        WHEN i.flow_status = '1' THEN '审批中' ")
                .append("        WHEN i.flow_status IN ('2', '8') THEN '已通过' ")
                .append("        WHEN i.flow_status = '9' THEN '已拒绝' ")
                .append("        WHEN i.flow_status IN ('6', '11') THEN '已撤回' ")
                .append("        WHEN i.flow_status IN ('4', '5', '10') THEN '已终止' ")
                .append("        ELSE '未知状态' ")
                .append("    END AS flow_status, ")
                .append("    COALESCE( ")
                .append("        (SELECT STRING_AGG(DISTINCT fu.processed_by, ', ') ")
                .append("         FROM bpm_flow_user fu ")
                .append("         INNER JOIN bpm_flow_task t ON fu.associated = t.id ")
                .append("         WHERE t.instance_id = i.id AND t.deleted = 0 AND fu.type = '1'), ")
                .append("        i.ext::json->'processInfo'->>'initiator' ")
                .append("    ) AS currentNodeHandler, ")
                .append("    i.create_time, ")
                .append("    i.update_time ")
                .append("FROM ")
                .append("    bpm_flow_instance i ")
                .append("WHERE ")
                .append("    i.deleted = 0 ")
                .append("    AND i.creator = ").append(userId).append(" ");

        // 流程状态筛选
        if (reqVO.getFlowStatus() != null && !"ALL".equals(reqVO.getFlowStatus())) {
            switch (reqVO.getFlowStatus()) {
                case "DRAFT":
                    sql.append(" AND i.flow_status = '0' ");
                    break;
                case "APPROVING":
                    sql.append(" AND i.flow_status = '1' ");
                    break;
                case "APPROVED":
                    sql.append(" AND i.flow_status IN ('2', '8') "); // 已通过和已完成
                    break;
                case "REJECTED":
                    sql.append(" AND i.flow_status = '9' ");
                    break;
                case "REVOKED":
                    sql.append(" AND i.flow_status = '6' ");
                    break;
                case "TERMINATED":
                    sql.append(" AND i.flow_status IN ('4', '5', '10') "); // 已终止、已作废、已失效
                    break;
            }
        }

        // 模糊搜索条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'processTitle' LIKE '%").append(reqVO.getProcessTitle()).append("%' ");
        }

        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'initiator' LIKE '%").append(reqVO.getInitiator()).append("%' ");
        }

        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            sql.append(" AND i.ext::json->'processInfo'->>'formSummary' LIKE '%").append(reqVO.getFormSummary()).append("%' ");
        }

        // 发起时间范围
        if (reqVO.getSubmitTime() != null && reqVO.getSubmitTime().length == 2) {
            sql.append(" AND i.create_time >= '").append(reqVO.getSubmitTime()[0]).append("' ");
            sql.append(" AND i.create_time <= '").append(reqVO.getSubmitTime()[1]).append("' ");
        }

        // 排序逻辑
        sql.append(" ORDER BY ");
        if (reqVO.getSortType() != null) {
            switch (reqVO.getSortType()) {
                case "asc":
                    sql.append(" i.update_time ASC "); // 最早处理的
                    break;
                case "desc":
                default:
                    sql.append(" i.update_time DESC "); // 最新处理的（默认）
            }
        } else {
            sql.append(" i.update_time DESC "); // 默认最新处理的
        }
    }

}