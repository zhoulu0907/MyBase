package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.service.user.UserAppRelationService;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserApplicationRespVO;
import com.cmsr.onebase.module.system.vo.user.UserRelationAppReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "空间外部用户服务 - 用户应用关系")
@RestController
@Validated
@RequestMapping("/user/user-app-relation")
public class TenantUserAppRelationController {

    @Resource
    private UserAppRelationService userAppRelationService;


    @PostMapping("/user-no-relation-app-list")
    @Operation(summary = "获取用户未关联应用", description = "主要用于前端的下拉选项")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<List<ApplicationDTO>> getUserNoRelationAppList(UserRelationAppReqVO relationAppReqVO) {
        List<ApplicationDTO>  applicationsList=  userAppRelationService.getUserNoRelationAppList(relationAppReqVO);
        return success( applicationsList);
    }

}