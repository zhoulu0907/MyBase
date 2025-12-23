package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/17 17:35
 */
@Schema(description = "节点分类 - 分页查询节点分类请求参数")
@Data
public class PageNodeConfigReqVO extends PageParam {

    @Schema(description = "level1_code")
    private String level1Code;

    @Schema(description = "level2_code")
    private String level2Code;

    @Schema(description = "level3_code")
    private String level3Code;

    @Schema(description = "节点名称")
    private String nodeName;

}
