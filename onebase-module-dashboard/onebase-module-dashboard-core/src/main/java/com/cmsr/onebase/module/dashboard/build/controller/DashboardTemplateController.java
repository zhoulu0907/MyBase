package com.cmsr.onebase.module.dashboard.build.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProject;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProjectData;
import com.cmsr.onebase.module.dashboard.build.service.IDashboardTemplateService;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectService;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplatePageReqVO;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplateRespVO;
import com.cmsr.onebase.module.dashboard.build.vo.template.DashboardTemplateSaveReqVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.module.dashboard.build.enums.TemplateTypeEnum.APP_TYPE;
import static com.cmsr.onebase.module.dashboard.build.enums.TemplateTypeEnum.SYSTEM_TYPE;

/**
 * 仪表盘模板 Controller
 *
 * @author lingma
 */
@Api(tags = "仪表盘模板")
@RestController
@RequestMapping("/dashboard/template")
public class DashboardTemplateController {

    @Resource
    private IDashboardTemplateService dashboardTemplateService;

    @Resource
    private IGoviewProjectDataService iGoviewProjectDataService;

    @Resource
    private IGoviewProjectService iGoviewProjectService;
    /**
     * 创建仪表盘模板
     *
     * @param saveReqVO 创建信息
     * @return 仪表盘模板ID
     */
    @PostMapping("/create")
    @ApiOperation("创建模板")
    @ApiSignIgnore
    public CommonResult<Long> createDashboardTemplate(@RequestBody @Validated DashboardTemplateSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getTemplateType())) {
            saveReqVO.setTemplateType(SYSTEM_TYPE.getValue());
        }
        return success(dashboardTemplateService.createDashboardTemplate(saveReqVO));
    }

    /**
     * 创建仪表盘模板
     *
     * @param saveReqVO 创建信息
     * @return 仪表盘模板ID
     */
    @PostMapping("/saveOtherDashboardTemplate")
    @ApiOperation("另存为模板")
    @ApiSignIgnore
    public CommonResult<Long> saveOtherDashboardTemplate(@RequestBody DashboardTemplateSaveReqVO saveReqVO) {

        saveReqVO.setTemplateType(APP_TYPE.getValue());
        GoviewProject goviewProject= iGoviewProjectService.getById(saveReqVO.getId());
        GoviewProjectData goviewProjectData = iGoviewProjectDataService.getProjectid(saveReqVO.getId());
        saveReqVO.setContent(goviewProjectData.getContent());
        saveReqVO.setIndexImage(goviewProject.getIndexImage());

        return success(dashboardTemplateService.createDashboardTemplate(saveReqVO));
    }

    /**
     * 更新仪表盘模板
     *
     * @param saveReqVO 更新信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @ApiOperation("更新仪表盘模板")
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    public CommonResult<Boolean> updateDashboardTemplate(@RequestBody @Validated DashboardTemplateSaveReqVO saveReqVO) {
        dashboardTemplateService.updateDashboardTemplate(saveReqVO);
        return success(true);
    }

    /**
     * 删除仪表盘模板
     *
     * @param id 仪表盘模板ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @ApiOperation("删除仪表盘模板")
    @ApiSignIgnore
    public CommonResult<Boolean> deleteDashboardTemplate(@RequestParam("id") Long id) {
        dashboardTemplateService.deleteDashboardTemplate(id);
        return success(true);
    }

    /**
     * 获取仪表盘模板
     *
     * @param id 仪表盘模板ID
     * @return 仪表盘模板信息
     */
    @GetMapping("/get")
    @ApiOperation("获取模板")
    @ApiSignIgnore
    @PermitAll
    @TenantIgnore
    public CommonResult<DashboardTemplateRespVO> getDashboardTemplate(@RequestParam("id") Long id) {
        DashboardTemplateDO template = dashboardTemplateService.getDashboardTemplate(id);
        return success(BeanUtils.toBean(template, DashboardTemplateRespVO.class));
    }

    /**
     * 获取仪表盘模板列表
     *
     * @param ids 仪表盘模板ID集合
     * @return 仪表盘模板列表
     */
    @GetMapping("/list")
    @ApiOperation("获取模板列表")
    @ApiSignIgnore
    public CommonResult<List<DashboardTemplateRespVO>> getDashboardTemplateList(@RequestParam("ids") List<Long> ids) {
        List<DashboardTemplateDO> list = dashboardTemplateService.getDashboardTemplateList(ids);
        return success(BeanUtils.toBean(list, DashboardTemplateRespVO.class));
    }

    /**
     * 分页查询仪表盘模板
     *
     * @param pageReqVO 分页查询条件
     * @return 仪表盘模板分页列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询模板")
    @ApiSignIgnore
    public CommonResult<PageResult<DashboardTemplateRespVO>> getDashboardTemplatePage(DashboardTemplatePageReqVO pageReqVO) {
        Page<DashboardTemplateDO> dashboardTemplatePage = dashboardTemplateService.getDashboardTemplatePage(pageReqVO);
        PageResult<DashboardTemplateRespVO> pageResult = new PageResult<>(
                BeanUtils.toBean(dashboardTemplatePage.getRecords(), DashboardTemplateRespVO.class),
                dashboardTemplatePage.getTotalRow()
        );
        return success(pageResult);
    }
}