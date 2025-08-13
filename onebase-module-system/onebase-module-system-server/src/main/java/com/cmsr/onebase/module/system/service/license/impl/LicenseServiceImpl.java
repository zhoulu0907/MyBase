package com.cmsr.onebase.module.system.service.license.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicensePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.dal.database.LicenseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * License 服务实现类
 * <p>
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
    private LicenseDataRepository licenseDataRepository;

    /**
     * 创建License
     *
     * @param reqVO License创建请求参数
     * @return License主键ID
     */
    @Override
    public Long createLicense(LicenseSaveReqVO reqVO) {
        LicenseDO license = BeanUtils.toBean(reqVO, LicenseDO.class);
        licenseDataRepository.insert(license);
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
        licenseDataRepository.update(license);
    }

    /**
     * 删除License
     *
     * @param id License主键ID
     */
    @Override
    public void deleteLicense(Long id) {
        licenseDataRepository.deleteById(id);
    }

    /**
     * 获取License详情
     *
     * @param id License主键ID
     * @return License详情
     */
    @Override
    public LicenseDO getLicense(Long id) {
        return licenseDataRepository.findById(id);
    }

    /**
     * 根据状态获取License
     *
     * @param status License状态
     * @return License
     */
    @Override
    public LicenseDO getLicenseByStatus(String status) {
        return licenseDataRepository.findOneByStatus(status);
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
            return licenseDataRepository.findPage(reqVO);
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
        return licenseDataRepository.findSimpleList();
    }

    @Override
    public List<LicenseDO> getEnableLicenseList() {
        return licenseDataRepository.findAllByConfig(new DefaultConfigStore()
                .eq(LicenseDO.STATUS, LicenseStatusEnum.ENABLE.getStatus()));
    }
}
