package com.cmsr.onebase.module.system.vo.project;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cmsr.onebase.framework.excel.core.annotations.DictFormat;
import com.cmsr.onebase.framework.excel.core.convert.DictConvert;
import com.cmsr.onebase.module.system.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectInfoRespVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("项目ID")
    private Long id;

    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PROJECT001")
    @ExcelProperty("项目编码")
    private String projectCode;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发项目")
    @ExcelProperty("项目名称")
    private String projectName;

    @Schema(description = "来源平台", example = "lingji")
    @ExcelProperty("来源平台")
    private String sourcePlatform;

    @Schema(description = "外部平台项目ID", example = "EXT001")
    private String externalProjectId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "描述", example = "这是一个研发项目")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}