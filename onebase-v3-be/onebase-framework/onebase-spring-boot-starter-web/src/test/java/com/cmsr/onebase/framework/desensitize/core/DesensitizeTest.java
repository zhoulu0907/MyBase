package com.cmsr.onebase.framework.desensitize.core;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.desensitize.annotation.*;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link DesensitizeTest} 的单元测试
 */
@ExtendWith(MockitoExtension.class)
public class DesensitizeTest {

    @Test
    public void test() {
        // 准备参数
        DesensitizeDemo desensitizeDemo = new DesensitizeDemo();
        desensitizeDemo.setNickname("用户");
        desensitizeDemo.setBankCard("9988002866797031");
        desensitizeDemo.setFixedPhone("01086551122");
        desensitizeDemo.setIdCard("530321199204074611");
        desensitizeDemo.setPassword("123456");
        desensitizeDemo.setPhoneNumber("13248765917");
        desensitizeDemo.setSlider1("ABCDEFG");
        desensitizeDemo.setSlider2("ABCDEFG");
        desensitizeDemo.setSlider3("ABCDEFG");
        desensitizeDemo.setEmail("testsuppline@email.com");
        desensitizeDemo.setOrigin("用户");

        String jsonString = JsonUtils.toJsonString(desensitizeDemo);
        //System.out.println(jsonString);
        // 调用
        DesensitizeDemo d = JsonUtils.parseObject(jsonString, DesensitizeDemo.class);
        // 断言
        System.out.println(d);
    }

    @Data
    public static class DesensitizeDemo {

        @ChineseNameDesensitize
        private String nickname;
        @BankCardDesensitize
        private String bankCard;
        @FixedPhoneDesensitize
        private String fixedPhone;
        @IdCardDesensitize
        private String idCard;
        @PasswordDesensitize
        private String password;
        @MobileDesensitize
        private String phoneNumber;
        @MaskDesensitize(prefixKeep = 6, suffixKeep = 1, replacer = "#")
        private String slider1;
        @MaskDesensitize(prefixKeep = 3, suffixKeep = 3)
        private String slider2;
        @MaskDesensitize(prefixKeep = 10)
        private String slider3;
        @EMailDesensitize
        private String email;


        private String origin;

    }

}
