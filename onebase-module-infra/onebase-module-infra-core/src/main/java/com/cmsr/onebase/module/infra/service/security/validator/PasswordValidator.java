package com.cmsr.onebase.module.infra.service.security.validator;

import com.cmsr.onebase.framework.common.exception.ErrorCode;
import com.cmsr.onebase.module.infra.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 密码强度校验核心逻辑
 * 
 * 基于3级标准校验规则：
 * - 口令应为英文字母（区分大小写）+数字+特殊字符三者的组合，长度不小于8位
 * - 口令中的字符在键盘物理位置上横向不允许有连续3个及以上相邻
 * - 口令中字符在键盘物理位置上斜线方向不允许有连续3个及以上相邻
 * - 口令中字符在逻辑位置上不允许有连续3个及以上相邻的
 * - 口令中的特殊字符集为"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~" 共32位字符
 * - 相邻单字符重复次数不得超过3次
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Slf4j
public class PasswordValidator {

    /**
     * 特殊符号集合
     */
    private static final String SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    /**
     * 键盘横向方向规则
     */
    private static final String[] KEYBOARD_HORIZONTAL_ARR = {
            "01234567890-=",
            "!@#$%^&*()_+",
            "qwertyuiop[]",
            "QWERTYUIOP{}",
            "asdfghjkl;'",
            "ASDFGHJKL:\"",
            "zxcvbnm,./",
            "ZXCVBNM<>?"
    };

    /**
     * 键盘斜线方向规则
     */
    private static final String[] KEYBOARD_SLOPE_ARR = {
            "1qaz",
            "!QAZ",
            "2wsx",
            "@WSX",
            "3edc",
            "#EDC",
            "4rfv",
            "$RFV",
            "5tgb",
            "%TGGB",
            "6yhn",
            "^YHN",
            "7ujm",
            "&UJM",
            "8ik,",
            "*IK<",
            "9ol.",
            "(OL>",
            "0p;/",
            ")P:?",
            "=[;.",
            "+{:>",
            "-pl,",
            "_PL<",
            "0okm",
            ")OKM",
            "9ijn",
            "(IJN",
            "8uhb",
            "*UHB",
            "7ygv",
            "&YGV",
            "6tfc",
            "^TFC",
            "5rdx",
            "%RDX",
            "4esz",
            "$ESZ"
    };

    /**
     * 校验密码强度
     *
     * @param password 待校验的密码
     * @param config   密码策略配置
     */
    public void validate(String password, PasswordPolicyConfig config) {
        if (password == null || password.trim().isEmpty()) {
            throw exception(ErrorCodeConstants.WEAK_PASSWORD_EMPTY);
        }

        // 检查是否启用弱密码校验
        if (!Boolean.TRUE.equals(config.getEnableWeakPassword())) {
            log.debug("租户未启用弱密码校验，跳过密码强度检查");
            return;
        }

        // 检查密码长度
        if (Boolean.TRUE.equals(config.getCheckPwdLength())) {
            checkPasswordLength(password, config.getMinLength(), config.getMaxLength());
        }

        // 检查包含数字
        if (Boolean.TRUE.equals(config.getCheckContainDigit())) {
            checkContainDigit(password);
        }

        // 检查包含字母
        if (Boolean.TRUE.equals(config.getCheckContainLetter())) {
            if (Boolean.TRUE.equals(config.getCheckDistinguishCase())) {
                // 区分大小写：必须包含大小写字母
                if (Boolean.TRUE.equals(config.getCheckLowerCase())) {
                    checkContainLowerCase(password);
                }
                if (Boolean.TRUE.equals(config.getCheckUpperCase())) {
                    checkContainUpperCase(password);
                }
            } else {
                checkContainLetter(password);
            }
        }

        // 检查包含特殊符号
        if (Boolean.TRUE.equals(config.getCheckContainSpecialChar())) {
            checkContainSpecialChar(password);
        }

        // 检查键盘横向连续
        if (Boolean.TRUE.equals(config.getCheckHorizontalKeySeq())) {
            checkHorizontalKeySeq(password, config.getCheckDistinguishCase(), config.getLimitHorizontalNum());
        }

        // 检查键盘斜向连续
        if (Boolean.TRUE.equals(config.getCheckSlopeSeq())) {
            checkSlopeSeq(password, config.getCheckDistinguishCase(), config.getLimitSlopeNum());
        }

        // 检查逻辑位置连续
        if (Boolean.TRUE.equals(config.getCheckLogicSeq())) {
            checkSequentialChars(password, config.getCheckDistinguishCase(), config.getLimitLogicNum());
        }

        // 检查相同字符连续
        if (Boolean.TRUE.equals(config.getCheckSameCharSeq())) {
            checkSequentialSameChars(password, config.getLimitSameCharNum());
        }
    }

    /**
     * 检测密码中字符长度
     *
     * @param password  密码字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     */
    private void checkPasswordLength(String password, Integer minLength, Integer maxLength) {
        if (maxLength != null && password.length() > maxLength) {
            throw exception(ErrorCodeConstants.WEAK_PASSWORD_TOO_LONG, maxLength);
        }
        if (minLength != null && password.length() < minLength) {
            throw exception(ErrorCodeConstants.WEAK_PASSWORD_TOO_SHORT, minLength);
        }
    }

