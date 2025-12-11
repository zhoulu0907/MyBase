package com.cmsr.api.dataset.union;

import com.cmsr.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author gin
 */
@Data
public class UnionItemDTO implements Serializable {
    private DatasetTableFieldDTO parentField;
    private DatasetTableFieldDTO currentField;
}
