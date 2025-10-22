package com.cmsr.onebase.module.formula.config;

import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公式引擎自动配置类
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "onebase.formula", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FormulaEngineAutoConfiguration {

    @Resource
    private FormulaEngineProperties formulaEngineProperties;

    /**
     * 创建公式引擎服务Bean
     *
     * @return 公式引擎服务实例
     */
    @Bean
    public FormulaEngineService formulaEngineService() {
        log.info("初始化公式引擎服务，配置信息：{}", formulaEngineProperties);
        return new FormulaEngineServiceImpl(formulaEngineProperties);
    }
}
