package com.cmsr.onebase.framework.security.build.context;

import java.util.Map;

public class AiBridgeContextHolder {

    private static final ThreadLocal<Context> CTX = new ThreadLocal<>();

    public static void set(Context ctx) {
        CTX.set(ctx);
    }

    public static Context get() {
        return CTX.get();
    }

    public static void clear() {
        CTX.remove();
    }

    public static class Context {
        private final String requestId;
        private final String keyId;
        private final Map<String, Object> meta;
        private final String userId;
        private final String tenantId;
        private final String appId;

        public Context(String requestId, String keyId, Map<String, Object> meta,
                       String userId, String tenantId, String appId) {
            this.requestId = requestId;
            this.keyId = keyId;
            this.meta = meta;
            this.userId = userId;
            this.tenantId = tenantId;
            this.appId = appId;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getKeyId() {
            return keyId;
        }

        public Map<String, Object> getMeta() {
            return meta;
        }

        public String getUserId() {
            return userId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getAppId() {
            return appId;
        }
    }
}
