package com.cmsr.onebase.module.infra.convert.security;

import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigCategoryDO;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityConfigTemplateDO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigCategoryRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 安全配置分类转换器
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Mapper
public interface SecurityConfigCategoryConvert {

    SecurityConfigCategoryConvert INSTANCE = Mappers.getMapper(SecurityConfigCategoryConvert.class);

    SecurityConfigCategoryRespVO convert(SecurityConfigCategoryDO bean);

    List<SecurityConfigCategoryRespVO> convertList(List<SecurityConfigCategoryDO> list);

    SecurityConfigItemRespVO convert(SecurityConfigTemplateDO bean);
}
