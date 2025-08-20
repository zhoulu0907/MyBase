package com.cmsr.onebase.module.infra.convert.file;

import com.cmsr.onebase.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import com.cmsr.onebase.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 文件配置 Convert
 *
 */
@Mapper
public interface FileConfigConvert {

    FileConfigConvert INSTANCE = Mappers.getMapper(FileConfigConvert.class);

    FileConfigDO convert(FileConfigSaveReqVO bean);

    @Mapping(target = "config", ignore = true)
    FileConfigRespVO convertToFileConfigRespVO(FileConfigDO fileConfigDO);
}
