package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowUser;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowUserMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowUserRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.ArrayUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;

import java.util.Arrays;
import java.util.List;

/**
 * WarmFlow 用户 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUserMapper, FlowUser> implements FlowUserDao<FlowUser> {
    @Resource
    private FlowUserRepository flowUserRepository;

    @Override
    public ServiceImpl<FlowUserMapper, FlowUser> getRepository() {
        return flowUserRepository;
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowUser::getAssociated, taskIdList);

        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (CollUtil.isNotEmpty(associateds)) {
            if (associateds.size() == 1) {
                queryWrapper.eq(FlowUser::getAssociated, associateds.get(0));
            } else {
                queryWrapper.in(FlowUser::getAssociated, associateds);
            }
        }

        queryWrapper.in(FlowUser::getType, Arrays.asList(types), ArrayUtil.isNotEmpty(types));

        return getRepository().list(queryWrapper);
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowUser::getAssociated, associated, ObjectUtil.isNotNull(associated));

        if (CollUtil.isNotEmpty(processedBys)) {
            if (processedBys.size() == 1) {
                queryWrapper.eq(FlowUser::getProcessedBy, processedBys.get(0));
            } else {
                queryWrapper.in(FlowUser::getProcessedBy, processedBys);
            }
        }

        queryWrapper.in(FlowUser::getType, types, ArrayUtil.isNotEmpty(types));

        return getRepository().list(queryWrapper);
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }
}


