package com.cmsr.onebase.module.system.framework.sms.core.enums;

import com.cmsr.onebase.framework.common.tools.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信渠道枚举
 *
 * @author zzf
 * @since 2021/1/25 10:56
 */
@Getter
@AllArgsConstructor
public enum SmsChannelEnum {

    DEBUG_DING_TALK("DEBUG_DING_TALK", "调试(钉钉)"),
    ALIYUN("ALIYUN", "阿里云"),
    TENCENT("TENCENT", "腾讯云"),
    HUAWEI("HUAWEI", "华为云"),
    QINIU("QINIU", "七牛云"),
    ;

    /**
     * 编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static SmsChannelEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }

}

