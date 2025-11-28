package com.cmsr.onebase.module.app.runtime.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 19:10
 */
@Schema(description = "应用管理 - 标签 Request VO")
@Data
public class TagRespVO {

    @Schema(description = "标签id")
    private Long id;

    @Schema(description = "标签名称")
    private String tagName;

}
