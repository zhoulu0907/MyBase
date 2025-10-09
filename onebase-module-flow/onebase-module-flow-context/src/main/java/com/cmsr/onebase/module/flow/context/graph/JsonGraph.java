package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.flow.context.graph.nodes.IfBlockNodeData;
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
                "startForm", "startEntity", "startTime", "startDateField", "startAPI", "startBPM",
                "end",
                "log",
                "dataQuery", "dataQueryMultiple", "dataAdd", "dataDelete", "dataUpdate")) {
            return toDefine(node);
        } else if (StringUtils.equals(node.getType(), "loop")) {
            return loopNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "switch")) {
            return switchNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "ifCase")) {
            return ifCaseNodeDefine(deep, node);
        } else if (StringUtils.equals(node.getType(), "ifBlock")) {
            return ifBlockNodeDefine(deep, node);
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
        StringBuilder define = new StringBuilder();
        define.append("SWITCH(").append(toDefine(node)).append(".TO(");
        for (JsonGraphNode caseDefaultNode : node.getBlocks()) {
            if (Objects.equals(caseDefaultNode.getType(), "case")) {
                define.append(NEW_LINE).append(switchCaseNodeDefine(deep + 1, caseDefaultNode)).append(",");
            }
        }
        define.append(NEW_LINE).append(")");
        for (JsonGraphNode caseDefaultNode : node.getBlocks()) {
            if (Objects.equals(caseDefaultNode.getType(), "caseDefault")) {
                define.append(switchDefaultNodeDefine(deep + 1, caseDefaultNode));
            }
        }
        return define.toString();
    }

    private String switchCaseNodeDefine(int deep, JsonGraphNode caseJsonGraphNode) {
        String blocksNodeDefine = blocksNodeDefine(deep, caseJsonGraphNode.getBlocks());
        return String.format("%s.tag(\"%s\")", blocksNodeDefine, caseJsonGraphNode.getId());
    }

    private String switchDefaultNodeDefine(int deep, JsonGraphNode defaultJsonGraphNode) {
        String blocksNodeDefine = blocksNodeDefine(deep, defaultJsonGraphNode.getBlocks());
        StringBuilder define = new StringBuilder();
        define.append(".DEFAULT(");
        define.append(NEW_LINE).append(blocksNodeDefine).append(".tag(\"").append(defaultJsonGraphNode.getId()).append("\")");
        define.append(")");
        return define.toString();
    }

    private String ifCaseNodeDefine(int deep, JsonGraphNode node) {
        List<JsonGraphNode> blocks = node.getBlocks();
        //
        JsonGraphNode trueNode = blocks.stream().filter(jsonGraphNode -> ((IfBlockNodeData) jsonGraphNode.getData()).isValue() == true).findFirst().get();
        JsonGraphNode falseNode = blocks.stream().filter(jsonGraphNode -> ((IfBlockNodeData) jsonGraphNode.getData()).isValue() == false).findFirst().get();
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
