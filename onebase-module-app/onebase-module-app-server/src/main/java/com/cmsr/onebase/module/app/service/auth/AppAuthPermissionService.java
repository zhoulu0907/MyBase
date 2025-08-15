package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthDetailPermissionVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
import jakarta.validation.Valid;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthPermissionService {

    AuthDetailPermissionVO getPermission(AuthPermissionReqVO reqVO);

    void updatePermission(@Valid AuthDetailPermissionVO detailVO);

}
