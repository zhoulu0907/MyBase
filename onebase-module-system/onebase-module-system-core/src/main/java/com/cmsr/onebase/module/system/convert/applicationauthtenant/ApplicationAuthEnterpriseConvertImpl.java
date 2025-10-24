package com.cmsr.onebase.module.system.convert.applicationauthtenant;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseInertReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;


import java.util.List;

public class ApplicationAuthEnterpriseConvertImpl implements ApplicationAuthEnterpriseConvert {


    @Override
    public ApplicationAuthEnterpriseDO convert(ApplicationAuthEnterpriseInertReqVO bean) {
        return BeanUtils.toBean(bean, ApplicationAuthEnterpriseDO.class);
    }

    @Override
    public ApplicationAuthEnterpriseVO convert(ApplicationAuthEnterpriseDO bean) {
        return BeanUtils.toBean(bean, ApplicationAuthEnterpriseVO.class);
    }

    @Override
    public List<ApplicationAuthEnterpriseVO> convertList(List<ApplicationAuthEnterpriseDO> list) {
        return BeanUtils.toBean(list, ApplicationAuthEnterpriseVO.class);
    }
}