package com.cmsr.onebase.module.system.runtime.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.service.user.UserAppRelationService;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeSimpleRespVO;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppVO;
import com.cmsr.onebase.module.system.vo.user.UserApplicationRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "外部用户服务 - 用户应用关系")
@RestController
@Validated
@RequestMapping("/user/user-app-relation")
public class UserAppRelationController {

    @Resource
    private UserAppRelationService userAppRelationService;


    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('tenant:app-auth:create')")
    @Operation(summary = "新增企业应用关联")
    public CommonResult<Boolean> createUserAppRelation(@Valid @RequestBody UserAppRelationInertReqVO userAppRelationInertReqVO) {
        userAppRelationService.createUserAppRelation(userAppRelationInertReqVO);
        return success(true);
    }

    @GetMapping("/get-app-by-user-id")
    @Operation(summary = "获得用户授权应用列表-分页")
    @PreAuthorize("@ss.hasPermission('user:app-auth:query')")
    public CommonResult<List<UserAppVO>> getAppByUserId(@RequestParam("userId") Long userId) {
        List<UserAppVO> list = userAppRelationService.getAppByUserId(userId);
        return success(list);
    }

}