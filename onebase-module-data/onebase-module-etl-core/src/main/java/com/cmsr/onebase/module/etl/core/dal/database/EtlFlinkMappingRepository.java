package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlFlinkMappingDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlFlinkMappingMapper;
import com.cmsr.onebase.module.etl.core.dto.FlinkMappings;
import com.cmsr.onebase.module.etl.core.enums.EtlConstants;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.etl.core.dal.dataobject.table.EtlFlinkMappingTableDef.ETL_FLINK_MAPPING;

@Slf4j
@Repository
public class EtlFlinkMappingRepository extends ServiceImpl<EtlFlinkMappingMapper, EtlFlinkMappingDO> {

    public FlinkMappings findByDatasourceType(String datasourceType) {
        QueryWrapper queryWrapper = query()
                .select(ETL_FLINK_MAPPING.DATASOURCE_TYPE, ETL_FLINK_MAPPING.ORIGIN_TYPE, ETL_FLINK_MAPPING.FLINK_TYPE)
                .where(ETL_FLINK_MAPPING.DATASOURCE_TYPE.eq(datasourceType)
                        .or(ETL_FLINK_MAPPING.DATASOURCE_TYPE.eq(EtlConstants.DEFAULT))
                );
        List<EtlFlinkMappingDO> list = list(queryWrapper);
        return new FlinkMappings(list);
    }

}
