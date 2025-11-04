package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.module.etl.core.vo.datasource.DataPreviewVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.TablePreviewVO;

public interface DataInspectService {
    DataPreviewVO previewData(TablePreviewVO previewVO);
}
