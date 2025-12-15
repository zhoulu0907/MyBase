package com.cmsr.onebase.module.system.build.controller.config;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import com.cmsr.onebase.module.system.service.config.SystemGeneralConfigService;
import com.cmsr.onebase.module.system.vo.config.SystemConfigPageReqVO;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigSaveReqVO;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigUpdateReqVO;
import com.cmsr.onebase.module.system.vo.config.SystemGeneralConfigVO;
import com.cmsr.onebase.module.system.vo.post.PostPageReqVO;
import com.cmsr.onebase.module.system.vo.post.PostRespVO;
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

@Tag(name = "管理后台 - 参数配置")
@RestController
@RequestMapping("/system/config")
@Validated
public class SystemGeneralConfigController {

    @Resource
    private SystemGeneralConfigService systemGeneralConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:create')")
    public CommonResult<Long> createConfig(@Valid @RequestBody SystemGeneralConfigSaveReqVO createReqVO) {
        return success(systemGeneralConfigService.createConfig(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody SystemGeneralConfigUpdateReqVO updateReqVO) {
        systemGeneralConfigService.updateConfig(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除参数配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:config:delete')")
    public CommonResult<Boolean> deleteConfig(@RequestParam("id") Long id) {
        systemGeneralConfigService.deleteConfig(id);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "获得参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:query')")
    public CommonResult<SystemGeneralConfigVO> getConfig(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(systemGeneralConfigService.getConfig(id), SystemGeneralConfigVO.class));
    }


    @GetMapping(value = "/get-config-by-key")
    @Operation(summary = "根据参数键名查询", description = "不可见的配置，不允许返回给前端")
    @Parameter(name = "key", description = "参数键", required = true, example = "yunai.biz.username")
    @PreAuthorize("@ss.hasPermission('system:config:query')")
    public CommonResult<SystemGeneralConfigVO> getConfigByKey(@RequestParam("configKey") String configKey) {
        return success(systemGeneralConfigService.getConfigByKey(configKey));
    }


    @GetMapping("/list")
    @Operation(summary = "获得配置项列表-不分页")
    @PreAuthorize("@ss.hasPermission('tenant:post:query')")
    public CommonResult<List<SystemGeneralConfigVO>> getConfigList(@Validated SystemConfigPageReqVO pageReqVO) {
         List<SystemGeneralConfigDO> pageResult = systemGeneralConfigService.getConfigList(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SystemGeneralConfigVO.class));
    }

}
