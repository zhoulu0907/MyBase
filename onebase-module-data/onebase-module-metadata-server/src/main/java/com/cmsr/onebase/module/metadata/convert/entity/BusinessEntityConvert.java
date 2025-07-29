package com.cmsr.onebase.module.metadata.convert.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessEntityConvert {

    BusinessEntityConvert INSTANCE = Mappers.getMapper(BusinessEntityConvert.class);

    BusinessEntityRespVO convert(MetadataBusinessEntityDO bean);

    List<BusinessEntityRespVO> convertList(List<MetadataBusinessEntityDO> list);

    default PageResult<BusinessEntityRespVO> convertPage(PageResult<MetadataBusinessEntityDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
