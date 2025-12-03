package com.cmsr.onebase.module.app.build.vo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 16:50
 */
@Data
@Schema(description = "应用管理 - 数据 Filter VO")
public class AuthDataFilterVO {

    @Schema(description = "字段id")
    private String fieldUuid;

    @Schema(description = "比较操作符号")
    private String fieldOperator;

    @Schema(description = "字段值类型")
    private String fieldValueType;

    @Schema(description = "字段值")
    private String fieldValue;


}
