package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.vo.resource.GetComponentListByPageIdReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.GetComponentPageListByPageIdRespVO;
import com.cmsr.onebase.module.app.build.service.resource.ComponentService;
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
    @Operation(summary = "根据page_id获取表单字段")
    public CommonResult<GetComponentPageListByPageIdRespVO> getFormPageListByAppId(@RequestBody GetComponentListByPageIdReqVO getComponentListByPageIdReqVO) {
        List<ComponentDTO> components = componentService.listComponent(getComponentListByPageIdReqVO.getPageId());

        GetComponentPageListByPageIdRespVO getComponentPageListByPageIdRespVO = new GetComponentPageListByPageIdRespVO();
        getComponentPageListByPageIdRespVO.setList(components);
        return CommonResult.success(getComponentPageListByPageIdRespVO);
    }
}
