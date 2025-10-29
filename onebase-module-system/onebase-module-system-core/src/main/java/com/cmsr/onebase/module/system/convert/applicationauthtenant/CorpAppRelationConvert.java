package com.cmsr.onebase.module.system.convert.applicationauthtenant;



import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;


import java.util.List;

public interface CorpAppRelationConvert {

    CorpAppRelationConvert INSTANCE = new CorpAppRelationConvertImpl();

    CorpAppRelationDO convert(CorpAppRelationInertReqVO bean);

    CorpAppRelationVO convert(CorpAppRelationDO bean);

    List<CorpAppRelationVO> convertList(List<CorpAppRelationDO> list);

}