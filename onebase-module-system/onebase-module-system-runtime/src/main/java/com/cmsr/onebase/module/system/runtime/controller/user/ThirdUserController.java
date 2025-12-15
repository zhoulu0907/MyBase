package com.cmsr.onebase.module.system.runtime.controller.user;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.excel.core.util.ExcelUtils;
import com.cmsr.onebase.module.system.convert.user.UserConvert;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.common.SexEnum;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSimpleListRespVO;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "外部用户服务 - 用户")
@RestController
@RequestMapping("/third/user")
@Validated
public class ThirdUserController {

    @Resource
    private UserService userService;



    @PostMapping("/supplement-user")
    @Operation(summary = "补充用户信息")
    @PermitAll
    public CommonResult<Long> supplementUser(@RequestBody  @Valid ThirdSupplementUserReqVO reqVO) {
        Long id = userService.supplementUser(reqVO);
        return success(id);
    }


    @PostMapping("/forget-password")
    @Operation(summary = "忘记密码")
    @PreAuthorize("@ss.hasPermission('tenant:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserForgetPasswordReqVO reqVO) {
        userService.forgetPassword(reqVO);
        return success(true);
    }





}
