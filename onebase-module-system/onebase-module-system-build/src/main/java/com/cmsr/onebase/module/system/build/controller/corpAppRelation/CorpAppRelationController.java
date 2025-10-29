package com.cmsr.onebase.module.system.build.controller.corpAppRelation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.service.corpAppRelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationUpdateReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;



import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "企业应用关联表")
@RestController
@Validated
@RequestMapping("/system/corp-app-relation")
public class CorpAppRelationController   {

    @Resource
    private CorpAppRelationService corpAppRelationService;

    @PostMapping("/create")
   // @PermitAll
    @PreAuthorize("@ss.hasPermission('system:corp-app-relation:create')")
    @Operation(summary = "创建企业应用关联")
    public CommonResult<Boolean> createCorpAppRelation(@Valid @RequestBody CorpAppRelationInertReqVO createReqVO) {
        corpAppRelationService.createCorpAppRelation(createReqVO);
        return success(true);
    }

    @PostMapping("/update")
    @PreAuthorize("@ss.hasPermission('system:corp-app-relation:update')")
    @Operation(summary = "更新企业应用关联")
    public CommonResult<Boolean> updateCorpApplication(@Valid @RequestBody CorpAppRelationUpdateReqVO updateReqVO) {
        corpAppRelationService.updateCorpAppRelation(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission('system:corp-app-relation:delete')")
    @Operation(summary = "删除应用授权企业")
    public CommonResult<Boolean> deleteCorpApplication(@RequestParam("id") Long id) {
        corpAppRelationService.deleteCorpAppRelation(id);
        return success(true);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:corp-app-relation:query')")
    @Operation(summary = "获得企业关联应用")
    public CommonResult<CorpAppRelationVO> getCorpApplication(@RequestParam("id") Long id) {
        return success(corpAppRelationService.getCorpAppRelation(id));
    }

    @GetMapping("/page")
    @PermitAll
    //@PreAuthorize("@ss.hasPermission('system:corp-app-relation:query')")
    @Operation(summary = "获得企业关联应用分页")
    public CommonResult<PageResult<CorpAppRelationVO>> getCorpApplication(@Valid CorpAppRelationPageReqVO pageReqVO) {
        PageResult<CorpAppRelationVO> pageResult = corpAppRelationService.getCorpAppRelationPage(pageReqVO);
        return success(pageResult);
    }
}