package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;

public interface ETLService {

    PageResult<ETLBriefVO> getETLPage(ETLPageReqVO pageReqVO);

    ETLDetailVO getETLDetailInfo();

    Long createETL(ETLDetailVO createVO);

    void updateETL(ETLDetailVO updateVO);

    void deleteETL(Long etlId);

    void startETLManually(Long etlId);

    void configScheduleStrategy(ETLScheduleVO scheduleVO);

    void queryRunLogs(Long etlId);

    PageResult<ETLInstanceLogVO> previewOutputData(Long id);
}
