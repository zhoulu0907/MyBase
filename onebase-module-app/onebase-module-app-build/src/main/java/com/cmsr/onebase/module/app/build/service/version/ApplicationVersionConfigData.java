package com.cmsr.onebase.module.app.build.service.version;

import java.util.List;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;

import lombok.Data;

/**
 * 应用版本配置数据
 *
 * @author zhoumingji
 * @date 2025-01-12
 */
@Data
public class ApplicationVersionConfigData {

    /**
     * 工作台组件
     */
    private List<AppResourceWorkbenchComponentDO> workbenchComponents;

    /**
     * 组件
     */
    private List<AppResourceComponentDO> components;

    /**
     * 工作台页面
     */
    private List<AppResourceWorkbenchPageDO> workbenchPages;

    /**
     * 页面
     */
    private List<AppResourcePageDO> pages;

    /**
     * 页面集
     */
    private List<AppResourcePagesetDO> pageSets;

    /**
     * 菜单
     */
    private List<AppMenuDO> menus;

    /**
     * 权限视图
     */
    private List<AppAuthViewDO> authViews;

    /**
     * 权限字段
     */
    private List<AppAuthFieldDO> authFields;

    /**
     * 权限数据组
     */
    private List<AppAuthDataGroupDO> authDataGroups;

    /**
     * 权限
     */
    private List<AppAuthPermissionDO> authPermissions;

    /**
     * 导航
     */
    private AppNavigationDO navigation;

    /**
     * 权限角色
     */
    private List<AppAuthRoleDO> authRoles;

    /**
     * 元数据配置
     */
    private Object metaDataConfig;

    /**
     * Bpm配置
     */
    private Object bpmConfig;
}
