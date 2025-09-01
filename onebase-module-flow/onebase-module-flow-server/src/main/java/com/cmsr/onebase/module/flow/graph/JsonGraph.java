package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

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

    private List<JsonNode> nodes;

    public String toFlowChain() {
        return blocksNodeDefine(1, nodes);
    }

    private String blocksNodeDefine(int deep, List<JsonNode> blocks) {
        if (CollectionUtils.isEmpty(blocks)) {
            throw new IllegalArgumentException("blocks子节点不能为空");
        }
        StringBuilder define = new StringBuilder();
        define.append("SER(").append(NEW_LINE);
        for (int i = 0; i < blocks.size(); i++) {
            String nodeCmp = nodeDefine(deep, blocks.get(i));
            define.append(StringUtils.repeat(INDENT, deep)).append(INDENT).append(nodeCmp);
            if (i != blocks.size() - 1) {
                define.append(",").append(NEW_LINE);
            } else {
                define.append(NEW_LINE);
            }
        }
        define.append(StringUtils.repeat(INDENT, deep)).append(")");
        return define.toString();
    }

    private String nodeDefine(int deep, JsonNode node) {
        if (StringUtils.equalsAny(node.getType(),
                "start", "end", "dataAdd", "dataDelete", "dataUpdate")) {
            return node.toDefine();
        } else if (Objects.equals(node.getType(), "loop")) {
            return loopNodeDefine(deep + 1, node);
        } else if (Objects.equals(node.getType(), "switch")) {
            return switchNodeDefine(deep + 1, node);
        }
        throw new IllegalArgumentException("未知的节点类型: " + node.getType());
    }

    private String loopNodeDefine(int deep, JsonNode node) {
        StringBuilder define = new StringBuilder();
        define.append("WHILE(").append(node.toDefine()).append(".DO(").append(NEW_LINE);
        define.append(blocksNodeDefine(deep + 1, node.getBlocks()));
        define.append(NEW_LINE).append(StringUtils.repeat(INDENT, deep)).append(")");
        return define.toString();
    }

    private String switchNodeDefine(int deep, JsonNode node) {
        StringBuilder define = new StringBuilder();
        define.append("SWITCH(").append(node.toDefine()).append(".TO(");
        for (JsonNode caseDefaultNode : node.getBlocks()) {
            if (Objects.equals(caseDefaultNode.getType(), "case")) {
                define.append(NEW_LINE).append(StringUtils.repeat(INDENT, deep + 1)).append(switchCaseNodeDefine(deep + 1, caseDefaultNode)).append(",");
            }
        }
        define.append(NEW_LINE).append(StringUtils.repeat(INDENT, deep)).append(")");
        for (JsonNode caseDefaultNode : node.getBlocks()) {
            if (Objects.equals(caseDefaultNode.getType(), "caseDefault")) {
                define.append(switchDefaultNodeDefine(deep + 1, caseDefaultNode));
            }
        }
        return define.toString();
    }

    private String switchCaseNodeDefine(int deep, JsonNode caseJsonNode) {
        String blocksNodeDefine = blocksNodeDefine(deep, caseJsonNode.getBlocks());
        return String.format("%s.id(\"%s\")", blocksNodeDefine, caseJsonNode.getId());
    }

    private String switchDefaultNodeDefine(int deep, JsonNode defaultJsonNode) {
        String blocksNodeDefine = blocksNodeDefine(deep, defaultJsonNode.getBlocks());
        StringBuilder define = new StringBuilder();
        define.append(".DEFAULT(").append(blocksNodeDefine).append(NEW_LINE);
        define.append(StringUtils.repeat(INDENT, deep)).append(")").append(".id(\"").append(defaultJsonNode.getId()).append("\"");
        return define.toString();
    }


}
