package com.cmsr.onebase.module.infra.convert.config;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.config.ConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigRespVO;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigSaveReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    PageResult<ConfigRespVO> convertPage(PageResult<ConfigDO> page);

    List<ConfigRespVO> convertList(List<ConfigDO> list);

    @Mapping(source = "configKey", target = "key")
    ConfigRespVO convert(ConfigDO bean);

    @Mapping(source = "key", target = "configKey")
    ConfigDO convert(ConfigSaveReqVO bean);

}
