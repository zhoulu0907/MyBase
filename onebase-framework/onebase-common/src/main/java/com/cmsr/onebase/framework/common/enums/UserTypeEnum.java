package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局用户类型枚举
 * 现在这个用户类型有四种：1平台，2空间，3企业，4是三方。
 * 一般情况是哪种类型的用户创建的就是该类型的用户，比方说平台管理员，他创建的是就是平台管理员，这是一一对应的正常情况。
 * 但是有些情况是例外的，就是平台管理员在创建空间的时候，连带创建的空间管理员的类型是2空间；
 * 还有一种情况是，空间用户（类型2）在创建企业的时候，连带创建的企业管理员的类型是企业用户（类型3）；
 * 三方用户有两种方式，一个是通过空间用户来完成它的创建，另一种是它自行的注册，这两种情况会产生类型4；
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum implements ArrayValuable<Integer> {

    PLATFORM(1, "平台管理员"),
    TENANT(2, "空间用户"),
    CORP(3, "p企业用户"),
    THIRD(4, "三方用户");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(UserTypeEnum::getValue).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer value;
    /**
     * 类型名
     */
    private final String  name;

    public static UserTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), UserTypeEnum.values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
