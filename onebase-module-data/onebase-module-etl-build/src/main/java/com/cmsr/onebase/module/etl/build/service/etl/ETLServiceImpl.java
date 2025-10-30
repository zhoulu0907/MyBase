package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import org.springframework.stereotype.Service;

@Service
public class ETLServiceImpl implements ETLService {
    @Override
    public PageResult<ETLBriefVO> getETLPage(ETLPageReqVO pageReqVO) {
        return null;
    }

    @Override
    public ETLDetailVO getETLDetailInfo() {
        return null;
    }

    @Override
    public Long createETL(ETLDetailVO createVO) {
        return 0L;
    }

    @Override
    public void updateETL(ETLDetailVO updateVO) {

    }

    @Override
    public void deleteETL(Long etlId) {

    }

    @Override
    public void startETLManually(Long etlId) {

    }

    @Override
    public void configScheduleStrategy(ETLScheduleVO scheduleVO) {

    }

    @Override
    public void queryRunLogs(Long etlId) {

    }

    @Override
    public PageResult<ETLInstanceLogVO> previewOutputData(Long id) {
        return null;
    }
}
