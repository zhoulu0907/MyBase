package com.cmsr.onebase.framework.signature.core.aop;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.signature.config.ApiSignatureProperties;
import com.cmsr.onebase.framework.signature.core.redis.ApiSignatureRedisDAO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.cmsr.onebase.framework.common.consts.ENConstant.*;
import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

@Slf4j
public class ApiSignHelper {

    @Resource
    private ApiSignatureRedisDAO signatureRedisDAO;

    /**
     * 签名请求超时时间（秒）
     */
    private final Integer signRequestTimeout;

    /**
     * 构造方法
     *
     * @param properties API签名配置属性
     */
    public ApiSignHelper(ApiSignatureProperties properties) {
        this.signRequestTimeout = properties.getRequestTimeout();
    }

    public boolean verifySignature(HttpServletRequest request) {
        if(!signatureRedisDAO.isApiSignEnabled()){
            return true;
        }

        // 1.1 校验 Header
        verifyHeaders(request);

        // 1.2 校验 appId 是否能获取到对应的 appSecret
        String appKey = SIGN_APP_KEY;
        String appSecret = SIGN_APP_SECRET;

        // 2. 校验签名【重要！】
        String clientSignature = request.getHeader(SIGN_HEADER_KEY_SIGN); // 客户端签名
        String serverSignatureString = buildSignatureString(request, appSecret); // 服务端签名字符串
        String serverSignature = DigestUtil.sha256Hex(serverSignatureString); // 服务端签名
        if (ObjUtil.notEqual(clientSignature, serverSignature)) {
            throw new ServiceException(BAD_REQUEST.getCode(), "请求签名不正确");
        }

        // 3. 将 nonce 记入缓存，防止重复使用（此处将 ttl 设定为允许 timestamp 时间差的值 x 2 ）
        String nonce = request.getHeader(SIGN_HEADER_KEY_NONCE);
        if (BooleanUtil.isFalse(signatureRedisDAO.setNonce(appKey, nonce, signRequestTimeout * 2, TimeUnit.SECONDS))) {
            String timestamp = request.getHeader(SIGN_HEADER_KEY_TIME);
            log.info("[verifySignature][appId({}) timestamp({}) nonce({}) sign({}) 存在重复请求]", appKey, timestamp, nonce, clientSignature);
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), "存在重复请求");
        }
        return true;
    }

    /**
     * 校验请求头加签参数
     * <p>
     * 1. appId 是否为空
     * 2. timestamp 是否为空，请求是否已经超时，默认 10 分钟
     * 3. nonce 是否为空，随机数是否 10 位以上，是否在规定时间内已经访问过了
     * 4. sign 是否为空
     *
     * @param request   request
     * @return 是否校验 Header 通过
     */
    private void verifyHeaders(HttpServletRequest request) {
        // 1. 非空校验
        String sign = request.getHeader(SIGN_HEADER_KEY_SIGN);
        if (StrUtil.isBlank(sign)) {
            throw new ServiceException(BAD_REQUEST.getCode(), "API签名为空");
        }

        // 2. 检查 timestamp 是否超出允许的范围 （此处需要取绝对值）
        String timestamp = request.getHeader(SIGN_HEADER_KEY_TIME);
        if (StrUtil.isBlank(timestamp)) {
            throw new ServiceException(BAD_REQUEST.getCode(), "请求时间为空");
        }
        long expireTime = TimeUnit.SECONDS.toMillis(signRequestTimeout);
        long requestTimestamp = Long.parseLong(timestamp);
        long timestampDisparity = Math.abs(System.currentTimeMillis() - requestTimestamp);
        if (timestampDisparity > expireTime) {
            throw new ServiceException(BAD_REQUEST.getCode(), "请求时间已过期");
        }

        // 3. 检查 nonce 是否存在，有且仅能使用一次
        String appKey = SIGN_APP_KEY;
        String nonce = request.getHeader(SIGN_HEADER_KEY_NONCE);
        if (StrUtil.length(nonce) < 10) {
            throw new ServiceException(BAD_REQUEST.getCode(), "请求随机数过短");
        }
        String existNonce = signatureRedisDAO.getNonce(appKey, nonce);
        if (StringUtils.isNotBlank(existNonce)) {
            throw new ServiceException(BAD_REQUEST.getCode(), "请求重复");
        }
    }

    /**
     * 构建签名字符串
     * <p>
     * 格式为 = 请求参数 + 请求体 + 请求头 + 密钥
     *
     * @param request   request
     * @param appSecret appSecret
     * @return 签名字符串
     */
    private String buildSignatureString(HttpServletRequest request, String appSecret) {
        SortedMap<String, String> parameterMap = getRequestParameterMap(request); // 请求头
        SortedMap<String, String> headerMap = getRequestHeaderMap(request); // 请求参数
        String requestBody = StrUtil.nullToDefault(ServletUtils.getBody(request), ""); // 请求体
        return MapUtil.join(parameterMap, "&", "=")
                + requestBody
                + MapUtil.join(headerMap, "&", "=")
                + appSecret;
    }

    /**
     * 获取请求头加签参数 Map
     *
     * @param request   请求
     * @return signature params
     */
    private static SortedMap<String, String> getRequestHeaderMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put(SIGN_HEADER_KEY_APPKEY, request.getHeader(SIGN_HEADER_KEY_APPKEY));
        sortedMap.put(SIGN_HEADER_KEY_TIME, request.getHeader(SIGN_HEADER_KEY_TIME));
        sortedMap.put(SIGN_HEADER_KEY_NONCE, request.getHeader(SIGN_HEADER_KEY_NONCE));
        return sortedMap;
    }

    /**
     * 获取请求参数 Map
     *
     * @param request 请求
     * @return queryParams
     */
    private static SortedMap<String, String> getRequestParameterMap(HttpServletRequest request) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return sortedMap;
    }
}
