package com.cmsr.onebase.module.system.controller.admin.platform.vo;

import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.platform.PlatformInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 平台信息响应VO
 */
@Data
public class PlatformInfoRespVo {

    @Schema(description = "平台信息")
    private PlatformInfoDto platformInfoDto;

    @Schema(description = "凭证列表")
    private List<LicenseDO> licenseList;

}