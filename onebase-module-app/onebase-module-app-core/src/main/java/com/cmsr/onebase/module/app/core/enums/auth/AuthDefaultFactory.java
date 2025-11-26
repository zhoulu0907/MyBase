package com.cmsr.onebase.module.app.core.enums.auth;


import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;


public class AuthDefaultFactory {

    /**
     * "本人提交"
     */
    private static final String OWN_SUBMIT = "ownSubmit";

    /**
     * "创建"
     */
    private static final String CREATE = "create";


    /**
     * "编辑"
     */
    private static final String EDIT = "edit";

    /**
     * "删除"
     */
    private static final String DELETE = "delete";

    /**
     * "导入"
     */
    private static final String IMPORT = "import";

    /**
     * "导出"
     */
    private static final String EXPORT = "export";

    /**
     * "分享"
     */
    private static final String SHARE = "share";

    private static final String DEFAULT_OPERATION_TAGS = JsonUtils.toJsonString(List.of(CREATE, EDIT, DELETE, IMPORT, EXPORT, SHARE));

    private static final String DEFAULT_DATA_FILTER = JsonUtils.toJsonString(List.of());

    //应用 AppAuthPermissionDO
    public static AppAuthPermissionDO createAuthPermissionDO(AuthPermissionReq req) {
        AppAuthPermissionDO ap = new AppAuthPermissionDO();
        ap.setApplicationId(req.getApplicationId());
        ap.setRoleId(req.getRoleId());
        ap.setMenuId(req.getMenuId());
        ap.setIsPageAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllViewsAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllFieldsAllowed(NumberUtils.INTEGER_ONE);
        ap.setOperationTags(DEFAULT_OPERATION_TAGS);
        return ap;
    }

    public static AppAuthPermissionDO createAuthPermissionDO() {
        AppAuthPermissionDO ap = new AppAuthPermissionDO();
        ap.setIsPageAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllViewsAllowed(NumberUtils.INTEGER_ONE);
        ap.setIsAllFieldsAllowed(NumberUtils.INTEGER_ONE);
        ap.setOperationTags(DEFAULT_OPERATION_TAGS);
        return ap;
    }

    //数据组权限 authDataGroupDOS
    public static AppAuthDataGroupDO createAuthDataGroupDO(AuthPermissionReq req) {
        AppAuthDataGroupDO adg = new AppAuthDataGroupDO();
        adg.setGroupName("默认权限");
        adg.setApplicationId(req.getApplicationId());
        adg.setRoleId(req.getRoleId());
        adg.setMenuId(req.getMenuId());
        adg.setScopeTags(JsonUtils.toJsonString(List.of(OWN_SUBMIT)));
        adg.setDataFilter(DEFAULT_DATA_FILTER);
        adg.setOperationTags(JsonUtils.toJsonString(List.of(EDIT, DELETE)));
        return adg;
    }

    public static AppAuthDataGroupDO createAuthDataGroupDO() {
        AppAuthDataGroupDO adg = new AppAuthDataGroupDO();
        adg.setGroupName("默认权限");
        adg.setScopeTags(JsonUtils.toJsonString(List.of(OWN_SUBMIT)));
        adg.setDataFilter(DEFAULT_DATA_FILTER);
        adg.setOperationTags(JsonUtils.toJsonString(List.of(EDIT, DELETE)));
        return adg;
    }


}
