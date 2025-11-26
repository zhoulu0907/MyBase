package com.cmsr.onebase.module.etl.build.service.preview;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.service.DatasourceFactory;
import com.cmsr.onebase.module.etl.build.vo.datasource.TestConnectionVO;
import com.cmsr.onebase.module.etl.build.vo.preview.TablePreviewVO;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.common.preview.PreviewColumn;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLFlinkMappingRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.MetadataType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataInspectServiceImpl implements DataInspectService {

    @Value("${onebase.etl.inspect-size:20}")
    private Integer inspectSize;

    @Resource
    private DatasourceFactory dataSourceFactory;

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLTableRepository tableRepository;

    @Resource
    private ETLFlinkMappingRepository flinkMappingRepository;

    @Override
    public boolean testConnection(TestConnectionVO pingVO) {
        ETLDatasourceDO datasourceDO = BeanUtils.toBean(pingVO, ETLDatasourceDO.class);
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceDO, true);
        String runnerKey = "ping-" + UuidCreator.getTimeOrderedEpoch();

        try {
            DataSourceHolder.reg(runnerKey, datasource);
            AnylineService<?> temporary = ServiceProxy.service(runnerKey);
            boolean validity = temporary.validity();
            boolean hit = temporary.hit();
            return validity || hit;
        } catch (Exception ex) {
            log.error("测试数据源连接异常，数据源信息: {}", pingVO, ex);
            return false;
        } finally {
            unregisterDataSource(runnerKey);
        }
    }

    @Override
    public DataPreview previewData(TablePreviewVO previewVO) {
        Long datasourceId = previewVO.getDatasourceId();
        ETLDatasourceDO datasourceDO = datasourceRepository.getById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        String datasourceType = datasourceDO.getDatasourceType();
        Long tableId = previewVO.getTableId();
        ETLTableDO tableDO = tableRepository.getById(tableId);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.TABLE_NOT_EXIST);
        }

        DataSource dataSource = dataSourceFactory.constructDataSource(datasourceDO, true);
        String runnerKey = "preview-" + datasourceId + UuidCreator.getTimeOrderedEpoch();
        try {
            DataSourceHolder.reg(runnerKey, dataSource);
            AnylineService<?> temporary = ServiceProxy.service(runnerKey);
            Table<?> table;
            MetadataType metadataType = MetadataType.getType(tableDO.getTableType());
            switch (metadataType) {
                case TABLE -> table = temporary.metadata().table(tableDO.getTableName());
                case VIEW -> table = temporary.metadata().view(tableDO.getTableName());
                default -> throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_METADATA_TYPE);
            }
            Map<String, String> fieldTypeMapping = flinkMappingRepository.findAllMappingsByDatasourceType(datasourceType);
            DataPreview dataPreview = new DataPreview();
            TableData tableData = JsonUtils.parseObject(tableDO.getMetaInfo(), TableData.class);
            List<ColumnData> columnDataList = tableData.getColumns();
            List<PreviewColumn> columnList = extractPreviewColumns(columnDataList, fieldTypeMapping);
            dataPreview.setColumns(columnList);
            ConfigStore cs = new DefaultConfigStore();
            cs.limit(inspectSize);

            DataSet dataSet = temporary.querys(table, cs);
            List<DataRow> rows = dataSet.getRows();
            int rowIdx = 1;
            for (DataRow row : rows) {
                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("key", rowIdx);
                for (int colIdx = 0; colIdx < columnDataList.size(); colIdx++) {
                    String key = columnDataList.get(colIdx).getName();
                    rowMap.put("_" + key, row.get(key));
                }
                dataPreview.getData().add(rowMap);
                rowIdx++;
            }
            return dataPreview;
        } catch (Exception e) {
            log.error("数据源连接异常，数据源信息: {}", datasourceDO, e);
            throw new RuntimeException(e);
        } finally {
            unregisterDataSource(runnerKey);
        }
    }

    private static List<PreviewColumn> extractPreviewColumns(List<ColumnData> columnDataList, Map<String, String> fieldTypeMapping) {
        List<PreviewColumn> columnList = new ArrayList<>(columnDataList.size());
        for (ColumnData columnData : columnDataList) {
            PreviewColumn previewColumn = new PreviewColumn();

            String columnName = columnData.getName();
            String displayName = columnData.getDisplayName();
            String flinkType = fieldTypeMapping.get(columnData.getType());
            previewColumn.setDataIndex("_" + columnName);
            previewColumn.setTitle(displayName);
            previewColumn.setFieldType(flinkType);
            int metaColumnIdx = columnData.getPosition() - 1;
            columnList.add(metaColumnIdx, previewColumn);
        }
        return columnList;
    }

    private void unregisterDataSource(String datasourceKey) {
        try {
            DataSourceHolder.destroy(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }
}
