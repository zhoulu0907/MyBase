package com.cmsr.onebase.module.bpm.runtime.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.springframework.stereotype.Component;

/**
 * @author liyang
 */
@Slf4j
@Component
public class BpmGlobalListener implements GlobalListener {
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


