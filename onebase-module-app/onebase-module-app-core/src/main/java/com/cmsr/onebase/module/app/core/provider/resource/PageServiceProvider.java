package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRespDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Service
public class PageServiceProvider {

    @Autowired
    private AppPageRepository pageRepository;

    @Autowired
    private AppPageSetPageRepository pageSetPageRepository;

    @Autowired
    private AppPageSetRepository pageSetRepository;

    @Autowired
    private AppMenuRepository appMenuRepository;


    public PageRespDTO getPage(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    public List<PageDTO> getFormPageListByAppId(Long appId) {
        List<AppMenuDO> menuDOList = appMenuRepository.findByApplicationId(appId);
        List<Long> menuIdList = menuDOList.stream().map(AppMenuDO::getId).toList();
        if (CollectionUtils.isEmpty(menuIdList)) {
            return Collections.emptyList();
        }
        List<AppResourcePagesetDO> pageSetDoList = pageSetRepository.findByMenuIds(menuIdList);
        if (CollectionUtils.isEmpty(pageSetDoList)) {
            return Collections.emptyList();
        }
        List<Long> pageSetIdList = pageSetDoList.stream()
                .map(AppResourcePagesetDO::getId)
                .collect(Collectors.toList());
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetIds(pageSetIdList);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;

    }

    public String getMetadataByPageId(Long pageId) {
        AppResourcePagesetPageDO pageSetPageDO = pageSetPageRepository.findByPageId(pageId);
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetPageDO.getPageSetId());

        return pageSetDO.getMainMetadata();
    }


    public List<PageDTO> listPageView(Long pageSetId) {
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetId(pageSetId);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;
    }


}
