package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetPageRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageSetPageServiceProvider {

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageSetRepository pageSetDataRepository;


    public PageSetPageRespDTO getPageSetPage(Long id) {
        AppResourcePagesetPageDO pageSetPageDO = pageSetPageDataRepository.getById(id);
        return BeanUtils.toBean(pageSetPageDO, PageSetPageRespDTO.class);
    }

    public List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(pageSetId);
        List<AppResourcePagesetPageDO> pageSetPageDOList = pageSetPageDataRepository.findByPageSetId(pageSetDO.getId());
        return BeanUtils.toBean(pageSetPageDOList, PageSetPageRespDTO.class);
    }
}
