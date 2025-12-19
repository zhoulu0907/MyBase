package com.cmsr.onebase.plugin.demo.hello.processor;

import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.context.PluginContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据脱敏处理器示例
 * <p>
 * 演示如何实现数据处理器扩展点，对敏感数据进行脱敏处理
 * </p>
 *
 * @author OneBase Team
 * @date 2025-12-18
 */
public class DataMaskProcessor implements DataProcessor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String type() {
        return "DATA_MASK";
    }

    @Override
    public String description() {
        return "数据脱敏处理器，支持手机号、身份证、邮箱、银行卡等敏感数据脱敏";
    }

    @Override
    public Class<?> inputType() {
        return List.class;
    }

    @Override
    public Class<?> outputType() {
        return List.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object process(PluginContext ctx, Object input, Map<String, Object> config) throws Exception {
        List<Map<String, Object>> data = (List<Map<String, Object>>) input;
        
        // 获取需要脱敏的字段配置
        Map<String, String> maskFields = (Map<String, String>) config.getOrDefault("maskFields", new HashMap<>());
        
        // 是否记录处理日志
        boolean enableLog = Boolean.parseBoolean(String.valueOf(config.getOrDefault("enableLog", "false")));

        if (enableLog) {
            String userId = ctx.getUserId();
            System.out.printf("[%s] 用户 %s 正在进行数据脱敏处理，数据量: %d%n",
                    LocalDateTime.now().format(FORMATTER), userId, data.size());
        }

        return data.stream()
                .map(row -> maskRow(row, maskFields))
                .collect(Collectors.toList());
    }

    private Map<String, Object> maskRow(Map<String, Object> row, Map<String, String> maskFields) {
        Map<String, Object> result = new HashMap<>(row);
        
        for (Map.Entry<String, String> entry : maskFields.entrySet()) {
            String fieldName = entry.getKey();
            String maskType = entry.getValue();
            
            if (result.containsKey(fieldName) && result.get(fieldName) != null) {
                String value = String.valueOf(result.get(fieldName));
                result.put(fieldName, maskValue(value, maskType));
            }
        }
        
        return result;
    }

    private String maskValue(String value, String maskType) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return switch (maskType) {
            case "phone" -> maskPhone(value);
            case "idcard" -> maskIdCard(value);
            case "email" -> maskEmail(value);
            case "bankcard" -> maskBankCard(value);
            case "name" -> maskName(value);
            case "address" -> maskAddress(value);
            default -> value;
        };
    }

    /**
     * 手机号脱敏：138****1234
     */
    private String maskPhone(String phone) {
        if (phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 身份证脱敏：110***********1234
     */
    private String maskIdCard(String idCard) {
        if (idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 邮箱脱敏：t***@example.com
     */
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * 银行卡脱敏：6222 **** **** 1234
     */
    private String maskBankCard(String bankCard) {
        String card = bankCard.replaceAll("\\s", "");
        if (card.length() < 8) {
            return bankCard;
        }
        return card.substring(0, 4) + " **** **** " + card.substring(card.length() - 4);
    }

    /**
     * 姓名脱敏：张*三
     */
    private String maskName(String name) {
        if (name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 地址脱敏：北京市朝阳区***
     */
    private String maskAddress(String address) {
        if (address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "***";
    }
}
