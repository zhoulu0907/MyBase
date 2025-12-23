package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.dept.UserPostDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemUserPostMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 用户岗位关联数据访问层
 * <p>
 * 基于 MyBatis-Flex 实现用户岗位关联相关的 CRUD 及常用查询能力。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class UserPostDataRepository extends BaseDataServiceImpl<SystemUserPostMapper, UserPostDO> {

    /**
     * 根据用户ID查询用户岗位关联
     *
     * @param userId 用户ID
     * @return 用户岗位关联列表
     */
    public List<UserPostDO> findAllByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(UserPostDO.USER_ID, userId));
    }

    /**
     * 根据岗位ID列表查询用户岗位关联
     *
     * @param postIds 岗位ID列表
     * @return 用户岗位关联列表
     */
    public List<UserPostDO> findAllByPostIds(Collection<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(UserPostDO.POST_ID, postIds));
    }

    /**
     * 根据用户ID删除用户岗位关联
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        remove(query().eq(UserPostDO.USER_ID, userId));
    }

    /**
     * 根据用户ID和岗位ID列表删除用户岗位关联
     *
     * @param userId 用户ID
     * @param postIds 岗位ID列表
     */
    public void deleteByUserIdAndPostIds(Long userId, Collection<Long> postIds) {
        if (userId == null || postIds == null || postIds.isEmpty()) {
            return;
        }
        remove(query().eq(UserPostDO.USER_ID, userId).in(UserPostDO.POST_ID, postIds));
    }
}
