package com.cmsr.onebase.module.system.vo.license;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * License 分页查询请求 VO
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Data
public class LicensePageReqVO extends PageParam {

    @Schema(description = "企业名称(模糊)")
    private String enterpriseName;

    @Schema(description = "企业编号")
    private String enterpriseCode;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "到期时间-开始")
    private LocalDateTime expireTimeFrom;

    @Schema(description = "到期时间-结束")
    private LocalDateTime expireTimeTo;

}
