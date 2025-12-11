package com.cmsr.system.manage;

import com.cmsr.api.permissions.auth.dto.BusiPerCheckDTO;
//import com.cmsr.license.config.XpackInteract;
import org.springframework.stereotype.Component;

@Component
public class CorePermissionManage {

    //@XpackInteract(value = "corePermissionManage", replace = true)
    public boolean checkAuth(BusiPerCheckDTO dto) {
        return true;
    }
}
