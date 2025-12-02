package com.cmsr.onebase.module.metadata.core.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberResetLogRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberStateRepository;
import com.cmsr.onebase.module.metadata.core.enums.NumberModeEnum;
import com.cmsr.onebase.module.metadata.core.enums.ResetCycleEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 自动编号状态管理器
 * 负责序号状态的维护和并发控制
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Component
public class AutoNumberStateManager {

    @Resource
    private MetadataAutoNumberStateRepository stateRepository;

    @Resource
    private MetadataAutoNumberResetLogRepository resetLogRepository;

    /**
     * 获取下一个序号（带并发控制）
     *
     * @param config    自动编号配置
     * @param periodKey 周期键
     * @return 下一个序号
     */
    @Transactional(rollbackFor = Exception.class)
    public Long getNextSequence(MetadataAutoNumberConfigDO config, String periodKey) {
        // 使用行锁查询当前状态
        MetadataAutoNumberStateDO state = stateRepository.selectByConfigIdAndPeriodKeyForUpdate(config.getId(), periodKey);

        // 如果状态不存在，创建初始状态
        if (state == null) {
            state = createInitialState(config, periodKey);
        }

        // 检查是否需要重置
        if (needReset(config, state, periodKey)) {
            resetSequence(config, state, periodKey, "周期自动重置", null);
            // 重新获取状态
            state = stateRepository.selectByConfigIdAndPeriodKeyForUpdate(config.getId(), periodKey);
        }

        // 获取下一个序号
        Long nextValue = state.getCurrentValue() + 1;

        // 检查序号溢出
        checkSequenceOverflow(config, nextValue);

        // 更新状态
        state.setCurrentValue(nextValue);
        state.setUpdateTime(LocalDateTime.now());
        stateRepository.updateById(state);

        log.debug("Generated next sequence: {} for config: {}, periodKey: {}", nextValue, config.getId(), periodKey);
        return nextValue;
    }

    /**
     * 重置序号状态
     *
     * @param configId    配置ID
     * @param periodKey   周期键
     * @param resetReason 重置原因
     * @param operator    操作者ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetSequence(Long configId, String periodKey, String resetReason, Long operator) {
        // 获取当前状态
        MetadataAutoNumberStateDO state = stateRepository.selectByConfigIdAndPeriodKey(configId, periodKey);
        if (state == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "序号状态不存在");
        }

        // 重置序号
        resetSequence(null, state, periodKey, resetReason, operator);
    }

    /**
     * 检查是否需要重置
     *
     * @param config          自动编号配置
     * @param state           当前状态
     * @param currentPeriodKey 当前周期键
     * @return 是否需要重置
     */
    public boolean needReset(MetadataAutoNumberConfigDO config, MetadataAutoNumberStateDO state, String currentPeriodKey) {
        // 如果是不重置模式，直接返回false
        if (ResetCycleEnum.NEVER.getCode().equals(config.getResetCycle())) {
            return false;
        }

        // 如果周期键不匹配，说明需要重置
        return !currentPeriodKey.equals(state.getPeriodKey());
    }

    /**
     * 创建初始状态
     *
     * @param config    自动编号配置
     * @param periodKey 周期键
     * @return 初始状态
     */
    private MetadataAutoNumberStateDO createInitialState(MetadataAutoNumberConfigDO config, String periodKey) {
        MetadataAutoNumberStateDO state = new MetadataAutoNumberStateDO();
        state.setConfigId(config.getId());
        state.setPeriodKey(periodKey);
        state.setCurrentValue(config.getInitialValue() - 1);
        state.setLastResetTime(LocalDateTime.now());
        state.setApplicationId(config.getApplicationId());

        stateRepository.save(state);
        log.debug("Created initial state for config: {}, periodKey: {}", config.getId(), periodKey);
        return state;
    }

    /**
     * 重置序号状态
     *
     * @param config      自动编号配置
     * @param state       当前状态
     * @param periodKey   周期键
     * @param resetReason 重置原因
     * @param operator    操作者ID
     */
    private void resetSequence(MetadataAutoNumberConfigDO config, MetadataAutoNumberStateDO state, 
                              String periodKey, String resetReason, Long operator) {
        Long prevValue = state.getCurrentValue();
        Long nextValue = (config != null ? config.getInitialValue() : 1L) - 1; // 减1，因为获取下一个序号时会加1

        // 更新状态
        state.setPeriodKey(periodKey);
        state.setCurrentValue(nextValue);
        state.setLastResetTime(LocalDateTime.now());
        state.setUpdateTime(LocalDateTime.now());
        stateRepository.updateById(state);

        // 记录重置日志
        MetadataAutoNumberResetLogDO resetLog = new MetadataAutoNumberResetLogDO();
        resetLog.setConfigId(state.getConfigId());
        resetLog.setPeriodKey(periodKey);
        resetLog.setPrevValue(prevValue);
        resetLog.setNextValue(nextValue + 1);
        resetLog.setResetReason(resetReason);
        resetLog.setOperator(operator);
        resetLog.setResetTime(LocalDateTime.now());
        resetLog.setApplicationId(state.getApplicationId());

        resetLogRepository.save(resetLog);
        log.info("Reset sequence for config: {}, periodKey: {}, from: {} to: {}, reason: {}", 
                state.getConfigId(), periodKey, prevValue, nextValue + 1, resetReason);
    }

    /**
     * 检查序号溢出
     *
     * @param config    自动编号配置
     * @param nextValue 下一个序号值
     */
    private void checkSequenceOverflow(MetadataAutoNumberConfigDO config, Long nextValue) {
        // 如果是自然数编号，不检查溢出
        if (NumberModeEnum.NATURAL.getCode().equals(config.getNumberMode())) {
            return;
        }

        // 如果是指定位数编号，检查是否溢出
        if (NumberModeEnum.FIXED_DIGIT.getCode().equals(config.getNumberMode())) {
            long maxValue = (long) Math.pow(10, config.getDigitWidth()) - 1;
            
            if (nextValue > maxValue) {
                // 如果允许溢出继续，则不抛异常
                if (config.getOverflowContinue() != null && config.getOverflowContinue() == 1) {
                    log.warn("Sequence overflow but continue: config: {}, nextValue: {}, maxValue: {}", 
                            config.getId(), nextValue, maxValue);
                    return;
                }
                
                // 否则抛出溢出异常
                throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                        String.format("序号已达到最大值 %d，无法继续生成", maxValue));
            }
        }
    }
}
