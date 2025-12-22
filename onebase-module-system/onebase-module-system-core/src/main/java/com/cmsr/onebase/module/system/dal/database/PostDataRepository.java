package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemPostMapper;
import com.cmsr.onebase.module.system.vo.post.PostPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 岗位数据访问层
 *
 * 负责岗位相关的数据操作。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class PostDataRepository extends BaseDataServiceImpl<SystemPostMapper, PostDO> {

    /**
     * 根据岗位名称查询岗位
     *
     * @param name 岗位名称
     * @return 岗位对象
     */
    public PostDO findOneByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(PostDO.NAME, name));
    }

    /**
     * 根据岗位编码查询岗位
     *
     * @param code 岗位编码
     * @return 岗位对象
     */
    public PostDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(PostDO.CODE, code));
    }

    /**
     * 根据ID集合和状态查询岗位列表
     *
     * @param ids ID集合
     * @param statuses 状态集合
     * @return 岗位列表
     */
    public List<PostDO> findListByIdsAndStatuses(Collection<Long> ids, Collection<Integer> statuses) {
        QueryWrapper queryWrapper = query();
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.in(PostDO.COL_ID, ids);
        }
        if (CollectionUtils.isNotEmpty(statuses)) {
            queryWrapper.in(PostDO.STATUS, statuses);
        }
        if ((ids == null || ids.isEmpty()) && (statuses == null || statuses.isEmpty())) {
            return list(queryWrapper);
        }
        return list(queryWrapper);
    }

    /**
     * 分页查询岗位
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<PostDO> findPage(PostPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .like(PostDO.CODE, reqVO.getCode(), StringUtils.isNotBlank(reqVO.getCode()))
                .like(PostDO.NAME, reqVO.getName(), StringUtils.isNotBlank(reqVO.getName()))
                .eq(PostDO.STATUS, reqVO.getStatus(), reqVO.getStatus() != null)
                .orderBy(ID, false);

        Page<PostDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
