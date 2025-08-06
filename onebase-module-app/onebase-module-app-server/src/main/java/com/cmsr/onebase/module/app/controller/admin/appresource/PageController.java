package com.cmsr.onebase.module.app.controller.admin.appresource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageNameDTO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.UpdatePageNameReqVO;
import com.cmsr.onebase.module.app.service.appresource.PageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;


/**
 * @ClassName PageController
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/4 10:17
 */
@Tag(name = "应用资源管理-页面管理")
@RestController
@RequestMapping("/app_resource/page")
@Validated
public class PageController {

    @Resource
    private PageService pageService;

    @PostMapping("/name/update")
    @Operation(summary = "更新页面名称")
    public CommonResult<Boolean> updatePageName(@RequestBody UpdatePageNameReqVO updatePageNameReqVO) {

        UpdatePageNameDTO updatePageDTO = new UpdatePageNameDTO();
        updatePageDTO.setPageCode(updatePageNameReqVO.getPageCode());
        updatePageDTO.setPageName(updatePageNameReqVO.getPageName());
        pageService.updatePageName(updatePageDTO);

        return CommonResult.success(true);
    }
}
