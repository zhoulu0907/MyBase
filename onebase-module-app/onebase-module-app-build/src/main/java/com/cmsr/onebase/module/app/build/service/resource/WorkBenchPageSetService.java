package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.SavePageSetReqVO;

public interface WorkBenchPageSetService {

    /**
     * 初始化一个空的工作台页面
     *
     * @param pageSetDO 页面集信息
     */
    void initWorkbenchPage(AppResourcePagesetDO pageSetDO);

    LoadPageSetRespVO loadWorkbenchPageSet(AppResourcePagesetDO pageSetDO);

    void saveWorkbenchPage(SavePageSetReqVO savePageSetReqVO, AppResourcePagesetDO pageSetDO);

}
