package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.cmsr.onebase.module.flow.component.external.connector.AbstractConnector;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorExecutor;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 163邮箱连接器实现
 * 支持文本和HTML邮件发送
 *
 * @author zhoulu
 * @since 2025-01-10
 */
@Slf4j
@Component
public class Email163Connector extends AbstractConnector {

    @Override
    public String getConnectorType() {
        return "EMAIL_163";
    }

    @Override
    public String getConnectorName() {
        return "163邮箱连接器";
    }

    @Override
    public String getConnectorDescription() {
        return "163邮箱发送连接器，支持文本和HTML邮件发送";
    }

    @Override
    protected Map<String, Object> doExecute(String actionType, Map<String, Object> config) throws Exception {
        // 1. 创建邮件会话
        Session session = createMailSession(config);

        // 2. 创建并发送邮件消息
        Message message = createMessage(session, config);

        try {
            Transport.send(message);
            log.info("163邮件发送成功");
        } catch (MessagingException e) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "邮件发送失败", e);
        }

        // 3. 返回执行结果
        return buildSuccessResult("邮件发送成功");
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        return config != null &&
               config.containsKey("smtpHost") &&
               config.containsKey("smtpPort") &&
               config.containsKey("username") &&
               config.containsKey("password");
    }

    /**
     * 创建邮件会话
     */
    private Session createMailSession(Map<String, Object> config) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.get("smtpHost"));
        props.put("mail.smtp.port", config.get("smtpPort"));
        props.put("mail.smtp.ssl.enable", config.getOrDefault("useSSL", true));
        props.put("mail.smtp.timeout", config.getOrDefault("timeout", 30000));
        props.put("mail.smtp.connectiontimeout", config.getOrDefault("connectionTimeout", 10000));
        props.put("mail.smtp.auth", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.get("username").toString(),
                    config.get("password").toString()
                );
            }
        });
    }

    /**
     * 创建邮件消息
     */
    private Message createMessage(Session session, Map<String, Object> config) throws Exception {
        Message message = new MimeMessage(session);

        // 设置发件人
        String from = config.getOrDefault("from", "noreply@163.com").toString();
        message.setFrom(new InternetAddress(from));

        // 设置收件人（从 inputData 中获取）
        Object inputData = config.get("inputData");
        if (inputData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = (Map<String, Object>) inputData;

            Object toObj = inputMap.get("to");
            if (toObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> toList = (List<String>) toObj;
                InternetAddress[] toAddresses = new InternetAddress[toList.size()];
                for (int i = 0; i < toList.size(); i++) {
                    toAddresses[i] = new InternetAddress(toList.get(i));
                }
                message.setRecipients(Message.RecipientType.TO, toAddresses);
            } else if (toObj instanceof String) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toObj.toString()));
            }

            log.info("开始发送163邮件，收件人: {}, 主题: {}", inputMap.get("to"), inputMap.get("subject"));

            // 设置主题
            String subject = inputMap.getOrDefault("subject", "无主题").toString();
            message.setSubject(subject);

            // 设置内容
            String content = inputMap.getOrDefault("content", "").toString();
            String templateType = config.getOrDefault("templateType", "TEXT").toString();
            String charset = config.getOrDefault("charset", "UTF-8").toString();

            if ("HTML".equals(templateType)) {
                message.setContent(content, "text/html; charset=" + charset);
            } else {
                message.setText(content);
            }
        }

        // 设置优先级
        Integer priority = (Integer) config.get("priority");
        if (priority != null) {
            message.setHeader("X-Priority", priority.toString());
        }

        // 设置回执请求
        Boolean requireReadReceipt = (Boolean) config.get("requireReadReceipt");
        if (Boolean.TRUE.equals(requireReadReceipt)) {
            message.setHeader("Disposition-Notification-To", from);
            message.setHeader("Return-Receipt-To", from);
        }

        // 设置发送时间
        message.setSentDate(new java.util.Date());

        return message;
    }
}
