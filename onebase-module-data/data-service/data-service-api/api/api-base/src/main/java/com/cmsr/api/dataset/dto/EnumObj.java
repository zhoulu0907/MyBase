package com.cmsr.api.dataset.dto;

import com.cmsr.api.dataset.union.DatasetGroupInfoDTO;
import com.cmsr.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

/**
 * @Author Junjun
 */
@Data
public class EnumObj {
    private DatasetTableFieldDTO field;
    private DatasetGroupInfoDTO dataset;
}
