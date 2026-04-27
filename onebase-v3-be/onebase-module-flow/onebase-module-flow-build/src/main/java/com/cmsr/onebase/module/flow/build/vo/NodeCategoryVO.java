package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:16
 */
@Schema(description = "自动化工作流 - 连接器分类VO")
@Data
public class NodeCategoryVO {

    @Schema(description = "分类编号")
    private String code;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "子类")
    private List<NodeCategoryVO> subNodeCategories;

}
