package com.cmsr.common.util;


import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IDUtils {


    public static String randomID(Integer num) {
        num = ObjectUtils.isEmpty(num) ? 16 : num;
        // 使用UUID生成安全的随机ID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, Math.min(num, uuid.length()));
    }

    public static void main(String[] args) {
        for (int i = 0; i< 10; i++){
            System.out.println(randomID(16));
        }
    }
}
