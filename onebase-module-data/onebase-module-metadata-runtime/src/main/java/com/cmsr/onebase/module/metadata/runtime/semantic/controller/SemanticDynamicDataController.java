package com.cmsr.onebase.module.metadata.runtime.semantic.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.SemanticDynamicDataService;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.apache.commons.lang3.StringUtils;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metadata/{entityCode}")
@Validated
@Tag(name = "动态数据(语义)", description = "基于RecordDTO的动态数据接口")
/**
 * 动态数据(语义)控制器
 *
 * <p>面向语义化请求体，提供基于 RecordDTO 的动态数据 CRUD 与分页接口。
 * 控制器仅负责：
 * - 接收路由参数与请求体
 * - 生成/透传并回写 `X-Trace-Id`
 * - 委派业务到 {@link com.cmsr.onebase.module.metadata.runtime.semantic.service.SemanticDynamicDataService}
 *
 * 遵循薄控制器原则，所有业务装配与校验在服务层完成。</p>
 */
public class SemanticDynamicDataController {

    @Resource
    private SemanticDynamicDataService semanticDynamicDataService;

    @PostMapping("/create")
    @Operation(summary = "创建")
    /**
     * 语义创建数据
     *
     * @param entityCode 实体编码，用于解析实体ID
     * @param menuId 菜单ID（用于权限与上下文）
     * @param body 语义化合并请求体，顶层键为业务字段或连接器
     * @param request HTTP 请求对象，用于读取 `X-Trace-Id`
     * @param response HTTP 响应对象，用于回写 `X-Trace-Id`
     * @return 创建后的动态数据响应
     */
    public CommonResult<DynamicDataRespVO> create(@PathVariable("entityCode") String entityCode,
                                                  @RequestParam("menuId") Long menuId,
                                                  @RequestBody SemanticMergeBodyVO body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        DynamicDataRespVO resp = semanticDynamicDataService.create(entityCode, menuId, body, traceId);
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId);
        }
        return CommonResult.success(resp);
    }

    @PostMapping("/update")
    @Operation(summary = "更新")
    /**
     * 语义更新数据
     *
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 语义化合并请求体，包含主键 `id` 及待更新字段
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 更新后的动态数据响应
     */
    public CommonResult<DynamicDataRespVO> update(@PathVariable("entityCode") String entityCode,
                                                  @RequestParam("menuId") Long menuId,
                                                  @RequestBody SemanticMergeBodyVO body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id"); 
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        DynamicDataRespVO resp = semanticDynamicDataService.update(entityCode, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }

    @PostMapping("/remove")
    @Operation(summary = "删除")
    /**
     * 语义删除数据
     *
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 目标请求体，`data.id` 为删除主键，支持可选方法编码
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 删除成功返回被删除数据的ID；失败返回 null
     */
    public CommonResult<Long> remove(@PathVariable("entityCode") String entityCode,
                                        @RequestParam("menuId") Long menuId,
                                        @RequestBody SemanticTargetBodyVO body,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        Long deletedId = semanticDynamicDataService.remove(entityCode, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(deletedId);
    }

    @PostMapping("/detail")
    @Operation(summary = "详情")
    /**
     * 语义查询详情
     *
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 目标请求体，`data.id` 为查询主键，支持包含子表/关系控制
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 详情动态数据响应
     */
    public CommonResult<DynamicDataRespVO> detail(@PathVariable("entityCode") String entityCode,
                                                  @RequestParam("menuId") Long menuId,
                                                  @RequestBody SemanticTargetBodyVO body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        DynamicDataRespVO resp = semanticDynamicDataService.detail(entityCode, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }

    @PostMapping("/page")
    @Operation(summary = "分页")
    /**
     * 语义分页查询
     *
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 分页请求体，包含分页、排序、过滤等上下文
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 分页结果
     */
    public CommonResult<PageResult<DynamicDataRespVO>> page(@PathVariable("entityCode") String entityCode,
                                                            @RequestParam("menuId") Long menuId,
                                                            @RequestBody SemanticPageBodyVO body,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        PageResult<DynamicDataRespVO> resp = semanticDynamicDataService.page(entityCode, menuId, body, traceId);
        if (traceId != null) response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(resp);
    }

    // 保留控制器轻薄，业务逻辑在 SemanticDynamicDataService 中实现
}
