package com.cmsr.onebase.module.system.service.applicationauthtenant;

import com.cmsr.onebase.framework.common.pojo.PageResult;

import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import jakarta.validation.Valid;


/**
 * 应用授权企业表 Service 接口
 */
public interface ApplicationAuthEnterpriseService {

    /**
     * 创建应用授权企业表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseSaveReqVO createReqVO);

    /**
     * 更新应用授权企业表
     *
     * @param updateReqVO 更新信息
     */
    void updateApplicationAuthEnterprise(@Valid ApplicationAuthEnterpriseSaveReqVO updateReqVO);

    /**
     * 删除应用授权企业表
     *
     * @param id 编号
     */
    void deleteApplicationAuthEnterprise(Long id);

    /**
     * 获得应用授权企业表
     *
     * @param id 编号
     * @return 应用授权企业表
     */
    ApplicationAuthEnterpriseVO getApplicationAuthEnterprise(Long id);

    /**
     * 获得应用授权企业表分页
     *
     * @param pageReqVO 分页查询
     * @return 应用授权企业表分页
     */
    PageResult<ApplicationAuthEnterpriseVO> getApplicationAuthEnterprisePage(ApplicationAuthEnterprisePageReqVO pageReqVO);
}