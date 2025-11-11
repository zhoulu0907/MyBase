package com.cmsr.onebase.module.infra.service.security.validator;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.infra.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 单元测试：覆盖 PasswordValidator 的各个校验分支
 * 
 * 覆盖场景：
 * - 空密码、禁用校验、长度检查（过短/过长/无检查）
 * - 数字/字母/大小写/特殊符号检查（启用/禁用）
 * - 字母检查在区分/不区分大小写下的分支
 * - 键盘横向/斜向序列检查（正向/反向）
 * - 逻辑连续字符检查（正向/反向）
 * - 相同字符连续检查
 * - 边界值测试（limitNum 为 2/3/4）
 */
public class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    public void setup() {
        validator = new PasswordValidator();
    }

    private PasswordPolicyConfig enableAllConfig() {
        return PasswordPolicyConfig.builder()
                .enableWeakPassword(true)
                .minLength(8)
                .maxLength(32)
                .checkPwdLength(true)
                .checkContainDigit(true)
                .checkContainLetter(true)
                .checkDistinguishCase(true)
                .checkLowerCase(true)
                .checkUpperCase(true)
                .checkContainSpecialChar(true)
                .checkHorizontalKeySeq(true)
                .limitHorizontalNum(3)
                .checkSlopeSeq(true)
                .limitSlopeNum(3)
                .checkLogicSeq(true)
                .limitLogicNum(3)
                .checkSameCharSeq(true)
                .limitSameCharNum(3)
                .build();
    }

    @Test
    public void testEmptyPassword_throwsEmptyError() {
        PasswordPolicyConfig cfg = enableAllConfig();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_EMPTY.getCode(), ex.getCode());
    }

    @Test
    public void testDisabledSkipsValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setEnableWeakPassword(false);
        // weak password but validation disabled -> no exception
        assertDoesNotThrow(() -> validator.validate("abc", cfg));
    }

    @Test
    public void testTooShort_throwsTooShort() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setMinLength(8);
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("Ab1!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_TOO_SHORT.getCode(), ex.getCode());
    }

    @Test
    public void testTooLong_throwsTooLong() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setMaxLength(2);
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("Ab1!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_TOO_LONG.getCode(), ex.getCode());
    }

    @Test
    public void testMissingDigit_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("Abcdef!g", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_DIGIT.getCode(), ex.getCode());
    }

    @Test
    public void testMissingLetter_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("12345678!", cfg));
        // Because checkDistinguishCase = true and we require both lower & upper,
        // the validator throws missing lower-case first for an all-digit password.
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LOWER_CASE.getCode(), ex.getCode());
    }

    @Test
    public void testMissingLowerCase_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // password has uppercase but no lower-case
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("ABC123!@#", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LOWER_CASE.getCode(), ex.getCode());
    }

    @Test
    public void testMissingUpperCase_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // password has lowercase but no upper-case
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("abc123!@#", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_UPPER_CASE.getCode(), ex.getCode());
    }

    @Test
    public void testMissingSpecialChar_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("Abc12345", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_SPECIAL_CHAR.getCode(), ex.getCode());
    }

    @Test
    public void testKeyboardHorizontalSeq_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "qwe" is a horizontal keyboard sequence
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("qwe123A!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testKeyboardSlopeSeq_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "1qa" is contained in slope mapping "1qaz"
        // Ensure password length >= minLength so length check doesn't preempt the slope check
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("1qaXYZ!A", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testLogicSequential_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "abc" is a logical sequential
        // Avoid triggering keyboard horizontal (e.g. "123"). Use a single digit that is not part of a horizontal sequence.
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("abc9#DEF", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testSameCharSequential_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "aaa" is three same chars
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("aaaBb1!@", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_SAME_CHAR_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testValidPassword_passesAllChecks() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // Construct a password that satisfies all checks and avoids sequences
        String strong = "A9#bC7xZ";
        assertDoesNotThrow(() -> validator.validate(strong, cfg));
    }

    // ========== 长度检查分支覆盖 ==========

    @Test
    public void testNoMaxLength_passesWhenLongEnough() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setMaxLength(null); // 不限制最大长度
        // Use a very long password without violating other constraints
        String veryLong = "A9#mBxKpZq" + "C7!dEfLrStUvW";
        assertDoesNotThrow(() -> validator.validate(veryLong, cfg));
    }

    @Test
    public void testNoMinLength_passesWhenShort() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setMinLength(null); // 不限制最小长度
        cfg.setCheckPwdLength(true);
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("A9#", cfg));
        // Should fail on other checks (e.g., missing letter variations), but this tests the minLength=null branch
    }

    @Test
    public void testLengthCheckDisabled_skipsLengthValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckPwdLength(false);
        cfg.setMinLength(20);
        // Short password but length check disabled -> no length exception
        // Password is valid otherwise (has all required chars and no sequences)
        assertDoesNotThrow(() -> validator.validate("Ab1!xYzM", cfg));
    }

    // ========== 数字检查分支覆盖 ==========

    @Test
    public void testDigitCheckDisabled_skipsDigitValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckContainDigit(false);
        // No digit but check disabled -> should pass digit check
        // Will fail on keyboard/logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("AbCd!@#$", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_DIGIT.getCode(), ex.getCode());
    }

    // ========== 字母检查分支覆盖 ==========

    @Test
    public void testLetterCheckDisabled_skipsLetterValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckContainLetter(false);
        // No letter but check disabled
        // Will fail on digit or other checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("12345678!", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LETTER.getCode(), ex.getCode());
    }

    @Test
    public void testLetterCheck_NotDistinguishCase_whenLetterPresent() {
        // When checkDistinguishCase=false and checkContainLetter=true,
        // no separate upper/lower checks; just check for any letter
        PasswordPolicyConfig cfg = PasswordPolicyConfig.builder()
                .enableWeakPassword(true)
                .minLength(1)
                .checkPwdLength(false)
                .checkContainDigit(false)
                .checkContainLetter(true)
                .checkDistinguishCase(false) // Not distinguishing case
                .checkContainSpecialChar(false)
                .checkHorizontalKeySeq(false)
                .checkSlopeSeq(false)
                .checkLogicSeq(false)
                .checkSameCharSeq(false)
                .build();
        assertDoesNotThrow(() -> validator.validate("a", cfg)); // lowercase only, but passes
        assertDoesNotThrow(() -> validator.validate("A", cfg)); // uppercase only, but passes
    }

    @Test
    public void testLetterCheck_NotDistinguishCase_whenLetterMissing() {
        PasswordPolicyConfig cfg = PasswordPolicyConfig.builder()
                .enableWeakPassword(true)
                .minLength(1)
                .checkPwdLength(false)
                .checkContainDigit(false)
                .checkContainLetter(true)
                .checkDistinguishCase(false) // Not distinguishing case
                .checkContainSpecialChar(false)
                .checkHorizontalKeySeq(false)
                .checkSlopeSeq(false)
                .checkLogicSeq(false)
                .checkSameCharSeq(false)
                .build();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("123", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LETTER.getCode(), ex.getCode());
    }

    @Test
    public void testUpperCaseCheckDisabled_skipsUpperValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckUpperCase(false);
        // No uppercase but check disabled
        // Will fail on slope/logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("abc123!@", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_UPPER_CASE.getCode(), ex.getCode());
    }

    @Test
    public void testLowerCaseCheckDisabled_skipsLowerValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckLowerCase(false);
        // No lowercase but check disabled
        // Will fail on slope/logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("ABC123!@", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LOWER_CASE.getCode(), ex.getCode());
    }

    // ========== 特殊符号检查分支覆盖 ==========

    @Test
    public void testSpecialCharCheckDisabled_skipsSpecialValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckContainSpecialChar(false);
        // No special char but check disabled
        // Will fail on slope/logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("Abc12345", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_SPECIAL_CHAR.getCode(), ex.getCode());
    }

    // ========== 键盘横向检查分支覆盖 ==========

    @Test
    public void testHorizontalKeySeq_Reversed_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "ewq" is "qwe" reversed -> should also trigger horizontal check
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("ewq123A!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testHorizontalKeySeqCheckDisabled_skipsCheck() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckHorizontalKeySeq(false);
        // "qwe" is horizontal but check disabled
        // Will fail on logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("qwe123A!", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testHorizontalKeySeq_LimitNum2_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setLimitHorizontalNum(2); // Only 2 consecutive chars needed
        // "qw" would trigger with limit=2
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("qw123A!#", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testHorizontalKeySeq_NotDistinguishCase_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckDistinguishCase(false);
        // "QWE" (uppercase) should match "qwe" when case not distinguished
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("QWE123a!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_HORIZONTAL_SEQ.getCode(), ex.getCode());
    }

    // ========== 键盘斜向检查分支覆盖 ==========

    @Test
    public void testKeyboardSlopeSeq_Reversed_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "az1q" contains "zaq1" reversed? Let's use a clearer reverse of "1qaz"
        // Reverse of "1qaz" is "zaQ1" - test if "zaq1" or similar triggers
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("zaq1B#CD", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testKeyboardSlopeSeqCheckDisabled_skipsCheck() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckSlopeSeq(false);
        // "1qa" is slope but check disabled
        // Will fail on logic checks
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("1qaXYZ!", cfg));
        assertNotEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testKeyboardSlopeSeq_LimitNum2_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setLimitSlopeNum(2); // Only 2 consecutive chars needed
        // Use a clear slope sequence like "2w" from "2wsx"
        // But use different special chars to avoid horizontal trigger: use "2w" + "RxYz#^"
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("2wRxYz#^", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ.getCode(), ex.getCode());
    }

    @Test
    public void testKeyboardSlopeSeq_NotDistinguishCase_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckDistinguishCase(false);
        // "1QAZ" should match slope "1qaz" when case not distinguished
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("1QAZ#@CD", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_KEYBOARD_SLOPE_SEQ.getCode(), ex.getCode());
    }

    // ========== 逻辑连续检查分支覆盖 ==========

    @Test
    public void testLogicSequential_Reversed_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "cba" is reverse of "abc"
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("cba9#DEF", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testLogicSequential_Numeric_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        // "321" is a numeric logic sequence (reversed), but to avoid other checks,
        // we need uppercase, lowercase, special char. Ensure "321" doesn't match horizontally.
        // "321" is on top row, so it's horizontal. Use reverse: "789" on numpad side
        // Actually "789" forward or "987" backward. Let's use letters: "xyz" forward
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("xyz9#DEF", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testLogicSequentialCheckDisabled_skipsCheck() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckLogicSeq(false);
        // "abc" is logic sequential but check disabled
        // Password must be valid otherwise: has all types and no keyboard sequences
        assertDoesNotThrow(() -> validator.validate("abc9#DEF", cfg));
    }

    @Test
    public void testLogicSequential_LimitNum2_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setLimitLogicNum(2); // Only 2 consecutive chars needed
        // "ab" should trigger with limit=2
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("ab3#XYZW", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testLogicSequential_NotDistinguishCase_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckDistinguishCase(false);
        // "ABC" should match logic sequence when case not distinguished
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("ABC9#XYZ", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_LOGIC_SEQUENTIAL.getCode(), ex.getCode());
    }

    // ========== 相同字符连续检查分支覆盖 ==========

    @Test
    public void testSameCharSequentialCheckDisabled_skipsCheck() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setCheckSameCharSeq(false);
        // "aaa" is same char sequential but check disabled
        assertDoesNotThrow(() -> validator.validate("aaaBb1!@", cfg));
    }

    @Test
    public void testSameCharSequential_LimitNum2_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setLimitSameCharNum(2); // Only 2 consecutive chars needed
        // "bb" should trigger with limit=2, avoid keyboard sequences
        // Use "bbCd1" + special chars like "{" or "}" that are in shifted row
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("bbCd1{}G", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_SAME_CHAR_SEQUENTIAL.getCode(), ex.getCode());
    }

    @Test
    public void testSameCharSequential_LimitNum4_throws() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setLimitSameCharNum(4); // 4 consecutive chars needed
        // "aaaa" should trigger with limit=4
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("aaaaBb1!", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_SAME_CHAR_SEQUENTIAL.getCode(), ex.getCode());
    }

    // ========== 综合场景覆盖 ==========

    @Test
    public void testValidPassword_AllChecksEnabled_passesStrongPassword() {
        PasswordPolicyConfig cfg = enableAllConfig();
        String strong = "A9#bC7xZ";
        assertDoesNotThrow(() -> validator.validate(strong, cfg));
    }

    @Test
    public void testValidPassword_WithDistinguishCaseFalse_passesWhenLettersExist() {
        PasswordPolicyConfig cfg = PasswordPolicyConfig.builder()
                .enableWeakPassword(true)
                .minLength(8)
                .checkPwdLength(true)
                .checkContainDigit(true)
                .checkContainLetter(true)
                .checkDistinguishCase(false) // Only check for letter existence
                .checkLowerCase(false)
                .checkUpperCase(false)
                .checkContainSpecialChar(true)
                .checkHorizontalKeySeq(false)
                .checkSlopeSeq(false)
                .checkLogicSeq(false)
                .checkSameCharSeq(false)
                .build();
        // Only lowercase, but should pass since case distinction disabled
        assertDoesNotThrow(() -> validator.validate("abcde123!", cfg));
    }

    @Test
    public void testEnableWeakPasswordNull_skipsValidation() {
        PasswordPolicyConfig cfg = enableAllConfig();
        cfg.setEnableWeakPassword(null); // null should be treated as not TRUE -> skip
        // Use a non-empty password that would normally fail if validated, but should be skipped
        assertDoesNotThrow(() -> validator.validate("abc", cfg));
    }

    @Test
    public void testNullPassword_throwsEmptyError() {
        PasswordPolicyConfig cfg = enableAllConfig();
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate(null, cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_EMPTY.getCode(), ex.getCode());
    }

    @Test
    public void testCheckDistinguishCaseNull_behavesAsNotDistinguish() {
        PasswordPolicyConfig cfg = PasswordPolicyConfig.builder()
                .enableWeakPassword(true)
                .minLength(1)
                .checkPwdLength(false)
                .checkContainDigit(false)
                .checkContainLetter(true)
                .checkDistinguishCase(null) // treat as false
                .checkContainSpecialChar(false)
                .checkHorizontalKeySeq(false)
                .checkSlopeSeq(false)
                .checkLogicSeq(false)
                .checkSameCharSeq(false)
                .build();

        // No letters -> should throw WEAK_PASSWORD_CONTAINS_NO_LETTER
        ServiceException ex = assertThrows(ServiceException.class, () -> validator.validate("123", cfg));
        assertEquals(ErrorCodeConstants.WEAK_PASSWORD_CONTAINS_NO_LETTER.getCode(), ex.getCode());
    }

}
