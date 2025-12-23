package com.cmsr.onebase.module.infra.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.config.ConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigPageReqVO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 参数配置数据访问层
 *
 * 负责参数配置相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ConfigDataRepositoryOld extends DataRepository<ConfigDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public ConfigDataRepositoryOld() {
        super(ConfigDO.class);
    }

    /**
     * 根据配置键查询配置
     *
     * @param key 配置键
     * @return 配置对象
     */
    public ConfigDO findByKey(String key) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(ConfigDO.CONFIG_KEY, key);
        return findOne(configStore);
    }

    /**
     * 分页查询配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ConfigDO> findPage(ConfigPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.like(ConfigDO.NAME, pageReqVO.getName())
                .like(ConfigDO.CONFIG_KEY, pageReqVO.getKey())
                .eq(ConfigDO.TYPE, pageReqVO.getType());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.ge(ConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            configStore.le(ConfigDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
