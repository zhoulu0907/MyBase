package com.cmsr.onebase.module.infra.service.security.validator.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 密码策略配置对象，用于密码校验
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicyConfig {

    /**
     * 密码校验总开关，false时不进行密码校验
     */
    private Boolean enableWeakPassword;

    /**
     * 密码最小长度
     */
    private Integer minLength;

    /**
     * 密码最大长度，固定值32
     */
    @Builder.Default
    private Integer maxLength = 32;

    /**
     * 是否检查包含大写字母
     */
    private Boolean checkUpperCase;

    /**
     * 是否检查包含特殊符号
     */
    private Boolean checkContainSpecialChar;

    /**
     * 以下参数为3级标准固定参数
     */

    /**
     * 是否检查密码长度
     */
    @Builder.Default
    private Boolean checkPwdLength = true;

    /**
     * 是否检查包含数字
     */
    @Builder.Default
    private Boolean checkContainDigit = true;

    /**
     * 是否检查包含字母
     */
    @Builder.Default
    private Boolean checkContainLetter = true;

    /**
     * 是否区分大小写
     */
    @Builder.Default
    private Boolean checkDistinguishCase = true;

    /**
     * 是否检查包含小写字母
     */
    @Builder.Default
    private Boolean checkLowerCase = true;

    /**
     * 是否检查键盘横向连续
     */
    @Builder.Default
    private Boolean checkHorizontalKeySeq = true;

    /**
     * 键盘物理位置横向不允许最小的连续个数，3级标准为3
     */
    @Builder.Default
    private Integer limitHorizontalNum = 3;

    /**
     * 是否检查键盘斜向连续
     */
    @Builder.Default
    private Boolean checkSlopeSeq = true;

    /**
     * 键盘物理位置斜向不允许最小的连续个数，3级标准为3
     */
    @Builder.Default
    private Integer limitSlopeNum = 3;

    /**
     * 是否检查逻辑位置连续
     */
    @Builder.Default
    private Boolean checkLogicSeq = true;

    /**
     * 密码口令中字符在逻辑位置上不允许最小的连续个数，3级标准为3
     */
    @Builder.Default
    private Integer limitLogicNum = 3;

    /**
     * 是否检查相同字符连续
     */
    @Builder.Default
    private Boolean checkSameCharSeq = true;

    /**
     * 密码口令中相同字符不允许最小的连续个数，3级标准为3
     */
    @Builder.Default
    private Integer limitSameCharNum = 3;

}
