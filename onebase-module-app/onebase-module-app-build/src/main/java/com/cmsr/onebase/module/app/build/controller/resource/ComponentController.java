package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.resource.ComponentService;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ComponentService componentService;

    @PostMapping("/list")
    @Operation(summary = "根据page_uuid获取表单字段")
    public CommonResult<QueryComponentListRespVO> getComponentListByPageUuid(@RequestBody QueryComponentListReqVO queryComponentListReqVO) {
        List<ComponentDTO> components = componentService.listComponent(queryComponentListReqVO.getPageUuid());

        QueryComponentListRespVO queryComponentListRespVO = new QueryComponentListRespVO();
        queryComponentListRespVO.setList(components);
        return CommonResult.success(queryComponentListRespVO);
    }
}
