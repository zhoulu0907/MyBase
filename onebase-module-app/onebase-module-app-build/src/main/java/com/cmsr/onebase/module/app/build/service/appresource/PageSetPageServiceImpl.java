package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetPageRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageSetPageServiceImpl implements PageSetPageService {

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Override
    public PageSetPageRespDTO getPageSetPage(Long id) {
        AppResourcePagesetPageDO pageSetPageDO = pageSetPageDataRepository.getById(id);
        return BeanUtils.toBean(pageSetPageDO, PageSetPageRespDTO.class);
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
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(pageSetId);
        List<AppResourcePagesetPageDO> pageSetPageDOList = pageSetPageDataRepository.findByPageSetId(pageSetDO.getId());
        return BeanUtils.toBean(pageSetPageDOList, PageSetPageRespDTO.class);
    }
}
