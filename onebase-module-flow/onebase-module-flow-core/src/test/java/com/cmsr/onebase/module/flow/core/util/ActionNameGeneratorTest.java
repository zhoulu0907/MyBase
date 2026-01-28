package com.cmsr.onebase.module.flow.core.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ActionNameGenerator 单元测试
 * <p>
 * 测试动作复制时的命名生成逻辑
 */
class ActionNameGeneratorTest {

    private ActionNameGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ActionNameGenerator();
    }

    // ==================== generateCopyName 测试用例 ====================

    @Test
    void testGenerateCopyName_FirstCopy() {
        // Given
        String originalName = "获取用户信息";
        List<String> existingNames = Collections.singletonList("获取用户信息");

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("获取用户信息_copy1", result);
    }

    @Test
    void testGenerateCopyName_SequentialCopy() {
        // Given
        String originalName = "获取用户信息";
        List<String> existingNames = Arrays.asList(
                "获取用户信息",
                "获取用户信息_copy1",
                "获取用户信息_copy2"
        );

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("获取用户信息_copy3", result);
    }

    @Test
    void testGenerateCopyName_NonSequentialNumbers() {
        // Given - 存在 copy1 和 copy3，缺少 copy2
        String originalName = "获取用户信息";
        List<String> existingNames = Arrays.asList(
                "获取用户信息",
                "获取用户信息_copy1",
                "获取用户信息_copy3"
        );

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then - 应该生成 copy4（最大序号+1）
        assertEquals("获取用户信息_copy4", result);
    }

    @Test
    void testGenerateCopyName_CopyFromCopied() {
        // Given - 从 "获取用户信息_copy1" 复制
        String originalName = "获取用户信息_copy1";
        List<String> existingNames = Arrays.asList(
                "获取用户信息",
                "获取用户信息_copy1",
                "获取用户信息_copy2",
                "获取用户信息_copy3"
        );

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then - 应该基于原名称生成 copy4
        assertEquals("获取用户信息_copy4", result);
    }

    @Test
    void testGenerateCopyName_EmptyExistingList() {
        // Given
        String originalName = "测试动作";
        List<String> existingNames = Collections.emptyList();

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("测试动作_copy1", result);
    }

    @Test
    void testGenerateCopyName_NameWithUnderscore() {
        // Given - 名称本身包含下划线
        String originalName = "get_user_info";
        List<String> existingNames = Arrays.asList(
                "get_user_info",
                "get_user_info_copy1"
        );

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("get_user_info_copy2", result);
    }

    @Test
    void testGenerateCopyName_ChineseCharacters() {
        // Given
        String originalName = "发送邮件通知";
        List<String> existingNames = Collections.singletonList("发送邮件通知");

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("发送邮件通知_copy1", result);
    }

    // ==================== generateCopyCode 测试用例 ====================

    @Test
    void testGenerateCopyCode_FirstCopy() {
        // Given
        String originalCode = "GET_USER";
        List<String> existingCodes = Collections.singletonList("GET_USER");

        // When
        String result = generator.generateCopyCode(originalCode, existingCodes);

        // Then
        assertEquals("GET_USER_copy1", result);
    }

    @Test
    void testGenerateCopyCode_SequentialCopy() {
        // Given
        String originalCode = "GET_USER";
        List<String> existingCodes = Arrays.asList(
                "GET_USER",
                "GET_USER_copy1",
                "GET_USER_copy2",
                "GET_USER_copy3"
        );

        // When
        String result = generator.generateCopyCode(originalCode, existingCodes);

        // Then
        assertEquals("GET_USER_copy4", result);
    }

    @Test
    void testGenerateCopyCode_CopyFromCopied() {
        // Given
        String originalCode = "GET_USER_copy2";
        List<String> existingCodes = Arrays.asList(
                "GET_USER",
                "GET_USER_copy1",
                "GET_USER_copy2",
                "GET_USER_copy3"
        );

        // When
        String result = generator.generateCopyCode(originalCode, existingCodes);

        // Then
        assertEquals("GET_USER_copy4", result);
    }

    @Test
    void testGenerateCopyCode_EmptyExistingList() {
        // Given
        String originalCode = "SEND_EMAIL";
        List<String> existingCodes = Collections.emptyList();

        // When
        String result = generator.generateCopyCode(originalCode, existingCodes);

        // Then
        assertEquals("SEND_EMAIL_copy1", result);
    }

    // ==================== 边界情况测试 ====================

    @Test
    void testGenerateCopyName_LargeNumber() {
        // Given - 存在 copy99
        String originalName = "动作";
        List<String> existingNames = Arrays.asList(
                "动作",
                "动作_copy99"
        );

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("动作_copy100", result);
    }

    @Test
    void testGenerateCopyName_MultipleUnderscores() {
        // Given - 名称包含多个下划线
        String originalName = "get_user_info_by_id";
        List<String> existingNames = Collections.singletonList("get_user_info_by_id");

        // When
        String result = generator.generateCopyName(originalName, existingNames);

        // Then
        assertEquals("get_user_info_by_id_copy1", result);
    }
}
