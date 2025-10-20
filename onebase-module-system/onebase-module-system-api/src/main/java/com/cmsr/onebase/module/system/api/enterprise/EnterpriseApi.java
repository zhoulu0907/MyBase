package com.cmsr.onebase.module.system.api.enterprise;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseRespVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 企业管理接口
 * <p>
 * 提供企业信息的增删改查等接口。
 *
 * @author matianyu
 * @date 2025-08-20
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "企业管理")
@PermitAll
public interface EnterpriseApi {
    String PREFIX = ApiConstants.PREFIX + "/enterprise";
    /**
     * 创建企业
     *
     * @param reqVO 企业创建请求参数
     * @return 企业主键ID
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建企业")

    CommonResult<Long> createEnterprise(@RequestBody EnterpriseSaveReqVO reqVO);

    /**
     * 更新企业
     *
     * @param reqVO 企业更新请求参数
     * @return 是否成功
     */
    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新企业")
    CommonResult<Boolean> updateEnterprise(@RequestBody EnterpriseSaveReqVO reqVO);

    /**
     * 删除企业
     *
     * @param id 企业主键ID
     * @return 是否成功
     */
    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除企业")
    CommonResult<Boolean> deleteEnterprise(@RequestParam("id") Long id);

    /**
     * 获得企业分页
     *
     * @param pageReqVO 分页查询参数
     * @return 企业分页结果
     */
    @GetMapping(PREFIX + "/page")
    @Operation(summary = "获得企业分页")
    CommonResult<PageResult<EnterpriseRespVO>> getEnterprisePage(EnterprisePageReqVO pageReqVO);

    /**
     * 获得企业详情
     *
     * @param id 企业主键ID
     * @return 企业详情
     */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得企业详情")
    @PermitAll
    CommonResult<EnterpriseRespVO> getEnterprise(@RequestParam("id") Long id);
}
