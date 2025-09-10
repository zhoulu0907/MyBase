package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:06
 */
@Data
public class JsonGraph {

    private static final String NEW_LINE = "\n";

    public static final String INDENT = "    ";

    public static JsonGraph of(String json) {
        JsonGraph jsonGraph = JsonUtils.parseObject(json, JsonGraph.class);
        return jsonGraph;
    }

    private List<JsonGraphNode> nodes;

    public JsonGraphNode getStartNode() {
        JsonGraphNode jsonGraphNode = nodes.get(0);
        if (!jsonGraphNode.getType().contains("start")) {
            throw new IllegalArgumentException("第一个节点必须是开始节点");
        }
        return jsonGraphNode;
    }

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
                "start", "end", "dataAdd", "dataDelete", "dataUpdate")) {
            return toDefine(node);
        } else if (Objects.equals(node.getType(), "loop")) {
            return loopNodeDefine(deep, node);
        } else if (Objects.equals(node.getType(), "switch")) {
            return switchNodeDefine(deep, node);
        }
        throw new IllegalArgumentException("未知的节点类型: " + node.getType());
    }

    private String loopNodeDefine(int deep, JsonGraphNode node) {
        StringBuilder define = new StringBuilder();
        define.append("WHILE(").append(toDefine(node)).append(".DO(");
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

    private String toDefine(JsonGraphNode node) {
        StringBuilder define = new StringBuilder();
        define.append(node.getType()).append(".tag(\"").append(node.getId()).append("\")");
        return define.toString();
    }

    @NotNull
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
}
