package com.cmsr.onebase.module.system.api.license;

import com.cmsr.onebase.framework.common.biz.system.license.LicenseCommonApi;
import com.cmsr.onebase.framework.common.biz.system.license.dto.LicenseRespDTO;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class LicenseApiImpl implements LicenseCommonApi {

    @Resource
    private LicenseService licenseService;

    @Override
    public CommonResult<LicenseRespDTO> getActiveLicense() {
        LicenseDO licenseDO = licenseService.getLatestActiveLicense();
        return success(BeanUtils.toBean(licenseDO, LicenseRespDTO.class));
    }

}