package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import jakarta.validation.Valid;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthPermissionService {

    AuthDetailFunctionPermissionVO getFunctionPermission(AuthPermissionReqVO reqVO);

    AuthDetailDataPermissionVO getDataPermission(AuthPermissionReqVO reqVO);

    AuthDetailFieldPermissionVO getFieldPermission(AuthPermissionReqVO reqVO);

    void updatePageAllowed(AuthUpdatePageAllowedReqVO reqVO);

    void updateOperation(AuthUpdateOperationReqVO reqVO);

    void updateDataGroup(AuthUpdateDataGroupReqVO reqVO);

    void deleteDataGroup(Long id);

    void updateField(@Valid AuthUpdateFieldReqVO reqVO);

}
