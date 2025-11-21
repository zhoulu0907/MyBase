package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

public class CorpDeptUserHelper {

    public static DefaultConfigStore getDeptConfigStore() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        String fromSceneType = WebFrameworkUtils.getXFromSceneType();
        if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            // configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (null != corpId) {
                configStore.and(Compare.EQUAL, DeptDO.CORP_ID, corpId);
                configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.CORP.getCode());
            } else {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
        }
        return configStore;
    }


    public static DefaultConfigStore getUserConfigStore() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        String fromSceneType = WebFrameworkUtils.getXFromSceneType();
        if (XFromSceneTypeEnum.PLATFORM.getCode().equals(fromSceneType)) {
            configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.PLATFORM.getValue());
        } else if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            // configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.TENANT.getValue());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (null != corpId) {
                configStore.and(Compare.EQUAL, DeptDO.CORP_ID, corpId);
                configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.CORP.getValue());
            } else {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
        }
        return configStore;
    }
}

