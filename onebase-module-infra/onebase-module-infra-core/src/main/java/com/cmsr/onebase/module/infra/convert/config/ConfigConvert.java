package com.cmsr.onebase.module.infra.convert.config;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.infra.dal.dataobject.config.ConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigRespVO;
import com.cmsr.onebase.module.infra.dal.vo.config.ConfigSaveReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    PageResult<ConfigRespVO> convertPage(PageResult<ConfigDO> page);

    List<ConfigRespVO> convertList(List<ConfigDO> list);

    @Mapping(source = "configKey", target = "key")
    @Mapping(source = "visible", target = "visible", qualifiedByName = "integerToBoolean")
    ConfigRespVO convert(ConfigDO bean);

    @Mapping(source = "key", target = "configKey")
    @Mapping(source = "visible", target = "visible", qualifiedByName = "booleanToInteger")
    ConfigDO convert(ConfigSaveReqVO bean);

    @Named("integerToBoolean")
    default Boolean integerToBoolean(Integer value) {
        return value != null && value != NumberUtils.INTEGER_ZERO;
    }

    @Named("booleanToInteger")
    default Integer booleanToInteger(Boolean value) {
        if (value == null) {
            return NumberUtils.INTEGER_ZERO;
        }
        return value ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

}