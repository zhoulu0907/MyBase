package com.cmsr.onebase.module.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:48
 */
@Schema(description = "应用管理 - 标签列表 Request VO")
@Data
public class TagListRespVO {

    @Schema(description = "标签名称")
    private String tagName;
}
