package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.infra.dal.dataflexdo.config.ConfigDO;
import com.cmsr.onebase.module.infra.dal.mapper.config.ConfigMapper;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 参数配置数据访问层
 *
 * 负责参数配置相关的数据操作，基于MyBatis-Flex实现
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class ConfigDataRepository extends ServiceImpl<ConfigMapper, ConfigDO> {

    /**
     * 根据配置键查询配置
     *
     * @param key 配置键
     * @return 配置对象
     */
    public ConfigDO findByKey(String key) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(ConfigDO.CONFIG_KEY, key);
        return getOne(queryWrapper);
    }

    /**
     * 分页查询配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<ConfigDO> findPage(ConfigPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.like(ConfigDO.NAME, pageReqVO.getName())
                .like(ConfigDO.CONFIG_KEY, pageReqVO.getKey())
                .eq(ConfigDO.TYPE, pageReqVO.getType());

        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            queryWrapper.ge(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            queryWrapper.le(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
        }

        Page<ConfigDO> page = this.page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}