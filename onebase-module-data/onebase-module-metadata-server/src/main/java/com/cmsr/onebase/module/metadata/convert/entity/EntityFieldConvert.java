package com.cmsr.onebase.module.metadata.convert.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
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

    // DTO 转换方法
    EntityFieldRespDTO convertToDTO(MetadataEntityFieldDO bean);

    List<EntityFieldRespDTO> convertListToDTO(List<MetadataEntityFieldDO> list);

    EntityFieldQueryVO convertDTOToQueryVO(EntityFieldQueryReqDTO reqDTO);

    EntityFieldQueryVO convertVOToQueryVO(EntityFieldQueryReqVO reqVO);

}
