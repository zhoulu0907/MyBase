package com.cmsr.onebase.module.infra.convert.file;

import com.cmsr.onebase.module.infra.dal.dataobject.file.FileConfigDO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigRespVO;
import com.cmsr.onebase.module.infra.dal.vo.file.config.FileConfigSaveReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 文件配置 Convert
 *
 */
@Mapper
@Named("FileConfigConvert")
public interface FileConfigConvert {

    FileConfigConvert INSTANCE = Mappers.getMapper(FileConfigConvert.class);

    FileConfigDO convert(FileConfigSaveReqVO bean);

    @Mapping(target = "config", ignore = true)
    @Mapping(target = "master", source = "master", qualifiedByName = "integerToBoolean")
    FileConfigRespVO convertToFileConfigRespVO(FileConfigDO fileConfigDO);

    @Named("integerToBoolean")
    default Boolean integerToBoolean(Integer value) {
        return value != null && value != NumberUtils.INTEGER_ZERO;
    }
}