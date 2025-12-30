package com.cmsr.onebase.module.formula.build.controller.function;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.formula.dal.dataflexdo.FunctionDO;
import com.cmsr.onebase.module.formula.service.function.FormulaFunctionService;
import com.cmsr.onebase.module.formula.vo.function.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 公式引擎 - 函数 Controller
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Tag(name = "公式函数")
@RestController
@RequestMapping("/formula/function")
@Validated
public class FormulaFunctionController {

    @Resource(name = "formulaFunctionService") // 使用指定的bean名称
    private FormulaFunctionService functionService;

    @PostMapping("/create")
    @Operation(summary = "创建函数")
    public CommonResult<Long> createFunction(@Valid @RequestBody FunctionInsertReqVO createReqVO) {
        Long functionId = functionService.createFunction(createReqVO);
        return success(functionId);
    }

    @PostMapping("/update")
    @Operation(summary = "更新函数")
    public CommonResult<Boolean> updateFunction(@Valid @RequestBody FunctionUpdateReqVO updateReqVO) {
        functionService.updateFunction(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除函数")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteFunction(@RequestParam("id") Long id) {
        functionService.deleteFunction(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得函数信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<FunctionRespVO> getFunction(@RequestParam("id") Long id) {
        FunctionDO function = functionService.getFunction(id);
        return success(BeanUtils.toBean(function, FunctionRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取函数列表")
    public CommonResult<List<FunctionRespVO>> getFunctionList(FunctionListReqVO reqVO) {
        List<FunctionDO> list = functionService.getFunctionList(reqVO);
        return success(BeanUtils.toBean(list, FunctionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取函数分页")
    public CommonResult<PageResult<FunctionRespVO>> getFunctionPage(@Valid FunctionPageReqVO reqVO) {
        PageResult<FunctionDO> pageResult = functionService.getFunctionPage(reqVO);
        return success(BeanUtils.toBean(pageResult, FunctionRespVO.class));
    }

    @GetMapping(value = {"/simple-list"})
    @Operation(summary = "获取函数精简信息列表", description = "只包含被开启的函数，主要用于前端的下拉选项")
    public CommonResult<List<FunctionSimpleRespVO>> getSimpleFunctionList() {
        List<FunctionDO> list = functionService.getFunctionList(
                new FunctionListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, FunctionSimpleRespVO.class));
    }

    @GetMapping("/list-group-by-type")
    @Operation(summary = "根据类型获取函数分组列表")
    public CommonResult<List<FunctionGroupRespVo>> getFunctionListGroupByType(@Valid FunctionListReqVO reqVO) {
        List<FunctionGroupRespVo> functionListGroupByType = functionService.getFunctionListGroupByType(
                reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(functionListGroupByType);
    }

}
