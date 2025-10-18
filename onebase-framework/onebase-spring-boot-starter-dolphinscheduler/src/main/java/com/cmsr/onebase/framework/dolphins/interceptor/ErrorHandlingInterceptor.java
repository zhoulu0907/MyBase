package com.cmsr.onebase.framework.dolphins.interceptor;

import com.cmsr.onebase.framework.dolphins.exception.DolphinSchedulerErrorCode;
import com.cmsr.onebase.framework.dolphins.exception.DolphinSchedulerException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 错误处理拦截器 - 统一处理 HTTP 错误响应
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Slf4j
@Component
public class ErrorHandlingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        try {
            Response response = chain.proceed(request);
            
            // 如果响应成功，直接返回
            if (response.isSuccessful()) {
                return response;
            }
            
            // 处理错误响应
            handleErrorResponse(request, response);
            
            return response;
            
        } catch (SocketTimeoutException e) {
            log.error("请求超时: url={}", request.url(), e);
            throw new DolphinSchedulerException(
                    DolphinSchedulerErrorCode.TIMEOUT_ERROR,
                    "请求超时: " + request.url(),
                    e
            );
        } catch (UnknownHostException e) {
            log.error("网络异常，无法连接到服务器: url={}", request.url(), e);
            throw new DolphinSchedulerException(
                    DolphinSchedulerErrorCode.NETWORK_ERROR,
                    "网络异常，无法连接到服务器: " + request.url(),
                    e
            );
        } catch (IOException e) {
            log.error("网络异常: url={}", request.url(), e);
            throw new DolphinSchedulerException(
                    DolphinSchedulerErrorCode.NETWORK_ERROR,
                    "网络异常: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 处理错误响应
     *
     * @param request 请求
     * @param response 响应
     */
    private void handleErrorResponse(Request request, Response response) throws IOException {
        int statusCode = response.code();
        String url = request.url().toString();
        String errorBody = "";
        
        // 读取错误响应体
        ResponseBody body = response.body();
        if (body != null) {
            errorBody = body.string();
        }
        
        log.error("API 调用失败: url={}, status={}, body={}", url, statusCode, errorBody);
        
        // 根据 HTTP 状态码分类处理
        DolphinSchedulerErrorCode errorCode;
        String errorMessage;
        
        switch (statusCode) {
            case 401:
                errorCode = DolphinSchedulerErrorCode.TOKEN_INVALID;
                errorMessage = "Token 无效或已过期";
                break;
            case 403:
                errorCode = DolphinSchedulerErrorCode.PERMISSION_DENIED;
                errorMessage = "权限不足，无法访问该资源";
                break;
            case 404:
                errorCode = determineNotFoundError(url);
                errorMessage = errorCode.getMessage();
                break;
            case 409:
                errorCode = DolphinSchedulerErrorCode.RESOURCE_ALREADY_EXISTS;
                errorMessage = "资源已存在";
                break;
            case 400:
                errorCode = DolphinSchedulerErrorCode.PARAM_INVALID;
                errorMessage = "参数校验失败: " + errorBody;
                break;
            case 500:
            case 502:
            case 503:
            case 504:
                errorCode = DolphinSchedulerErrorCode.API_CALL_FAILED;
                errorMessage = "服务器内部错误: " + errorBody;
                break;
            default:
                errorCode = DolphinSchedulerErrorCode.UNKNOWN_ERROR;
                errorMessage = "未知错误，HTTP 状态码: " + statusCode;
        }
        
        throw new DolphinSchedulerException(statusCode, errorCode.getCode(), errorMessage);
    }

    /**
     * 根据 URL 路径判断 404 错误的具体类型
     *
     * @param url 请求 URL
     * @return 错误码
     */
    private DolphinSchedulerErrorCode determineNotFoundError(String url) {
        if (url.contains("/workflows/")) {
            return DolphinSchedulerErrorCode.WORKFLOW_NOT_FOUND;
        } else if (url.contains("/projects/")) {
            return DolphinSchedulerErrorCode.PROJECT_NOT_FOUND;
        } else if (url.contains("/tasks/")) {
            return DolphinSchedulerErrorCode.TASK_NOT_FOUND;
        } else if (url.contains("/schedules/")) {
            return DolphinSchedulerErrorCode.SCHEDULE_NOT_FOUND;
        } else if (url.contains("/queues/")) {
            return DolphinSchedulerErrorCode.QUEUE_NOT_FOUND;
        } else if (url.contains("/workflow-instances/")) {
            return DolphinSchedulerErrorCode.WORKFLOW_INSTANCE_NOT_FOUND;
        } else if (url.contains("/task-instances/")) {
            return DolphinSchedulerErrorCode.TASK_INSTANCE_NOT_FOUND;
        } else {
            return DolphinSchedulerErrorCode.API_CALL_FAILED;
        }
    }
}
