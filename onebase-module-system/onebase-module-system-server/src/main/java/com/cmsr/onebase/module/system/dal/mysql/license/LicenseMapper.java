package com.cmsr.onebase.module.system.dal.mysql.license;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * License 数据库访问层
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Mapper
public interface LicenseMapper extends BaseMapper<LicenseDO> {
    // 可根据需要扩展自定义SQL方法
}
