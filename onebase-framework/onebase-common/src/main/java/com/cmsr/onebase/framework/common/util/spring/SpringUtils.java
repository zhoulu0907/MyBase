package com.cmsr.onebase.framework.common.util.spring;

import com.cmsr.onebase.framework.common.tools.extra.spring.SpringUtil;

import java.util.Objects;

/**
 * Spring 工具类
 *
 */
public class SpringUtils extends SpringUtil {

    /**
     * 是否为生产环境
     *
     * @return 是否生产环境
     */
    public static boolean isProd() {
        String activeProfile = getActiveProfile();
        return Objects.equals("prod", activeProfile);
    }

}
