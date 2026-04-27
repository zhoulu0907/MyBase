package com.cmsr.onebase.module.system.convert.tenant;

import com.cmsr.onebase.module.system.vo.tenant.TenantRespVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantSimpleRespVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 空间 Convert
 *
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    TenantSimpleRespVO convertToSimpleRespVO(TenantDO tenant);

    TenantRespVO convert(TenantDO tenantDO);
}
