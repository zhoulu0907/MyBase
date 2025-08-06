package com.cmsr.onebase.module.app.controller.admin.app.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/31 8:26
 */
@Schema(description = "应用管理 - 应用菜单更新顺序 Resp VO")
@Data
public class MenuOrderUpdateReqVO {

    private Long id;

    private String parentUuid;

    private List<Long> ids;

}
