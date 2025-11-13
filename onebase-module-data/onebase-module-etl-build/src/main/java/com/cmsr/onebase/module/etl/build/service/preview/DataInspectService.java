package com.cmsr.onebase.module.etl.build.service.preview;

import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.build.service.preview.vo.TablePreviewVO;

public interface DataInspectService {

    boolean testConnection(ETLDatasourceDO datasourceDO);

    DataPreview previewData(TablePreviewVO previewVO);

}
