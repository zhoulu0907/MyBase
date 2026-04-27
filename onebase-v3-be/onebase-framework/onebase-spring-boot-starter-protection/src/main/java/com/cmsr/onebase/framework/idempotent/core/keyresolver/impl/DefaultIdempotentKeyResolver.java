package com.cmsr.onebase.framework.idempotent.core.keyresolver.impl;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.idempotent.core.annotation.Idempotent;
import com.cmsr.onebase.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 默认（全局级别）幂等 Key 解析器，使用方法名 + 方法参数，组装成一个 Key
 *
 * 为了避免 Key 过长，使用 MD5 进行“压缩”
 *
 */
public class DefaultIdempotentKeyResolver implements IdempotentKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtil.join(",", joinPoint.getArgs());
        return DigestUtils.md5DigestAsHex((methodName + argsStr).getBytes(StandardCharsets.UTF_8));
    }

}
