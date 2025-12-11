package com.cmsr.api.dataset.union;

import com.cmsr.extensions.datasource.dto.DatasetTableDTO;
import com.cmsr.extensions.datasource.model.SQLObj;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author gin
 */
@Data
public class UnionParamDTO implements Serializable {
    private String unionType;
    private List<UnionItemDTO> unionFields;
    private DatasetTableDTO parentDs;
    private DatasetTableDTO currentDs;
    private SQLObj parentSQLObj;
    private SQLObj currentSQLObj;
}
