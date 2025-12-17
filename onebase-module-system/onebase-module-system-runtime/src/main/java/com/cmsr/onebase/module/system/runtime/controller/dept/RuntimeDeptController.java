package com.cmsr.onebase.module.system.runtime.controller.dept;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
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
}
