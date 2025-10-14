package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author：huangjie
 * @Date：2025/10/14 16:03
 */
@Data
public class FlowMessage {
    public static FlowMessage decode(ByteBuffer body) {
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
            // 使用Jackson解析JSON字符串为对象
            FlowMessage message = JsonUtils.parseObject(jsonString, FlowMessage.class);
            // 重置position以便其他操作可以重新读取
            body.position(position);
            return message;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse FlowEvent from JSON ByteBuffer", e);
        }
    }

    private Long processId;

    private String tag;

    private String time;
}
