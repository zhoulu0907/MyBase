package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.flow.context.graph.nodes.IfBlockNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.SwitchCaseNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.SwitchConditionNodeData;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:06
 */
@Getter
@Setter
public class JsonGraph {

    private static final String NEW_LINE = "\n";

    public static final String INDENT = "    ";

    private List<JsonGraphNode> nodes;

    public String toFlowChain() {
        return blocksNodeDefine(0, nodes);
    }

    private String blocksNodeDefine(int deep, List<JsonGraphNode> blocks) {
        if (CollectionUtils.isEmpty(blocks)) {
            throw new IllegalArgumentException("blocks子节点不能为空");
        }
        StringBuilder define = new StringBuilder();
        define.append(repeatIndent(deep)).append("SER(");
        for (int i = 0; i < blocks.size(); i++) {
            String nodeCmp = nodeDefine(deep, blocks.get(i));
            define.append(NEW_LINE).append(repeatIndent(deep + 1, nodeCmp));
            if (i != blocks.size() - 1) {
                define.append(",");
            }
        }
        define.append(NEW_LINE).append(repeatIndent(deep)).append(")");
        return define.toString();
    }


    private String nodeDefine(int deep, JsonGraphNode node) {
        if (StringUtils.equalsAny(node.getType(),
                "dataAdd", "dataCalc", "dataDelete", "dataQueryMultiple", "dataQuery", "dataUpdate",
                "modal", "refresh", "navigate",
                "startDateField", "startForm", "startEntity", "startTime", "startAPI", "startBPM",
                "end", "log")) {
            return toDefine(node);
        } else if (StringUtils.equals(node.getType(), "ifBlock")) {
            return ifBlockNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "ifCase")) {
            return ifCaseNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "loop")) {
            return loopNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "switchCondition")) {
            return switchNodeDefine(deep, node);
        }
        throw new IllegalArgumentException("未知的节点类型: " + node.getType());
    }

    private String loopNodeDefine(int deep, JsonGraphNode node) {
        StringBuilder define = new StringBuilder();
        define.append("FOR(").append(toDefine(node)).append(").DO(");
        define.append(NEW_LINE).append(blocksNodeDefine(deep + 1, node.getBlocks()));
        define.append(NEW_LINE).append(")");
        return define.toString();
    }

    private String switchNodeDefine(int deep, JsonGraphNode node) {
        SwitchConditionNodeData switchConditionNodeData = (SwitchConditionNodeData) node.getData();
        StringBuilder define = new StringBuilder();
        define.append("SWITCH(").append(toDefine(node)).append(").TO( ");
        int caseCount = 0;
        for (JsonGraphNode switchCaseNode : node.getBlocks()) {
            if (Objects.equals(switchCaseNode.getType(), "switchCase")) {
                if (caseCount > 0) {
                    define.append(",");
                }
                SwitchCaseNodeData switchCaseNodeData = (SwitchCaseNodeData) switchCaseNode.getData();
                switchConditionNodeData.addCase(switchCaseNode.getId(), switchCaseNodeData.getFilterCondition());
                define.append(NEW_LINE).append(switchCaseNodeDefine(deep + 1, switchCaseNode));
                caseCount++;
            }
        }
        define.append(NEW_LINE).append(" )");
        for (JsonGraphNode switchCaseNode : node.getBlocks()) {
            if (Objects.equals(switchCaseNode.getType(), "switchDefault")) {
                switchConditionNodeData.setDefaultId(switchCaseNode.getId());
                define.append(switchDefaultNodeDefine(deep + 1, switchCaseNode));
            }
        }
        return define.toString();
    }

    private String switchCaseNodeDefine(int deep, JsonGraphNode caseJsonGraphNode) {
        String blocksNodeDefine = blocksNodeDefine(deep, caseJsonGraphNode.getBlocks());
        return String.format("%s.tag(\"%s\")", blocksNodeDefine, caseJsonGraphNode.getId());
    }

    private String switchDefaultNodeDefine(int deep, JsonGraphNode defaultJsonGraphNode) {
        String blocksNodeDefine;
        if (CollectionUtils.isNotEmpty(defaultJsonGraphNode.getBlocks())) {
            blocksNodeDefine = blocksNodeDefine(deep, defaultJsonGraphNode.getBlocks());
        } else {
            StringBuilder define = new StringBuilder();
            define.append(repeatIndent(deep)).append("noop");
            blocksNodeDefine = define.toString();
        }
        StringBuilder define = new StringBuilder();
        define.append(".DEFAULT(");
        define.append(NEW_LINE).append(blocksNodeDefine).append(".tag(\"").append(defaultJsonGraphNode.getId()).append("\")");
        define.append(")");
        return define.toString();
    }

    private String ifCaseNodeDefine(int deep, JsonGraphNode node) {
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
        StringBuilder define = new StringBuilder();
        define.append("IF(").append(toDefine(node)).append(",");
        define.append(ifBlockNodeDefine(deep + 1, trueNode)).append(",");
        define.append(ifBlockNodeDefine(deep + 1, falseNode)).append("");
        define.append(NEW_LINE).append(")");

        return define.toString();
    }

    private String ifBlockNodeDefine(int deep, JsonGraphNode jsonGraphNode) {
        if (CollectionUtils.isEmpty(jsonGraphNode.getBlocks())) {
            return NEW_LINE + repeatIndent(deep) + "noop";
        }
        String blocksNodeDefine = blocksNodeDefine(deep, jsonGraphNode.getBlocks());
        StringBuilder define = new StringBuilder();
        define.append(NEW_LINE).append(blocksNodeDefine).append(".tag(\"").append(jsonGraphNode.getId()).append("\")");
        return define.toString();
    }

    private String toDefine(JsonGraphNode node) {
        StringBuilder define = new StringBuilder();
        define.append(node.getType()).append(".tag(\"").append(node.getId()).append("\")");
        return define.toString();
    }

    private static String repeatIndent(int deep) {
        if (deep <= 0) {
            return "";
        }
        return StringUtils.repeat(INDENT, deep);
    }

    /**
     * 在content的每行数据前面都加上deep个缩进
     *
     * @param deep
     * @param content
     * @return
     */
    private static String repeatIndent(int deep, String content) {
        String collect = content.lines().map(line -> repeatIndent(deep) + line).collect(Collectors.joining(NEW_LINE));
        return collect;
    }

    public JsonGraphNode getStartNode() {
        JsonGraphNode jsonGraphNode = nodes.get(0);
        if (!jsonGraphNode.getType().contains("start")) {
            throw new IllegalArgumentException("第一个节点必须是开始节点");
        }
        return jsonGraphNode;
    }

    public Map<String, NodeData> getNodeData() {
        Map<String, NodeData> result = new HashMap<>();
        recursiveNode(result, nodes);
        return result;
    }

    private void recursiveNode(Map<String, NodeData> result, List<JsonGraphNode> nodes) {
        for (JsonGraphNode node : nodes) {
            result.put(node.getId(), node.getData());
            if (node.getBlocks() != null && node.getBlocks().size() > 0) {
                recursiveNode(result, node.getBlocks());
            }
        }
    }
}
