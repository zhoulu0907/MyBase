package com.cmsr.license.manage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoreLicManage {

    @Value("1.0.0-SNAPSHOT")
    private String version;

    public String getVersion() {
        return version;
    }

}
