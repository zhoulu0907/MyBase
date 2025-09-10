package com.cmsr.onebase.module.metadata.convert.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;

import java.util.List;

/**
 * 实体字段转换器
 *
 * @author matianyu
 * @date 2025-08-26
 */
public class EntityFieldConvert {

    public static final EntityFieldConvert INSTANCE = new EntityFieldConvert();

    public EntityFieldRespVO convert(MetadataEntityFieldDO bean) {
        return BeanUtils.toBean(bean, EntityFieldRespVO.class);
    }

    public List<EntityFieldRespVO> convertList(List<MetadataEntityFieldDO> list) {
        return BeanUtils.toBean(list, EntityFieldRespVO.class);
    }

    public PageResult<EntityFieldRespVO> convertPage(PageResult<MetadataEntityFieldDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    // DTO 转换方法
    public EntityFieldRespDTO convertToDTO(MetadataEntityFieldDO bean) {
        return BeanUtils.toBean(bean, EntityFieldRespDTO.class);
    }

    public List<EntityFieldRespDTO> convertListToDTO(List<MetadataEntityFieldDO> list) {
        return BeanUtils.toBean(list, EntityFieldRespDTO.class);
    }

    public EntityFieldQueryVO convertDTOToQueryVO(EntityFieldQueryReqDTO reqDTO) {
        return BeanUtils.toBean(reqDTO, EntityFieldQueryVO.class);
    }

    public EntityFieldQueryVO convertVOToQueryVO(EntityFieldQueryReqVO reqVO) {
    return BeanUtils.toBean(reqVO, EntityFieldQueryVO.class);
    }

}
