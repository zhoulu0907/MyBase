package com.cmsr.onebase.module.flow.core.external;

import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/12/10 16:40
 */
@Setter
@Component
public class FlowSystemProviderImpl implements FlowSystemProvider {

    @Autowired
    private AdminUserApi adminUserApi;

    @Override
    public Long findUserDeptId(Long userId) {
        AdminUserRespDTO userRespDTO = adminUserApi.getUser(userId).getCheckedData();
        return userRespDTO.getDeptId();
    }

}
