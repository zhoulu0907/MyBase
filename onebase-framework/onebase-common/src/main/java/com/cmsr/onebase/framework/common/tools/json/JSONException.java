package com.cmsr.onebase.framework.common.tools.json;


import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

/**
 * JSON异常
 *
 * @author looly
 * @since 3.0.2
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public JSONException(Throwable e) {
        super(null == e ? StrUtil.NULL : StrUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage()), e);
    }

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public JSONException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
