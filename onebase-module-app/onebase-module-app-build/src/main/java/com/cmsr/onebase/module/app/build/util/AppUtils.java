package com.cmsr.onebase.module.app.build.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/8/12 19:12
 */
public class AppUtils {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 返回一个4位的随机数，有小写字母和数字组成，其中第一位必须是小写字母
     *
     * @return
     */
    public static String createAppUid() {
        // 生成一个4位的随机字符串，第一位是小写字母，其余位可以是小写字母或数字
        StringBuilder sb = new StringBuilder();

        // 第一位必须是小写字母
        int firstIndex = RandomUtils.secure().randomInt(0, LETTERS.length());
        sb.append(LETTERS.charAt(firstIndex));

        // 剩余3位可以是小写字母或数字
        for (int i = 0; i < 3; i++) {
            int index = RandomUtils.secure().randomInt(0, CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 把long转换成一个特殊的编码，4位
     * 1、4位，由数字和小写字母组成
     * 2、首位必须是字母，剩余3位可以是数字或字母
     * 3、输入数字会大于等于1
     * 4、尽量考虑编码算法和代码的可理解性和可读性
     * <p>
     * 算法核心思想：
     * - 采用混合进制转换：第1位使用26进制(a-z)，第2-4位使用36进制(a-z,0-9)
     * - 字符映射：数值0→'a', 数值1→'b', ..., 数值26→'0', 数值27→'1', ...
     * - 容量计算：总共可表示 26×36×36×36 = 1,213,056 个不同的ID
     * - 转换过程：通过取模(%)获取当前位字符，通过整除(/=)移除已处理位
     * - 示例：id=100 → index=99 → 从右到左依次计算 → "aac1"
     *
     * @param id 输入的ID，范围：1 到 1,213,056
     * @return 4位编码字符串，格式：[a-z][a-z0-9][a-z0-9][a-z0-9]
     */
    public static String transformAppUid(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID must be greater than or equal to 1");
        }

        // 总容量检查
        int maxCapacity = 26 * 36 * 36 * 36 - 1;
        if (id > maxCapacity) {
            throw new IllegalArgumentException("ID exceeds maximum capacity: " + maxCapacity);
        }

        // 调整id为从0开始的索引
        int index = id;

        StringBuilder result = new StringBuilder();

        // 从右到左计算每一位
        // 第4位（最右、个位）
        result.insert(0, CHARS.charAt(index % 36));
        index /= 36;

        // 第3位
        result.insert(0, CHARS.charAt(index % 36));
        index /= 36;

        // 第2位
        result.insert(0, CHARS.charAt(index % 36));
        index /= 36;

        // 第1位（最左，只能是字母）
        result.insert(0, LETTERS.charAt(index % 26));

        return result.toString();
    }


    public static String createAppCode() {
        return "APP-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
