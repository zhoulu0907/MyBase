package com.cmsr.api.dataset.vo;

import com.cmsr.api.dataset.dto.DatasetNodeDTO;
import com.cmsr.model.ITreeBase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DatasetTreeNodeVO extends DatasetNodeDTO implements Serializable, ITreeBase<DatasetTreeNodeVO> {

    private List<DatasetTreeNodeVO> children;

    private Boolean leaf;

}
