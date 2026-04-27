package com.cmsr.onebase.module.flow.core.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动作命名生成器
 * <p>
 * 用于生成复制动作时的唯一名称和编码
 * <p>
 * 核心逻辑：
 * - 从已有名称中提取最大序号
 * - 生成新的序号为最大序号+1
 * - 支持从已复制的动作再次复制
 *
 * @author onebase
 * @since 2026-01-26
 */
public class ActionNameGenerator {

    private static final String COPY_SUFFIX = "_copy";
    private static final Pattern COPY_PATTERN = Pattern.compile("(.+)_copy(\\d+)");

    /**
     * 生成复制动作名称：原名称_copy1
     * <p>
     * 示例：
     * - "获取用户信息" → "获取用户信息_copy1"
     * - "获取用户信息_copy1" → "获取用户信息_copy2"
     * - "获取用户信息_copy3" → "获取用户信息_copy4"
     *
     * @param originalName  原始名称
     * @param existingNames 已存在的名称列表
     * @return 生成的唯一名称
     */
    public String generateCopyName(String originalName, List<String> existingNames) {
        String baseName = extractBaseName(originalName);

        // 查找最大序号
        int maxCounter = findMaxCounter(baseName, existingNames);

        return baseName + COPY_SUFFIX + (maxCounter + 1);
    }

    /**
     * 生成复制动作编码：原编码_copy1
     *
     * @param originalCode  原始编码
     * @param existingCodes 已存在的编码列表
     * @return 生成的唯一编码
     */
    public String generateCopyCode(String originalCode, List<String> existingCodes) {
        String baseCode = extractBaseName(originalCode);

        // 查找最大序号
        int maxCounter = findMaxCounter(baseCode, existingCodes);

        return baseCode + COPY_SUFFIX + (maxCounter + 1);
    }

    /**
     * 提取基础名称（去除_copy后缀）
     * <p>
     * 规则：
     * - 匹配 "名称_copy数字" 模式，提取"名称"部分
     * - 如果名称包含 _copy 但不匹配模式，去除最后的 _copy
     *
     * @param name 原始名称
     * @return 基础名称
     */
    private String extractBaseName(String name) {
        Matcher matcher = COPY_PATTERN.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        // 如果名称已包含 _copy 但不匹配模式（如 "test_copy"），去掉后缀
        if (name.contains(COPY_SUFFIX)) {
            return name.substring(0, name.lastIndexOf(COPY_SUFFIX));
        }
        return name;
    }

    /**
     * 查找指定基础名称的最大序号
     * <p>
     * 遍历现有名称列表，找出匹配 "基础名称_copy数字" 模式的最大数字
     *
     * @param baseName      基础名称
     * @param existingNames 现有名称列表
     * @return 最大序号（未找到返回0）
     */
    private int findMaxCounter(String baseName, List<String> existingNames) {
        int maxCounter = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(baseName + COPY_SUFFIX) + "(\\d+)");

        for (String name : existingNames) {
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                int counter = Integer.parseInt(matcher.group(1));
                maxCounter = Math.max(maxCounter, counter);
            }
        }

        return maxCounter;
    }
}
