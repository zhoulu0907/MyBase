package com.cmsr.onebase.module.metadata.core.service.datamethod.engine;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 多表写入引擎 - 简化版本，使用基础数据类型，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Component
@Slf4j
public class MultiTableWriteEngine {

    /**
     * 主表创建后处理
     *
     * @param methodCode 方法编码
     * @param planJson 写入计划JSON
     * @param entity 实体信息
     * @param primaryKeyValue 主键值
     * @param inputData 输入数据
     */
    public void afterPrimaryCreate(String methodCode, String planJson, MetadataBusinessEntityDO entity,
                                   Object primaryKeyValue, Map<String, Object> inputData) {
        try {
            log.info("开始处理主表创建后的子表写入，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue);

            // TODO: 实现具体的子表写入逻辑
            handleChildTableWrites(planJson, entity, primaryKeyValue, inputData, "CREATE");

            log.info("主表创建后的子表写入完成");

        } catch (Exception e) {
            log.error("主表创建后的子表写入失败，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue, e);
        }
    }

    /**
     * 主表更新后处理
     *
     * @param methodCode 方法编码
     * @param planJson 写入计划JSON
     * @param entity 实体信息
     * @param primaryKeyValue 主键值
     * @param inputData 输入数据
     */
    public void afterPrimaryUpdate(String methodCode, String planJson, MetadataBusinessEntityDO entity,
                                   Object primaryKeyValue, Map<String, Object> inputData) {
        try {
            log.info("开始处理主表更新后的子表写入，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue);

            // TODO: 实现具体的子表更新逻辑
            handleChildTableWrites(planJson, entity, primaryKeyValue, inputData, "UPDATE");

            log.info("主表更新后的子表写入完成");

        } catch (Exception e) {
            log.error("主表更新后的子表写入失败，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue, e);
        }
    }

    /**
     * 主表删除后处理
     *
     * @param methodCode 方法编码
     * @param planJson 写入计划JSON
     * @param entity 实体信息
     * @param primaryKeyValue 主键值
     */
    public void afterPrimaryDelete(String methodCode, String planJson, MetadataBusinessEntityDO entity,
                                   Object primaryKeyValue) {
        try {
            log.info("开始处理主表删除后的子表处理，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue);

            // TODO: 实现具体的子表删除逻辑
            handleChildTableWrites(planJson, entity, primaryKeyValue, null, "DELETE");

            log.info("主表删除后的子表处理完成");

        } catch (Exception e) {
            log.error("主表删除后的子表处理失败，方法编码: {}, 实体: {}, 主键值: {}",
                    methodCode, entity.getDisplayName(), primaryKeyValue, e);
        }
    }

    /**
     * 处理子表写入
     */
    private void handleChildTableWrites(String planJson, MetadataBusinessEntityDO entity,
                                       Object primaryKeyValue, Map<String, Object> inputData, String operation) {
        // TODO: 实现具体的子表写入逻辑
        log.info("处理子表写入，操作类型: {}, 主键值: {}", operation, primaryKeyValue);

        // 这里应该根据planJson中的配置，处理相关的子表操作
        // 由于这是复杂的业务逻辑，暂时留空，等待具体需求
    }
}
