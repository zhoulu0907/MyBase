package com.cmsr.onebase.module.app.controller.admin.tag.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName GetTagReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/6 16:35
 */
@Data
public class ListTagReqVO {
    @NotNull(message = "tag名不能为空")
    private String tagName;
}
