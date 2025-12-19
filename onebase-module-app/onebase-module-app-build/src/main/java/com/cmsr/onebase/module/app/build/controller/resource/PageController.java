package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.event.AppEntityChangePublisher;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.resource.PageService;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageViewDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.UpdatePageNameDTO;
import com.cmsr.onebase.module.app.core.vo.resource.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Setter;
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
@Setter
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
        Boolean updated = pageService.updatePageName(updatePageDTO);
        AppEntityChangePublisher.publishEvent();
        return CommonResult.success(updated);
    }

    @PostMapping("/app_id")
    @Operation(summary = "根据app_id获取表单页面")
    public CommonResult<GetPageListByAppIdRespVO> getPageListByAppId(@RequestBody GetPageListByAppIdReqVO getPageListByAppIdReqVO) {
        List<PageDTO> pageDtoList = pageService.getPageListByAppId(getPageListByAppIdReqVO.getAppId());

        GetPageListByAppIdRespVO getPageListByAppIdRespVO = new GetPageListByAppIdRespVO();
        getPageListByAppIdRespVO.setPages(pageDtoList);
        return CommonResult.success(getPageListByAppIdRespVO);
    }

    @PostMapping("/metadata")
    @Operation(summary = "根据page_id获取页面绑定的元数据id")
    public CommonResult<GetMetadataByPageIdRespVO> getMetadataByPageId(@RequestBody GetMetadataByPageIdReqVO getMetadataByPageIdReqVO) {
        String metadata;
        if (getMetadataByPageIdReqVO.getPageId() != null) {
            metadata = pageService.getMetadataByPageId(getMetadataByPageIdReqVO.getPageId());
        } else if (getMetadataByPageIdReqVO.getPageUuid() != null) {
            metadata = pageService.getMetadataByPageUuid(getMetadataByPageIdReqVO.getPageUuid());
        } else {
            throw ServiceExceptionUtil.invalidParamException("page_id或page_uuid不能同时为空");
        }

        GetMetadataByPageIdRespVO getMetadataByPageIdRespVO = new GetMetadataByPageIdRespVO();
        getMetadataByPageIdRespVO.setMetadata(metadata);
        return CommonResult.success(getMetadataByPageIdRespVO);
    }

    @PostMapping("/view/create")
    @Operation(summary = "创建视图")
    public CommonResult<Boolean> createPageView(@RequestBody CreatePageViewReqVO createPageViewReqVO) {
        CreatePageViewDTO createPageViewDTO = BeanUtils.toBean(createPageViewReqVO, CreatePageViewDTO.class);
        pageService.createPageView(createPageViewDTO);
        AppEntityChangePublisher.publishEvent();
        return CommonResult.success(true);
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
