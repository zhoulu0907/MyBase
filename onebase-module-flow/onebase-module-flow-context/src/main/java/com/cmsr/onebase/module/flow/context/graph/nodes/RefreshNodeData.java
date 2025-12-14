package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@Data
@NodeType("refresh")
public class RefreshNodeData extends NodeData implements Serializable {

    private String refreshRange;

    private String refreshStrategy;

    private String title;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("refreshRange", refreshRange);
        map.put("refreshStrategy", refreshStrategy);
        map.put("title", title);
        return map;
    }
}
