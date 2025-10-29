package com.cmsr.onebase.module.system.convert.applicationauthtenant;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;

import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;

import java.util.List;
public class CorpAppRelationConvertImpl implements CorpAppRelationConvert {

    @Override
    public CorpAppRelationDO convert(CorpAppRelationInertReqVO bean) {
        return BeanUtils.toBean(bean, CorpAppRelationDO.class);
    }

    @Override
    public CorpAppRelationVO convert(CorpAppRelationDO bean) {
        return BeanUtils.toBean(bean, CorpAppRelationVO.class);
    }

    @Override
    public List<CorpAppRelationVO> convertList(List<CorpAppRelationDO> list) {
        return BeanUtils.toBean(list, CorpAppRelationVO.class);
    }
}