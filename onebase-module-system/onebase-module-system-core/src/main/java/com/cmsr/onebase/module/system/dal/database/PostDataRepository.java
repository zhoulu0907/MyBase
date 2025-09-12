package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.post.PostPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 岗位数据访问层
 *
 * 负责岗位相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class PostDataRepository extends DataRepository<PostDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public PostDataRepository() {
        super(PostDO.class);
    }

    /**
     * 根据岗位名称查询岗位
     *
     * @param name 岗位名称
     * @return 岗位对象
     */
    public PostDO findOneByName(String name) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, PostDO.NAME, name);
        return findOne(configs);
    }

    /**
     * 根据岗位编码查询岗位
     *
     * @param code 岗位编码
     * @return 岗位对象
     */
    public PostDO findOneByCode(String code) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, PostDO.CODE, code);
        return findOne(configs);
    }

    /**
     * 根据ID集合和状态查询岗位列表
     *
     * @param ids ID集合
     * @param statuses 状态集合
     * @return 岗位列表
     */
    public List<PostDO> findListByIdsAndStatuses(Collection<Long> ids, Collection<Integer> statuses) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if (ids != null && !ids.isEmpty()) {
            configs.and(Compare.IN, PostDO.ID, ids);
        }
        if (statuses != null && !statuses.isEmpty()) {
            configs.and(Compare.IN, PostDO.STATUS, statuses);
        }
        return findAllByConfig(configs);
    }

    /**
     * 分页查询岗位
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<PostDO> findPage(PostPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getCode() != null && !reqVO.getCode().trim().isEmpty()) {
            configs.and(Compare.LIKE, PostDO.CODE, reqVO.getCode());
        }
        if (reqVO.getName() != null && !reqVO.getName().trim().isEmpty()) {
            configs.and(Compare.LIKE, PostDO.NAME, reqVO.getName());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, PostDO.STATUS, reqVO.getStatus());
        }

        // 添加排序条件，按ID降序排列
        configs.order(PostDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
