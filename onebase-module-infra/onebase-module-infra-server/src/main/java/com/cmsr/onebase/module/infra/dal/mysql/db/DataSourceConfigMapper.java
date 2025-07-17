package com.cmsr.onebase.module.infra.dal.mysql.db;

import com.cmsr.onebase.framework.mybatis.core.mapper.BaseMapperX;
import com.cmsr.onebase.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
