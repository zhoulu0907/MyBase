package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.service.appresource.PageSetService;

import jakarta.annotation.Resource;

/**
 * @ClassName PageSetApiImpl
 * @Description 页面集合API实现
 * @Author mickey
 * @Date 2025/7/31 18:03
 */

@RestController
@Validated
public class PageSetApiImpl implements PageSetApi {

    @Resource
    private PageSetService pageSetService;

    @Override
    public CommonResult<PageSetRespDTO> getPageSet(String code) {
        PageSetRespDTO pageSetRespDTO = pageSetService.getPageSet(code);
        return CommonResult.success(pageSetRespDTO);
    }

    @Override
    public CommonResult<Long> getAppId(String code) {
        Long appId = pageSetService.getAppId(code);
        return CommonResult.success(appId);
    }

    @Override
    public CommonResult<String> createPageSet(CreatePageSetDTO createPageSetDTO) {
        String pageSetCode = pageSetService.createPageSet(createPageSetDTO);
        return CommonResult.success(pageSetCode);
    }

    @Override
    public CommonResult<Boolean> deletePageSet(String menuCode) {
        pageSetService.deletePageSet(menuCode);
        return CommonResult.success(true);
    }
}
