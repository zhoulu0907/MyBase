package com.cmsr.onebase.module.app.util;

import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;

public class PageUtils {

    public static PageDO initPage(String pageCode, String pageName, String routerPath) {
        PageDO pageDO = new PageDO();
        pageDO.setPageCode(pageCode);
        pageDO.setPageName(pageName);
        pageDO.setTitle(pageName);
        pageDO.setLayout("horizontal");
        pageDO.setWidth("auto");
        pageDO.setMargin("0");
        pageDO.setBackgroundColor("#FFFFFF");
        pageDO.setMainMetadata("");
        pageDO.setBpmEnabled(false);
        pageDO.setRouterPath(routerPath);
        pageDO.setRouterName(pageName);
        pageDO.setRouterMetaAuthRequired(false);
        pageDO.setRouterMetaTitle(pageName);

        return pageDO;
    }

}
