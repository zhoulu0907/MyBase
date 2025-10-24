// 6. 创建 enterprise 模块的 Service 接口
package com.cmsr.onebase.module.system.service.enterprise;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.*;
import jakarta.validation.Valid;


/**
 * 企业服务接口
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface EnterpriseService {

    /**
     * 创建企业
     *
     * @param reqVO 企业创建请求参数
     * @return 企业主键ID
     */
    Long createEnterprise(EnterpriseSaveReqVO reqVO);

    /**
     * 更新企业
     *
     * @param reqVO 企业更新请求参数
     */
    void updateEnterprise(EnterpriseSaveReqVO reqVO);

    /**
     * 删除企业
     *
     * @param id 企业主键ID
     */
    void deleteEnterprise(Long id);

    /**
     * 获得企业分页
     *
     * @param pageReqVO 分页查询参数
     * @return 企业分页结果
     */
    PageResult<EnterpriseRespVO> getEnterprisePage(EnterprisePageReqVO pageReqVO);

    /**
     * 获得企业详情
     *
     * @param id 企业主键ID
     * @return 企业详情
     */
    EnterpriseRespVO getEnterprise(Long id);
    /**
     * 创建企业管理员
     *
     */
    EnterpriseUserRespVO createUser( EnterpriseUserReqVO reqVO);
    /**
     * 创建企业管理员
     *
     */
    void updateStatus(Long id, Long status);
    /**
     * 创建企业对于的应用
     *
     */
    PageResult<EnterpriseApplicationRespVO> enterpriseApplicationPage(ApplicationAuthEnterprisePageReqVO pageReqVO);
}
