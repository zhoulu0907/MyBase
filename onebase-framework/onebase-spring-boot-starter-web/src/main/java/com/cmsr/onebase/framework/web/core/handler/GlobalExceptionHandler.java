package com.cmsr.onebase.framework.web.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.cmsr.onebase.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import com.cmsr.onebase.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.*;

/**
 * 全局异常处理器，将 Exception 翻译成 CommonResult + 对应的异常编号
 */
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 忽略的 ServiceException 错误提示，避免打印过多 logger
     */
    public static final Set<String> IGNORE_ERROR_MESSAGES = Set.of("无效的刷新令牌");

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final String applicationName;

    private final ApiErrorLogCommonApi apiErrorLogApi;

    private final com.cmsr.onebase.framework.web.config.WebProperties webProperties;

    /**
     * 处理所有异常，主要是提供给 Filter 使用
     * 因为 Filter 不走 SpringMVC 的流程，但是我们又需要兜底处理异常，所以这里提供一个全量的异常处理过程，保持逻辑统一。
     *
     * @param request 请求
     * @param ex      异常
     * @return 通用返回
     */
    public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return missingServletRequestParameterExceptionHandler((MissingServletRequestParameterException) ex);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return methodArgumentTypeMismatchExceptionHandler((MethodArgumentTypeMismatchException) ex);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            return methodArgumentNotValidExceptionExceptionHandler((MethodArgumentNotValidException) ex);
        }
        if (ex instanceof BindException) {
            return bindExceptionHandler((BindException) ex);
        }
        if (ex instanceof ConstraintViolationException) {
            return constraintViolationExceptionHandler((ConstraintViolationException) ex);
        }
        if (ex instanceof ValidationException) {
            return validationException((ValidationException) ex);
        }
        if (ex instanceof HttpMessageNotReadableException) {
            return methodArgumentTypeInvalidFormatExceptionHandler((HttpMessageNotReadableException) ex);
        }
        if (ex instanceof NoHandlerFoundException) {
            return noHandlerFoundExceptionHandler((NoHandlerFoundException) ex);
        }
        if (ex instanceof NoResourceFoundException) {
            return noResourceFoundExceptionHandler(request, (NoResourceFoundException) ex);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return httpRequestMethodNotSupportedExceptionHandler((HttpRequestMethodNotSupportedException) ex);
        }
        if (ex instanceof ServiceException) {
            return serviceExceptionHandler((ServiceException) ex);
        }
        if (ex instanceof IllegalArgumentException) {
            return illegalArgumentExceptionHandler(request, (IllegalArgumentException) ex);
        }
        if (ex instanceof IllegalStateException) {
            return illegalStateExceptionHandler(request, (IllegalStateException) ex);
        }
        if (ex instanceof AccessDeniedException) {
            return accessDeniedExceptionHandler(request, (AccessDeniedException) ex);
        }
        return defaultExceptionHandler(request, ex);
    }

    /**
     * 处理 SpringMVC 请求参数缺失
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        // 获取 errorMessage
        String errorMessage = null;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            // 组合校验，参考自 https://t.zsxq.com/3HVTx
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            if (CollUtil.isNotEmpty(allErrors)) {
                errorMessage = allErrors.get(0).getDefaultMessage();
            }
        } else {
            errorMessage = fieldError.getDefaultMessage();
        }
        // 转换 CommonResult
        if (StrUtil.isEmpty(errorMessage)) {
            return CommonResult.error(BAD_REQUEST);
        }
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", errorMessage));
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        if (fieldError == null) {
            return CommonResult.error(BAD_REQUEST.getCode(), "请求参数不正确");
        }
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误和JSON格式错误
     * <p>
     * 例如说，接口上设置了 @RequestBody实体中 xx 属性类型为 Integer，结果传递 xx 参数类型为 String
     * 或者JSON格式错误，包含非法字符、格式不正确等
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<?> methodArgumentTypeInvalidFormatExceptionHandler(HttpMessageNotReadableException ex) {
        log.warn("[methodArgumentTypeInvalidFormatExceptionHandler]", ex);

        // 处理具体的JSON格式类型错误
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", invalidFormatException.getValue()));
        }

        // 处理JSON解析异常（如格式错误、非法字符等）
        if (ex.getCause() instanceof com.fasterxml.jackson.core.JsonParseException) {
            com.fasterxml.jackson.core.JsonParseException parseException = (com.fasterxml.jackson.core.JsonParseException) ex.getCause();
            String errorMsg = parseException.getOriginalMessage();
            // 提取更友好的错误信息
            if (errorMsg != null && errorMsg.contains("Illegal unquoted character")) {
                return CommonResult.error(BAD_REQUEST.getCode(), "JSON格式错误：请求参数中包含非法字符（如未转义的换行符或特殊字符），请检查参数格式");
            } else if (errorMsg != null && errorMsg.contains("Unexpected character")) {
                return CommonResult.error(BAD_REQUEST.getCode(), "JSON格式错误：请求参数包含意外的字符，请检查JSON格式");
            } else {
                return CommonResult.error(BAD_REQUEST.getCode(), String.format("JSON格式错误：%s", errorMsg != null ? errorMsg : "请求参数格式不正确"));
            }
        }

        // 处理JSON映射异常（如类型不匹配、字段缺失等）
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            com.fasterxml.jackson.databind.JsonMappingException mappingException = (com.fasterxml.jackson.databind.JsonMappingException) ex.getCause();
            String originalMessage = mappingException.getOriginalMessage();

            // 特殊处理 MismatchedInputException，提供更友好的错误提示
            if (mappingException instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
                com.fasterxml.jackson.databind.exc.MismatchedInputException mismatchException =
                        (com.fasterxml.jackson.databind.exc.MismatchedInputException) mappingException;

                // 获取目标类型名称
                String targetType = mismatchException.getTargetType() != null ?
                        mismatchException.getTargetType().getSimpleName() : "对象";

                // 获取字段路径
                String fieldPath = "";
                if (mismatchException.getPath() != null && !mismatchException.getPath().isEmpty()) {
                    fieldPath = mismatchException.getPath().stream()
                            .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                            .reduce((a, b) -> a + "." + b)
                            .orElse("");
                }

                // 构造友好的错误提示
                String errorMessage = "JSON数据格式错误";
                if (StrUtil.isNotBlank(fieldPath)) {
                    errorMessage += "，字段 [" + fieldPath + "]";
                }
                errorMessage += " 需要是 " + targetType + " 类型";

                // 针对Map类型的特殊提示
                if (targetType.contains("Map") || targetType.contains("HashMap") || targetType.contains("LinkedHashMap")) {
                    errorMessage += "，请确保该字段的值是一个JSON对象（用{}包裹），而不是字符串或其他类型";
                }

                return CommonResult.error(BAD_REQUEST.getCode(), errorMessage);
            }

            // 其他JSON映射异常
            return CommonResult.error(BAD_REQUEST.getCode(), String.format("JSON映射错误：%s", originalMessage));
        }

        // 其他HTTP消息不可读异常，返回通用的JSON格式错误提示
        String message = ex.getMessage();
        if (message != null && message.contains("JSON")) {
            return CommonResult.error(BAD_REQUEST.getCode(), "JSON格式错误：请求参数格式不正确，请检查JSON格式");
        }

        // 其他未知类型的异常
        return CommonResult.error(BAD_REQUEST.getCode(), "请求参数格式错误：无法解析请求体");
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }

    /**
     * 处理 Dubbo Consumer 本地参数校验时，抛出的 ValidationException 异常
     */
    @ExceptionHandler(value = ValidationException.class)
    public CommonResult<?> validationException(ValidationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        // 无法拼接明细的错误信息，因为 Dubbo Consumer 抛出 ValidationException 异常时，是直接的字符串信息，且人类不可读
        return CommonResult.error(BAD_REQUEST);
    }

    /**
     * 处理 SpringMVC 请求地址不存在
     * <p>
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getRequestURL()));
    }

    /**
     * 处理 SpringMVC 请求地址不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    private CommonResult<?> noResourceFoundExceptionHandler(HttpServletRequest req, NoResourceFoundException ex) {
        log.warn("[noResourceFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getResourcePath()));
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     * <p>
     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return CommonResult.error(METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }

    /**
     * 处理 Spring Security 权限不足的异常
     * <p>
     * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]", SecurityFrameworkUtils.getLoginUserId(),
                req.getRequestURL(), ex);
        return CommonResult.error(FORBIDDEN);
    }

    /**
     * 处理业务异常 ServiceException
     * <p>
     * 例如说，商品库存不足，用户手机号已存在。
     */
    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        // 不包含的时候，才进行打印，避免 ex 堆栈过多
        if (!IGNORE_ERROR_MESSAGES.contains(ex.getMessage())) {
            // 即使打印，也只打印第一层 StackTraceElement，并且使用 warn 在控制台输出，更容易看到
            try {
                StackTraceElement[] stackTraces = ex.getStackTrace();
                for (StackTraceElement stackTrace : stackTraces) {
                    if (ObjUtil.notEqual(stackTrace.getClassName(), ServiceExceptionUtil.class.getName())) {
                        log.warn("[serviceExceptionHandler]\n\t{}", stackTrace);
                        break;
                    }
                }
            } catch (Exception ignored) {
                // 忽略日志，避免影响主流程
            }
        }
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理 IllegalArgumentException 异常
     * <p>
     * 通常是业务参数校验失败，将具体的错误信息返回给前端
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public CommonResult<?> illegalArgumentExceptionHandler(HttpServletRequest req, IllegalArgumentException ex) {
        log.warn("[illegalArgumentExceptionHandler][url({}) 参数校验失败: {}]", req.getRequestURI(), ex.getMessage(), ex);
        // 将具体的错误信息放到 msg 中返回
        String errorMessage = StrUtil.isNotBlank(ex.getMessage()) ? ex.getMessage() : "参数校验失败";
        return CommonResult.error(BAD_REQUEST.getCode(), errorMessage);
    }

    /**
     * 处理 IllegalStateException 异常
     * <p>
     * 通常是业务状态校验失败，将具体的错误信息返回给前端
     */
    @ExceptionHandler(value = IllegalStateException.class)
    public CommonResult<?> illegalStateExceptionHandler(HttpServletRequest req, IllegalStateException ex) {
        log.warn("[illegalStateExceptionHandler][url({}) 状态校验失败: {}]", req.getRequestURI(), ex.getMessage(), ex);
        // 将具体的错误信息放到 msg 中返回
        String errorMessage = StrUtil.isNotBlank(ex.getMessage()) ? ex.getMessage() : "状态校验失败";
        return CommonResult.error(BAD_REQUEST.getCode(), errorMessage);
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        log.error("[defaultExceptionHandler]", ex);

        // 优先检查异常消息中是否包含"校验失败"关键字，如果包含则认为是业务异常
        // 这样可以提取到完整的错误信息（包含字段名和校验类型）
        String exceptionMessage = ex.getMessage();
        if (StrUtil.isNotBlank(exceptionMessage) && exceptionMessage.contains("校验失败")) {
            // 提取具体的校验失败信息
            String errorMessage = extractValidationError(exceptionMessage);
            log.warn("[defaultExceptionHandler][业务校验失败] url: {}, 错误: {}", req.getRequestURI(), errorMessage);
            return CommonResult.error(BAD_REQUEST.getCode(), errorMessage);
        }

        // 其次检查根因异常，如果是业务相关的异常，提取具体错误信息
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause != null && rootCause instanceof IllegalArgumentException) {
            // 业务参数校验失败，返回具体错误信息
            String errorMessage = StrUtil.isNotBlank(rootCause.getMessage()) ? rootCause.getMessage() : "参数校验失败";
            log.warn("[defaultExceptionHandler][业务参数异常] url: {}, 错误: {}", req.getRequestURI(), errorMessage);
            return CommonResult.error(BAD_REQUEST.getCode(), errorMessage);
        }

        // 插入异常日志
        createExceptionLog(req, ex);
        // 返回 ERROR；在开发/测试环境返回结构化 JSON（放入 data），生产只返回简要 msg
        if (webProperties != null && webProperties.isReturnExceptionStackTrace()) {
            Map<String, Object> body = MapUtil.<String, Object>builder()
                    .put("message", ExceptionUtils.getMessage(ex))
                    .put("rootCause", ExceptionUtils.getRootCauseMessage(ex))
                    .put("stackTrace", Arrays.stream(ExceptionUtils.getStackFrames(ex)).map(stackTrace -> stackTrace.replace("\t", "  ")).toArray())
                    .put("timestamp", LocalDateTime.now().toString())
                    .put("path", req.getRequestURI())
                    .put("method", req.getMethod())
                    .build();
            return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg(), body);
        } else {
            return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
        }
    }

    /**
     * 从异常消息中提取校验错误信息
     *
     * @param exceptionMessage 异常消息
     * @return 提取后的错误信息
     */
    private String extractValidationError(String exceptionMessage) {
        // 尝试匹配 "执行xxx数据异常：字段[xxx]-校验类型-校验失败：yyy" 格式
        // 提取从 "字段[" 开始的完整错误信息
        if (StrUtil.isNotBlank(exceptionMessage)) {
            // 查找 "字段[" 的位置
            int fieldStartIndex = exceptionMessage.indexOf("字段[");
            if (fieldStartIndex >= 0) {
                // 从 "字段[" 开始提取完整的错误信息
                return exceptionMessage.substring(fieldStartIndex);
            }

            // 如果没有找到 "字段["，则查找最后一个冒号
            int lastColonIndex = exceptionMessage.lastIndexOf("：");
            if (lastColonIndex > 0 && lastColonIndex < exceptionMessage.length() - 1) {
                return exceptionMessage.substring(lastColonIndex + 1).trim();
            }
        }

        // 如果都没找到，返回完整的异常消息
        return exceptionMessage;
    }

    private void createExceptionLog(HttpServletRequest req, Throwable e) {
        // 插入错误日志
        ApiErrorLogCreateReqDTO errorLog = new ApiErrorLogCreateReqDTO();
        try {
            // 初始化 errorLog
            buildExceptionLog(errorLog, req, e);
            // 执行插入 errorLog
            apiErrorLogApi.createApiErrorLogAsync(errorLog);
        } catch (Throwable th) {
            log.error("[createExceptionLog][url({}) log({}) 发生异常]", req.getRequestURI(), JsonUtils.toJsonString(errorLog), th);
        }
    }

    private void buildExceptionLog(ApiErrorLogCreateReqDTO errorLog, HttpServletRequest request, Throwable e) {
        // 处理用户信息
        errorLog.setUserId(SecurityFrameworkUtils.getLoginUserId());
        errorLog.setUserType(WebFrameworkUtils.getLoginUserType(request));
        // 设置异常字段
        errorLog.setExceptionName(e.getClass().getName());
        errorLog.setExceptionMessage(ExceptionUtils.getMessage(e));
        errorLog.setExceptionRootCauseMessage(ExceptionUtils.getRootCauseMessage(e));
        errorLog.setExceptionStackTrace(ExceptionUtils.getStackTrace(e));
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        Assert.notEmpty(stackTraceElements, "异常 stackTraceElements 不能为空");
        StackTraceElement stackTraceElement = stackTraceElements[0];
        errorLog.setExceptionClassName(stackTraceElement.getClassName());
        errorLog.setExceptionFileName(stackTraceElement.getFileName());
        errorLog.setExceptionMethodName(stackTraceElement.getMethodName());
        errorLog.setExceptionLineNumber(stackTraceElement.getLineNumber());
        // 设置其它字段
        errorLog.setTraceId("n/a");
        errorLog.setApplicationName(applicationName);
        errorLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                .put("query", JakartaServletUtil.getParamMap(request))
                .put("body", JakartaServletUtil.getBody(request)).build();
        errorLog.setRequestParams(JsonUtils.toJsonString(requestParams));
        errorLog.setRequestMethod(request.getMethod());
        errorLog.setUserAgent(ServletUtils.getUserAgent(request));
        errorLog.setUserIp(JakartaServletUtil.getClientIP(request));
        errorLog.setExceptionTime(LocalDateTime.now());
    }


}
