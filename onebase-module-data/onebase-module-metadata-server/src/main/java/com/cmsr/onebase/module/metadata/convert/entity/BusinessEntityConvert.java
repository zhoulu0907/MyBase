package com.cmsr.onebase.module.metadata.convert.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;

import java.util.List;

/**
 * 业务实体转换器
 *
 * @author matianyu
 * @date 2025-08-26
 */
public class BusinessEntityConvert {

    public static final BusinessEntityConvert INSTANCE = new BusinessEntityConvert();

    public BusinessEntityRespVO convert(MetadataBusinessEntityDO bean) {
        return BeanUtils.toBean(bean, BusinessEntityRespVO.class);
    }

    public List<BusinessEntityRespVO> convertList(List<MetadataBusinessEntityDO> list) {
        return BeanUtils.toBean(list, BusinessEntityRespVO.class);
    }

    public PageResult<BusinessEntityRespVO> convertPage(PageResult<MetadataBusinessEntityDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
