package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author：huangjie
 * @Date：2025/10/10 12:44
 */
@Data
public class FlowEvent {

    public static FlowEvent decode(ByteBuffer body) {
        if (body == null || body.remaining() <= 0) {
            throw new IllegalArgumentException("ByteBuffer cannot be null or empty");
        }
        try {
            // 保存当前position以便重置
            int position = body.position();
            // 从ByteBuffer中读取字节数组
            byte[] bytes = new byte[body.remaining()];
            body.get(bytes);
            // 将字节数组转换为JSON字符串
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            // 使用Jackson解析JSON字符串为FlowEvent对象
            FlowEvent event = JsonUtils.parseObject(jsonString, FlowEvent.class);
            // 重置position以便其他操作可以重新读取
            body.position(position);
            return event;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse FlowEvent from JSON ByteBuffer", e);
        }
    }

    public static byte[] encode(String type, Long processId) {
        FlowEvent event = new FlowEvent();
        event.setType(type);
        event.setProcessId(processId);
        return JsonUtils.toJsonByte(event);
    }

    public static final String UPDATE = "update";

    public static final String DELETE = "delete";

    private String type;

    private Long processId;


}
