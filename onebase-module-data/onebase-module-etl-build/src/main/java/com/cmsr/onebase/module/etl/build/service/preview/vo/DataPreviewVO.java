package com.cmsr.onebase.module.etl.build.service.preview.vo;

import com.cmsr.onebase.module.etl.build.service.datasource.vo.ColumnDefine;
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

    public DataPreviewVO appendData(DataSet dataSet) {
        List<DataRow> rows = dataSet.getRows();
        rows.forEach(row -> data.add(row.values()));

        return this;
    }
}
