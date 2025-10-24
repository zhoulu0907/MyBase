package com.cmsr.onebase.module.system.api.enterprise;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;

import com.cmsr.onebase.module.system.api.enterprise.dto.EnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseRespVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.service.enterprise.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 企业管理接口实现类
 *
 * @author matianyu
 * @date 2025-08-20
 */
@RestController
public class EnterpriseApiImpl implements EnterpriseApi {



    @Resource
    private EnterpriseService enterpriseService;
    @Override
    public CommonResult<Long> createEnterprise(@RequestBody @Valid EnterpriseSaveReqVO reqVO) {
        return success(enterpriseService.createEnterprise(reqVO));
    }


    @Override
    public CommonResult<Boolean> updateEnterprise(@RequestBody @Valid  EnterpriseSaveReqVO reqVO) {
        enterpriseService.updateEnterprise(reqVO);
        return success(true);
    }


    @Override
    public CommonResult<Boolean> deleteEnterprise(@RequestParam("id") Long id) {
        enterpriseService.deleteEnterprise(id);
        return success(true);
    }


    @Override
    public CommonResult<PageResult<EnterpriseRespVO>> getEnterprisePage(EnterprisePageReqVO pageReqVO) {
        PageResult<  EnterpriseRespVO> pageResult = enterpriseService.getEnterprisePage(pageReqVO);
        return success(pageResult);
    }

    @Override
    public CommonResult< EnterpriseRespVO> getEnterprise(@RequestParam("id") Long id) {
       EnterpriseRespVO enterprise = enterpriseService.getEnterprise(id);
        return success(enterprise);
    }
}
