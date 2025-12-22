package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.module.system.dal.dataobject.user.UserAppRelationDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemUserAppRelationMapper;
import com.cmsr.onebase.module.system.vo.user.UserAppPageReqVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 用户应用关联 数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class UserAppRelationDataRepository extends BaseDataServiceImpl<SystemUserAppRelationMapper, UserAppRelationDO> {

    public List<UserAppRelationDO> getUserAppRelationByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(query()
                .eq(UserAppRelationDO.USER_ID, userId)
                .eq(UserAppRelationDO.STATUS, CorpAppReationStatusEnum.ENABLE.getValue()));
    }

    public List<UserAppRelationDO> getUserAppRelationList(UserAppPageReqVO userAppPageReqVO) {
        if (userAppPageReqVO == null || CollectionUtils.isEmpty(userAppPageReqVO.getUserIds())) {
            return Collections.emptyList();
        }
        return list(query()
                .in(UserAppRelationDO.USER_ID, userAppPageReqVO.getUserIds())
                .eq(UserAppRelationDO.STATUS, CorpAppReationStatusEnum.ENABLE.getValue()));
    }
}