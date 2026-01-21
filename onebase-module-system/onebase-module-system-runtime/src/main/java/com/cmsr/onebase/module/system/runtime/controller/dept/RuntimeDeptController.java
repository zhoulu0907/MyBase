package com.cmsr.onebase.module.system.runtime.controller.dept;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.vo.dept.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 部门 Controller
 *
 * @author
 * @date 2025-12-05
 */
@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class RuntimeDeptController {

    @Resource
    private DeptService deptService;

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    public CommonResult<List<DeptRespVO>> getDeptList(DeptListReqVO reqVO) {
        List<DeptRespVO> respList = deptService.getDeptListWithUserCount(reqVO);
        return success(respList);
    }

    @GetMapping("/get-depts-by-id")
    @Operation(summary = "根据ID和类型获取其所属部门及其父部门列表")
    @PreAuthorize("@ss.hasPermission('tenant:dept:query')")
    public CommonResult<List<DeptSimpleRespVO>> getParentDeptsListById(@RequestParam("id") Long id,
                                                                       @RequestParam("idType") String idType) {
        List<DeptDO> deptDOList = deptService.getParentDeptsListById(id,idType);
        return success(BeanUtils.toBean(deptDOList, DeptSimpleRespVO.class));
    }

}
