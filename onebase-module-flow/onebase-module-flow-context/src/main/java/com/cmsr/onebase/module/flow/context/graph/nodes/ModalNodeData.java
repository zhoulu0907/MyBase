package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;

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

    private String cancelText;

    private String title;

    private String prompt;

    private Integer afterCancel;


}
