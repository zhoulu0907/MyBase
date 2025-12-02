package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.provider.resource.WorkBenchPageSetServiceProvider;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.SavePageSetReqVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkBenchPageSetServiceImpl implements WorkBenchPageSetService {


    @Resource
    private AppWorkbenchPageRepository appWorkbenchPageRepository;

    @Resource

    private AppWorkbenchComponentRepository appWorkbenchComponentRepository;

    @Resource
    private WorkBenchPageSetService workBenchPageSetService;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private AppPageRepository pageDataRepository;

    @Resource
    private AppWorkbenchPageRepository workbenchPageRepository;

    @Resource
    private AppComponentRepository componentDataRepository;
    @Resource
    private AppWorkbenchComponentRepository workbenchComponentRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Resource
    private WorkBenchPageSetServiceProvider workBenchPageSetServiceProvider;

    @Override
    public void initWorkbenchPage(AppResourcePagesetDO pageSetDO) {
        //1. 初始化工作台页面配置（空页面配置）
        AppResourceWorkbenchPageDO workBenchPageDO = this.buildEmptyWorkbenchPage(pageSetDO);
        appWorkbenchPageRepository.save(workBenchPageDO);
    }

    @Override
    public LoadPageSetRespVO loadWorkbenchPageSet(AppResourcePagesetDO pageSetDO) {
        return workBenchPageSetServiceProvider.loadWorkbenchPageSet(pageSetDO);
    }

    private AppResourceWorkbenchPageDO buildEmptyWorkbenchPage(AppResourcePagesetDO pageSetDO) {
        String pageName = StringUtils.isBlank(pageSetDO.getDisplayName()) ? pageSetDO.getPageSetName() : pageSetDO.getDisplayName();
        AppResourceWorkbenchPageDO workBenchPageDO = new AppResourceWorkbenchPageDO();
        workBenchPageDO.setPageUuid(UuidUtils.getUuid());
        workBenchPageDO.setPageSetUuid(pageSetDO.getPageSetUuid());
        workBenchPageDO.setPageName(pageName);
        workBenchPageDO.setTitle(pageName);
        workBenchPageDO.setPageType(PageEnum.WORKBENCH.getValue());
        //补全必填字段
        workBenchPageDO.setLayout("horizontal");
        workBenchPageDO.setWidth("auto");
        workBenchPageDO.setMargin("0");
        workBenchPageDO.setBackgroundColor("#FFFFFF");
        workBenchPageDO.setMainMetadata("{}");
        workBenchPageDO.setRouterPath("workbench/" + UUID.randomUUID());
        workBenchPageDO.setRouterName(pageName);
        workBenchPageDO.setRouterMetaTitle(pageName);

        return workBenchPageDO;
    }

    public void saveWorkbenchPage(SavePageSetReqVO savePageSetReqVO, AppResourcePagesetDO pageSetDO) {
        savePageSetReqVO.getPages().forEach(page -> {
            if (Boolean.TRUE.equals(page.getCreated())) {
                // 插入新的视图
                String pageCode = UUID.randomUUID().toString();
                String pageName = page.getPageName();
                String routerPath = pageCode + "/form";
                String pageType = PageEnum.FORM.getValue();
                Boolean openViewMode = false;

                AppResourceWorkbenchPageDO pageDO = PageUtils.initWorkbenchPage(pageSetDO.getPageSetUuid(), pageName, routerPath, pageType,
                        openViewMode);
                pageDO.setId(page.getId());

                workbenchPageRepository.save(pageDO);

                page.setId(pageDO.getId());
            }

            AppResourceWorkbenchPageDO pageDO = workbenchPageRepository.getById(page.getId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }

            final AppResourceWorkbenchPageDO finalPageDO = pageDO;
            finalPageDO.setPageName(page.getPageName());
            finalPageDO.setEditViewMode(page.getEditViewMode());
            finalPageDO.setDetailViewMode(page.getDetailViewMode());
            finalPageDO.setIsDefaultEditViewMode(page.getIsDefaultEditViewMode());
            finalPageDO.setIsDefaultDetailViewMode(page.getIsDefaultDetailViewMode());
            finalPageDO.setIsLatestUpdated(page.getIsLatestUpdated());

            workbenchPageRepository.updateById(finalPageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageUuid(finalPageDO.getPageUuid());

            // 插入新的component
            List<AppResourceWorkbenchComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                AppResourceWorkbenchComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), AppResourceWorkbenchComponentDO.class);
                componentDO.setPageUuid(finalPageDO.getPageUuid());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                workbenchComponentRepository.saveBatch(componentDOs);
            }
        });

    }


}
