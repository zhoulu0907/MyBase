package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.module.app.api.auth.AppAuthRoleApi;
import com.cmsr.onebase.module.app.api.auth.dto.AuthRoleDTO;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Setter
@Service
public class AppAuthRoleApiImpl implements AppAuthRoleApi {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;
    @Override
    public AuthRoleDTO findRoleByAppIdAndRoleCode(Long appId, String roleCode) {
        AuthRoleDO authRoleDO= appAuthRoleRepository.findByAppIdAndRoleCode(appId,roleCode);
        if(null != authRoleDO){
            AuthRoleDTO dto= new AuthRoleDTO();
            dto.setId(authRoleDO.getId());
            return dto;
        }
        return null;
    }
}
