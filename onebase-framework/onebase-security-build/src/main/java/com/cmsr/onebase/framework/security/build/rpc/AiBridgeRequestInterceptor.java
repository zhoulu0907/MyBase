package com.cmsr.onebase.framework.security.build.rpc;

import com.cmsr.onebase.framework.security.build.config.AiBridgeProperties;
import com.cmsr.onebase.framework.security.build.context.AiBridgeContextHolder;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.security.build.util.AiBridgeCryptoUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class AiBridgeRequestInterceptor implements RequestInterceptor {

    private static final String HDR_KEY_ID = "X-AI-KeyId";
    private static final String HDR_SIG = "X-AI-Signature";
    private static final String HDR_TS = "X-AI-Timestamp";
    private static final String HDR_NONCE = "X-AI-Nonce";
    private static final String HDR_REQ_ID = "X-AI-Request-Id";
    private static final String HDR_USER_ID = "X-AI-User-Id";
    private static final String HDR_TENANT_ID = "X-AI-Tenant-Id";
    private static final String HDR_APP_ID = "X-AI-App-Id";
    // Meta 加密已移除，保留最简签名校验

    private final AiBridgeProperties properties;

    @Override
    public void apply(RequestTemplate template) {
        AiBridgeContextHolder.Context ctx = AiBridgeContextHolder.get();
        if (ctx == null) {
            return;
        }
        String reqId = Objects.toString(ctx.getRequestId(), java.util.UUID.randomUUID().toString());
        String ts = String.valueOf(System.currentTimeMillis());
        String nonce = java.util.UUID.randomUUID().toString().replace("-", "");
        template.header(HDR_REQ_ID, reqId);
        if (StringUtils.isNotBlank(ctx.getKeyId())) {
            template.header(HDR_KEY_ID, ctx.getKeyId());
        }
        if (StringUtils.isNotBlank(ctx.getUserId())) {
            template.header(HDR_USER_ID, ctx.getUserId());
        }
        if (StringUtils.isNotBlank(ctx.getTenantId())) {
            template.header(HDR_TENANT_ID, ctx.getTenantId());
        }
        if (StringUtils.isNotBlank(ctx.getAppId())) {
            template.header(HDR_APP_ID, ctx.getAppId());
        }
        if (StringUtils.isNotBlank(properties.getSm3Key())) {
            String userId = StringUtils.defaultString(ctx.getUserId());
            String tenantId = StringUtils.defaultString(ctx.getTenantId());
            String appId = StringUtils.defaultString(ctx.getAppId());
            String canonical = template.method() + "|" + template.path() + "|" + ts + "|" + nonce + "|" + userId + "|" + tenantId + "|" + appId + "|";
            String sig = AiBridgeCryptoUtils.hmacSm3Hex(properties.getSm3Key(), canonical);
            template.header(HDR_TS, ts);
            template.header(HDR_NONCE, nonce);
            template.header(HDR_SIG, sig);
        }
    }
}
