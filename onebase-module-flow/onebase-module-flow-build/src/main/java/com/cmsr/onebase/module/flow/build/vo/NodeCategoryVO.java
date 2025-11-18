package com.cmsr.onebase.module.flow.build.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:16
 */
@Data
public class NodeCategoryVO {

    private String code;

    private String name;

    private List<NodeCategoryVO> subNodeCategories;

}
