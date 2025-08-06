package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetPageRespDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageSetRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;

import jakarta.annotation.Resource;

@Service
public class PageSetPageServiceImpl implements PageSetPageService {

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Override
    public PageSetPageRespDTO getPageSetPage(Long id) {
        PageSetPageDO pageSetPageDO = pageSetPageDataRepository.findById(id);
        return BeanUtils.toBean(pageSetPageDO, PageSetPageRespDTO.class);
    }

    @Override
    public Long createPageSetPage(CreatePageSetPageDTO createPageSetPageDTO) {
        PageSetPageDO pageSetPageDO = BeanUtils.toBean(createPageSetPageDTO, PageSetPageDO.class);
        pageSetPageDO = pageSetPageDataRepository.insert(pageSetPageDO);
        return pageSetPageDO.getId();
    }

    @Override
    public Boolean deletePageSetPage(Long id) {
        pageSetPageDataRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean updatePageSetPage(PageSetPageRespDTO pageSetPageRespDTO) {
        PageSetPageDO pageSetPageDO = BeanUtils.toBean(pageSetPageRespDTO, PageSetPageDO.class);
        pageSetPageDO = pageSetPageDataRepository.update(pageSetPageDO);
        return true;
    }

    @Override
    public List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId) {
        PageSetDO pageSetDO = pageSetDataRepository.findById(pageSetId);
        List<PageSetPageDO> pageSetPageDOList = pageSetPageDataRepository.findByPageSetCode(pageSetDO.getPageSetCode());
        return BeanUtils.toBean(pageSetPageDOList, PageSetPageRespDTO.class);
    }
}