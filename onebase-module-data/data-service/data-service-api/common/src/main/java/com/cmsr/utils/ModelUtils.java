package com.cmsr.utils;

import com.cmsr.model.DeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ModelUtils {

    private static String modelValue;

    @Value("${spring.profiles.active:standalone}")
    public void setModelValue(String modelValue) {
        ModelUtils.modelValue = modelValue;
    }

    public static DeModel get() {
        try {
            return DeModel.valueOf(modelValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 如果无法映射到枚举，默认返回STANDALONE
            return DeModel.STANDALONE;
        }
    }

    public static boolean isDesktop() {
        return get() == DeModel.DESKTOP;
    }
}
