package com.cmsr.onebase.module.metadata.convert.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EntityFieldConvert {

    EntityFieldConvert INSTANCE = Mappers.getMapper(EntityFieldConvert.class);

    EntityFieldRespVO convert(MetadataEntityFieldDO bean);

    List<EntityFieldRespVO> convertList(List<MetadataEntityFieldDO> list);

    default PageResult<EntityFieldRespVO> convertPage(PageResult<MetadataEntityFieldDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
