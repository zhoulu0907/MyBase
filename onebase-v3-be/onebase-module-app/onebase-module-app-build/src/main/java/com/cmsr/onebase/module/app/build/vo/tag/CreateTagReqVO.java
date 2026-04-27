package com.cmsr.onebase.module.app.build.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName CreateTagReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/6 16:35
 */
@Data
@Schema(description = "应用管理 - 创建标签 Request VO")
public class CreateTagReqVO {


    @Schema(description = "标签名")
    @NotNull(message = "tag名不能为空")
    private String tagName;

}
