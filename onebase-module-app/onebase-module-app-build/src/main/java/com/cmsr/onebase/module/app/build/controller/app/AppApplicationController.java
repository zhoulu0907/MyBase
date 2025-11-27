package com.cmsr.onebase.module.app.build.controller.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.app.AppApplicationService;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateReqVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationCreateRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationSimpleRespVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.enums.app.ApplicationStatusEnum;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * @Author：huangjie
 * @Date：2025/7/22 14:48
 */
@Tag(name = "应用管理")
@RestController
@RequestMapping("/app/application")
@Validated
public class AppApplicationController {

    @Resource
    private AppApplicationService appApplicationService;

    @GetMapping("/page")
    @Operation(summary = "获得应用列表")
    public CommonResult<PageResult<ApplicationRespVO>> getApplicationPage(@Validated ApplicationPageReqVO pageReqVO) {
        return CommonResult.success(appApplicationService.getApplicationPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用")
    public CommonResult<ApplicationRespVO> getApplication(@RequestParam("id") Long id) {
        return CommonResult.success(appApplicationService.getApplication(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用")
    public CommonResult<ApplicationCreateRespVO> createApplication(@Validated @RequestBody ApplicationCreateReqVO applicationCreateReqVO) {
        return CommonResult.success(appApplicationService.createApplication(applicationCreateReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新应用")
    public CommonResult<Boolean> updateApplication(@Validated @RequestBody ApplicationCreateReqVO applicationCreateReqVO) {
        appApplicationService.updateApplication(applicationCreateReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/update-name")
    @Operation(summary = "更新应用名称")
    public CommonResult<Boolean> updateApplicationName(@RequestParam("id") Long id,
                                                       @RequestParam("name") String name) {
        appApplicationService.updateApplicationName(id, name);
        return CommonResult.success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用")
    public CommonResult<Boolean> deleteApplication(@RequestParam("id") Long id,
                                                   @RequestParam("name") String name) {
        appApplicationService.deleteApplication(id, name);
        return CommonResult.success(true);
    }
    @GetMapping("/id/generate")
    @Operation(summary = "发号器")
    public CommonResult<Long> generateId() {
        return CommonResult.success(appApplicationService.generateId());
    }

    @GetMapping(value = {"/simple-list"})
    @Operation(summary = "获取应用精简信息列表-不分页", description = "只包含被开启的应用，主要用于前端的下拉选项")
    public CommonResult<List<ApplicationSimpleRespVO>> getSimpleAppList() {
        List<AppApplicationDO> list = appApplicationService.getSimpleAppList(ApplicationStatusEnum.PUBLISHED.getValue());
        return success(BeanUtils.toBean(list, ApplicationSimpleRespVO.class));
    }

    @GetMapping(value = {"/simple-list-by-name"})
    @Operation(summary = "获取我创建的应用列表-不分页", description = "获取我创建的应用列表")
    public CommonResult<List<ApplicationSimpleRespVO>> getSimpleAppListByName(@RequestParam(value = "appName", required = false)  String appName) {
        List<AppApplicationDO> list = appApplicationService.getMySimpleAppListByName(appName);
        return success(BeanUtils.toBean(list, ApplicationSimpleRespVO.class));
    }

}
