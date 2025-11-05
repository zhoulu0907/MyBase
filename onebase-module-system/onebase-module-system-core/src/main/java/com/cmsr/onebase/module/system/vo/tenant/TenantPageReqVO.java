package com.cmsr.onebase.module.system.vo.tenant;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 租户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPageReqVO extends PageParam {

    @Schema(description = "租户编码", example = "1")
    private String tenantCode;

    @Schema(description = "租户名", example = "onebase")
    private String name;

    @Schema(description = "联系人", example = "onebase")
    private String contactName;

    @Schema(description = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(description = "租户状态（1正常 0停用）", example = "1")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

    @Schema(description = "关键词，模糊搜索使用")
    private String keyword;

    @Schema(description = "管理员id")
    private String adminUserId;

    @Schema(description = "时间排序 asc/desc")
    private  String sortType;

}
