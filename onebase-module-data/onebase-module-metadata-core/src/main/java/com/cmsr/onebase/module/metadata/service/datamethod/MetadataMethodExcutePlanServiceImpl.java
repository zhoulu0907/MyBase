package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.common.exception.DatabaseAccessException;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataMethodExcutePlanDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataMethodExcutePlanRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 持久化查询计划（QueryPlan）服务实现
 *
 * 当底层表 metadata_method_excute_plan 尚未创建时，自动返回 null 以便上层回退到单表逻辑，避免接口报错。
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
@Slf4j
public class MetadataMethodExcutePlanServiceImpl implements MetadataMethodExcutePlanService {

    @Resource
    private MetadataMethodExcutePlanRepository repository;
    @Resource
    private MetadataDataSystemMethodService metadataDataSystemMethodService;

    /**
     * 根据方法编码获取启用的执行计划
     *
     * @param methodCode 方法编码
     * @return 启用的执行计划；如果底层表不存在或未初始化，返回 null 以便业务回退
     */
    @Override
    public MetadataMethodExcutePlanDO getEnabledByMethodCode(String methodCode) {
        try {
            // 解析 methodCode -> methodId
            com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataSystemMethodDO method =
                    metadataDataSystemMethodService.getDataMethodByCode(methodCode);
            if (method == null) {
                return null;
            }
            return repository.getEnabledByMethodId(method.getId());
        } catch (DatabaseAccessException ex) {
            if (isUndefinedPlanTable(ex)) {
                log.warn("metadata_method_excute_plan 表不存在，跳过计划读取并回退到单表执行。methodCode={}", methodCode);
                return null;
            }
            throw ex;
        }
    }

    @Override
    public MetadataMethodExcutePlanDO getEnabledByMethodId(Long methodId) {
        try {
            return repository.getEnabledByMethodId(methodId);
        } catch (DatabaseAccessException ex) {
            if (isUndefinedPlanTable(ex)) {
                log.warn("metadata_method_excute_plan 表不存在，跳过计划读取并回退到单表执行。methodId={}", methodId);
                return null;
            }
            throw ex;
        }
    }

    /**
     * 判断异常是否由于 metadata_method_excute_plan 表不存在导致
     *
     * @param ex DatabaseAccessException
     * @return true 表示表不存在
     */
    private boolean isUndefinedPlanTable(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("relation \"metadata_method_excute_plan\"") && lower.contains("does not exist")) {
                    return true;
                }
                if (lower.contains("metadata_method_excute_plan") && lower.contains("does not exist")) {
                    return true;
                }
                // PostgreSQL 未定义表 SQLState: 42P01，部分堆栈中可能包含
                if (lower.contains("42p01")) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }
}
