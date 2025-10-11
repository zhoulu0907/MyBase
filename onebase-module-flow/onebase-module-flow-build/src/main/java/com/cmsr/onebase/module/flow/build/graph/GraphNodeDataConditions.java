package com.cmsr.onebase.module.flow.build.graph;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/29 20:20
 */
@Data
public class GraphNodeDataConditions {

    private List<ConditionItem> conditions;

}
