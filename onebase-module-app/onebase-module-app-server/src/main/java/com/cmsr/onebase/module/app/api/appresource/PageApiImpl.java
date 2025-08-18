package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.service.appresource.PageService;

import jakarta.annotation.Resource;

@RestController
public class PageApiImpl implements PageApi {

    @Resource
    private PageService pageService;

    @Override
    public CommonResult<PageRespDTO> getPage(Long id) {
        PageRespDTO pageRespDTO = pageService.getPage(id);
        return CommonResult.success(pageRespDTO);
    }

    @Override
    public CommonResult<Long> createPage(CreatePageDTO createPageDTO) {
        Long id = pageService.createPage(createPageDTO);
        return CommonResult.success(id);
    }

    @Override
    public CommonResult<Boolean> deletePage(Long id) {
        Boolean result = pageService.deletePage(id);
        return CommonResult.success(result);
    }
}
