package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2ApproveMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO.CLIENT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO.USER_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO.USER_TYPE;

/**
 * OAuth2 批准数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2ApproveDataRepository extends BaseDataServiceImpl<SystemOauth2ApproveMapper, OAuth2ApproveDO> {

    /**
     * 根据用户ID、用户类型和客户端ID查询批准列表
     *
     * @param userId 用户ID
     * @param userType 用户类型
     * @param clientId 客户端ID
     * @return 批准列表
     */
    public List<OAuth2ApproveDO> findListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        if (userId == null || userType == null || clientId == null || clientId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return list(query()
                .eq(USER_ID, userId)
                .eq(USER_TYPE, userType)
                .eq(CLIENT_ID, clientId));
    }
}
