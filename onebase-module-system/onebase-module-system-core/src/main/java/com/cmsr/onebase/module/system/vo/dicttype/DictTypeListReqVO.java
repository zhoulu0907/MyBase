package com.cmsr.onebase.module.system.vo.dicttype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 字典类型列表查询 Request VO
 *
 * @author bty418
 * @date 2025-10-24
 */
@Schema(description = "管理后台 - 字典类型列表查询 Request VO")
@Data
public class DictTypeListReqVO {

    @Schema(description = "字典类型名称，模糊匹配", example = "onebase")
    private String name;

    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String type;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

    @Schema(description = "字典所有者类型（app-应用自定义字典，tenant-空间公共字典）", example = "tenant")
    private String dictOwnerType;

    @Schema(description = "字典所有者ID（应用ID或租户ID）", example = "1")
    private Long dictOwnerId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}

