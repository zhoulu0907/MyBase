package com.cmsr.onebase.module.flow.context.condition;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:48
 */
@Data
public class SortItem implements Serializable {

    private String sortField;

    private String sortType;

}
