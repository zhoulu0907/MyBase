package com.cmsr.api.permissions.dataset.dto;


import com.cmsr.model.ITreeBase;
import lombok.Data;

import java.util.List;

@Data
public class BaseTreeNode implements ITreeBase<BaseTreeNode> {

    private Long id;

    private Long pid;

    private String text;

    private String nodeType;

    private List<BaseTreeNode> children;

    public BaseTreeNode(Long id, Long pid, String text, String nodeType) {
        this.id = id;
        this.pid = pid;
        this.text = text;
        this.nodeType = nodeType;
    }

    @Override
    public void setChildren(List<BaseTreeNode> children) {
        this.children = children;
    }

    @Override
    public void setPid(Long pid) {
        this.pid = pid;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}
