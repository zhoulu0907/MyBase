package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/22 9:24
 */
@Data
public class StartDateFieldNodeData extends NodeData {

    private Long processId;

    private Long entityId;

    private Long offsetFiledId;

    private String offsetMode;

    private Integer offsetValue;

    private String offsetUnit;

    private String dailyExecTime;

    private boolean batchMode;

    private Integer batchSize;

    /**
     * 过滤条件
     */
    private List<Conditions> filterCondition;

    /**
     * 过滤条件缓存的表达式
     */
    @JsonIgnore
    private Serializable compiledExpression;


    public String createCronExpression() {
        Cron cron = new Cron();
        cron.setMinuteAndHour(dailyExecTime);
        return cron.toCron();
    }
}
