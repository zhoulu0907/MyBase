package com.cmsr.onebase.module.system.api.corpapprelation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



@Tag(name = "企业应用关联表 API")
@RestController
@RequestMapping("/system/corp-and-app-relation")
public interface CorpAndAppRelationApi {

    @PostMapping("/create")
    @Operation(summary = "创建企业应用关联表")
    CommonResult<Long> createCorpAppRelation(@Valid @RequestBody CorpAppRelationInertReqVO createReqVO);

    @PutMapping("/update")
    @Operation(summary = "更新企业应用关联表")
    CommonResult<Boolean> updateCorpApplication(@Valid @RequestBody CorpAppRelationInertReqVO updateReqVO);

    @DeleteMapping("/delete")
    @Operation(summary = "删除企业应用关联表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<Boolean> deleteCorpApplication(@RequestParam("id") Long id);

    @GetMapping("/get")
    @Operation(summary = "获得企业应用关联表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<CorpAppRelationVO> getCorpApplication(@RequestParam("id") Long id);

    @GetMapping("/page")
    @Operation(summary = "获得企业应用关联表分页")
    CommonResult<PageResult<CorpAppRelationVO>> getCorpApplicationPage(@Valid CorpAppRelationPageReqVO pageReqVO);
}