package com.cmsr.onebase.module.system.mq.tiangong.consumer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${rocketmq.topic.resource-sync}",
        selectorExpression = "${rocketmq.tag.project-lifecycle}",
        consumerGroup = "${rocketmq.consumer.group-ai}"
)
public class engineCapabilityConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("==================== 收到天工平台资源同步消息 ====================");
        log.info("消息内容：{}", message);

        try {
            // 1. 解析消息（按天工协议）
            Map<String, Object> msgMap = JSONUtil.toBean(message, Map.class);
            String resource = msgMap.get("resource").toString();
            String action = msgMap.get("action").toString();
            Map<String, Object> payload = (Map<String, Object>) msgMap.get("payload");

            log.info("资源类型：{}", resource);
            log.info("操作动作：{}", action);
            log.info("业务数据：{}", payload);

            // 2. 只处理 project 资源（文档规定）
            if (!"project".equals(resource)) {
                log.info("非 project 资源，忽略");
                return;
            }

            switch (action) {
                case "create":
                    handleCreate(payload);
                    break;
                case "update":
                    handleUpdate(payload);
                    break;
                case "delete":
                    handleDelete(payload);
                    break;
                case "add_members":
                    handleAddMembers(payload);
                    break;
                case "del_members":
                    handleDelMembers(payload);
                    break;
                default:
                    log.info("未知动作：{}，忽略", action);
            }

            log.info("==================== 消息处理完成 ====================");

        } catch (Exception e) {
            log.error("消息处理异常，即将触发重试：{}", message, e);

            // 👇 关键：把异常抛出去，让RocketMQ知道消费失败了
            throw new RuntimeException("业务处理失败，触发MQ重试", e);
        }
    }

    // -------------------- 业务处理方法（按文档协议实现） --------------------
    private void handleCreate(Map<String, Object> payload) {
        String code = getAndCheck(payload, "code");
        String name = getAndCheck(payload, "name");
        String tenantId = getAndCheck(payload, "tenantId");
        Map<String, Object> meta = (Map<String, Object>) payload.get("meta");

        log.info("【创建项目】code={}, name={}, tenantId={}", code, name, tenantId);
        // 这里写你的创建项目业务逻辑
    }

    private void handleUpdate(Map<String, Object> payload) {
        String code = getAndCheck(payload, "code");
        String name = payload.get("name") != null ? payload.get("name").toString() : null;
        String tenantId = getAndCheck(payload, "tenantId");

        log.info("【更新项目】code={}, name={}, tenantId={}", code, name, tenantId);
        // 这里写你的更新项目业务逻辑
    }

    private void handleDelete(Map<String, Object> payload) {
        String code = getAndCheck(payload, "code");
        String tenantId = getAndCheck(payload, "tenantId");

        log.info("【删除项目】code={}, tenantId={}", code, tenantId);
        // 这里写你的删除项目业务逻辑
    }

    private void handleAddMembers(Map<String, Object> payload) {
        String code = getAndCheck(payload, "code");
        String tenantId = getAndCheck(payload, "tenantId");
        List<Map<String, Object>> members = (List<Map<String, Object>>) payload.get("members");
        if (members == null || members.isEmpty()) {
            log.warn("【添加成员】成员列表为空，忽略处理");
            return;
        }

        log.info("【添加成员】code={}, members={}", code, members);
        // 这里写你的添加成员业务逻辑
    }

    private void handleDelMembers(Map<String, Object> payload) {
        String code = getAndCheck(payload, "code");
        String tenantId = getAndCheck(payload, "tenantId");
        List<Map<String, Object>> members = (List<Map<String, Object>>) payload.get("members");
        if (members == null || members.isEmpty()) {
            log.warn("【删除成员】成员列表为空，忽略处理");
            return;
        }

        log.info("【删除成员】code={}, members={}", code, members);
        // 这里写你的删除成员业务逻辑
    }

    private String getAndCheck(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("消息体缺少必填字段：" + key);
        }
        return value.toString();
    }
}
