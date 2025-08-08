package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionDetailVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthPermissionVO;
import jakarta.validation.Valid;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthPermissionService {

    AuthPermissionDetailVO getPermission(AuthPermissionReqVO reqVO);

    void updatePermission(@Valid AuthPermissionDetailVO detailVO);

}
