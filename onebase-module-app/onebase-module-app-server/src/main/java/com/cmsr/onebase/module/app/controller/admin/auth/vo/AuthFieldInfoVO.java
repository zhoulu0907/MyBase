package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 18:29
 */
@Data
@Schema(description = "应用管理 - 字段信息 Response VO")
public class AuthFieldInfoVO {

    @Schema(description = "字段code")
    private String fieldName;

    @Schema(description = "字段名称")
    private String displayName;

    @Schema(description = "字段类型")
    private String fieldType;

    @Schema(description = "字段操作符号")
    private List<Operator> operatorMap;

    @Data
    public static class Operator{

        private String value;

        private String label;

    }

}
