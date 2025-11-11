package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.vo.datasource.DatasourcePageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class ETLDatasourceRepository extends DataRepository<ETLDatasourceDO> {

    public ETLDatasourceRepository() {
        super(ETLDatasourceDO.class);
    }

    public boolean existsByDatasourceCodeFilterById(String datasourceCode, Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_code", datasourceCode);
        if (datasourceId != null) {
            cs.ne("id", datasourceId);
        }
        ETLDatasourceDO datasourceDO = findOne(cs);
        return datasourceDO != null;
    }

    public void changeCollectStatusById(Long datasourceId, CollectStatus collectStatus) {
        ETLDatasourceDO datasourceDO = findById(datasourceId);
        LocalDateTime currentTime = LocalDateTime.now();
        switch (collectStatus) {
            case RUNNING: {
                datasourceDO.setCollectStartTime(currentTime);
                datasourceDO.setCollectEndTime(null);
                break;
            }
            case SUCCESS, FAILED: {
                datasourceDO.setCollectEndTime(currentTime);
                break;
            }
        }
        datasourceDO.setCollectStatus(collectStatus);
        update(datasourceDO);
    }

    public PageResult<ETLDatasourceDO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", pageReqVO.getApplicationId());
        if (StringUtils.isNotBlank(pageReqVO.getDatasourceCode())) {
            cs.like("datasource_code", pageReqVO.getDatasourceCode());
        }
        if (StringUtils.isNotBlank(pageReqVO.getDatasourceName())) {
            cs.like("datasource_name", pageReqVO.getDatasourceName());
        }
        if (StringUtils.isNotBlank(pageReqVO.getDatasourceType())) {
            cs.eq("datasource_type", pageReqVO.getDatasourceType());
        }
        if (pageReqVO.getReadonly() != null) {
            cs.eq("readonly", pageReqVO.getReadonly());
        }
        if (StringUtils.isNotBlank(pageReqVO.getCollectStatus())) {
            cs.eq("collect_status", pageReqVO.getCollectStatus());
        }
        cs.order("create_time", Order.TYPE.DESC);
        cs.order("update_time", Order.TYPE.DESC);

        return findPageWithConditions(cs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    public List<ETLDatasourceDO> findAllByApplicationIdWithWritable(Long applicationId, Integer writable) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", applicationId);
        if (writable != null) {
            cs.eq("readonly", 0);
        }

        return findAllByConfig(cs);
    }
}
