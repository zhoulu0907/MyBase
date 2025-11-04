package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigCategoryRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigUpdateReqVO;

import java.util.List;

/**
 * 安全配置服务接口
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
public interface SecurityConfigService {

    /**
     * 接口1：获取所有安全配置分类（deleted=0）
     *
     * @return 分类列表
     */
    List<SecurityConfigCategoryRespVO> getAllCategories();

    /**
     * 接口2：根据租户ID和分类ID获取该租户的安全配置项
     * 如果租户配置不存在，则返回模板默认值
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 安全配置项列表
     */
    List<SecurityConfigItemRespVO> getTenantConfigItems(Long tenantId, Long categoryId);

    /**
     * 接口3：根据租户ID和配置键更新配置值（单个）
     *
     * @param tenantId    租户ID
     * @param updateReqVO 更新请求
     */
    void updateConfig(Long tenantId, SecurityConfigUpdateReqVO updateReqVO);

    /**
     * 接口4：根据租户ID和配置键批量更新配置值
     *
     * @param tenantId       租户ID
     * @param updateReqVOList 批量更新请求列表
     */
    void batchUpdateConfig(Long tenantId, List<SecurityConfigUpdateReqVO> updateReqVOList);

}