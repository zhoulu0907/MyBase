package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberResetLogRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberStateRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutoNumberStateBuildServiceImpl implements AutoNumberStateBuildService {

    @Resource
    private AutoNumberConfigBuildService configService;
    @Resource
    private MetadataAutoNumberStateRepository stateRepository;
    @Resource
    private MetadataAutoNumberResetLogRepository resetLogRepository;
    @Resource
    private com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberConfigRepository configRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long nextNumber(Long configId, java.time.LocalDateTime now) {
        // 先获取config对象以获取UUID
        MetadataAutoNumberConfigDO configObj = configRepository.getById(configId);
        if (configObj == null) {
            throw new IllegalStateException("自动编号配置不存在");
        }
        String configUuid = configObj.getConfigUuid();
        MetadataAutoNumberConfigDO cfg = configService.getByFieldId(configObj.getFieldUuid());
        // 使用新的枚举值：1-启用，0-禁用
        if (cfg == null || cfg.getIsEnabled() == null || !CommonStatusEnum.isEnabled(cfg.getIsEnabled())) {
            throw new IllegalStateException("自动编号未启用");
        }
        String periodKey = buildPeriodKey(cfg.getResetCycle(), now);

        // 简化实现：未加锁演示，实际可用版本号或数据库锁
        MetadataAutoNumberStateDO st = stateRepository.findOneByPeriod(configUuid, periodKey);
        if (st == null) {
            st = new MetadataAutoNumberStateDO();
            st.setConfigUuid(configUuid);
            st.setPeriodKey(periodKey);
            long start = cfg.getInitialValue() != null ? cfg.getInitialValue() : 1L;
            st.setCurrentValue(start - 1); // 初始化为初始值-1，后续+1再使用
            st.setApplicationId(cfg.getApplicationId());
            stateRepository.save(st);
        }
        long next = st.getCurrentValue() + 1;
        // 溢出控制（仅当 FIXED_DIGITS 且不继续递增时回绕）
        // 使用新的枚举值：1-是，0-否
        if ("FIXED_DIGITS".equalsIgnoreCase(cfg.getNumberMode()) && (cfg.getOverflowContinue() != null && !BooleanStatusEnum.isYes(cfg.getOverflowContinue())) && cfg.getDigitWidth() != null) {
            long max = (long) Math.pow(10, cfg.getDigitWidth());
            if (next >= max) {
                next = cfg.getInitialValue() != null ? cfg.getInitialValue() : 1L;
            }
        }
        MetadataAutoNumberStateDO upd = new MetadataAutoNumberStateDO();
        upd.setId(st.getId());
        upd.setCurrentValue(next);
        stateRepository.updateById(upd);
        return next;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long configId, String periodKey, long nextValue, Long operator, String reason) {
        // 先获取config对象以获取UUID
        MetadataAutoNumberConfigDO configObj = configRepository.getById(configId);
        if (configObj == null) {
            throw new IllegalStateException("自动编号配置不存在");
        }
        String configUuid = configObj.getConfigUuid();
        MetadataAutoNumberStateDO st = stateRepository.findOneByPeriod(configUuid, periodKey);
        Long prev = st != null ? st.getCurrentValue() : null;
        if (st == null) {
            st = new MetadataAutoNumberStateDO();
            st.setConfigUuid(configUuid);
            st.setPeriodKey(periodKey);
            st.setCurrentValue(nextValue);
            stateRepository.save(st);
        } else {
            MetadataAutoNumberStateDO upd = new MetadataAutoNumberStateDO();
            upd.setId(st.getId());
            upd.setCurrentValue(nextValue);
            stateRepository.updateById(upd);
        }
        MetadataAutoNumberResetLogDO log = new MetadataAutoNumberResetLogDO();
        log.setConfigId(configId); // ResetLogDO可能还没有改造UUID，使用configId
        log.setPeriodKey(periodKey);
        log.setPrevValue(prev);
        log.setNextValue(nextValue);
        log.setOperator(operator);
        log.setResetReason(reason);
        log.setApplicationId(st != null ? st.getApplicationId() : null);
        resetLogRepository.save(log);
    }

    private String buildPeriodKey(String cycle, java.time.LocalDateTime now) {
        if (cycle == null || "NONE".equalsIgnoreCase(cycle)) return "ALL";
        if ("DAILY".equalsIgnoreCase(cycle)) return now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        if ("MONTHLY".equalsIgnoreCase(cycle)) return now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        if ("YEARLY".equalsIgnoreCase(cycle)) return now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy"));
        return "ALL";
    }
}


