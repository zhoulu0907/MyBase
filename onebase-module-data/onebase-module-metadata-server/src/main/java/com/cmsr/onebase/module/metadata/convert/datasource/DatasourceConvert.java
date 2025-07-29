package com.cmsr.onebase.module.metadata.convert.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DatasourceConvert {

    DatasourceConvert INSTANCE = Mappers.getMapper(DatasourceConvert.class);

    DatasourceRespVO convert(MetadataDatasourceDO bean);

    List<DatasourceRespVO> convertList(List<MetadataDatasourceDO> list);

    default PageResult<DatasourceRespVO> convertPage(PageResult<MetadataDatasourceDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
