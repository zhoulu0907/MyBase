package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.*;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.ViewEnmu;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageRepository;

    @Resource
    private AppPageSetRepository pageSetRepository;

    @Resource(name = "appMenuRepository")
    private AppMenuRepository menuDataRepository;

    @Override
    public PageRespDTO getPage(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }

        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    @Override
    public Long createPage(CreatePageDTO createPageDTO) {
        AppResourcePageDO pageDO = BeanUtils.toBean(createPageDTO, AppResourcePageDO.class);
        pageRepository.save(pageDO);
        return pageDO.getId();
    }

    @Override
    public Long createPageView(CreatePageViewDTO createPageViewDTO) {
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = createPageViewDTO.getViewName();
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = false;
        AppResourcePageDO formPageDO = PageUtils.initPage(createPageViewDTO.getPageSetId(), formPageName, formRouterPath, formPageType, formOpenViewMode);

        String viewType = createPageViewDTO.getViewType();

        if (viewType.equals(ViewEnmu.MIX.getValue())) {
            formPageDO.setEditViewMode(1);
            formPageDO.setDetailViewMode(1);
        }
        if (viewType.equals(ViewEnmu.EDIT.getValue())) {
            formPageDO.setEditViewMode(1);
        }
        if (viewType.equals(ViewEnmu.DETAIL.getValue())) {
            formPageDO.setDetailViewMode(1);
        }

        pageRepository.save(formPageDO);

        AppResourcePagesetPageDO formPageSetPageDO = new AppResourcePagesetPageDO();
        formPageSetPageDO.setPageSetId(createPageViewDTO.getPageSetId());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageId(formPageDO.getId());
        formPageSetPageDO.setIsDefault(0);
        formPageSetPageDO.setDefaultSeq(1);

        pageSetPageRepository.save(formPageSetPageDO);

        return formPageDO.getId();
    }

    @Override
    public Boolean updatePageName(UpdatePageNameDTO updatePageNameDTO) {
        pageRepository.updatePageName(updatePageNameDTO.getPageId(), updatePageNameDTO.getPageName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePage(Long pageId) {
        // 删除页面集关联的页面
        pageSetPageRepository.deleteByPageId(pageId);
        // 删除页面
        pageRepository.removeById(pageId);

        return true;
    }

    @Override
    public List<PageDTO> getFormPageListByAppId(Long appId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", appId);

        List<AppMenuDO> menuDOList = menuDataRepository.findByApplicationId(appId);
        List<Long> menuIdList = menuDOList.stream()
                .map(AppMenuDO::getId)
                .toList();

        configs = new DefaultConfigStore();
        configs.in(Compare.EMPTY_VALUE_SWITCH.BREAK, "menu_id", menuIdList, true, true);

//        List<AppResourcePagesetDO> pageSetDoList = pageSetRepository.findAllByConfig(configs);
//
//        List<Long> pageSetIdList = pageSetDoList.stream()
//                .map(AppResourcePagesetDO::getId)
//                .collect(Collectors.toList());
//
//        configs = new DefaultConfigStore();
//        configs.in(Compare.EMPTY_VALUE_SWITCH.BREAK, "pageset_id", pageSetIdList, true, true);
//        configs.eq("page_type", PageEnum.FORM.getValue());
//        List<AppResourcePageDO> pageDOList = pageRepository.findAllByConfig(configs);

//        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);

//        return pageDTOList;
        return null;
    }

    @Override
    public String getMetadataByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_id", pageId);

        AppResourcePagesetPageDO pageSetPageDO = pageSetPageRepository.findByPageId(pageId);
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetPageDO.getPageSetId());

        return pageSetDO.getMainMetadata();
    }

    @Override
    public List<PageDTO> listPageView(Long pageSetId) {
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetId(pageSetId);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);

        return pageDTOList;
    }


}
