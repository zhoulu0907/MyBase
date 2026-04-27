package com.cmsr.onebase.module.bpm.core.dto;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowSkip;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyang
 */
@Data
public class BpmExportDataDto {
    /**
     * 流程定义数据
     */
    private List<FlowDefinition> flowDefinitions = new ArrayList<>();

    /**
     * 流程节点数据
     */
    private List<FlowNode> flowNodes = new ArrayList<>();

    /**
     * 流程跳转数据
     */
    private List<FlowSkip> flowSkips = new ArrayList<>();

}
