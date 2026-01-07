package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实体类。
 *
 * @author HuangJie
 * @since 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("flow_node_config")
public class FlowNodeConfigDO extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 类别1，用于搜索
     */
    private String level1Code;

    /**
     * 类别2，用于搜索
     */
    private String level2Code;

    /**
     * 类别3，用于搜索
     */
    private String level3Code;

    /**
     * 展示名称
     */
    private String nodeName;

    /**
     * 类别编码，唯一的
     */
    private String nodeCode;

    /**
     * 简单描述，一句话
     */
    private String simpleRemark;

    /**
     * 消息描述，大段文字
     */
    private String detailDescription;

    /**
     * 是否开启
     */
    private Integer activeStatus;

    /**
     * 默认参数
     */
    private String defaultProperties;

    private String connConfigType;

    private String connConfig;

    private String actionConfigType;

    private String actionConfig;

    private Integer sortOrder;

}
