package com.cmsr.onebase.module.dashboard.build.controller;

import cn.hutool.core.date.DateUtil;
import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.dashboard.build.common.base.BaseController;
import com.cmsr.onebase.module.dashboard.build.common.config.V2Config;
import com.cmsr.onebase.module.dashboard.build.common.domain.AjaxResult;
import com.cmsr.onebase.module.dashboard.build.common.domain.Tablepar;
import com.cmsr.onebase.module.dashboard.build.model.DashboardFile;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.model.vo.DashboardProjectVO;
import com.cmsr.onebase.module.dashboard.build.service.DashboardFileService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectService;
import com.cmsr.onebase.module.dashboard.build.service.impl.DashboardProjectServiceImpl;
import com.cmsr.onebase.module.dashboard.build.util.SnowflakeIdWorker;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardProjectController extends BaseController {
    @Resource
    private DashboardFileService dashboardFileService;
    @Resource
    private V2Config             v2Config;
    @Resource
    private DashboardProjectService     dashboardProjectService;
    @Resource
    private DashboardProjectDataService dashboardProjectDataService;
    @Resource
    private FileApi                     fileApi;

    @ApiOperation(value = "分页跳转", notes = "分页跳转")
    @GetMapping("/list")
    @ResponseBody
    @ApiSignIgnore
    public CommonResult<PageResult<DashboardProject>> list(Tablepar tablepar) {

        if (tablepar.getPage() == null && tablepar.getLimit() == null) {
            tablepar.setPage(1);
            tablepar.setLimit(10);
        }
        Page<DashboardProject> page = new Page<>(tablepar.getPage(), tablepar.getLimit());
        QueryWrapper queryWrapper = new QueryWrapper()
                .eq(DashboardProject::getAppId, ApplicationManager.getApplicationId())
                .like(DashboardProject::getProjectName, tablepar.getSearchText(), StringUtils.isNotBlank(tablepar.getSearchText()))
                .orderBy(DashboardProject::getCreateTime, false);

        Page<DashboardProject> iPages = dashboardProjectService.page(page, queryWrapper);

        return CommonResult.success(new PageResult<>(iPages.getRecords(), iPages.getTotalRow()));
    }


    /**
     * 新增保存
     *
     * @param
     * @return
     */
    //@Log(title = "项目表新增", action = "111")
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/create")
    @ResponseBody
    @ApiSignIgnore
    public AjaxResult add(@RequestBody DashboardProject goviewProject) {
        goviewProject.setState(-1);
        boolean b = dashboardProjectService.save(goviewProject);
        if (b) {
            return successData(0, goviewProject.getId()).put("msg", "新增成功");
        } else {
            return error();
        }
    }


    /**
     * 项目表删除
     *
     * @param
     * @return
     */
    //@Log(title = "项目表删除", action = "111")
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    @ResponseBody
    @ApiSignIgnore
    public AjaxResult remove(@RequestParam Long id) {
        Boolean b = dashboardProjectService.removeById(id);
        if (b) {
            return successData(0, id).put("msg", "删除成功");
        } else {
            return error();
        }
    }

    @ApiOperation(value = "修改保存", notes = "修改保存")
    @PostMapping("/edit")
    @ResponseBody
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    public AjaxResult editSave(@RequestBody DashboardProject goviewProject) {
        Boolean b = dashboardProjectService.updateById(goviewProject);
        if (b) {
            return successData(0, goviewProject).put("msg", "保存成功");
        }
        return error();
    }


    @ApiOperation(value = "项目重命名", notes = "项目重命名")
    @PostMapping("/rename")
    @ResponseBody
    @ApiSignIgnore
    public AjaxResult rename(@RequestBody DashboardProject goviewProject) {

        Boolean b = dashboardProjectService.updateById(goviewProject);
        if (b) {
            return successData(0, goviewProject.getId()).put("msg", "操作成功");
        }
        return error();
    }


    // 发布/取消项目状态
    @PostMapping("/publish")
    @ResponseBody
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    public AjaxResult updateVisible(@RequestBody DashboardProject goviewProject) {
        if (goviewProject.getState() == -1 || goviewProject.getState() == 1) {

            Boolean b = dashboardProjectService.updateById(goviewProject);
            if (b) {
                return success();
            }
            return error();
        }
        return error("警告非法字段");
    }

    @ApiOperation(value = "获取项目存储数据", notes = "获取项目存储数据")
    @GetMapping("/getScreenDSLData")
    @ResponseBody
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    public CommonResult<DashboardProjectVO> getScreenDSLData(Long projectId, ModelMap map) {
        DashboardProject goviewProject = dashboardProjectService.getById(projectId);

        DashboardProjectData blogText = dashboardProjectDataService.getProjectid(projectId);
        if (blogText != null) {
            DashboardProjectVO dashboardProjectVO = new DashboardProjectVO();
            BeanUtils.copyProperties(goviewProject, dashboardProjectVO);
            dashboardProjectVO.setId(String.valueOf(goviewProject.getId()));
            dashboardProjectVO.setContent(blogText.getContent());
            return CommonResult.success(dashboardProjectVO);
        }
        return CommonResult.success(null);
    }

    @ApiOperation(value = "保存项目数据", notes = "保存项目数据")
    @PostMapping("/save/data")
    @ResponseBody
    @ApiSignIgnore
    @PermitAll
    @TenantIgnore
    public AjaxResult saveData(DashboardProjectData data) {

        DashboardProject goviewProject = dashboardProjectService.getById(data.getProjectId());
        if (goviewProject == null) {
            return error("没有该项目ID");
        }
        DashboardProjectData goviewProjectData = dashboardProjectDataService.getOne(new QueryWrapper().eq(DashboardProjectData::getProjectId, goviewProject.getId()));
        if (goviewProjectData != null) {
            data.setId(goviewProjectData.getId());
            dashboardProjectDataService.updateById(data);
            return successData(0, data.getProjectId()).put("msg", "数据保存成功");
        } else {
            dashboardProjectDataService.save(data);
            return successData(0, data.getProjectId()).put("msg", "数据保存成功");
        }
    }

    /**
     * 上传文件
     *
     * @param object 文件流对象
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    public CommonResult<String> upload(MultipartFile object) throws IOException {

        String fileName = object.getOriginalFilename();
        // 默认文件格式
        String suffixName = v2Config.getDefaultFormat();
        String mediaKey = "";
        Long filesize = object.getSize();
        // 文件名字
        String fileSuffixName = "";
        if (fileName.lastIndexOf(".") != -1) {// 有后缀
            suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            // mediaKey=MD5.create().digestHex(fileName);
            mediaKey = SnowflakeIdWorker.getUUID();
            fileSuffixName = mediaKey + suffixName;
        } else {// 无后缀
            // 取得唯一id
            // mediaKey = MD5.create().digestHex(fileName+suffixName);
            mediaKey = SnowflakeIdWorker.getUUID();
            // fileSuffixName=mediaKey+suffixName;
        }
        String virtualKey = DashboardFileController.getFirstNotNull(v2Config.getXnljmap());
        String absolutePath = v2Config.getXnljmap().get(DashboardFileController.getFirstNotNull(v2Config.getXnljmap()));

        DashboardFile sysFile = new DashboardFile();
        // sysFile.setId(SnowflakeIdWorker.getUUID());
        sysFile.setFileName(fileSuffixName);
        sysFile.setFileSize(Integer.parseInt(filesize + ""));
        sysFile.setFileSuffix(suffixName);
        sysFile.setCreateTime(LocalDateTime.now());
        String filepath = DateUtil.formatDate(new Date());
        sysFile.setRelativePath(filepath);
        sysFile.setVirtualKey(virtualKey);
        sysFile.setAbsolutePath(absolutePath.replace("file:", ""));

        CommonResult<String> result = fileApi.dashboardUpload(new FileCreateReqDTO().setName(object.getOriginalFilename())
                .setType(object.getContentType()).setContent(object.getBytes()));
        // 保存文件标识
        sysFile.setFileId(Long.valueOf(result.getData()));

        dashboardFileService.saveOrUpdate(sysFile);

        return result;
    }


    /**
     * 从模板创建大屏
     *
     * @param templateId 从模板创建大屏
     * @return
     * @throws Exception
     */
    @PostMapping("/create-dashboard-by-template")
    @ApiOperation("从模板创建大屏")
    @ApiSignIgnore
    public CommonResult<Long> createDashboardByTemplate(@RequestParam("templateId") Long templateId) throws IOException {

        return CommonResult.success(dashboardProjectService.createDashboardByTemplate(templateId));
    }

}
