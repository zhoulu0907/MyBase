package com.cmsr.onebase.module.build.controller.appresource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.build.controller.appresource.vo.GetFormPageListByAppIdReqVO;
import com.cmsr.onebase.module.build.controller.appresource.vo.GetFormPageListByAppIdRespVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.core.dto.appresource.UpdatePageNameDTO;
import com.cmsr.onebase.module.build.controller.appresource.vo.UpdatePageNameReqVO;
import com.cmsr.onebase.module.build.service.appresource.PageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import java.util.List;

/**
 * @ClassName PageController
 * @Description TODO
 * @Author mickey
 * @Date 2025/8/4 10:17
 */
@Tag(name = "应用资源管理-页面管理")
@RestController
@RequestMapping("/app/resource/page")
@Validated
public class PageController {

    @Resource
    private PageService pageService;

    @PostMapping("/name/update")
    @Operation(summary = "更新页面名称")
    public CommonResult<Boolean> updatePageName(@RequestBody UpdatePageNameReqVO updatePageNameReqVO) {

        UpdatePageNameDTO updatePageDTO = new UpdatePageNameDTO();
        updatePageDTO.setPageId(updatePageNameReqVO.getId());
        updatePageDTO.setPageName(updatePageNameReqVO.getPageName());
        pageService.updatePageName(updatePageDTO);

        return CommonResult.success(true);
    }

    @PostMapping("/form/app_id")
    @Operation(summary = "根据app_id获取表单页面")
    public CommonResult<GetFormPageListByAppIdRespVO> getFormPageListByAppId(@RequestBody GetFormPageListByAppIdReqVO getFormPageListByAppIdReqVO) {
        List<PageDTO> pages = pageService.getFormPageListByAppId(getFormPageListByAppIdReqVO.getAppId());

        GetFormPageListByAppIdRespVO getFormPageListByAppIdRespVO = new GetFormPageListByAppIdRespVO();
        getFormPageListByAppIdRespVO.setPages(pages);
        return CommonResult.success(getFormPageListByAppIdRespVO);
    }
}
