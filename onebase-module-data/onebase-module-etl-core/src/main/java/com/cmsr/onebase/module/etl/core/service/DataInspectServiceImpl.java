package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.vo.DataPreviewVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLTablePreviewVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Slf4j
@Service
public class DataInspectServiceImpl implements DataInspectService {
    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLTableRepository tableRepository;

    @Override
    public DataPreviewVO previewData(ETLTablePreviewVO previewVO) {
        Long datasourceId = previewVO.getDatasourceId();
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        Long tableId = previewVO.getTableId();
        ETLTableDO tableDO = tableRepository.findById(tableId);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.TABLE_NOT_EXIST);
        }

        DataSource dataSource = dataSourceFactory.constructDataSource(datasourceDO, true);

        try {
            AnylineService temporary = ServiceProxy.temporary(dataSource);
            Table table = temporary.metadata().table(tableDO.getTableName());
            DataPreviewVO dataPreviewVO = DataPreviewVO.of(tableDO);
            ConfigStore cs = new DefaultConfigStore();
            // TODO: 魔法值，后续需要做成配置
            cs.limit(50);
            DataSet dataSet = temporary.querys(table, cs);
            return dataPreviewVO.appendData(dataSet);
        } catch (Exception e) {
            log.error("数据源连接异常，数据源信息: {}", datasourceDO, e);
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.UNKNOWN_ERROR);
        }
    }
}
