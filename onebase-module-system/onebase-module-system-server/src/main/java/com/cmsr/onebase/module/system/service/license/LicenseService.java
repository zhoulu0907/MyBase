package com.cmsr.onebase.module.system.service.license;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicensePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;

import java.util.List;

/**
 * License 服务接口
 *
 * 提供License的增删改查等核心服务能力。
 *
 * @author matianyu
 * @date 2025-07-25
 */
public interface LicenseService {

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    Long createLicense(LicenseSaveReqVO reqVO);

    /**
     * 更新License
     *
     * @param reqVO License更新请求参数
     */
    void updateLicense(LicenseSaveReqVO reqVO);

    /**
     * 删除License
     *
     * @param id License主键ID
     */
    void deleteLicense(Long id);

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    LicenseDO getLicense(Long id);

    /**
     * 根据状态获取License
     *
     * @param status License主键ID
     * @return License
     */
    LicenseDO getLicenseByStatus(String status);

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    PageResult<LicenseDO> getLicensePage(LicensePageReqVO reqVO);

    /**
     * 获取全部License（精简信息）
     *
     * @return License列表
     */
    List<LicenseDO> getSimpleLicenseList();
}
