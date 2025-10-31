package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.vo.corp.CorpPageReqVO;
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
public class CorpDataRepository extends DataRepository<CorpDO> {

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
            configStore.like("corp_name", pageReqVO.getCorpName());
        }
        // 按照状态查询
        if (pageReqVO.getStatus() != null) {
            configStore.eq("status", pageReqVO.getStatus());
        }
        // 按照行业类型查询
        if (pageReqVO.getIndustryType() != null) {
            configStore.eq("industry_type", pageReqVO.getIndustryType());
        }
        // 按创建时间区间查询
        if (pageReqVO.getBeginCreateTime() != null) {
            configStore.ge("create_time", pageReqVO.getBeginCreateTime());
        }
        if (pageReqVO.getEndCreateTime() != null) {
            configStore.le("create_time", pageReqVO.getEndCreateTime());
        }
        // 按创建时间倒序排列
        configStore.order("create_time", Order.TYPE.DESC);
        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }


    public CorpDO findCorpByName(String name) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("corp_name", name);
        return findOne(configStore);
    }

    public CorpDO findCorpByCorpId(String corpId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("corp_id", corpId);
        return findOne(configStore);
    }

    public List<CorpDO> getSimpleCorpList(Integer staus) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("staus", staus)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}