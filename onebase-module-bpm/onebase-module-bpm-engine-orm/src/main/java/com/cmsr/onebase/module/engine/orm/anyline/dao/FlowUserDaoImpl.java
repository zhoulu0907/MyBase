package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowUser;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowUserRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.ArrayUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;

import java.util.List;

/**
 * WarmFlow 用户 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser> implements FlowUserDao<FlowUser> {

    @Resource
    private FlowUserRepository flowUserRepository;

    @Override
    public DataRepository<FlowUser> getRepository() {
        return flowUserRepository;
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowUser.ASSOCIATED, taskIdList);
        return (int) getRepository().deleteByConfig(configStore);
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {
        ConfigStore configStore = new DefaultConfigStore();
        if (CollUtil.isNotEmpty(associateds)) {
            if (associateds.size() == 1) {
                configStore.eq(FlowUser.ASSOCIATED, associateds.get(0));
            } else {
                configStore.in(FlowUser.ASSOCIATED, associateds);
            }
        }

        if (ArrayUtil.isNotEmpty(types)) {
            configStore.in(FlowUser.TYPE, types);
        }

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        ConfigStore configStore = new DefaultConfigStore();

        if (ObjectUtil.isNotNull(associated)) {
            configStore.eq(FlowUser.ASSOCIATED, associated);
        }

        if (CollUtil.isNotEmpty(processedBys)) {
            if (processedBys.size() == 1) {
                configStore.eq(FlowUser.PROCESSED_BY, processedBys.get(0));
            } else {
                configStore.in(FlowUser.PROCESSED_BY, processedBys);
            }
        }

        if (ArrayUtil.isNotEmpty(types)) {
            configStore.in(FlowUser.TYPE, types);
        }

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }
}


