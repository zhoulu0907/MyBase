package com.cmsr.onebase.module.system.util.encrypt;

import org.apache.commons.text.RandomStringGenerator;

public class PasswordRandomGenerator {
    /**
     * 生成符合等保三级要求的随机密码
     * 等保三级密码要求：
     * 1. 长度至少8位
     * 2. 包含大写字母、小写字母、数字和特殊字符
     * 3. 不能包含三个及以上连续重复字符
     * 4. 不能包含常见弱密码
     * @param length 密码长度
     * @return 符合要求的随机密码
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("密码长度至少需要8位");
        }

        // 定义字符集
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        // 创建生成器
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(' ', '~') // 可打印ASCII字符范围
                .filteredBy(codePoint ->
                        Character.isLetterOrDigit(codePoint) ||
                                specialChars.indexOf((char)codePoint) >= 0)
                .build();

        String password;
        do {
            // 生成随机字符串
            password = generator.generate(length);

            // 检查是否满足所有条件
        } while (!isPasswordStrongEnough(password, upperCaseLetters,
                lowerCaseLetters, numbers, specialChars));

        return password;
    }

    private static boolean isPasswordStrongEnough(String password,
                                                  String upperCaseLetters, String lowerCaseLetters,
                                                  String numbers, String specialChars) {
        // 检查是否包含至少一个大写字母
        boolean hasUpper = password.chars()
                .anyMatch(c -> upperCaseLetters.indexOf((char)c) >= 0);

        // 检查是否包含至少一个小写字母
        boolean hasLower = password.chars()
                .anyMatch(c -> lowerCaseLetters.indexOf((char)c) >= 0);

        // 检查是否包含至少一个数字
        boolean hasNumber = password.chars()
                .anyMatch(c -> numbers.indexOf((char)c) >= 0);

        // 检查是否包含至少一个特殊字符
        boolean hasSpecial = password.chars()
                .anyMatch(c -> specialChars.indexOf((char)c) >= 0);

        // 检查是否有三个或更多连续重复字符
        boolean hasConsecutiveRepeats = false;
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1)
                    && password.charAt(i) == password.charAt(i + 2)) {
                hasConsecutiveRepeats = true;
                break;
            }
        }

        return hasUpper && hasLower && hasNumber && hasSpecial && !hasConsecutiveRepeats;
    }

    public static void main(String[] args) {
        // 测试生成10个密码
        for (int i = 0; i < 10; i++) {
            System.out.println("生成的密码: " + generateSecurePassword(15));
        }
    }
}
