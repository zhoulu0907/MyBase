package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.UserPostDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 用户岗位关联数据访问层
 *
 * 负责用户岗位关联相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class UserPostDataRepository extends DataRepository<UserPostDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public UserPostDataRepository() {
        super(UserPostDO.class);
    }

    /**
     * 根据用户ID查询用户岗位关联
     *
     * @param userId 用户ID
     * @return 用户岗位关联列表
     */
    public List<UserPostDO> findAllByUserId(Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserPostDO.USER_ID, userId);
        return findAllByConfig(configStore);
    }

    /**
     * 根据岗位ID列表查询用户岗位关联
     *
     * @param postIds 岗位ID列表
     * @return 用户岗位关联列表
     */
    public List<UserPostDO> findAllByPostIds(Collection<Long> postIds) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(UserPostDO.POST_ID, postIds);
        return findAllByConfig(configStore);
    }

    /**
     * 根据用户ID删除用户岗位关联
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserPostDO.USER_ID, userId);
        deleteByConfig(configStore);
    }

    /**
     * 根据用户ID和岗位ID列表删除用户岗位关联
     *
     * @param userId 用户ID
     * @param postIds 岗位ID列表
     */
    public void deleteByUserIdAndPostIds(Long userId, Collection<Long> postIds) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserPostDO.USER_ID, userId).in(UserPostDO.POST_ID, postIds);
        deleteByConfig(configStore);
    }
}
