package com.cmsr.onebase.framework.desensitize.serializer;

import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.consts.DesensitizedFieldConstant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Author：huangjie
 * @Date：2025/8/31 9:37
 */
public class EMailJsonSerializer extends JsonSerializer<String> {

    @Resource
    private SecurityConfigApi securityConfigApi;

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示
     * <例子:g**@163.com>
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        // 获取租户配置项
        Set<String> tenantConfigValues = securityConfigApi.getTenantDesensitizedFieldValues();

        if(!tenantConfigValues.contains(DesensitizedFieldConstant.EMAIL)){
            gen.writeString(value);
            return;
        }

        int index = value.indexOf("@");
        if (index <= 1) {
            gen.writeString(value);
            return;
        }
        String begin = value.substring(0, 1);
        String end = value.substring(index);
        String stars = "**";
        gen.writeString(begin + stars + end);
    }

}
