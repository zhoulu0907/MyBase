package com.cmsr.onebase.module.system.service.license.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.license.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.controller.admin.license.LicensePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * License 服务实现类
 *
 * 提供License的增删改查等核心服务能力。
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Service
@Validated
@Slf4j
public class LicenseServiceImpl implements LicenseService {

    @Resource
    private DataRepository dataRepository;

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    @Override
    public Long createLicense(LicenseSaveReqVO reqVO) {
        LicenseDO license = BeanUtils.toBean(reqVO, LicenseDO.class);
        dataRepository.insert(license);
        return license.getId();
    }

    /**
     * 更新License
     *
     * @param reqVO License更新请求参数
     */
    @Override
    public void updateLicense(LicenseSaveReqVO reqVO) {
        LicenseDO license = BeanUtils.toBean(reqVO, LicenseDO.class);
        dataRepository.update(license);
    }

    /**
     * 删除License
     *
     * @param id License主键ID
     */
    @Override
    public void deleteLicense(Long id) {
        dataRepository.deleteById(LicenseDO.class, id);
    }

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    @Override
    public LicenseDO getLicense(Long id) {
        return dataRepository.findById(LicenseDO.class, id);
    }

    /**
     * 根据状态获取License
     *
     * @param status License主键ID
     * @return License
     */
    @Override
    public LicenseDO getLicenseByStatus(String status) {
        return dataRepository.findOne(LicenseDO.class, new DefaultConfigStore().and(Compare.EQUAL,"status",status));
    }

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<LicenseDO> getLicensePage(LicensePageReqVO reqVO) {
        try {
            ConfigStore cs = new DefaultConfigStore();

            // 按LicensePageReqVO实际参数动态构建查询条件
            if (StrUtil.isNotBlank(reqVO.getEnterpriseName())) {
                cs.and(Compare.LIKE, "enterprise_name", reqVO.getEnterpriseName());
            }
            if (StrUtil.isNotBlank(reqVO.getEnterpriseCode())) {
                cs.and(Compare.EQUAL, "enterprise_code", reqVO.getEnterpriseCode());
            }
            if (StrUtil.isNotBlank(reqVO.getPlatformType())) {
                cs.and(Compare.EQUAL, "platform_type", reqVO.getPlatformType());
            }
            if (StrUtil.isNotBlank(reqVO.getStatus())) {
                cs.and(Compare.EQUAL, "status", reqVO.getStatus());
            }
            if (reqVO.getExpireTimeFrom() != null) {
                cs.and(Compare.GREAT_EQUAL, "expire_time", reqVO.getExpireTimeFrom());
            }
            if (reqVO.getExpireTimeTo() != null) {
                cs.and(Compare.LESS_EQUAL, "expire_time", reqVO.getExpireTimeTo());
            }

            cs.order("id", "DESC");

            return dataRepository.findPageWithConditions(
                    LicenseDO.class,
                    cs,
                    reqVO.getPageNo(),
                    reqVO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询License失败", e);
            throw new RuntimeException("分页查询License失败", e);
        }
    }

    /**
     * 获取全部License（精简信息）
     *
     * @return License列表
     */
    @Override
    public List<LicenseDO> getSimpleLicenseList() {
        return dataRepository.findAll(LicenseDO.class, new DefaultConfigStore());
    }
}
