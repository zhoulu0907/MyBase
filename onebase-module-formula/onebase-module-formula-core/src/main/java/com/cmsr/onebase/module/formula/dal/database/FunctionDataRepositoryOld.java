package com.cmsr.onebase.module.formula.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FunctionDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 函数数据访问层
 *
 * 负责函数相关的数据操作，继承DataRepository，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Repository
public class FunctionDataRepositoryOld extends DataRepository<FunctionDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public FunctionDataRepositoryOld() {
        super(FunctionDO.class);
    }

    /**
     * 根据函数类型查询函数列表
     *
     * @param type 函数类型
     * @return 函数列表
     */
    public List<FunctionDO> findAllByType(String type) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, FunctionDO.FIELD_TYPE, type);
        return findAllByConfig(configs);
    }

    /**
     * 根据函数名称查询函数
     *
     * @param name 函数名称
     * @return 函数对象
     */
    public FunctionDO findOneByName(String name) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, FunctionDO.FIELD_NAME, name);
        return findOne(configs);
    }

    /**
     * 根据函数状态查询函数列表
     *
     * @param status 函数状态
     * @return 函数列表
     */
    public List<FunctionDO> findAllByStatus(Integer status) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, FunctionDO.FIELD_STATUS, status);
        return findAllByConfig(configs);
    }

    /**
     * 根据函数类型和状态查询函数列表
     *
     * @param type 函数类型（可为空）
     * @param name 函数名称（可为空，支持模糊查询）
     * @param status 函数状态（可为空）
     * @return 函数列表
     */
    public List<FunctionDO> findAllByConditions(String type, String name, Integer status) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if (type != null) {
            configs.and(Compare.EQUAL, FunctionDO.FIELD_TYPE, type);
        }
        if (name != null) {
            configs.and(Compare.LIKE, FunctionDO.FIELD_NAME, name);
        }
        if (status != null) {
            configs.and(Compare.EQUAL, FunctionDO.FIELD_STATUS, status);
        }
        configs.order(FunctionDO.CREATE_TIME, org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configs);
    }
}
