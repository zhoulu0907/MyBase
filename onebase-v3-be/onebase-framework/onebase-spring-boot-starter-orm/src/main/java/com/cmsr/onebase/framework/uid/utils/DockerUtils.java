/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cmsr.onebase.framework.uid.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Docker环境工具类，用于检测应用是否运行在Docker环境中，并获取Docker的主机和端口信息
 *
 * @author yutianbao
 */
public class DockerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerUtils.class);

    /**
     * Environment param keys
     */
    private static final String ENV_KEY_HOST = "KUBERNETES_SERVICE_HOST";
    private static final String ENV_KEY_PORT = "KUBERNETES_SERVICE_PORT";

    /**
     * Docker host & port
     */
    private static String DOCKER_HOST = "";
    private static String DOCKER_PORT = "";

    /**
     * Whether is docker
     */
    private static boolean IS_DOCKER = false;

    static {
        retrieveFromEnv();
    }

    /**
     * 私有构造函数，防止实例化
     */
    private DockerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 获取Docker主机地址
     *
     * @return Docker主机地址，如果不在Docker环境中则返回空字符串
     */
    public static String getDockerHost() {
        return DOCKER_HOST;
    }

    /**
     * 获取Docker端口
     *
     * @return Docker端口，如果不在Docker环境中则返回空字符串
     */
    public static String getDockerPort() {
        return DOCKER_PORT;
    }

    /**
     * 判断当前应用是否运行在Docker环境中
     *
     * @return true表示在Docker环境中运行，false表示不在Docker环境中运行
     */
    public static boolean isDocker() {
        return IS_DOCKER;
    }

    /**
     * 从环境变量中获取Docker主机和端口信息
     */
    private static void retrieveFromEnv() {
        try {
            // 从环境变量获取主机和端口
            DOCKER_HOST = System.getenv(ENV_KEY_HOST);
            DOCKER_PORT = System.getenv(ENV_KEY_PORT);

            boolean hasEnvHost = StringUtils.isNotBlank(DOCKER_HOST);
            boolean hasEnvPort = StringUtils.isNotBlank(DOCKER_PORT);

            // 如果能同时找到主机和端口，则认为是在Docker环境中
            if (hasEnvHost && hasEnvPort) {
                IS_DOCKER = true;
            } else if (!hasEnvHost && !hasEnvPort) {
                IS_DOCKER = false;
            } else {
                LOGGER.error("Missing host or port from env for Docker. host:{}, port:{}", DOCKER_HOST, DOCKER_PORT);
            }
        } catch (Exception e) {
            // 捕获可能的异常，避免影响应用启动
            LOGGER.error("Error while detecting Docker environment", e);
            IS_DOCKER = false;
        }
    }
}