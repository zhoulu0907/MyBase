package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetPageRespDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;

import jakarta.annotation.Resource;

@Service
public class PageSetPageServiceImpl implements PageSetPageService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageSetPageRespDTO getPageSetPage(Long id) {
        PageSetPageDO pageSetPageDO = dataRepository.findById(PageSetPageDO.class, id);
        return BeanUtils.toBean(pageSetPageDO, PageSetPageRespDTO.class);
    }

    @Override
    public Long createPageSetPage(CreatePageSetPageDTO createPageSetPageDTO) {
        PageSetPageDO pageSetPageDO = BeanUtils.toBean(createPageSetPageDTO, PageSetPageDO.class);
        pageSetPageDO = dataRepository.insert(pageSetPageDO);
        return pageSetPageDO.getId();
    }

    @Override
    public Boolean deletePageSetPage(Long id) {
        dataRepository.deleteById(PageSetPageDO.class, id);
        return true;
    }

    @Override
    public Boolean updatePageSetPage(PageSetPageRespDTO pageSetPageRespDTO) {
        PageSetPageDO pageSetPageDO = BeanUtils.toBean(pageSetPageRespDTO, PageSetPageDO.class);
        pageSetPageDO = dataRepository.update(pageSetPageDO);
        return true;
    }

    @Override
    public List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_set_id", pageSetId);
        List<PageSetPageDO> pageSetPageDOList = dataRepository.findAll(PageSetPageDO.class, configs);
        return BeanUtils.toBean(pageSetPageDOList, PageSetPageRespDTO.class);
    }
}