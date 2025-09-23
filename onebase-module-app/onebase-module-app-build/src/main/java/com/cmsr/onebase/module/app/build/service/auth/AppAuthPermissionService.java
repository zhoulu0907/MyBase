package com.cmsr.onebase.module.app.build.service.auth;

import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import jakarta.validation.Valid;

import java.util.List;

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

    List<AuthPermissionScope> getPermissionScope();
}
