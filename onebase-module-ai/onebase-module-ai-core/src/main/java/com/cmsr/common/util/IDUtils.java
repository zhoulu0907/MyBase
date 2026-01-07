package com.cmsr.common.util;


import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class IDUtils {


    public static String randomID(Integer num) {
        num = ObjectUtils.isEmpty(num) ? 16 : num;
        return RandomStringUtils.randomAlphanumeric(num);
    }

    public static void main(String[] args) {
        for (int i = 0; i< 10; i++){
            System.out.println(randomID(16));
        }
    }
}
