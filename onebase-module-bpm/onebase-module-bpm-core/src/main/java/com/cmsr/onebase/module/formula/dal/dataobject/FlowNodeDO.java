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

import java.math.BigDecimal;

/**
 * @ClassName FlowNodeDO
 * @Description 流程节点表 DO
 * @Author bty418
 * @Date 2025/08/06 14:00
 */
@Table(name = "flow_node")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor


public class FlowNodeDO extends TenantBaseDO {

    // 列名常量
    public static final String NODE_TYPE = "node_type";
    public static final String DEFINITION_ID = "definition_id";
    public static final String NODE_CODE = "node_code";
    public static final String NODE_NAME = "node_name";
    public static final String PERMISSION_FLAG = "permission_flag";
    public static final String NODE_RATIO = "node_ratio";
    public static final String COORDINATE = "coordinate";
    public static final String ANY_NODE_SKIP = "any_node_skip";
    public static final String LISTENER_TYPE = "listener_type";
    public static final String LISTENER_PATH = "listener_path";
    public static final String HANDLER_TYPE = "handler_type";
    public static final String HANDLER_PATH = "handler_path";
    public static final String FORM_CUSTOM = "form_custom";
    public static final String FORM_PATH = "form_path";
    public static final String VERSION = "version";
    public static final String EXT = "ext";

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = NODE_TYPE)
    private Integer nodeType;

    /**
     * 流程定义id
     */
    @Column(name = DEFINITION_ID)
    private Long definitionId;

    /**
     * 流程节点编码
     */
    @Column(name = NODE_CODE)
    private String nodeCode;

    /**
     * 流程节点名称
     */
    @Column(name = NODE_NAME)
    private String nodeName;

    /**
     * 权限标识（权限类型:权限标识，可以多个，用@@隔开）
     */
    @Column(name = PERMISSION_FLAG)
    private String permissionFlag;

    /**
     * 流程签署比例值
     */
    @Column(name = NODE_RATIO)
    private BigDecimal nodeRatio;

    /**
     * 坐标
     */
    @Column(name = COORDINATE)
    private String coordinate;

    /**
     * 任意结点跳转
     */
    @Column(name = ANY_NODE_SKIP)
    private String anyNodeSkip;

    /**
     * 监听器类型
     */
    @Column(name = LISTENER_TYPE)
    private String listenerType;

    /**
     * 监听器路径
     */
    @Column(name = LISTENER_PATH)
    private String listenerPath;

    /**
     * 处理器类型
     */
    @Column(name = HANDLER_TYPE)
    private String handlerType;

    /**
     * 处理器路径
     */
    @Column(name = HANDLER_PATH)
    private String handlerPath;

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    @Column(name = FORM_CUSTOM)
    private String formCustom;

    /**
     * 审批表单路径
     */
    @Column(name = FORM_PATH)
    private String formPath;

    /**
     * 版本
     */
    @Column(name = VERSION)
    private String version;

    /**
     * 节点扩展属性
     */
    @Column(name = EXT)
    private String ext;



}
