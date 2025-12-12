package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.service.user.UserAppRelationService;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserApplicationRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "企业服务 - 企业应用关系")
@RestController
@Validated
@RequestMapping("/user/user-app-relation")
public class TenantUserAppRelationController {

    @Resource
    private UserAppRelationService userAppRelationService;


}