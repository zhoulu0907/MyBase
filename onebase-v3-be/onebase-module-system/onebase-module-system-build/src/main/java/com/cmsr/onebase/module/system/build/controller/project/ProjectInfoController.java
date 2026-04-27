package com.cmsr.onebase.module.system.build.controller.project;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectInfoDO;
import com.cmsr.onebase.module.system.service.project.ProjectInfoService;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoCreateReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoPageReqVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoRespVO;
import com.cmsr.onebase.module.system.vo.project.ProjectInfoUpdateReqVO;
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
 * 项目信息 Controller
 *
 * @author claude
 * @date 2026-03-23
 */
@Tag(name = "管理后台 - 项目信息")
@RestController
@RequestMapping("/system/project")
@Validated
public class ProjectInfoController {

    @Resource
    private ProjectInfoService projectInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    @PreAuthorize("@ss.hasPermission('system:project:create')")
    public CommonResult<Long> createProject(@Valid @RequestBody ProjectInfoCreateReqVO createReqVO) {
        return success(projectInfoService.createProject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目")
    @PreAuthorize("@ss.hasPermission('system:project:update')")
    public CommonResult<Boolean> updateProject(@Valid @RequestBody ProjectInfoUpdateReqVO updateReqVO) {
        projectInfoService.updateProject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:project:delete')")
    public CommonResult<Boolean> deleteProject(@RequestParam("id") Long id) {
        projectInfoService.deleteProject(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:project:query')")
    public CommonResult<ProjectInfoRespVO> getProject(@RequestParam("id") Long id) {
        ProjectInfoDO project = projectInfoService.getProject(id);
        return success(BeanUtils.toBean(project, ProjectInfoRespVO.class));
    }

    @GetMapping("/get-by-external-id")
    @Operation(summary = "通过外部系统ID获得项目详情")
    @Parameter(name = "externalId", description = "外部系统项目编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:project:query')")
    public CommonResult<ProjectInfoRespVO> getProjectByExternalId(@RequestParam("externalId") String externalId) {
        ProjectInfoDO project = projectInfoService.getProjectByExternalId(externalId);
        return success(BeanUtils.toBean(project, ProjectInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目分页")
    @PreAuthorize("@ss.hasPermission('system:project:query')")
    public CommonResult<PageResult<ProjectInfoRespVO>> getProjectPage(@Valid ProjectInfoPageReqVO pageVO) {
        return success(projectInfoService.getProjectPage(pageVO));
    }

    @GetMapping("/list-enabled")
    @Operation(summary = "获取启用的项目列表", description = "只包含被开启的项目，主要用于前端的下拉选项")
    public CommonResult<List<ProjectInfoRespVO>> getEnabledProjectList() {
        return success(projectInfoService.getEnabledProjectList());
    }

}