package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowUser;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowUserRepository;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;

import java.util.List;

public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser> implements FlowUserDao<FlowUser> {

    @Resource
    private FlowUserRepository flowUserRepository;

    @Override
    public DataRepository<FlowUser> getRepository() {
        return flowUserRepository;
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        // todo 待处理
        return 0;
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {
        // todo 待处理
        return null;
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        // todo 待处理
        return null;
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }
}


