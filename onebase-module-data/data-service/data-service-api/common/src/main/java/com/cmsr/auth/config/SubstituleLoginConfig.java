package com.cmsr.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cmsr.utils.CommonBeanFactory;
import com.cmsr.utils.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnMissingBean(name = "loginServer")
@Configuration
public class SubstituleLoginConfig {

    // 直接使用值注入，不再读取文件路径
    @Value("${dataease.default-pwd:DataEase@123456}")
    private String defaultPwd;

    private static String pwd;

    private static boolean ready = false;


    @ConditionalOnMissingBean(name = "loginServer")
    @Bean
    public Map<String, Object> substituleLoginData(ResourceLoader resourceLoader) {
        // 不再尝试读取或创建文件，直接返回默认密码
        pwd = defaultPwd;
        Map<String, Object> result = new HashMap<>();
        result.put("pwd", pwd);
        return result;
    }

    public static String getPwd() {
        if (!ready) {
            ready = true;
            Object substituleLoginDataObject = CommonBeanFactory.getBean("substituleLoginData");
            if (substituleLoginDataObject != null) {
                Map<String, Object> substituleLoginData = (Map<String, Object>) substituleLoginDataObject;
                if (ObjectUtils.isNotEmpty(substituleLoginData.get("pwd"))) {
                    pwd = substituleLoginData.get("pwd").toString();
                    return substituleLoginData.get("pwd").toString();
                }
            }
        }
        return pwd;
    }

    public void modifyPwd(String newPwd) {
        // 不再写入文件，只更新内存中的密码
        pwd = newPwd;
        // 更新Bean中的密码值
        try {
            Object substituleLoginDataObject = CommonBeanFactory.getBean("substituleLoginData");
            if (substituleLoginDataObject != null) {
                Map<String, Object> substituleLoginData = (Map<String, Object>) substituleLoginDataObject;
                substituleLoginData.put("pwd", newPwd);
            }
        } catch (Exception e) {
            LogUtil.error("Failed to update substitule password", e);
        }
    }
}
