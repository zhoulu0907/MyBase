package com.cmsr.onebase.plugin.ocr.mock;

import com.cmsr.onebase.plugin.service.PluginContextService;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * 本地模拟 PluginContextService，用于从本地 application.yml 读取配置
 */
@Service
public class LocalPluginContextService implements PluginContextService, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Long getTenantId() {
        return 1L; // 模拟 Tenant ID
    }

    @Override
    public Long getApplicationId() {
        return 100L; // 模拟 Application ID
    }

    @Override
    public Map<String, Object> getConfig(String pluginId, String version) {
        if ("onebase-plugin-ocr".equals(pluginId)) {
             try {
                 // 读取 onebase.plugin.ocr 下的配置
                 return Binder.get(environment)
                         .bind("onebase.plugin.ocr", Map.class)
                         .orElse(Collections.emptyMap());
             } catch (Exception e) {
                 e.printStackTrace();
                 return Collections.emptyMap();
             }
        }
        return Collections.emptyMap();
    }
}
