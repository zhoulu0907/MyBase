package com.cmsr.onebase.module.app.service.appresource;

import java.util.UUID;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.enums.appresource.AppResourceErrorCodeConstants;

import jakarta.annotation.Resource;

@Service
@Validated
public class PageSetServiceImpl implements PageSetService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createPageSet(CreatePageSetDTO createPageSetDTO) {
        PageSetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, PageSetDO.class);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO = dataRepository.insert(pageSetDO);
        return pageSetDO.getId();
    }

    @Override
    public void deletePageSet(String code) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", code);
        dataRepository.deleteByConfig(PageSetDO.class, configs);

        return;
    }

    @Override
    public PageSetRespDTO getPageSet(String code) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", code);
        PageSetDO pageSetDO = dataRepository.findOne(PageSetDO.class, configs);
        if (pageSetDO == null) {
            throw new ServiceException(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        PageSetRespDTO pageSetRespDTO = BeanUtils.toBean(pageSetDO, PageSetRespDTO.class);

        return pageSetRespDTO;
    }
}
