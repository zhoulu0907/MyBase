package com.cmsr.onebase.module.app.build.service.appresource.workbench;

import com.cmsr.onebase.module.app.build.vo.appresource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.build.vo.appresource.SavePageSetReqVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetPageDO;

import java.util.List;

public interface WorkBenchPageSetService {

    /**
     * 初始化一个空的工作台页面
     * @param pageSetDO 页面集信息
     */
    void initWorkbenchPage(PageSetDO pageSetDO);

    LoadPageSetRespVO loadWorkbenchPageSet(PageSetDO pageSetDO, List<PageSetPageDO> pageSetPageDOs);

    void saveWorkbenchPage(SavePageSetReqVO savePageSetReqVO, PageSetDO pageSetDO);

}
