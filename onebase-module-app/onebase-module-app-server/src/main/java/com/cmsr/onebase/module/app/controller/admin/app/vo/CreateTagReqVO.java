package com.cmsr.onebase.module.app.controller.admin.app.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName CreateTagReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/6 16:35
 */
@Data
public class CreateTagReqVO {
    @NotNull(message = "tag名不能为空")
    private String tagName;
}
