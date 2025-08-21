package com.cmsr.onebase.module.system.convert.tenant;

import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantSimpleRespVO;
import com.cmsr.onebase.module.system.controller.admin.user.vo.user.UserInsertReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 租户 Convert
 *
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    default UserInsertReqVO convert02(TenantInsertReqVO bean) {
        UserInsertReqVO reqVO = new UserInsertReqVO();
        reqVO.setUsername(bean.getAdminUserName());
        // reqVO.setPassword(bean.getPassword());
        reqVO.setNickname(bean.getAdminNickName()).setMobile(bean.getAdminMobile());
        reqVO.setAdminType(bean.getAdminType());
        return reqVO;
    }

    TenantSimpleRespVO convertToSimpleRespVO(TenantDO tenant);

    TenantRespVO convert(TenantDO tenantDO);
}
