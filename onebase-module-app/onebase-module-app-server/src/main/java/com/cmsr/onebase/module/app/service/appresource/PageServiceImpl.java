package com.cmsr.onebase.module.app.service.appresource;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageNameDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageRepository;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.enums.appresource.AppResourceErrorCodeConstants;

import jakarta.annotation.Resource;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private AppPageRepository pageDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Override
    public PageRespDTO getPage(String pageCode) {
        PageDO pageDO = pageDataRepository.selectPageByCode(pageCode);
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
        pageDataRepository.updatePageName(updatePageNameDTO.getPageCode(), updatePageNameDTO.getPageName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePage(String code) {
        // 删除页面集关联的页面
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_ref", code);
        pageDataRepository.deleteByConfig(configs);

        // 删除页面
        configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_code", code);
        pageDataRepository.deleteByConfig(configs);

        pageDataRepository.deletePageByCode(code);
        pageSetPageDataRepository.deleteByPageCode(code);
        return true;
    }
}