    /**
     * 检测密码中是否包含数字
     *
     * @param password 密码字符串
     */
    private void checkContainDigit(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return;
            }
        }
        throw exception(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_DIGIT);
    }

    /**
     * 检测密码中是否包含字母（不区分大小写）
     *
     * @param password 密码字符串
     */
    private void checkContainLetter(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                return;
            }
        }
        throw exception(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LETTER);
    }

    /**
     * 检测密码中是否包含小写字母
     *
     * @param password 密码字符串
     */
    private void checkContainLowerCase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return;
            }
        }
        throw exception(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LOWER_CASE);
    }

    /**
     * 检测密码中是否包含大写字母
     *
     * @param password 密码字符串
     */
    private void checkContainUpperCase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return;
            }
        }
        throw exception(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_UPPER_CASE);
    }

    /**
     * 检测密码中是否包含特殊符号
     *
     * @param password 密码字符串
     */
    private void checkContainSpecialChar(String password) {
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHAR.indexOf(c) != -1) {
                return;
            }
        }
        throw exception(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_SPECIAL_CHAR);
    }

    /**
     * 匹配键盘组合（横向或斜向）
     *
     * @param password      输入字符串
     * @param composeArr    键盘组合数组
     * @param limitNum      匹配的最小连续个数
     * @param distinguishCase 是否区分大小写
     * @param errorCode 错误代码
     */
    private void checkKeyCompose(String password, String[] composeArr, int limitNum, Boolean distinguishCase, ErrorCode errorCode) {
        // 检查正向连续字符
        for (int i = 0; i + limitNum <= password.length(); i++) {
            String str = password.substring(i, i + limitNum);

            for (String configStr : composeArr) {
                String compareStr = str;
                String compareConfig = configStr;
                
                // 如果不区分大小写，则转换为大写进行比对
                if (!Boolean.TRUE.equals(distinguishCase)) {
                    compareStr = str.toUpperCase(Locale.ROOT);
                    compareConfig = configStr.toUpperCase(Locale.ROOT);
                }

                if (compareConfig.contains(compareStr)) {
                    throw exception(errorCode);
                }
            }
        }
        
        // 检查反向连续字符
        String revPassword = new StringBuilder(password).reverse().toString();
        for (int i = 0; i + limitNum <= revPassword.length(); i++) {
            String revStr = revPassword.substring(i, i + limitNum);

            for (String configStr : composeArr) {
                String compareRevStr = revStr;
                String compareConfig = configStr;
                
                // 如果不区分大小写，则转换为大写进行比对
                if (!Boolean.TRUE.equals(distinguishCase)) {
                    compareRevStr = revStr.toUpperCase(Locale.ROOT);
                    compareConfig = configStr.toUpperCase(Locale.ROOT);
                }

                if (compareConfig.contains(compareRevStr)) {
                    throw exception(errorCode);
                }
            }
        }
    }

    /**
     * 检查键盘横向连续
     *
     * @param password        密码字符串
     * @param distinguishCase 是否区分大小写
     * @param limitNum        不允许的最小连续个数
     */
    private void checkHorizontalKeySeq(String password, Boolean distinguishCase, Integer limitNum) {
        checkKeyCompose(password, KEYBOARD_HORIZONTAL_ARR, limitNum, distinguishCase, ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ);
    }

    /**
     * 检查键盘斜向连续
     *
     * @param password        密码字符串
     * @param distinguishCase 是否区分大小写
     * @param limitNum        不允许的最小连续个数
     */
    private void checkSlopeSeq(String password, Boolean distinguishCase, Integer limitNum) {
        checkKeyCompose(password, KEYBOARD_SLOPE_ARR, limitNum, distinguishCase, ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ);
    }

    /**
     * 检查逻辑位置连续字符（如a-z, z-a, 0-9, 9-0）
     *
     * @param password        密码字符串
     * @param distinguishCase 是否区分大小写
     * @param limitNum        不允许的最小连续个数
     */
    private void checkSequentialChars(String password, Boolean distinguishCase, Integer limitNum) {
        // 如果不区分大小写，转换为小写进行检查
        String checkPassword = password;
        if (!Boolean.TRUE.equals(distinguishCase)) {
            checkPassword = password.toLowerCase(Locale.ROOT);
        }

        int n = checkPassword.length();
        char[] pwdCharArr = checkPassword.toCharArray();
        int queueLen = 0;
        int revQueueLen = 0;

        for (int i = 1; i < n; i++) {
            if (pwdCharArr[i] - pwdCharArr[i - 1] == 1) {
                queueLen += 1;
            } else {
                queueLen = 0;
            }

            if (pwdCharArr[i - 1] - pwdCharArr[i] == 1) {
                revQueueLen += 1;
            } else {
                revQueueLen = 0;
            }

            if (queueLen == limitNum - 1 || revQueueLen == limitNum - 1) {
                throw exception(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL);
            }
        }
    }

    /**
     * 检查相同字符连续（如aaaa, 1111）
     *
     * @param password 密码字符串
     * @param limitNum 不允许的最小连续个数
     */
    private void checkSequentialSameChars(String password, Integer limitNum) {
        int n = password.length();
        char[] pwdCharArr = password.toCharArray();
        int count = 0;

        for (int i = 1; i < n; i++) {
            if (pwdCharArr[i - 1] == pwdCharArr[i]) {
                count += 1;
                if (count == limitNum - 1) {
                    throw exception(ErrorCodeConstants.WEAK_PASSWORD_SAME_CHAR_SEQUENTIAL);
                }
            } else {
                count = 0;
            }
        }
    }

}
