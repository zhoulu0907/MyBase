package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkMappingDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLFlinkMappingMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ETLFlinkMappingRepository extends ServiceImpl<ETLFlinkMappingMapper, ETLFlinkMappingDO> {

    private DataRepository<ETLFlinkMappingDO> dataRepository;

    @Autowired
    private AnylineService<ETLFlinkMappingDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLFlinkMappingDO.class);
        dataRepository.setAnylineService(anylineService);
    }

    public Map<String, String> findAllMappingsByDatasourceType(String datasourceType) {
        QueryWrapper queryWrapper = query().select(ETLFlinkMappingDO::getOriginType, ETLFlinkMappingDO::getFlinkType)
                .eq(ETLFlinkMappingDO::getDatasourceType, datasourceType);

        List<ETLFlinkMappingDO> flinkMappingDOs = list(queryWrapper);
        return flinkMappingDOs.stream().collect(
                Collectors.toMap(
                        ETLFlinkMappingDO::getOriginType,
                        ETLFlinkMappingDO::getFlinkType
                )
        );
    }
}
