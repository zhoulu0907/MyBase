package com.cmsr.onebase.module.bpm.runtime.listener;

import com.cmsr.onebase.module.bpm.api.enums.BpmBusinessStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.springframework.stereotype.Component;

/**
 * @author liyang
 */
@Slf4j
@Component
public class BpmGlobalListener implements GlobalListener {
    @Resource
    private TaskService taskService;

    @Resource
    private InsService insService;

    @Override
    public void start(ListenerVariable listenerVariable) {
        // 获取节点ext信息

        String ext = listenerVariable.getNode().getExt();
        log.info("开始启动流程，节点ext信息：{}", ext);

    }

    public void assignment(ListenerVariable listenerVariable) {
        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        log.info("开始分派任务，节点ext信息：{}", ext);
    }

    public void finish(ListenerVariable listenerVariable) {

        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        log.info("完成任务，节点ext信息：{}", ext);

        // 判断下，如果是最后一个节点就直接结束
        Instance instance = listenerVariable.getInstance();

        if (taskService.getByInsId(instance.getId()).size() == 0) {
            // 结束流程
            String status = BpmBusinessStatusEnum.APPROVED.getCode();
            // 更新流程状态为已通过
            instance.setFlowStatus(status);
            insService.updateById(instance);
        }
    }

    /**
     * 创建监听器，任务创建时执行
     *
     * @param listenerVariable 监听器变量
     */
    public void create(ListenerVariable listenerVariable) {

        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        log.info("开始创建任务，节点ext信息：{}", ext);
    }
}


