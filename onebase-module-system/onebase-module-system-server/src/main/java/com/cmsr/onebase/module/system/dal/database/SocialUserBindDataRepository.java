package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.social.SocialUserBindDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社交用户绑定数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SocialUserBindDataRepository extends DataRepositoryNew<SocialUserBindDO> {

    public SocialUserBindDataRepository() {
        super(SocialUserBindDO.class);
    }

    /**
     * 根据用户ID和用户类型查找绑定列表
     *
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 绑定列表
     */
    public List<SocialUserBindDO> findListByUserIdAndUserType(Long userId, Integer userType) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserBindDO.USER_ID, userId)
                .and(Compare.EQUAL, SocialUserBindDO.USER_TYPE, userType));
    }

    /**
     * 根据用户ID、用户类型和社交类型查找绑定
     *
     * @param userId 用户ID
     * @param userType 用户类型
     * @param socialType 社交类型
     * @return 绑定对象
     */
    public SocialUserBindDO findByUserIdAndUserTypeAndSocialType(Long userId, Integer userType, Integer socialType) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserBindDO.USER_ID, userId)
                .and(Compare.EQUAL, SocialUserBindDO.USER_TYPE, userType)
                .and(Compare.EQUAL, SocialUserBindDO.SOCIAL_TYPE, socialType));
    }

    /**
     * 根据用户类型和社交用户ID查找绑定
     *
     * @param userType 用户类型
     * @param socialUserId 社交用户ID
     * @return 绑定对象
     */
    public SocialUserBindDO findByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserBindDO.USER_TYPE, userType)
                .and(Compare.EQUAL, SocialUserBindDO.SOCIAL_USER_ID, socialUserId));
    }

    /**
     * 根据条件删除绑定
     *
     * @param userType 用户类型
     * @param socialUserId 社交用户ID
     * @return 删除数量
     */
    public long deleteByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return deleteByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserBindDO.USER_TYPE, userType)
                .and(Compare.EQUAL, SocialUserBindDO.SOCIAL_USER_ID, socialUserId));
    }

    /**
     * 根据条件删除绑定
     *
     * @param userType 用户类型
     * @param userId 用户ID
     * @param socialType 社交类型
     * @return 删除数量
     */
    public long deleteByUserTypeAndUserIdAndSocialType(Integer userType, Long userId, Integer socialType) {
        return deleteByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserBindDO.USER_TYPE, userType)
                .and(Compare.EQUAL, SocialUserBindDO.USER_ID, userId)
                .and(Compare.EQUAL, SocialUserBindDO.SOCIAL_TYPE, socialType));
    }
}
