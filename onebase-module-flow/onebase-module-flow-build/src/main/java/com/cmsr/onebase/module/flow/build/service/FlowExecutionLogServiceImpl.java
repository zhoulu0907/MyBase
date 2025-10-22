package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.vo.ExecutionLogVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowExecutionLogRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/10/21 15:53
 */
@Setter
@Service
public class FlowExecutionLogServiceImpl implements FlowExecutionLogService {

    @Autowired
    private FlowExecutionLogRepository flowExecutionLogRepository;

    @Autowired
    private FlowCommonService flowCommonService;

    @Override
    public PageResult<ExecutionLogVO> pageList(PageExecutionLogReqVO reqVO) {
        PageResult<FlowExecutionLogDO> pageResult = flowExecutionLogRepository.findPageByQuery(reqVO);
        List<ExecutionLogVO> voList = pageResult.getList().stream().map(logDO -> {
            ExecutionLogVO logVO = BeanUtils.toBean(logDO, ExecutionLogVO.class);
            logVO.setProcessName(flowCommonService.getProcessName(logDO.getProcessId()));
            logVO.setExecutionTime(toSecondsDouble(logDO.getDurationTime()));
            return logVO;
        }).toList();
        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public ExecutionLogVO getDetail(Long id) {
        FlowExecutionLogDO logDO = flowExecutionLogRepository.findById(id);
        if (logDO == null) {
            return null;
        }
        ExecutionLogVO logVO = BeanUtils.toBean(logDO, ExecutionLogVO.class);
        logVO.setProcessName(flowCommonService.getProcessName(logDO.getProcessId()));
        logVO.setExecutionTime(toSecondsDouble(logDO.getDurationTime()));
        return logVO;
    }

    @Override
    public Map<String, Object> statisticTody(Long applicationId) {
        Map<String, Object> result = flowExecutionLogRepository.statisticTodyByApplicationId(applicationId);
        double avgs = MapUtils.getDoubleValue(result, "avgs", 0);
        result.put("avgs", toSecondsDouble(avgs));
        return result;
    }

    private String toSecondsDouble(Long duration) {
        if (duration == null) {
            return "0.00";
        }
        double v = duration / 1_000_000_000.0;
        return String.format("%.2f", v);
    }

    private String toSecondsDouble(double duration) {
        double v = duration / 1_000_000_000.0;
        return String.format("%.2f", v);
    }
}
