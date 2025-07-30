package com.cmsr.onebase.module.metadata.controller.admin.datamethod;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 数据方法管理
 *
 * @author bty418
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据方法管理")
@RestController
@RequestMapping("/metadata/data-method")
@Validated
public class DataMethodController {

    @Resource
    private MetadataDataMethodService dataMethodService;

    @GetMapping("/list")
    @Operation(summary = "查询业务实体的数据方法列表")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "2001")
    @Parameter(name = "methodType", description = "方法类型", required = false, example = "CREATE")
    @Parameter(name = "keyword", description = "搜索关键词", required = false, example = "新增")
    public CommonResult<List<DataMethodRespVO>> getDataMethodList(
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "methodType", required = false) String methodType,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<DataMethodRespVO> methods = dataMethodService.getDataMethodList(entityId, methodType, keyword);
        return success(methods);
    }

    @GetMapping("/{entityId}/{methodCode}")
    @Operation(summary = "获取指定数据方法的详细信息")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "2001")
    @Parameter(name = "methodCode", description = "方法编码", required = true, example = "create_single")
    public CommonResult<DataMethodDetailRespVO> getDataMethodDetail(
            @PathVariable("entityId") Long entityId,
            @PathVariable("methodCode") String methodCode) {
        DataMethodDetailRespVO detail = dataMethodService.getDataMethodDetail(entityId, methodCode);
        return success(detail);
    }

} 