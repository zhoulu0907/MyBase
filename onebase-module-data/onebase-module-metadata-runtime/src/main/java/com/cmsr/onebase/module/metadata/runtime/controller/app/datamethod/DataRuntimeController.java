package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.executor.DashMetaDataPageExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.SemanticDynamicDataService;
import com.cmsr.onebase.module.metadata.runtime.service.datamethod.RuntimeDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


/**
 * 运行时 - 系统级动态数据操作接口
 * 仅包含 /insert, /update, /delete, /data, /data/page 五个接口
 */
@Tag(name = "运行时 - 动态数据接口")
@RestController
@RequestMapping("/metadata/data-method/{tableName}")
@Validated
public class DataRuntimeController {


    @Resource
    private RuntimeDataService runtimeDataService;

    @Resource
    private SemanticDynamicDataService semanticDynamicDataService;

    @Resource
    DashMetaDataPageExecutor dashMetaDataPageExecutor;
    /**
     * 语义分页查询
     * 为大屏提供模型分页数据，目前暂不涉及权限控制，且不需要验签
     *
     * @param tableName 表名
     * @param menuId 菜单ID
     * @param body 分页请求体，包含分页、排序、过滤等上下文
     * @param isDev 是否开发模式（可选，默认false），为true时查询编辑态元数据
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页")
    @ApiSignIgnore
    @PermitAll
    public CommonResult<PageResult<Map<String, Object>>> page(@PathVariable("tableName") String tableName,
                                                              @RequestParam(value = "isDev", required = false, defaultValue = "false") Boolean isDev,
                                                              @RequestBody SemanticPageBodyVO body,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        PageResult<Map<String, Object>> resp = dashMetaDataPageExecutor.execute(tableName, null, traceId, body);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }
/*
    @PostMapping("/insert")
    @Operation(summary = "新增单条数据")
    @PermitAll
    public CommonResult<DynamicDataRespVO> createData(@Valid @RequestBody DynamicDataCreateReqVO reqVO) {
        return success(runtimeDataService.createData(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新单条数据")
    @PermitAll
    public CommonResult<DynamicDataRespVO> updateData(@Valid @RequestBody DynamicDataUpdateReqVO reqVO) {
        return success(runtimeDataService.updateData(reqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除单条数据")
    @PermitAll
    public CommonResult<Boolean> deleteData(@Valid @RequestBody DynamicDataDeleteReqVO reqVO) {
        return success(runtimeDataService.deleteData(reqVO));
    }

    @PostMapping("/data")
    @Operation(summary = "根据ID查询数据详情")
    @PermitAll
    public CommonResult<DynamicDataRespVO> getData(@Valid @RequestBody DynamicDataGetReqVO reqVO) {
        return success(runtimeDataService.getData(reqVO));
    }

    @PostMapping("/data/page")
    @Operation(summary = "分页查询数据列表")
    @PermitAll
    public CommonResult<PageResult<DynamicDataRespVO>> getDataPage(@Valid @RequestBody DynamicDataPageReqVO reqVO) {
        return success(runtimeDataService.getDataPage(reqVO));
    }*/

}
