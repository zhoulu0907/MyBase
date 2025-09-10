package com.cmsr.onebase.module.build.service.appresource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRespDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.UpdatePageNameDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        configs.in("menu_id", menuIdList);
        List<PageSetDO> pagesetDoList = pageSetDataRepository.findAllByConfig(configs);

        List<Long> pagesetIdList = pagesetDoList.stream()
                .map(PageSetDO::getId)
                .collect(Collectors.toList());

        configs = new DefaultConfigStore();
        configs.in("pageset_id", pagesetIdList);
        configs.eq("page_type", "form");
        List<PageDO> pageDOList = pageDataRepository.findAllByConfig(configs);

        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);

        return pageDTOList;
    }
}
