package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.module.flow.component.external.service.HttpExecuteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpNodeComponent 单元测试
 * 测试 HTTP 节点组件的变量替换等核心逻辑
 *
 * <p>注意：完整的组件集成测试需要 LiteFlow 框架支持，
 * 这里主要测试变量替换等可独立测试的逻辑
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@ExtendWith(MockitoExtension.class)
class HttpNodeComponentTest {

    private HttpNodeComponent httpNodeComponent;

    @Mock
    private HttpExecuteService httpExecuteService;

    @BeforeEach
    void setUp() {
        httpNodeComponent = new HttpNodeComponent();
        ReflectionTestUtils.setField(httpNodeComponent, "httpExecuteService", httpExecuteService);
    }

    @Test
    void testReplaceVariables_SingleVariable() throws Exception {
        // Given
        String template = "https://api.example.com/users/${userId}";
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "12345");

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("https://api.example.com/users/12345", result);
    }

    @Test
    void testReplaceVariables_MultipleVariables() throws Exception {
        // Given
        String template = "${protocol}://api.example.com/users/${userId}/posts/${postId}";
        Map<String, Object> context = new HashMap<>();
        context.put("protocol", "https");
        context.put("userId", "12345");
        context.put("postId", "67890");

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("https://api.example.com/users/12345/posts/67890", result);
    }

    @Test
    void testReplaceVariables_VariableInBody() throws Exception {
        // Given
        String template = "{\"name\":\"${userName}\",\"age\":${userAge}}";
        Map<String, Object> context = new HashMap<>();
        context.put("userName", "John");
        context.put("userAge", 30);

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("{\"name\":\"John\",\"age\":30}", result);
    }

    @Test
    void testReplaceVariables_VariableNotExists_KeepOriginal() throws Exception {
        // Given
        String template = "https://api.example.com/users/${nonExistentVar}";
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "12345");

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("https://api.example.com/users/${nonExistentVar}", result);
    }

    @Test
    void testReplaceVariables_NullTemplate() throws Exception {
        // Given
        String template = null;
        Map<String, Object> context = new HashMap<>();

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertNull(result);
    }

    @Test
    void testReplaceVariables_EmptyTemplate() throws Exception {
        // Given
        String template = "";
        Map<String, Object> context = new HashMap<>();

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("", result);
    }

    @Test
    void testReplaceVariables_NoVariables() throws Exception {
        // Given
        String template = "https://api.example.com/users";
        Map<String, Object> context = new HashMap<>();

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("https://api.example.com/users", result);
    }

    @Test
    void testReplaceVariables_NumericVariable() throws Exception {
        // Given
        String template = "https://api.example.com/users/${userId}?page=${page}";
        Map<String, Object> context = new HashMap<>();
        context.put("userId", 12345);
        context.put("page", 2);

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("https://api.example.com/users/12345?page=2", result);
    }

    @Test
    void testReplaceVariables_DuplicateVariable() throws Exception {
        // Given
        String template = "/users/${userId}/posts/${userId}";
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "12345");

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("/users/12345/posts/12345", result);
    }

    @Test
    void testReplaceVariables_AdjacentVariables() throws Exception {
        // Given
        String template = "${var1}${var2}${var3}";
        Map<String, Object> context = new HashMap<>();
        context.put("var1", "A");
        context.put("var2", "B");
        context.put("var3", "C");

        // When
        java.lang.reflect.Method method = HttpNodeComponent.class
                .getDeclaredMethod("replaceVariables", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(httpNodeComponent, template, context);

        // Then
        assertEquals("ABC", result);
    }
}
