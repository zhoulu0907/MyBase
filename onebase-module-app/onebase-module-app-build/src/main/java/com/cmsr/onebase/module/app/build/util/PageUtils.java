package com.cmsr.onebase.module.app.build.util;


import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;

public class PageUtils {

    public static PageDO initPage(Long pageSetId, String pageName, String routerPath, String pageType, Boolean openViewMode ) {
        PageDO pageDO = new PageDO();
        pageDO.setPageSetId(pageSetId);
        pageDO.setPageName(pageName);
        pageDO.setTitle(pageName);
        pageDO.setLayout("horizontal");
        pageDO.setWidth("auto");
        pageDO.setMargin("0");
        pageDO.setBackgroundColor("#FFFFFF");
        pageDO.setMainMetadata("{}");
        pageDO.setBpmEnabled(false);
        pageDO.setRouterPath(routerPath);
        pageDO.setRouterName(pageName);
        pageDO.setRouterMetaAuthRequired(false);
        pageDO.setRouterMetaTitle(pageName);
        pageDO.setPageType(pageType);

        if (openViewMode) {
            pageDO.setDetailViewMode(true);
            pageDO.setEditViewMode(true);
            pageDO.setIsDefaultDetailViewMode(true);
            pageDO.setIsDefaultEditViewMode(true);
        }else{
            pageDO.setDetailViewMode(false);
            pageDO.setEditViewMode(false);
            pageDO.setIsDefaultDetailViewMode(false);
            pageDO.setIsDefaultEditViewMode(false);
        }

        return pageDO;
    }

}
