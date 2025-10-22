package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@Data
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

        private String value;

    }
}
