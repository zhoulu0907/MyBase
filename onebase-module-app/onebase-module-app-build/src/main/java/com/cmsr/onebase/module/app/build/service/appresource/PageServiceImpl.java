package com.cmsr.onebase.module.app.build.service.appresource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.*;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.ViewEnmu;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;

import jakarta.annotation.Resource;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private AppPageRepository pageDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource(name = "appMenuRepository")
    private AppMenuRepository menuDataRepository;

    @Override
    public PageRespDTO getPage(Long pageId) {
        PageDO pageDO = pageDataRepository.findById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }

        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    @Override
    public Long createPage(CreatePageDTO createPageDTO) {
        PageDO pageDO = BeanUtils.toBean(createPageDTO, PageDO.class);
        pageDO = pageDataRepository.insert(pageDO);
        return pageDO.getId();
    }

    @Override
    public Long createPageView(CreatePageViewDTO createPageViewDTO) {
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = createPageViewDTO.getViewName();
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = false;
        PageDO formPageDO = PageUtils.initPage(createPageViewDTO.getPageSetId(), formPageName, formRouterPath, formPageType, formOpenViewMode);

        String viewType = createPageViewDTO.getViewType();

        if (viewType.equals(ViewEnmu.MIX.getValue())){
            formPageDO.setEditViewMode(1);
            formPageDO.setDetailViewMode(1);
        }
        if (viewType.equals(ViewEnmu.EDIT.getValue())){
            formPageDO.setEditViewMode(1);
        }
        if (viewType.equals(ViewEnmu.DETAIL.getValue())){
            formPageDO.setDetailViewMode(1);
        }

        pageDataRepository.insert(formPageDO);

        PageSetPageDO formPageSetPageDO = new PageSetPageDO();
        formPageSetPageDO.setPageSetId(createPageViewDTO.getPageSetId());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageId(formPageDO.getId());
        formPageSetPageDO.setIsDefault(0);
        formPageSetPageDO.setDefaultSeq(1);

        pageSetPageDataRepository.insert(formPageSetPageDO);

        return formPageDO.getId();
    }

    @Override
    public Boolean updatePageName(UpdatePageNameDTO updatePageNameDTO) {
        pageDataRepository.updatePageName(updatePageNameDTO.getPageId(), updatePageNameDTO.getPageName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePage(Long pageId) {
        // 删除页面集关联的页面
        pageSetPageDataRepository.deleteByPageId(pageId);
        // 删除页面
        pageDataRepository.deleteById(pageId);

        return true;
    }

    @Override
    public List<PageDTO> getFormPageListByAppId(Long appId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", appId);

        List<MenuDO> menuDOList = menuDataRepository.findAllByConfig(configs);
        List<Long> menuIdList = menuDOList.stream()
                .map(MenuDO::getId)
                .collect(Collectors.toList());

        configs = new DefaultConfigStore();
        configs.in(Compare.EMPTY_VALUE_SWITCH.BREAK, "menu_id", menuIdList, true, true);

        List<PageSetDO> pageSetDoList = pageSetDataRepository.findAllByConfig(configs);

        List<Long> pageSetIdList = pageSetDoList.stream()
                .map(PageSetDO::getId)
                .collect(Collectors.toList());

        configs = new DefaultConfigStore();
        configs.in(Compare.EMPTY_VALUE_SWITCH.BREAK, "pageset_id", pageSetIdList, true, true);
        configs.eq("page_type", PageEnum.FORM.getValue());
        List<PageDO> pageDOList = pageDataRepository.findAllByConfig(configs);

        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);

        return pageDTOList;
    }

    @Override
    public String getMetadataByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_id", pageId);

        PageSetPageDO pageSetPageDO =pageSetPageDataRepository.findByPageId(pageId);
        PageSetDO pageSetDO =  pageSetDataRepository.findById(pageSetPageDO.getPageSetId());

        return pageSetDO.getMainMetadata();
    }

    @Override
    public List<PageDTO> listPageView(Long pageSetId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("pageset_id", pageSetId);
        configs.eq("page_type",  PageEnum.FORM.getValue());

        List<PageDO> pageDOList = pageDataRepository.findAllByConfig(configs);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);

        return pageDTOList;
    }


}
