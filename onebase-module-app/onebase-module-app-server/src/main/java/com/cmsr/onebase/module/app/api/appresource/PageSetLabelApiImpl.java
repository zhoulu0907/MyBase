package com.cmsr.onebase.module.app.api.appresource;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelUpdateDTO;
import com.cmsr.onebase.module.app.service.appresource.PageSetLabelService;

import jakarta.annotation.Resource;

/**
 * @ClassName PageSetApiImpl
 * @Description 页面集合API实现
 * @Author mickey
 * @Date 2025/7/31 18:03
 */

@RestController
@Validated
public class PageSetLabelApiImpl implements PageSetLabelApi {

    @Resource
    private PageSetLabelService pageSetLabelService;

    @Override
    public CommonResult<List<PageSetLabelRespDTO>> getLabelsByPageSetId(Long id) {
        List<PageSetLabelRespDTO> pageSetLabelRespDTOs = pageSetLabelService.getLabelsByPageSetId(id);
        return CommonResult.success(pageSetLabelRespDTOs);
    }

    @Override
    public CommonResult<Long> createPageSetLabel(PageSetLabelCreateDTO createDTO) {
        Long id = pageSetLabelService.createPageSetLabel(createDTO);
        return CommonResult.success(id);
    }

    @Override
    public CommonResult<Boolean> updatePageSetLabel(PageSetLabelUpdateDTO updateDTO) {
        pageSetLabelService.updatePageSetLabel(updateDTO);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<Boolean> deletePageSetLabel(Long id) {
        pageSetLabelService.deletePageSetLabel(id);
        return CommonResult.success(true);
    }
}
