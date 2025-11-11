package com.cmsr.onebase.module.app.build.util;


import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench.WorkBenchPageDO;

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
        pageDO.setRouterPath(routerPath);
        pageDO.setRouterName(pageName);
        pageDO.setRouterMetaTitle(pageName);
        pageDO.setPageType(pageType);

        if (openViewMode) {
            pageDO.setDetailViewMode(1);
            pageDO.setEditViewMode(1);
            pageDO.setIsDefaultDetailViewMode(1);
            pageDO.setIsDefaultEditViewMode(1);
        }else{
            pageDO.setDetailViewMode(0);
            pageDO.setEditViewMode(0);
            pageDO.setIsDefaultDetailViewMode(0);
            pageDO.setIsDefaultEditViewMode(0);
        }

        return pageDO;
    }


    public static WorkBenchPageDO initWorkbenchPage(Long pageSetId, String pageName, String routerPath, String pageType, Boolean openViewMode ) {
        WorkBenchPageDO pageDO = new WorkBenchPageDO();
        pageDO.setPageSetId(pageSetId);
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
        }else{
            pageDO.setDetailViewMode(0);
            pageDO.setEditViewMode(0);
            pageDO.setIsDefaultDetailViewMode(0);
            pageDO.setIsDefaultEditViewMode(0);
        }

        return pageDO;
    }

}
