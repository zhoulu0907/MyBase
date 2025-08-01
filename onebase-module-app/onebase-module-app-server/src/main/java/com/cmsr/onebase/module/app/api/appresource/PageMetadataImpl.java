package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageMetadataDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageMetadataDTO;
import com.cmsr.onebase.module.app.service.appresource.PageMetadataService;

import jakarta.annotation.Resource;

@RestController
public class PageMetadataImpl implements PageMetadataApi {

    @Resource
    private PageMetadataService pageMetadataService;


    @Override
    public CommonResult<PageMetadataRespDTO> getPageMetadata(Long id) {
        PageMetadataRespDTO pageMetadataRespDTO = pageMetadataService.getPageMetadata(id);
        return CommonResult.success(pageMetadataRespDTO);
    }

    @Override
    public CommonResult<Long> createPageMetadata(CreatePageMetadataDTO createPageMetadataDTO) {
        Long id = pageMetadataService.createPageMetadata(createPageMetadataDTO);
        return CommonResult.success(id);
    }

    @Override
    public CommonResult<Boolean> deletePageMetadata(Long id) {
        Boolean result = pageMetadataService.deletePageMetadata(id);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<Boolean> updatePageMetadata(UpdatePageMetadataDTO updatePageMetadataDTO) {
        Boolean result = pageMetadataService.updatePageMetadata(updatePageMetadataDTO);
        return CommonResult.success(result);
    }
}
