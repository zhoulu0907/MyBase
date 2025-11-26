package com.cmsr.onebase.module.etl.build.service.preview;

import com.cmsr.onebase.module.etl.build.vo.datasource.TestConnectionVO;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.build.vo.preview.TablePreviewVO;

public interface DataInspectService {

    boolean testConnection(TestConnectionVO pingVO);

    DataPreview previewData(TablePreviewVO previewVO);

}
