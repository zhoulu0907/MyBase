package com.cmsr.onebase.module.flow.context.express;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/26 16:22
 */
@Data
public class OrExpresses {

    private List<AndExpresses> expressesList;
}
