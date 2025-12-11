package com.cmsr.api.dataset.union;

import com.cmsr.api.chart.dto.DeSortField;
import com.cmsr.api.dataset.dto.DatasetNodeDTO;
import com.cmsr.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Junjun
 */
@Data
public class DatasetGroupInfoDTO extends DatasetNodeDTO {

    private List<UnionDTO> union;// 关联数据集

    private List<DeSortField> sortFields;// 自定义排序（如仪表板查询组件）

    private Map<String, List> data;

    private List<DatasetTableFieldDTO> allFields;

    private String sql;

    private Long total;

    private String creator;

    private String updater;

}
