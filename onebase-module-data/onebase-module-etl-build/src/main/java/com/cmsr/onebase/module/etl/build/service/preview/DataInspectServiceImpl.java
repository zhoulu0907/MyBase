package com.cmsr.onebase.module.etl.build.service.preview;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.etl.build.service.DatasourceFactory;
import com.cmsr.onebase.module.etl.build.vo.datasource.TestConnectionVO;
import com.cmsr.onebase.module.etl.build.vo.preview.PreviewTableDef;
import com.cmsr.onebase.module.etl.build.vo.preview.TablePreviewVO;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.common.preview.PreviewColumn;
import com.cmsr.onebase.module.etl.core.dal.database.EtlDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.EtlFlinkMappingRepository;
import com.cmsr.onebase.module.etl.core.dal.database.EtlTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlTableDO;
import com.cmsr.onebase.module.etl.core.dto.FlinkMappings;
import com.cmsr.onebase.module.etl.core.enums.EtlErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.vo.ConnectCryptoProperties;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.apache.commons.lang3.StringUtils;
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
    private EtlDatasourceRepository datasourceRepository;

    @Resource
    private EtlTableRepository tableRepository;

    @Resource
    private EtlFlinkMappingRepository flinkMappingRepository;

    @Override
    public boolean testConnection(TestConnectionVO pingVO) {
        Long datasourceId = pingVO.getId();
        String datasourceType = pingVO.getDatasourceType();
        ConnectCryptoProperties connectProperties = pingVO.getConfig();
        if (datasourceId != null) {
            EtlDatasourceDO datasourceDO = datasourceRepository.getById(datasourceId);
            // 判断密码是否为脱敏数据
            if (StringUtils.isBlank(connectProperties.getPassword())) {
                ConnectCryptoProperties storedProperties = JsonUtils.parseObject(datasourceDO.getConfig(), ConnectCryptoProperties.class);
                connectProperties.setPassword(storedProperties.getPassword());
            }
        }

        DataSource datasource = dataSourceFactory.constructDataSource(datasourceType, connectProperties, true);
        String runnerKey = "ping-" + UuidUtils.getUuid();

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

    /**
     * Mybaits-Flex version,.
     */
    @Override
    public DataPreview previewData(TablePreviewVO previewVO) {
        String datasourceUuid = previewVO.getDatasourceUuid();
        EtlDatasourceDO datasourceDO = datasourceRepository.getByUuid(datasourceUuid);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        String datasourceType = datasourceDO.getDatasourceType();
        String tableUuid = previewVO.getTableUuid();
        EtlTableDO tableDO = tableRepository.getByUuid(tableUuid);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.TABLE_NOT_EXIST);
        }
        ConnectCryptoProperties connectProperties = JsonUtils.parseObject(datasourceDO.getConfig(), ConnectCryptoProperties.class);
        String datasourceKey = "preview-" + datasourceUuid + UuidUtils.getUuid();
        try {
            DataSource dataSource = dataSourceFactory.constructDataSource(datasourceType, connectProperties, true);
            FlinkMappings fieldTypeMapping = flinkMappingRepository.findByDatasourceType(datasourceType);
            DataPreview dataPreview = new DataPreview();
            TableData tableData = JsonUtils.parseObject(tableDO.getMetaInfo(), TableData.class);
            List<ColumnData> columnDataList = tableData.getColumns();
            List<PreviewColumn> columnList = extractPreviewColumns(datasourceType, columnDataList, fieldTypeMapping);
            dataPreview.setColumns(columnList);
            FlexGlobalConfig defaultOrmConfig = FlexGlobalConfig.getDefaultConfig();
            defaultOrmConfig.getDataSource().addDataSource(datasourceKey, dataSource);

            PreviewTableDef tableDef = PreviewTableDef.of(tableData);
            List<Row> rows = DataSourceKey.use(datasourceKey, () -> {
                QueryWrapper queryWrapper = QueryWrapper.create().from(tableDef).limit(inspectSize);
                return Db.selectListByQuery(queryWrapper);
            });

            int rowIdx = 1;
            for (Row row : rows) {
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
            unregisterDataSource(datasourceKey);
        }
    }

    private static List<PreviewColumn> extractPreviewColumns(String datasourceType, List<ColumnData> columnDataList, FlinkMappings fieldTypeMapping) {
        List<PreviewColumn> columnList = new ArrayList<>(columnDataList.size());
        for (ColumnData columnData : columnDataList) {
            PreviewColumn previewColumn = new PreviewColumn();

            String columnName = columnData.getName();
            String displayName = columnData.getDisplayName();
            String flinkType = fieldTypeMapping.getFlinkType(datasourceType, columnData.getType());
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
            FlexGlobalConfig.getDefaultConfig().getDataSource().removeDatasource(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }
}
