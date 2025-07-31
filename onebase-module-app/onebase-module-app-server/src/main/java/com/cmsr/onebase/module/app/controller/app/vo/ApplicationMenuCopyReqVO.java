package com.cmsr.onebase.module.app.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：mickey
 * @Date：2025/7/30 13:52
 */
@Schema(description = "应用管理 - 应用菜单复制 Request VO")
@Data
public class ApplicationMenuCopyReqVO {

    private Long id;

    private String menuName;

    private String parentUuid;
}
