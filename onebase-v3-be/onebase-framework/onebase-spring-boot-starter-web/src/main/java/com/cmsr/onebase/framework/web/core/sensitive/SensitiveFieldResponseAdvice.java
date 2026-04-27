package com.cmsr.onebase.framework.web.core.sensitive;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 敏感字段响应拦截器
 *
 * 针对特定接口的响应数据进行脱敏处理
 * 主要处理无法通过注解方式处理的字段（如动态字段、嵌套对象中的字段）
 */
@Slf4j
@RestControllerAdvice
public class SensitiveFieldResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 硬编码配置：接口路径 -> 需要脱敏的字段名
     */
    private static final Map<String, Set<String>> SENSITIVE_API_FIELDS = Map.of(
        "/admin-api/app/application/page", Set.of("createUser", "updateUser")
    );

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 所有接口都可能需要处理，在beforeBodyWrite中根据路径判断
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 获取请求路径
        String path = getRequestPath(request);
        if (path == null) {
            return body;
        }

        // 检查是否是需要脱敏的接口
        Set<String> fields = SENSITIVE_API_FIELDS.get(path);
        if (fields == null || fields.isEmpty()) {
            return body;
        }

        try {
            // 处理响应数据
            processSensitiveFields(body, fields);
        } catch (Exception e) {
            log.warn("敏感字段脱敏处理失败: {}", e.getMessage());
        }

        return body;
    }

    /**
     * 获取请求路径
     */
    private String getRequestPath(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            return httpRequest.getRequestURI();
        }
        return null;
    }

    /**
     * 处理响应数据中的敏感字段
     */
    private void processSensitiveFields(Object obj, Set<String> fields) {
        if (obj == null) {
            return;
        }

        // 处理CommonResult包装
        if (obj instanceof CommonResult<?> result) {
            Object data = result.getData();
            if (data instanceof PageResult<?> pageResult) {
                // 处理分页数据
                processPageResult(pageResult, fields);
            } else {
                // 处理单个对象
                processObject(data, fields);
            }
        }
    }

    /**
     * 处理分页结果
     */
    private void processPageResult(PageResult<?> pageResult, Set<String> fields) {
        List<?> list = pageResult.getList();
        if (list == null || list.isEmpty()) {
            return;
        }

        for (Object item : list) {
            processObject(item, fields);
        }
    }

    /**
     * 处理单个对象
     */
    private void processObject(Object obj, Set<String> fields) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();

        for (String fieldName : fields) {
            try {
                Field field = findField(clazz, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value instanceof String strValue && StringUtils.isNotEmpty(strValue)) {
                        // 对字符串值进行脱敏：前1位 + **
                        String maskedValue = maskString(strValue);
                        field.set(obj, maskedValue);
                    }
                }
            } catch (Exception e) {
                log.debug("字段脱敏失败: {} - {}", fieldName, e.getMessage());
            }
        }
    }

    /**
     * 查找字段（包括父类）
     */
    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 字符串脱敏：前1位 + **
     */
    private String maskString(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return value.substring(0, 1) + "**";
    }
}