package com.cmsr.onebase.module.app.core.enums.auth;


import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;


public class AuthDefaultFactory {


    //应用 AuthPermissionDO
    public static AuthPermissionDO createAuthPermissionDO(AuthPermissionReqVO reqVO) {
        AuthPermissionDO ap = new AuthPermissionDO();
        ap.setId(null);
        ap.setApplicationId(reqVO.getApplicationId());
        ap.setRoleId(reqVO.getRoleId());
        ap.setMenuId(reqVO.getMenuId());
        ap.setIsPageAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllViewsAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllFieldsAllowed(NumberUtils.INTEGER_ONE);
        ap.setOperationTags(JsonUtils.toJsonString(AuthOperationEnum.getOperations()));
        return ap;
    }


//    //数据组权限list authDataGroupDOS
//    public static List<AuthDataGroupDO> createListAuthDataGroupDOList(AuthPermissionReqVO reqVO) {
//        List<AuthDataGroupDO> adgList = new ArrayList<AuthDataGroupDO>();
//        adgList.add(createAuthDataGroupDO(reqVO));
//        return adgList;
//    }

    //数据组权限 authDataGroupDOS
    public static AuthDataGroupDO createAuthDataGroupDO(AuthPermissionReqVO reqVO) {
        AuthDataGroupDO adg = new AuthDataGroupDO();
        adg.setGroupName("默认权限");
        adg.setApplicationId(reqVO.getApplicationId());
        adg.setRoleId(reqVO.getRoleId());
        adg.setMenuId(reqVO.getMenuId());
        adg.setScopeTags(JsonUtils.toJsonString(List.of(AuthPermissionScopeTagEnum.OWN_SUBMIT.getCode())));
        adg.setOperationTags(JsonUtils.toJsonString(List.of(AuthOperationEnum.EDIT, AuthOperationEnum.DELETE)));
        return adg;
    }

}
