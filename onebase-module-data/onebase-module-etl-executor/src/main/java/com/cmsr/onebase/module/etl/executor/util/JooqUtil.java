package com.cmsr.onebase.module.etl.executor.util;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/**
 * @Author：huangjie
 * @Date：2025/11/8 20:03
 */
public class JooqUtil {
    public static final DSLContext DSL_CONTEXT = DSL.using(SQLDialect.DEFAULT,
            new Settings()
                    .withRenderFormatted(true)
                    .withRenderOptionalOuterKeyword(RenderOptionalKeyword.OFF));

}
