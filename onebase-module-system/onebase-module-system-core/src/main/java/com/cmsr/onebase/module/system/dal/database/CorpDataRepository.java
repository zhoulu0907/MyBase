package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.vo.corp.CorpPageReqVO;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.data.param.ConfigStore;
import java.util.List;

/**
 * 企业数据访问层
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Repository
public class
CorpDataRepository extends DataRepository<CorpDO> {

    public CorpDataRepository() {
        super(CorpDO.class);
    }

    public CorpDataRepository(Class<CorpDO> defaultClazz) {
        super(defaultClazz);
    }

    public PageResult<CorpDO> selectPage(CorpPageReqVO pageReqVO) {
        // 构建查询条件
        ConfigStore configStore = new DefaultConfigStore();

        // 按照企业名称模糊查询
        if (pageReqVO.getCorpName() != null && !pageReqVO.getCorpName().isEmpty()) {
            configStore.like(CorpDO.CORP_NAME, pageReqVO.getCorpName());
        }
        // 按照状态查询
        if (pageReqVO.getStatus() != null) {
            configStore.eq(CorpDO.STATUS, pageReqVO.getStatus());
        }
        // 按照行业类型查询
        if (pageReqVO.getIndustryType() != null) {
            configStore.eq(CorpDO.INDUSTRY_TYPE, pageReqVO.getIndustryType());
        }
        // 按创建时间区间查询
        if (pageReqVO.getBeginCreateTime() != null) {
            configStore.ge(CorpDO.CREATE_TIME, pageReqVO.getBeginCreateTime());
        }
        if (pageReqVO.getEndCreateTime() != null) {
            configStore.le(CorpDO.CREATE_TIME, pageReqVO.getEndCreateTime());
        }
        if(pageReqVO.getOwnerTag()!=null && pageReqVO.getOwnerTag().equals(OwnerTagEnum.MY.getValue())){
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if(loginUser!=null){
                configStore.and(Compare.EQUAL, CorpDO.CREATOR, loginUser.getId());
            }
        }
        // 按创建时间倒序排列
        configStore.order(CorpDO.CREATE_TIME, Order.TYPE.DESC);
        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }


    public CorpDO findCorpByName(String name) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(CorpDO.CORP_NAME, name);
        return findOne(configStore);
    }

    public CorpDO findCorpByCorpCode(String corpCode) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(CorpDO.CORP_CODE, corpCode);
        return findOne(configStore);
    }

    public List<CorpDO> getSimpleCorpList(Integer staus) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(CorpDO.STATUS, staus)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public  List<CorpDO> getAllEnableCorp() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(CorpDO.STATUS, CommonStatusEnum.ENABLE.getStatus());
        return findAllByConfig(configStore);
    }


    public List<CorpDO> getAllCorpList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        return findAllByConfig(configStore);
    }
}