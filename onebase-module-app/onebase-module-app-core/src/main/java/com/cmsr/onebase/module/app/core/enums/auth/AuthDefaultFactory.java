package com.cmsr.onebase.module.app.core.enums.auth;


import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthOperationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import org.apache.commons.lang3.math.NumberUtils;


import java.util.*;



public class AuthDefaultFactory {

    //    AuthPermissionDO-----------------------------------------------------------------
    private static final Integer AuthPermissionDO_Default_IsPageAllowed = NumberUtils.INTEGER_ONE;

    private static final Integer AuthPermissionDO_Default_IsAllViewsAllowed = NumberUtils.INTEGER_ONE;

    private static final Integer AuthPermissionDO_Default_IsAllFieldsAllowed = NumberUtils.INTEGER_ONE;

    //    AuthOperationDO------------------------------------------------------------------
    private static final Integer AuthOperationDO_Default_IsAllowed = NumberUtils.INTEGER_ONE;

    //    AuthDataGroupDO-----------------------------------------------------------------
    private static final String AuthDataGroupDO_Default_GroupName = "";

    private static final Integer AuthDataGroupDO_Default_GroupOrder = NumberUtils.INTEGER_ONE;

    private static final String AuthDataGroupDO_Default_Description = "";
//        StringUtils.EMPTY;

    private static final Long AuthDataGroupDO_Default_ScopeFieldId = NumberUtils.LONG_ONE;

    private static final String AuthDataGroupDO_Default_ScopeLevel = "";

    private static final String AuthDataGroupDO_Default_ScopeValue = "";

    private static final Integer AuthDataGroupDO_Default_IsOperable = NumberUtils.INTEGER_ONE;

    // AuthFieldDO-----------------------------------------------------------------------------------
    private static final Integer AuthFieldDO_Default_IsCanDownload = NumberUtils.INTEGER_ONE;

    private static final Integer AuthFieldDO_Default_IsCanEdit = NumberUtils.INTEGER_ONE;

    private static final Integer AuthFieldDO_Default_IsCanRead = NumberUtils.INTEGER_ONE;

    //应用 AuthPermissionDO
    public static AuthPermissionDO createAuthPermissionDO(AuthPermissionReqVO reqVO) {
        AuthPermissionDO ap = new AuthPermissionDO();
        ap.setApplicationId(reqVO.getApplicationId());
        ap.setRoleId(reqVO.getRoleId());
        ap.setMenuId(reqVO.getMenuId());
        ap.setIsPageAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsPageAllowed);
        ap.setIsAllViewsAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsAllViewsAllowed);
        ap.setIsAllFieldsAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsAllFieldsAllowed);
        return ap;
    }

    public static AuthPermissionDO createAuthPermissionDO(Long applicationId,Long roleId,Long menuId) {
        AuthPermissionDO ap = new AuthPermissionDO();
        ap.setApplicationId(applicationId);
        ap.setRoleId(roleId);
        ap.setMenuId(menuId);
        ap.setIsPageAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsPageAllowed);
        ap.setIsAllViewsAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsAllViewsAllowed);
        ap.setIsAllFieldsAllowed(AuthDefaultFactory.AuthPermissionDO_Default_IsAllFieldsAllowed);
        return ap;
    }



    //操作权限 AuthOperationDO
    public static List<AuthOperationDO> createAuthOperationDOList(AuthPermissionReqVO reqVO) {
        List<AuthOperationDO> aolist = new ArrayList<AuthOperationDO>();
        for (AuthOperationEnum ao : AuthOperationEnum.values()) {
            AuthOperationDO ao_Iterator = new AuthOperationDO();
            ao_Iterator.setApplicationId(reqVO.getApplicationId());
            ao_Iterator.setRoleId(reqVO.getRoleId());
            ao_Iterator.setMenuId(reqVO.getMenuId());
            ao_Iterator.setOperationCode(ao.name());
            ao_Iterator.setIsAllowed(AuthDefaultFactory.AuthOperationDO_Default_IsAllowed);
            aolist.add(ao_Iterator);
        }
        return aolist;
    }

    //数据组权限 authDataGroupDOS
    public static AuthDataGroupDO createAuthDataGroupDO(AuthPermissionReqVO reqVO) {
        AuthDataGroupDO adg = new AuthDataGroupDO();
        adg.setApplicationId(reqVO.getApplicationId());
        adg.setRoleId(reqVO.getRoleId());
        adg.setMenuId(reqVO.getMenuId());
        adg.setGroupName(AuthDefaultFactory.AuthDataGroupDO_Default_GroupName);
        adg.setGroupOrder(AuthDefaultFactory.AuthDataGroupDO_Default_GroupOrder);
        adg.setDescription(AuthDefaultFactory.AuthDataGroupDO_Default_Description);
        adg.setScopeFieldId(AuthDefaultFactory.AuthDataGroupDO_Default_ScopeFieldId);
        adg.setScopeLevel(AuthDefaultFactory.AuthDataGroupDO_Default_ScopeLevel);
        adg.setScopeValue(AuthDefaultFactory.AuthDataGroupDO_Default_ScopeValue);
        adg.setIsOperable(AuthDefaultFactory.AuthDataGroupDO_Default_IsOperable);
        return adg;
    }

    //数据组权限list authDataGroupDOS
    public static List<AuthDataGroupDO> createListAuthDataGroupDOList(AuthPermissionReqVO reqVO) {
        List<AuthDataGroupDO> adgList = new ArrayList<AuthDataGroupDO>();
        adgList.add(createAuthDataGroupDO(reqVO));
        return adgList;
    }


    public static AuthFieldDO createAuthFieldDO(){
        AuthFieldDO afdo = new AuthFieldDO();
        afdo.setIsCanDownload(AuthDefaultFactory.AuthFieldDO_Default_IsCanDownload);
        afdo.setIsCanEdit(AuthDefaultFactory.AuthFieldDO_Default_IsCanEdit);
        afdo.setIsCanRead(AuthDefaultFactory.AuthFieldDO_Default_IsCanRead);
        return afdo;
    }

}
