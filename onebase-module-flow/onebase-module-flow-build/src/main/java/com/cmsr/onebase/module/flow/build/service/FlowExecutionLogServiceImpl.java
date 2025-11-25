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

import java.time.LocalDateTime;
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
            logDO.setLogText(null);
            logDO.setErrorMessage(null);
            ExecutionLogVO logVO = BeanUtils.toBean(logDO, ExecutionLogVO.class);
            logVO.setProcessName(flowCommonService.getProcessName(logDO.getProcessId()));
            logVO.setExecutionTime(millisToSecondsDouble(logDO.getDurationTime()));
            return logVO;
        }).toList();
        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public ExecutionLogVO getDetail(Long id) {
        FlowExecutionLogDO logDO = flowExecutionLogRepository.getById(id);
        if (logDO == null) {
            return null;
        }
        ExecutionLogVO logVO = BeanUtils.toBean(logDO, ExecutionLogVO.class);
        logVO.setProcessName(flowCommonService.getProcessName(logDO.getProcessId()));
        logVO.setExecutionTime(millisToSecondsDouble(logDO.getDurationTime()));
        return logVO;
    }

    @Override
    public Map<String, Object> statisticTody(Long applicationId) {
        // 统计今天的数据
        LocalDateTime today = LocalDateTime.now();
        Map<String, Object> result = flowExecutionLogRepository.statisticByApplicationId(today, applicationId);
        double todayAvgs = MapUtils.getDoubleValue(result, "avgs", 0);
        result.put("avgs", millisToSecondsDouble(todayAvgs));

        // 统计昨天的数据
        LocalDateTime yesterday = today.minusDays(1);
        Map<String, Object> yesterdayResult = flowExecutionLogRepository.statisticByApplicationId(yesterday, applicationId);
        double yesterdayAvgs = MapUtils.getDoubleValue(yesterdayResult, "avgs", 0);

        // 将昨天的数据添加到结果中，添加前缀区分
        result.put("yesterdayTotal", MapUtils.getInteger(yesterdayResult, "total", 0));
        result.put("yesterdaySuccess", MapUtils.getInteger(yesterdayResult, "success", 0));
        result.put("yesterdayFailed", MapUtils.getInteger(yesterdayResult, "failed", 0));
        result.put("yesterdayAvgs", millisToSecondsDouble(yesterdayAvgs));

        // 计算环比值
        int todayTotal = MapUtils.getInteger(result, "total", 0);
        int yesterdayTotal = MapUtils.getInteger(yesterdayResult, "total", 0);
        int todaySuccess = MapUtils.getInteger(result, "success", 0);
        int yesterdaySuccess = MapUtils.getInteger(yesterdayResult, "success", 0);
        int todayFailed = MapUtils.getInteger(result, "failed", 0);
        int yesterdayFailed = MapUtils.getInteger(yesterdayResult, "failed", 0);

        // 计算环比百分比
        result.put("compareTotal", calculatePercentage(todayTotal, yesterdayTotal));
        result.put("compareSuccess", calculatePercentage(todaySuccess, yesterdaySuccess));
        result.put("compareFailed", calculatePercentage(todayFailed, yesterdayFailed));
        result.put("compareAvgs", calculatePercentage(todayAvgs, yesterdayAvgs));

        return result;
    }

    /**
     * 计算环比百分比
     *
     * @param current  当前值
     * @param previous 对比值
     * @return 环比百分比，格式为字符串（带%符号）
     */
    private String calculatePercentage(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? "100.00" : "0.00";
        }
        double percentage = ((current - previous) / previous) * 100;
        return String.format("%.2f", percentage);
    }

    /**
     * 计算环比百分比（整数版本）
     *
     * @param current  当前值
     * @param previous 对比值
     * @return 环比百分比，格式为字符串（带%符号）
     */
    private String calculatePercentage(int current, int previous) {
        return calculatePercentage((double) current, (double) previous);
    }

    private String millisToSecondsDouble(Long duration) {
        if (duration == null) {
            return "0.00";
        }
        double v = duration / 1_000.0;
        return String.format("%.2f", v);
    }

    private String millisToSecondsDouble(double duration) {
        double v = duration / 1_000.0;
        return String.format("%.2f", v);
    }
}
