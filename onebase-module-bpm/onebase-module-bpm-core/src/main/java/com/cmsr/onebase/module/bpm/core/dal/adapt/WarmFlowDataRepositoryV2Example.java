package com.cmsr.onebase.module.bpm.core.dal.adapt;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowInstanceDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WarmFlowDataRepositoryV2 使用示例
 *
 * @author liyang
 * @date 2025-01-27
 */
@Slf4j
@Component
public class WarmFlowDataRepositoryV2Example {

    // 流程实例 Repository
    private final WarmFlowDataRepositoryV2<BpmFlowInstanceDO> flowInstanceRepository;

    // 流程任务 Repository
    private final WarmFlowDataRepositoryV2<BpmFlowTaskDO> flowTaskRepository;

    public WarmFlowDataRepositoryV2Example() {
        this.flowInstanceRepository = new WarmFlowDataRepositoryV2<>(BpmFlowInstanceDO.class);
        this.flowTaskRepository = new WarmFlowDataRepositoryV2<>(BpmFlowTaskDO.class);
    }

    /**
     * 示例：基本的 CRUD 操作
     */
    public void basicCrudExample() {
        // 1. 插入
        BpmFlowInstanceDO instance = new BpmFlowInstanceDO();
        instance.setDefinitionId(1L);
        instance.setBusinessId("BIZ001");
        instance.setFlowStatus("RUNNING");
        BpmFlowInstanceDO savedInstance = flowInstanceRepository.insert(instance);
        log.info("插入流程实例: {}", savedInstance.getId());

        // 2. 查询
        BpmFlowInstanceDO foundInstance = flowInstanceRepository.findById(savedInstance.getId());
        log.info("查询流程实例: {}", foundInstance.getBusinessId());

        // 3. 更新
        foundInstance.setFlowStatus("COMPLETED");
        BpmFlowInstanceDO updatedInstance = flowInstanceRepository.update(foundInstance);
        log.info("更新流程实例状态: {}", updatedInstance.getFlowStatus());

        // 4. 删除
        long deletedCount = flowInstanceRepository.deleteById(updatedInstance.getId());
        log.info("删除流程实例数量: {}", deletedCount);
    }

    /**
     * 示例：条件查询
     */
    public void conditionalQueryExample() {
        // 1. 根据定义ID查询流程实例
        DefaultConfigStore config = flowInstanceRepository.createDefaultConfig();
        config.eq("definition_id", 1L);
        config.eq("flow_status", "RUNNING");

        List<BpmFlowInstanceDO> runningInstances = flowInstanceRepository.findAllByConfig(config);
        log.info("运行中的流程实例数量: {}", runningInstances.size());

        // 2. 分页查询
        config.like("business_id", "BIZ");
        var pageResult = flowInstanceRepository.findPage(config, 1, 10);
        log.info("分页查询结果: 总数={}, 当前页数据={}", pageResult.getTotal(), pageResult.getList().size());

        // 3. 带排序的分页查询
        var pageResultWithOrder = flowInstanceRepository.findPageWithOrder(
            config, 1, 10, "create_time", false);
        log.info("带排序的分页查询结果: {}", pageResultWithOrder.getList().size());
    }

    /**
     * 示例：批量操作
     */
    public void batchOperationExample() {
        // 1. 批量插入
        List<BpmFlowTaskDO> tasks = List.of(
            createTask(1L, "TASK001", "审批任务1"),
            createTask(1L, "TASK002", "审批任务2"),
            createTask(1L, "TASK003", "审批任务3")
        );

        List<BpmFlowTaskDO> savedTasks = flowTaskRepository.insertBatch(tasks);
        log.info("批量插入任务数量: {}", savedTasks.size());

        // 2. 批量更新
        savedTasks.forEach(task -> task.setFlowStatus("COMPLETED"));
        List<BpmFlowTaskDO> updatedTasks = flowTaskRepository.updateBatch(savedTasks);
        log.info("批量更新任务数量: {}", updatedTasks.size());

        // 3. 批量删除
        List<Long> taskIds = updatedTasks.stream()
            .map(BpmFlowTaskDO::getId)
            .toList();
        long deletedCount = flowTaskRepository.deleteByIds(taskIds);
        log.info("批量删除任务数量: {}", deletedCount);
    }

