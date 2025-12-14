package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@Data
@NodeType("modal")
public class ModalNodeData extends NodeData implements Serializable {

    private Boolean closeWarn;

    private String modalType;

    private Boolean cancelWarn;

    private String modalTitle;

    private String okText;

    private String cancelText;

    private String title;

    private String prompt;

    private Integer afterCancel;

    private Integer arrange;

    private List<Field> fields;

    @Data
    public static class Field implements Serializable {

        private String id;

        private String fieldName;

        private String fieldType;

        private Object value;

    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("closeWarn", closeWarn);
        map.put("modalType", modalType);
        map.put("cancelWarn", cancelWarn);
        map.put("modalTitle", modalTitle);
        map.put("okText", okText);
        map.put("cancelText", cancelText);
        map.put("title", title);
        map.put("prompt", prompt);
        map.put("afterCancel", afterCancel);
        map.put("arrange", arrange);
        map.put("fields", fields);
        return map;
    }
}
