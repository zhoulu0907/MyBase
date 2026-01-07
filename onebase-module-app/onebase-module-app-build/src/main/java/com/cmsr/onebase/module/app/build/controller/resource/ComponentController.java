package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.resource.ComponentService;
import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.QueryComponentListRespVO;
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
}
