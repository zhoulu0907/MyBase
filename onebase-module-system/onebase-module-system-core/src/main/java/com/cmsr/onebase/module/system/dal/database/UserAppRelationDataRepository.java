package com.cmsr.onebase.module.system.dal.database;


import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.catalina.User;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业应用关联 数据仓储接口
 */
@Repository
public class UserAppRelationDataRepository extends DataRepository<UserAppRelationDO> {

    public UserAppRelationDataRepository() {
        super(UserAppRelationDO.class);
    }


    public PageResult<UserAppRelationDO> selectPage(UserAppPageReqVO userAppPageReqVO) {
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        // 按创建时间倒序排列
        configStore.order(CorpAppRelationDO.CREATE_TIME, Order.TYPE.DESC);
        // 执行分页查询
        return findPageWithConditions(configStore, userAppPageReqVO.getPageNo(), userAppPageReqVO.getPageSize());

    }

    public List<UserAppRelationDO> getUserAppRelationByUserId(Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserAppRelationDO.USER_ID, userId);
        configStore.eq(UserAppRelationDO.STATUS, CorpAppReationStatusEnum.ENABLE.getValue());
        return findAllByConfig(configStore);
    }

    public List<UserAppRelationDO> getUserAppRelationList(UserAppPageReqVO userAppPageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        if (CollectionUtils.isNotEmpty(userAppPageReqVO.getUserIds())){
            configStore.in(AdminUserDO.ID, userAppPageReqVO.getUserIds());
        }
        configStore.eq(UserAppRelationDO.STATUS, CorpAppReationStatusEnum.ENABLE.getValue());
        return findAllByConfig(configStore);
    }
}