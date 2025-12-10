package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.nodes.IfBlockNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.LoopNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.SwitchCaseNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.SwitchConditionNodeData;
import com.yomahub.liteflow.builder.el.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author：huangjie
 * @Date：2025/11/13 8:57
 */
public class FlowChainBuilder {

    public static String toFlowChain(JsonGraph jsonGraph) {
        FlowChainBuilder builder = new FlowChainBuilder(jsonGraph);
        SerELWrapper elWrapper = builder.build();
        return elWrapper.toEL(true);
    }

    private JsonGraph jsonGraph;

    private List<JsonGraphNode> nodes;

    public FlowChainBuilder(JsonGraph jsonGraph) {
        this.jsonGraph = jsonGraph;
        this.nodes = jsonGraph.getNodes();
    }

    public SerELWrapper build() {
        return blocksNodeDefine(nodes);
    }

    private SerELWrapper blocksNodeDefine(List<JsonGraphNode> blocks) {
        if (CollectionUtils.isEmpty(blocks)) {
            return ELBus.ser("noop");
        }
        List<ELWrapper> elWrappers = blocks.stream().map(node -> nodeDefine(node)).toList();
        return ELBus.ser(elWrappers.toArray(new ELWrapper[0]));
    }


    private ELWrapper nodeDefine(JsonGraphNode node) {
        if (StringUtils.equalsAny(node.getType(),
                "dataAdd", "dataCalc", "dataDelete", "dataQueryMultiple", "dataQuery", "dataUpdate",
                "modal", "refresh", "navigate", "javascript",
                "startDateField", "startForm", "startEntity", "startTime", "startAPI", "startBPM",
                "end", "log")) {
            return toDefine(node);
        } else if (StringUtils.equals(node.getType(), "ifBlock")) {
            return ifBlockNodeDefine(node);
        } else if (StringUtils.equals(node.getType(), "ifCase")) {
            return ifCaseNodeDefine(node);
        } else if (StringUtils.equals(node.getType(), "loop")) {
            return loopNodeDefine(node);
        } else if (StringUtils.equals(node.getType(), "switchCondition")) {
            return switchNodeDefine(node);
        }
        throw new IllegalArgumentException("未知的节点类型: " + node.getType());
    }

    private ELWrapper loopNodeDefine(JsonGraphNode node) {
        LoopNodeData loopNodeData = (LoopNodeData) node.getData();
        if (loopNodeData.isBreakMode()) {
            return ELBus.catchException(ELBus.forOpt(toDefine(node)).doOpt(blocksNodeDefine(node.getBlocks())));
        } else if (loopNodeData.isContinueMode()) {
            return ELBus.forOpt(toDefine(node)).doOpt(ELBus.catchException(blocksNodeDefine(node.getBlocks())));
        } else {
            return ELBus.forOpt(toDefine(node)).doOpt(blocksNodeDefine(node.getBlocks()));
        }
    }

    private SwitchELWrapper switchNodeDefine(JsonGraphNode node) {
        SwitchConditionNodeData switchConditionNodeData = (SwitchConditionNodeData) node.getData();
        List<ELWrapper> toELWrappers = new ArrayList<>();
        for (JsonGraphNode switchCaseNode : node.getBlocks()) {
            if (Objects.equals(switchCaseNode.getType(), "switchCase")) {
                SwitchCaseNodeData switchCaseNodeData = (SwitchCaseNodeData) switchCaseNode.getData();
                switchConditionNodeData.addCase(switchCaseNode.getId(), switchCaseNodeData.getFilterCondition());
                toELWrappers.add(switchCaseNodeDefine(switchCaseNode));
            }
        }
        SerELWrapper defaultELWrapper = null;
        for (JsonGraphNode switchCaseNode : node.getBlocks()) {
            if (Objects.equals(switchCaseNode.getType(), "switchDefault")) {
                switchConditionNodeData.setDefaultId(switchCaseNode.getId());
                defaultELWrapper = switchDefaultNodeDefine(switchCaseNode);
            }
        }
        return ELBus.switchOpt(toDefine(node)).to(toELWrappers.toArray(new ELWrapper[0])).defaultOpt(defaultELWrapper);
    }

    private SerELWrapper switchCaseNodeDefine(JsonGraphNode caseJsonGraphNode) {
        return blocksNodeDefine(caseJsonGraphNode.getBlocks()).tag(caseJsonGraphNode.getId());
    }

    private SerELWrapper switchDefaultNodeDefine(JsonGraphNode defaultJsonGraphNode) {
        if (CollectionUtils.isNotEmpty(defaultJsonGraphNode.getBlocks())) {
            return blocksNodeDefine(defaultJsonGraphNode.getBlocks()).tag(defaultJsonGraphNode.getId());
        } else {
            return ELBus.ser("noop").tag(defaultJsonGraphNode.getId());
        }
    }

    private ELWrapper ifCaseNodeDefine(JsonGraphNode node) {
        List<JsonGraphNode> blocks = node.getBlocks();
        //
        JsonGraphNode trueNode = blocks.stream()
                .filter(jsonGraphNode -> ((IfBlockNodeData) jsonGraphNode.getData()).isValue() == true)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ifBlock节点下没有true分支"));
        JsonGraphNode falseNode = blocks.stream()
                .filter(jsonGraphNode -> ((IfBlockNodeData) jsonGraphNode.getData()).isValue() == false)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ifBlock节点下没有false分支"));
        //
        CommonNodeELWrapper ifElWrapper = toDefine(node);
        ELWrapper trueElWrapper = ifBlockNodeDefine(trueNode);
        ELWrapper falseElWrapper = ifBlockNodeDefine(falseNode);
        return ELBus.ifOpt(ifElWrapper, trueElWrapper, falseElWrapper);
    }

    private ELWrapper ifBlockNodeDefine(JsonGraphNode jsonGraphNode) {
        if (CollectionUtils.isEmpty(jsonGraphNode.getBlocks())) {
            return ELBus.node("noop").tag(jsonGraphNode.getId());
        }
        return blocksNodeDefine(jsonGraphNode.getBlocks()).tag(jsonGraphNode.getId());
    }

    private CommonNodeELWrapper toDefine(JsonGraphNode node) {
        return ELBus.node(node.getType()).tag(node.getId());
    }

}
