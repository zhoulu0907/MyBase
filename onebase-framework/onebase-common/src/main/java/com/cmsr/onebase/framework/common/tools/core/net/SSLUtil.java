package com.cmsr.onebase.framework.common.tools.core.net;

import com.cmsr.onebase.framework.common.tools.core.io.IORuntimeException;

import javax.net.ssl.SSLContext;

/**
 * SSL(Secure Sockets Layer 安全套接字协议)相关工具封装
 *
 * @author looly
 * @since 5.5.2
 */
public class SSLUtil {

    /**
     * 创建{@link SSLContext}，默认新人全部
     *
     * @param protocol SSL协议，例如TLS等
     * @return {@link SSLContext}
     * @throws IORuntimeException 包装 GeneralSecurityException异常
     * @since 5.7.8
     */
    public static SSLContext createSSLContext(String protocol) throws IORuntimeException {
        return SSLContextBuilder.create().setProtocol(protocol).build();
    }
}