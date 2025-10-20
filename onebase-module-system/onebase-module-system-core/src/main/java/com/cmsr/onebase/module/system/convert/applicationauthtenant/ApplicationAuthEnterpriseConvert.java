package com.cmsr.onebase.module.system.convert.applicationauthtenant;


import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;

import java.util.List;

public interface ApplicationAuthEnterpriseConvert {

    ApplicationAuthEnterpriseConvert INSTANCE = new ApplicationAuthEnterpriseConvertImpl();

    ApplicationAuthEnterpriseDO convert(ApplicationAuthEnterpriseSaveReqVO bean);

    ApplicationAuthEnterpriseVO convert(ApplicationAuthEnterpriseDO bean);

    List<ApplicationAuthEnterpriseVO> convertList(List<ApplicationAuthEnterpriseDO> list);

}