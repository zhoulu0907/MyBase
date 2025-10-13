package com.cmsr.onebase.module.formula.dal.dataobject;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName FlowSkipDO
 * @Description 节点跳转关联表 DO
 * @Author bty418
 * @Date 2025/08/06 14:00
 */
@Table(name = "flow_skip")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSkipDO extends TenantBaseDO {

    // 列名常量
    public static final String DEFINITION_ID = "definition_id";
    public static final String NOW_NODE_CODE = "now_node_code";
    public static final String NOW_NODE_TYPE = "now_node_type";
    public static final String NEXT_NODE_CODE = "next_node_code";
    public static final String NEXT_NODE_TYPE = "next_node_type";
    public static final String SKIP_NAME = "skip_name";
    public static final String SKIP_TYPE = "skip_type";
    public static final String SKIP_CONDITION = "skip_condition";
    public static final String COORDINATE = "coordinate";

    /**
     * 流程定义id
     */
    @Column(name = DEFINITION_ID)
    private Long definitionId;

    /**
     * 当前流程节点的编码
     */
    @Column(name = NOW_NODE_CODE)
    private String nowNodeCode;

    /**
     * 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = NOW_NODE_TYPE)
    private Integer nowNodeType;

    /**
     * 下一个流程节点的编码
     */
    @Column(name = NEXT_NODE_CODE)
    private String nextNodeCode;

    /**
     * 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = NEXT_NODE_TYPE)
    private Integer nextNodeType;

    /**
     * 跳转名称
     */
    @Column(name = SKIP_NAME)
    private String skipName;

    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    @Column(name = SKIP_TYPE)
    private String skipType;

    /**
     * 跳转条件
     */
    @Column(name = SKIP_CONDITION)
    private String skipCondition;

    /**
     * 坐标
     */
    @Column(name = COORDINATE)
    private String coordinate;

}