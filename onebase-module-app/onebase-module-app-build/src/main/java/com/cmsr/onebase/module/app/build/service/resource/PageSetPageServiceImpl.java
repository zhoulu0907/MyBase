package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetPageRespDTO;
import com.cmsr.onebase.module.app.core.provider.resource.PageSetPageServiceProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageSetPageServiceImpl implements PageSetPageService {

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private PageSetPageServiceProvider pageSetPageServiceProvider;


    @Override
    public PageSetPageRespDTO getPageSetPage(Long id) {
        return pageSetPageServiceProvider.getPageSetPage(id);
    }

    @Override
    public Long createPageSetPage(CreatePageSetPageDTO createPageSetPageDTO) {
        AppResourcePagesetPageDO pageSetPageDO = BeanUtils.toBean(createPageSetPageDTO, AppResourcePagesetPageDO.class);
        pageSetPageDataRepository.save(pageSetPageDO);
        return pageSetPageDO.getId();
    }

    @Override
    public Boolean deletePageSetPage(Long id) {
        pageSetPageDataRepository.removeById(id);
        return true;
    }

    @Override
    public Boolean updatePageSetPage(PageSetPageRespDTO pageSetPageRespDTO) {
        AppResourcePagesetPageDO pageSetPageDO = BeanUtils.toBean(pageSetPageRespDTO, AppResourcePagesetPageDO.class);
        pageSetPageDataRepository.updateById(pageSetPageDO);
        return true;
    }

    @Override
    public List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId) {
        return pageSetPageServiceProvider.getPageSetPageList(pageSetId);
    }
}
