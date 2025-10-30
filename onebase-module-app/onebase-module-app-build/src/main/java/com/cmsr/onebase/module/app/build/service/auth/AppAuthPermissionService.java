package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthPermissionService {

    AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReq reqVO);

    AuthDetailDataPermissionVO getDataPermission(AuthPermissionReq reqVO);

    AuthDetailFieldPermissionVO getFieldPermission(AuthPermissionReq reqVO);

    void updatePageAllowed(AuthUpdatePageAllowedReqVO reqVO);

    void updateOperation(AuthUpdateOperationReqVO reqVO);

    void updateView(AuthUpdateViewReqVO reqVO);

    void updateDataGroup(AuthUpdateDataGroupReqVO reqVO);

    void deleteDataGroup(Long id);

    void updateField(AuthUpdateFieldReqVO reqVO);

}
