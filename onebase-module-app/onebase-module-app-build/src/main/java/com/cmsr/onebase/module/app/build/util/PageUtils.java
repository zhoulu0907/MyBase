package com.cmsr.onebase.module.app.build.util;


import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;

public class PageUtils {

    public static AppResourcePageDO initPage(String pageSetUuid, String pageName, String routerPath, String pageType, Boolean openViewMode) {
        AppResourcePageDO pageDO = new AppResourcePageDO();
        pageDO.setPageUuid(UuidUtils.getUuid());
        pageDO.setPageSetUuid(pageSetUuid);
        pageDO.setPageName(pageName);
        pageDO.setTitle(pageName);
        pageDO.setLayout("horizontal");
        pageDO.setWidth("auto");
        pageDO.setMargin("0");
        pageDO.setBackgroundColor("#FFFFFF");
        pageDO.setMainMetadata("{}");
        pageDO.setRouterPath(routerPath);
        pageDO.setRouterName(pageName);
        pageDO.setRouterMetaTitle(pageName);
        pageDO.setPageType(pageType);

        if (openViewMode) {
            pageDO.setDetailViewMode(1);
            pageDO.setEditViewMode(1);
            pageDO.setIsDefaultDetailViewMode(1);
            pageDO.setIsDefaultEditViewMode(1);
        } else {
            pageDO.setDetailViewMode(0);
            pageDO.setEditViewMode(0);
            pageDO.setIsDefaultDetailViewMode(0);
            pageDO.setIsDefaultEditViewMode(0);
        }

        return pageDO;
    }


    public static AppResourceWorkbenchPageDO initWorkbenchPage(String pageSetUuid, String pageName, String routerPath, String pageType, Boolean openViewMode) {
        AppResourceWorkbenchPageDO pageDO = new AppResourceWorkbenchPageDO();
        pageDO.setPageUuid(UuidUtils.getUuid());
        pageDO.setPageSetUuid(pageSetUuid);
        pageDO.setPageName(pageName);
        pageDO.setTitle(pageName);
        pageDO.setLayout("horizontal");
        pageDO.setWidth("auto");
        pageDO.setMargin("0");
        pageDO.setBackgroundColor("#FFFFFF");
        pageDO.setMainMetadata("{}");
        pageDO.setRouterPath(routerPath);
        pageDO.setRouterName(pageName);
        pageDO.setRouterMetaTitle(pageName);
//        pageDO.setPageType(pageType);

        if (openViewMode) {
            pageDO.setDetailViewMode(1);
            pageDO.setEditViewMode(1);
            pageDO.setIsDefaultDetailViewMode(1);
            pageDO.setIsDefaultEditViewMode(1);
        } else {
            pageDO.setDetailViewMode(0);
            pageDO.setEditViewMode(0);
            pageDO.setIsDefaultDetailViewMode(0);
            pageDO.setIsDefaultEditViewMode(0);
        }

        return pageDO;
    }

}
