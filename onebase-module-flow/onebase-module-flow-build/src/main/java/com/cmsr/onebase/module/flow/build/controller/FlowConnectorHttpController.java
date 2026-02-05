package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorHttpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * HTTP连接器动作管理Controller
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Tag(name = "管理后台 - HTTP连接器动作")
@RestController
@RequestMapping("/flow/connector/http/action")
@Validated
public class FlowConnectorHttpController {

    @Setter
    @Resource
    private FlowConnectorHttpService connectorHttpService;

    @PostMapping("/create")
    @Operation(summary = "创建HTTP动作")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:create')")
    public CommonResult<Long> createHttpAction(@Valid @RequestBody CreateHttpActionReqVO createReqVO) {
        return success(connectorHttpService.createHttpAction(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新HTTP动作")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:update')")
    public CommonResult<Boolean> updateHttpAction(@Valid @RequestBody UpdateHttpActionReqVO updateReqVO) {
        connectorHttpService.updateHttpAction(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除HTTP动作")
    @Parameter(name = "id", description = "HTTP动作ID", required = true)
    @PreAuthorize("@ss.hasPermission('flow:connector:http:delete')")
    public CommonResult<Boolean> deleteHttpAction(@RequestParam("id") Long id) {
        connectorHttpService.deleteHttpAction(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取HTTP动作详情")
    @Parameter(name = "id", description = "HTTP动作ID", required = true)
    @PreAuthorize("@ss.hasPermission('flow:connector:http:query')")
    public CommonResult<HttpActionVO> getHttpAction(@RequestParam("id") Long id) {
        return success(connectorHttpService.getHttpAction(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询HTTP动作列表")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:query')")
    public CommonResult<PageResult<HttpActionVO>> getHttpActionPage(@Valid PageConnectorHttpReqVO pageReqVO) {
        return success(connectorHttpService.getHttpActionPage(pageReqVO));
    }
}
