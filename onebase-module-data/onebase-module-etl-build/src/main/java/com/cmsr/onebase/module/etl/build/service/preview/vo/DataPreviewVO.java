package com.cmsr.onebase.module.etl.build.service.preview.vo;

import com.cmsr.onebase.module.etl.common.meta.ColumnMeta;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import lombok.Data;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class DataPreviewVO {

    private List<String> columns;

    private List<Collection<Object>> data = new ArrayList<>();

    public static DataPreviewVO of(ETLTableDO tableDO) {
        DataPreviewVO dataPreviewVO = new DataPreviewVO();
        List<ColumnMeta> columnMetaList = tableDO.getMetaInfo().getColumns();
        List<String> columnList = new ArrayList<>(columnMetaList.size());
        for (ColumnMeta columnMeta : columnMetaList) {
            String columnDefine = columnMeta.getDisplayName();
            int metaColumnIdx = columnMeta.getPosition() - 1;
            columnList.add(metaColumnIdx, columnDefine);
        }
        dataPreviewVO.setColumns(columnList);

        return dataPreviewVO;
    }

    public DataPreviewVO appendData(DataSet dataSet) {
        List<DataRow> rows = dataSet.getRows();
        rows.forEach(row -> data.add(row.values()));

        return this;
    }
}
