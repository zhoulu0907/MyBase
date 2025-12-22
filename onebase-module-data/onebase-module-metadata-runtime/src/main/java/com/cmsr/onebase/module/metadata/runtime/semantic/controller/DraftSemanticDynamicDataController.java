package com.cmsr.onebase.module.metadata.runtime.semantic.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.DraftSemanticDynamicDataService;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.SemanticDynamicDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/runtime/metadata/draft/{tableName}")
@Validated
@Tag(name = "草稿数据", description = "草稿数据相关接口")
/**
 * 动态数据(语义)控制器
 *
 * <p>面向语义化请求体，提供基于 RecordDTO 的动态数据 CRUD 与分页接口。
 * 控制器仅负责：
 * - 接收路由参数与请求体
 * - 生成/透传并回写 `X-Trace-Id`
 * - 委派业务到 {@link SemanticDynamicDataService}
 *
 * 遵循薄控制器原则，所有业务装配与校验在服务层完成。</p>
 */
public class DraftSemanticDynamicDataController {

    @Resource
    private DraftSemanticDynamicDataService draftSemanticDynamicDataService;

    @PostMapping("/create")
    @Operation(summary = "创建")
    /**
     * 新增草稿
     *
     * @param tableName 表名，用于解析实体ID
     * @param menuId 菜单ID（用于权限与上下文）
     * @param body 语义化合并请求体，顶层键为业务字段或连接器
     * @param request HTTP 请求对象，用于读取 `X-Trace-Id`
     * @param response HTTP 响应对象，用于回写 `X-Trace-Id`
     * @return 创建后的动态数据响应
     */
    public CommonResult<Map<String, Object>> create(@PathVariable("tableName") String tableName,
                                                  @RequestParam("menuId") Long menuId,
                                                  @RequestBody SemanticMergeBodyVO body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        Map<String, Object> resp = draftSemanticDynamicDataService.create(tableName, menuId, body, traceId);
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId);
        }
        return CommonResult.success(resp);
    }




    @PostMapping("/detail")
    @Operation(summary = "详情")
    /**
     * 草稿详情
     *
     * @param tableName 表名
     * @param menuId 菜单ID
     * @param body 目标请求体，`data.id` 为查询主键，支持包含子表/关系控制
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 详情动态数据响应
     */
    public CommonResult<Map<String, Object>> detail(@PathVariable("tableName") String tableName,
                                                  @RequestParam("menuId") Long menuId,
                                                  @RequestBody SemanticTargetBodyVO body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        Map<String, Object> resp = draftSemanticDynamicDataService.detail(tableName, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }

    @PostMapping("/page")
    @Operation(summary = "分页")
    /**
     * 草稿分页
     *
     * @param tableName 表名
     * @param menuId 菜单ID
     * @param body 分页请求体，包含分页、排序、过滤等上下文
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 分页结果
     */
    public CommonResult<PageResult<Map<String, Object>>> page(@PathVariable("tableName") String tableName,
                                                            @RequestParam("menuId") Long menuId,
                                                            @RequestBody SemanticPageBodyVO body,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        PageResult<Map<String, Object>> resp = draftSemanticDynamicDataService.page(tableName, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }

}
