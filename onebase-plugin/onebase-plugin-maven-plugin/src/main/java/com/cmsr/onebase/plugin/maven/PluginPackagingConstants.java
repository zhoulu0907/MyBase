package com.cmsr.onebase.plugin.maven;

import java.util.Set;

/**
 * 插件打包常量
 * <p>
 * 定义插件打包和开发模式下的共享常量，确保一致性。
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-14
 */
public final class PluginPackagingConstants {

    /**
     * 宿主核心模块，不应打入插件 ZIP 包，也不应写入 dev-dependencies-classpath.txt
     * <p>
     * 采用保守策略，仅排除 OneBase 核心模块：
     * - onebase-plugin-sdk: 插件 SDK，由宿主提供
     * - onebase-common: 公共工具库，由宿主提供
     * - onebase-spring-boot-starter-plugin: 插件运行时，由宿主提供
     * - onebase-plugin-core: 插件核心，由宿主提供
     * - onebase-plugin-host-simulator: 模拟器，仅用于本地调试
     * - spring-boot-starter-web: Spring Boot Web
     * starter，由宿主提供，该依赖提供RestController注解等
     * </p>
     */
    public static final Set<String> HOST_PROVIDED_ARTIFACTS = Set.of(
            "onebase-plugin-sdk",
            "onebase-common",
            "onebase-spring-boot-starter-plugin",
            "onebase-plugin-core",
            "onebase-plugin-host-simulator",
            "spring-boot-starter-web");

    /**
     * 开发模式下依赖类路径文件名
     * <p>
     * 该文件在编译时生成，包含插件所有运行时依赖的绝对路径（每行一个路径）。
     * 在 dev 模式下，HotReloadManager 会读取该文件并将依赖加载到 ClassLoader 中。
     * </p>
     */
    public static final String DEV_DEPENDENCIES_CLASSPATH_FILE = "dev-dependencies-classpath.txt";

    /**
     * 私有构造函数，防止实例化
     */
    private PluginPackagingConstants() {}
}
