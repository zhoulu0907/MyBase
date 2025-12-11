package com.cmsr.license.server;

import com.cmsr.api.license.LicenseApi;
import com.cmsr.api.license.dto.LicenseRequest;
//import com.cmsr.license.bo.F2CLicResult;
import com.cmsr.license.manage.CoreLicManage;
//import com.cmsr.license.manage.F2CLicManage;
import jakarta.annotation.Resource;
//import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license")
public class LicenseServer implements LicenseApi {

    private static final String product = "DataEase v2";
    @Resource
    private CoreLicManage coreLicManage;

    //@Resource
    //private F2CLicManage f2CLicManage;


    @Override
    public void update(LicenseRequest request) {
        //return f2CLicManage.updateLicense(product, request.getLicense());
    }

    @Override
    public void validate(LicenseRequest request) {
/*         if (StringUtils.isBlank(request.getLicense())) {
            return f2CLicManage.validate();
        }
        return f2CLicManage.validate(product, request.getLicense()); */
    }

    @Override
    public String version() {
        return coreLicManage.getVersion();
    }
}
