package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.module.etl.core.vo.DataPreviewVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLTablePreviewVO;

public interface DataInspectService {
    DataPreviewVO previewData(ETLTablePreviewVO previewVO);
}
