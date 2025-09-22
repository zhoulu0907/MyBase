package com.cmsr.onebase.framework.common.tools.extra.ssh;


import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

/**
 * Jsch异常
 * @author xiaoleilu
 */
public class JschRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 8247610319171014183L;

	public JschRuntimeException(Throwable e) {
        super(null == e ? StrUtil.NULL : StrUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage()), e);
    }

	public JschRuntimeException(String message) {
		super(message);
	}

	public JschRuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public JschRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public JschRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public JschRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
