package com.cmsr.onebase.module.system.dal.database;


import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业应用关联 数据仓储接口
 */
@Repository
public class CorpAppRelationDataRepository extends DataRepository<CorpAppRelationDO> {

    public CorpAppRelationDataRepository() {
        super(CorpAppRelationDO.class);
    }

    public PageResult<CorpAppRelationDO> selectPage(CorpAppPageReqVO pageReqVO, List<Long> appIds) {
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(CorpAppRelationDO.CORP_ID, pageReqVO.getCorpId());

        if (CollectionUtils.isNotEmpty(appIds)) {
            configStore.in(CorpAppRelationDO.APPLICATION_ID, appIds);
        }
        // 添加状态查询条件
        if (pageReqVO.getStatus() != null) {
            Integer status = pageReqVO.getStatus();
            if (status.equals(CorpAppReationStatusEnum.DISABLE.getValue())) {
                // 查询企业状态为禁用的记录
                configStore.eq(CorpAppRelationDO.STATUS, CorpStatusEnum.DISABLE.getValue());
            } else {
                if (status.equals(CorpAppReationStatusEnum.ENABLE.getValue())) {
                    // 查询有效期内的（未过期的）
                    configStore.gt(CorpAppRelationDO.EXPIRES_TIME, java.time.LocalDateTime.now());
                } else if (status.equals(CorpAppReationStatusEnum.EXPIRES.getValue())) {
                    // 查询已过期的
                    configStore.le(CorpAppRelationDO.EXPIRES_TIME, java.time.LocalDateTime.now());
                    configStore.eq(CorpAppRelationDO.STATUS, CorpStatusEnum.ENABLE.getValue());
                }
            }
        }
        // 按创建时间倒序排列
        configStore.order(CorpAppRelationDO.CREATE_TIME, Order.TYPE.DESC);
        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

    }

    public void deleteCorpAppRelationByCorpId(Long corpID) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(CorpAppRelationDO.CORP_ID, corpID);
        deleteByConfig(configs);
    }

    public List<CorpAppRelationDO> getCorpAppRelationList(CorpAppRelationPageReqVO corpAppRelationPageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        if (CollectionUtils.isNotEmpty(corpAppRelationPageReqVO.getCorpIds())) {
            configStore.in(CorpAppRelationDO.CORP_ID, corpAppRelationPageReqVO.getCorpIds());
        }
        if (CollectionUtils.isNotEmpty(corpAppRelationPageReqVO.getAppIds())) {
            configStore.in(CorpAppRelationDO.APPLICATION_ID, corpAppRelationPageReqVO.getAppIds());
        }
        return findAllByConfig(configStore);

    }
}