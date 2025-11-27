package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlFlinkMappingDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlFlinkMappingMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class EtlFlinkMappingRepository extends ServiceImpl<EtlFlinkMappingMapper, EtlFlinkMappingDO> {

    public Map<String, String> findAllMappingsByDatasourceType(String datasourceType) {
        QueryWrapper queryWrapper = query().select(
                        EtlFlinkMappingDO::getOriginType,
                        EtlFlinkMappingDO::getFlinkType
                )
                .eq(EtlFlinkMappingDO::getDatasourceType, datasourceType);

        List<EtlFlinkMappingDO> flinkMappingDOs = list(queryWrapper);
        return flinkMappingDOs.stream().collect(
                Collectors.toMap(
                        EtlFlinkMappingDO::getOriginType,
                        EtlFlinkMappingDO::getFlinkType
                )
        );
    }
}
