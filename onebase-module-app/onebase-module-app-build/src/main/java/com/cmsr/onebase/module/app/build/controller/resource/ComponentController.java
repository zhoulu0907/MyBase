package com.cmsr.onebase.module.app.build.controller.resource;

import java.util.List;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.resource.ComponentService;
import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListRespVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;

/**
 * @ClassName ComponentController
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/2 10:07
 */
@Tag(name = "应用资源管理-页面管理")
@RestController
@RequestMapping("/app/resource/component")
@Validated
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @PostMapping("/list")
    @Operation(summary = "根据page_uuid获取表单字段")
    public CommonResult<QueryComponentListRespVO> getComponentListByPageUuid(
            @RequestBody QueryComponentListReqVO queryComponentListReqVO) {
        List<ComponentDTO> components;
        if (queryComponentListReqVO.getPageId() != null) {
            components = componentService.listComponentByPageId(queryComponentListReqVO.getPageId());
        } else if (queryComponentListReqVO.getPageUuid() != null) {
            components = componentService.listComponentByPageUuid(queryComponentListReqVO.getPageUuid());
        } else {
            throw ServiceExceptionUtil.invalidParamException("page_id或page_uuid不能同时为空");
        }
        QueryComponentListRespVO queryComponentListRespVO = new QueryComponentListRespVO();
        queryComponentListRespVO.setList(components);
        return CommonResult.success(queryComponentListRespVO);
    }

    @GetMapping("/list/list_page_components")
    @Operation(summary = "根据应用ID查询 page_type 为 list 的所有页面对应的组件")
    public CommonResult<QueryComponentListRespVO> listComponentForListPages(
            @RequestParam("applicationId") @NotNull(message = "应用ID不能为空") Long applicationId) {
        List<ComponentDTO> components = componentService.listComponentForListPages(applicationId);
        QueryComponentListRespVO respVO = new QueryComponentListRespVO();
        respVO.setList(components);
        return CommonResult.success(respVO);
    }

}
