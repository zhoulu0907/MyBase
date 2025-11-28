package com.cmsr.onebase.module.system.build.controller.corp.dept;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.vo.dept.*;
import com.cmsr.onebase.module.system.vo.user.UserAdminOrDirectorUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 部门 Controller
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Tag(name = "企业服务 - 部门管理")
@RestController
@RequestMapping("/corp/dept")
@Validated
public class CorpDeptController {

    @Resource
    private DeptService corpDeptService;

    @PostMapping("/create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('corp:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DeptSaveReqVO createReqVO) {
        Long deptId = corpDeptService.createDept(createReqVO);
        return success(deptId);
    }

    @PostMapping("/update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('corp:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DeptSaveReqVO updateReqVO) {
        corpDeptService.updateDept(updateReqVO);
        return success(true);
    }

    @PostMapping("/update-dept-admin-or-director")
    @Operation(summary = "修改部门用户管理员/主管")
    @PreAuthorize("@ss.hasPermission('corp:user:update')")
    public CommonResult<Boolean> updateAdminOrDirector(@Valid @RequestBody UserAdminOrDirectorUpdateReqVO reqVO) {
        corpDeptService.updateAdminOrDirector(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('corp:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        corpDeptService.deleteDept(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('corp:dept:query')")
    public CommonResult<List<DeptRespVO>> getDeptList(DeptListReqVO reqVO) {
        List<DeptRespVO> respList = corpDeptService.getDeptListWithUserCount(reqVO);
        return success(respList);
    }

    @GetMapping(value = {"/simple-list"})
    @PreAuthorize("@ss.hasPermission('corp:dept:query')")
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList() {
        List<DeptDO> list = corpDeptService.getDeptList(
                new DeptListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, DeptSimpleRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('corp:dept:query')")
    public CommonResult<DeptRespVO> getDept(@RequestParam("id") Long id) {
        DeptRespVO dept = corpDeptService.getDeptWithUserCountAndLeader(id);
        return success(dept);
    }

    @GetMapping("/get-dept-users")
    @Operation(summary = "指定/搜索获取部门和用户信息")
    @PreAuthorize("@ss.hasPermission('corp:dept:query')")
    public CommonResult<DeptAndUsersRespVO> getDeptAndUsers(@Valid DeptAndUsersReqVO reqVO) {
        DeptAndUsersRespVO result = corpDeptService.getDeptAndUsers(reqVO);
        return success(result);
    }

    @GetMapping("/get-depts-by-id")
    @Operation(summary = "根据ID和类型获取其所属部门及其父部门列表")
    @PreAuthorize("@ss.hasPermission('corp:dept:query')")
    public CommonResult<List<DeptSimpleRespVO>> getParentDeptsListById(@RequestParam("id") Long id,
                                                                       @RequestParam("idType") String idType) {
        List<DeptDO> deptDOList = corpDeptService.getParentDeptsListById(id,idType);
        return success(BeanUtils.toBean(deptDOList, DeptSimpleRespVO.class));
    }

}
