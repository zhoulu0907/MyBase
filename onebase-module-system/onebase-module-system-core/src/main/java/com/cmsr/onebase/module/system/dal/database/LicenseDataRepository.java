package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;
import com.cmsr.onebase.module.system.vo.license.LicensePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.enums.license.LicenseStatusEnum;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * License数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class LicenseDataRepository extends DataRepository<LicenseDO> {

    public LicenseDataRepository() {
        super(LicenseDO.class);
    }

    /**
     * 根据状态查找License
     *
     * @param status 状态
     * @return License详情
     */
    public LicenseDO findOneByStatus(String status) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, LicenseDO.STATUS, status));
    }

    /**
     * 分页查询License
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<LicenseDO> findPage(LicensePageReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (StrUtil.isNotBlank(reqVO.getEnterpriseName())) {
            configStore.and(Compare.LIKE, LicenseDO.ENTERPRISE_NAME, reqVO.getEnterpriseName());
        }
        if (StrUtil.isNotBlank(reqVO.getEnterpriseCode())) {
            configStore.and(Compare.EQUAL, LicenseDO.ENTERPRISE_CODE, reqVO.getEnterpriseCode());
        }
        if (StrUtil.isNotBlank(reqVO.getPlatformType())) {
            configStore.and(Compare.EQUAL, LicenseDO.PLATFORM_TYPE, reqVO.getPlatformType());
        }
        if (StrUtil.isNotBlank(reqVO.getStatus())) {
            configStore.and(Compare.EQUAL, LicenseDO.STATUS, reqVO.getStatus());
        }
        if (reqVO.getExpireTimeFrom() != null) {
            configStore.and(Compare.GREAT_EQUAL, LicenseDO.EXPIRE_TIME, reqVO.getExpireTimeFrom());
        }
        if (reqVO.getExpireTimeTo() != null) {
            configStore.and(Compare.LESS_EQUAL, LicenseDO.EXPIRE_TIME, reqVO.getExpireTimeTo());
        }

        configStore.order(LicenseDO.STATUS, "desc");

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 获取全部License列表
     *
     * @return License列表
     */
    public java.util.List<LicenseDO> findSimpleList() {
        return findAllByConfig(new DefaultConfigStore());
    }

    /**
     * 获取激活的License列表
     *
     * @return License列表
     */
    public List<LicenseDO> findActiveLicenseList() {
        ConfigStore config = new DefaultConfigStore()
                .and(Compare.EQUAL, LicenseDO.STATUS, LicenseStatusEnum.ENABLE.getStatus())
                .order(LicenseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(config);
    }
}
