package com.cmsr.onebase.module.metadata.build.service.number;

public interface AutoNumberStateBuildService {
    /** 获取下一计数值并自增（按周期） */
    long nextNumber(Long configId, java.time.LocalDateTime now);

    /** 手动重置到指定下一值 */
    void reset(Long configId, String periodKey, long nextValue, Long operator, String reason);
}