    /**
     * 示例：WarmFlow 特定功能
     */
    public void warmFlowSpecificExample() {
        // 1. 条件更新
        DefaultConfigStore updateConfig = flowInstanceRepository.createDefaultConfig();
        updateConfig.eq("flow_status", "RUNNING");

        // 使用 DataRow 进行条件更新
        org.anyline.entity.DataRow updateData = new org.anyline.entity.DataRow();
        updateData.put("flow_status", "SUSPENDED");
        updateData.put("update_time", new java.util.Date());

        long updatedCount = flowInstanceRepository.updateByConfig(updateData, updateConfig);
        log.info("条件更新流程实例数量: {}", updatedCount);

        // 2. 条件删除
        DefaultConfigStore deleteConfig = flowInstanceRepository.createDefaultConfig();
        deleteConfig.eq("flow_status", "CANCELLED");
        deleteConfig.lt("create_time", new java.util.Date(System.currentTimeMillis() - 86400000)); // 1天前

        long deletedCount = flowInstanceRepository.deleteByConfig(deleteConfig);
        log.info("条件删除流程实例数量: {}", deletedCount);

        // 3. 存在性检查
        DefaultConfigStore existsConfig = flowInstanceRepository.createDefaultConfig();
        existsConfig.eq("business_id", "BIZ001");

        boolean exists = flowInstanceRepository.existsByConfig(existsConfig);
        log.info("业务ID为BIZ001的流程实例是否存在: {}", exists);

        // 4. 分组统计
        DefaultConfigStore groupConfig = flowInstanceRepository.createDefaultConfig();
        groupConfig.group("flow_status");

        var groupResult = flowInstanceRepository.countByConfigWithGroup(groupConfig, "flow_status");
        log.info("按状态分组的统计结果: {}", groupResult.size());
    }

    /**
     * 示例：高级查询
     */
    public void advancedQueryExample() {
        // 1. 复杂条件查询
        DefaultConfigStore config = flowInstanceRepository.createDefaultConfig();
        config.eq("definition_id", 1L);
        config.in("flow_status", List.of("RUNNING", "SUSPENDED"));
        config.like("business_id", "BIZ");
        config.ge("create_time", new java.util.Date(System.currentTimeMillis() - 86400000)); // 1天内
        config.order("create_time", false); // 按创建时间降序

        List<BpmFlowInstanceDO> instances = flowInstanceRepository.findAllByConfig(config);
        log.info("复杂条件查询结果数量: {}", instances.size());

        // 2. 统计查询
        DefaultConfigStore countConfig = flowInstanceRepository.createDefaultConfig();
        countConfig.eq("definition_id", 1L);

        long totalCount = flowInstanceRepository.countByConfig(countConfig);
        log.info("定义ID为1的流程实例总数: {}", totalCount);

        // 3. 单条记录查询
        DefaultConfigStore oneConfig = flowInstanceRepository.createDefaultConfig();
        oneConfig.eq("business_id", "BIZ001");
        oneConfig.eq("flow_status", "RUNNING");

        var instance = flowInstanceRepository.findOneOptional(oneConfig);
        instance.ifPresentOrElse(
            inst -> log.info("找到运行中的流程实例: {}", inst.getId()),
            () -> log.info("未找到符合条件的流程实例")
        );
    }

    /**
     * 创建任务对象的辅助方法
     */
    private BpmFlowTaskDO createTask(Long definitionId, String nodeCode, String nodeName) {
        BpmFlowTaskDO task = new BpmFlowTaskDO();
        task.setDefinitionId(definitionId);
        task.setInstanceId(1L);
        task.setNodeCode(nodeCode);
        task.setNodeName(nodeName);
        task.setNodeType(1);
        task.setFlowStatus("PENDING");
        return task;
    }
}
