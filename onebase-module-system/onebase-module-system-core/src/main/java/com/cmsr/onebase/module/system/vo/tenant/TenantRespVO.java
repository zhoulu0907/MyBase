package com.cmsr.onebase.module.system.vo.tenant;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cmsr.onebase.framework.excel.core.annotations.DictFormat;
import com.cmsr.onebase.framework.excel.core.convert.DictConvert;
import com.cmsr.onebase.module.system.enums.DictTypeConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 空间 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TenantRespVO {

    @Schema(description = "空间编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("空间编号")
    private Long id;

    @Schema(description = "空间编码", example = "1")
    @ExcelProperty("空间编码")
    private String tenantCode;

    @Schema(description = "空间名", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @ExcelProperty("空间名")
    private String name;

    @Schema(description = "空间状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "域名", example = "http://cmsr.com")
    private String website;

    @Schema(description = "域名H5", example = "http://h5.cmsr.com")
    private String websiteH5;

    @Schema(description = "空间套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long packageId;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expireTime;

    @Schema(description = "账号数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer accountCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "key", example = "ket")
    private String tenantKey;

    @Schema(description = "secret", example = "secret")
    private String tenantSecret;

    @Schema(description = "应用数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer appCount;

    @Schema(description = "已存在用户数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer existUserCount;

    @Schema(description = "访问地址")
    private String accessUrl;

    @Schema(description = "用户logo")
    private String logoUrl;

    @Schema(description = "企业数")
    private Integer corpCount;

    @Schema(description = "发布模式")
    private String publishModel;

    @Schema(description = "管理员集合")
    private List<TenantAdminUserResVO> tenantAdminUserList;

}
