package com.cmsr.onebase.module.system.dal.database;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemLicenseMapper;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import com.cmsr.onebase.module.system.vo.license.LicensePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * License数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class LicenseDataRepository extends BaseDataServiceImpl<SystemLicenseMapper, LicenseDO> {

    /**
     * 根据状态查找License
     *
     * @param status 状态
     * @return License详情
     */
    public LicenseDO findOneByStatus(String status) {
        return getOne(query().eq(LicenseDO.STATUS, status));
    }

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<LicenseDO> findPage(LicensePageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .like(LicenseDO.ENTERPRISE_NAME, reqVO.getEnterpriseName(), StrUtil.isNotBlank(reqVO.getEnterpriseName()))
                .eq(LicenseDO.ENTERPRISE_CODE, reqVO.getEnterpriseCode(), StrUtil.isNotBlank(reqVO.getEnterpriseCode()))
                .eq(LicenseDO.PLATFORM_TYPE, reqVO.getPlatformType(), StrUtil.isNotBlank(reqVO.getPlatformType()))
                .eq(LicenseDO.STATUS, reqVO.getStatus(), StrUtil.isNotBlank(reqVO.getStatus()))
                .ge(LicenseDO.EXPIRE_TIME, reqVO.getExpireTimeFrom(), reqVO.getExpireTimeFrom() != null)
                .le(LicenseDO.EXPIRE_TIME, reqVO.getExpireTimeTo(), reqVO.getExpireTimeTo() != null)
                .orderBy(LicenseDO.STATUS, false);

        Page<LicenseDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 获取全部License列表
     *
     * @return License列表
     */
    public List<LicenseDO> findSimpleList() {
        return list(query());
    }

    /**
     * 获取激活的License列表
     *
     * @return License列表
     */
    public List<LicenseDO> findActiveLicenseList() {
        return list(query()
                .eq(LicenseDO.STATUS, LicenseStatusEnum.ENABLE.getStatus())
                .orderBy(LicenseDO.CREATE_TIME, false));
    }
}
