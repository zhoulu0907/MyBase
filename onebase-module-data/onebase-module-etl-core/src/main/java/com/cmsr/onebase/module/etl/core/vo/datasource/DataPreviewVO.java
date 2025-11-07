package com.cmsr.onebase.module.etl.core.vo.datasource;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo.MetaColumn;
import lombok.Data;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class DataPreviewVO {

    private List<ColumnDefine> columns;

    private List<Collection<Object>> data = new ArrayList<>();

    public static DataPreviewVO of(ETLTableDO tableDO) {
        DataPreviewVO dataPreviewVO = new DataPreviewVO();
        List<MetaColumn> metaColumnList = tableDO.getMetaInfo().getColumns();
        List<ColumnDefine> columnList = new ArrayList<>(metaColumnList.size());
        for (MetaColumn metaColumn : metaColumnList) {
            ColumnDefine columnDefine = new ColumnDefine();
            int metaColumnIdx = metaColumn.getPosition() - 1;
            columnDefine.setId(metaColumn.getId());
            columnDefine.setName(metaColumn.getDisplayName());
            columnDefine.setType(metaColumn.getFlinkType());
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
