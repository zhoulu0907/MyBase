package com.cmsr.onebase.module.app.runtime.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.vo.resource.*;
import com.cmsr.onebase.module.app.runtime.service.resource.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/app_id")
    @Operation(summary = "根据app_id获取表单页面")
    public CommonResult<GetPageListByAppIdRespVO> getPageListByAppId(@RequestBody GetPageListByAppIdReqVO getPageListByAppIdReqVO) {
        List<PageDTO> pages = pageService.getPageListByAppId(getPageListByAppIdReqVO.getAppId());

        GetPageListByAppIdRespVO getPageListByAppIdRespVO = new GetPageListByAppIdRespVO();
        getPageListByAppIdRespVO.setPages(pages);
        return CommonResult.success(getPageListByAppIdRespVO);
    }

    @PostMapping("/metadata")
    @Operation(summary = "根据page_id获取页面绑定的元数据id")
    public CommonResult<GetMetadataByPageIdRespVO> getMetadataByPageId(@RequestBody GetMetadataByPageIdReqVO getMetadataByPageIdReqVO) {
        String metadata = pageService.getMetadataByPageId(getMetadataByPageIdReqVO.getPageId());

        GetMetadataByPageIdRespVO getMetadataByPageIdRespVO = new GetMetadataByPageIdRespVO();
        getMetadataByPageIdRespVO.setMetadata(metadata);
        return CommonResult.success(getMetadataByPageIdRespVO);
    }

    @PostMapping("/view/list")
    @Operation(summary = "视图列表")
    public CommonResult<ListPageViewRespVO> listPageView(@RequestBody ListPageViewReqVO listPageViewReqVO) {
        List<PageDTO> pages = pageService.listPageView(listPageViewReqVO.getPageSetId());

        ListPageViewRespVO listPageViewRespVO = new ListPageViewRespVO();
        listPageViewRespVO.setPages(pages);

        return CommonResult.success(listPageViewRespVO);
    }
}
