package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OAuth2批准数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class OAuth2ApproveDataRepository extends DataRepository<OAuth2ApproveDO> {

    public OAuth2ApproveDataRepository() {
        super(OAuth2ApproveDO.class);
    }

    /**
     * 根据用户ID、用户类型和客户端ID查询批准列表
     *
     * @param userId 用户ID
     * @param userType 用户类型
     * @param clientId 客户端ID
     * @return 批准列表
     */
    public List<OAuth2ApproveDO> findListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, "user_id", userId)
                .and(Compare.EQUAL, "user_type", userType)
                .and(Compare.EQUAL, "client_id", clientId));
    }
}
