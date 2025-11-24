package com.cmsr.onebase.module.bpm.core.config;

import com.cmsr.onebase.module.bpm.core.expression.ConditionStrategyDefault;
import com.cmsr.onebase.module.bpm.core.expression.ConditionStrategySpel;
import com.cmsr.onebase.module.bpm.core.expression.ListenerStrategySpel;
import com.cmsr.onebase.module.bpm.core.expression.VariableStrategySpel;
import com.cmsr.onebase.module.bpm.core.helper.SpelHelper;
import com.cmsr.onebase.module.bpm.core.utils.SpringUtil;
import com.cmsr.onebase.module.engine.orm.anyline.dao.*;
import com.cmsr.onebase.module.engine.orm.anyline.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.dao.*;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.service.impl.*;
import org.dromara.warm.flow.core.utils.ExpressionUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * WarmFlow 流程引擎配置
 *
 * 基于warmflow源码的BeanConfig修改
 *
 * @author liyang
 * @date 2025-09-29
 */
@Configuration
@Slf4j
@Import({SpringUtil.class, SpelHelper.class})
@ConditionalOnProperty(
        value = {"warm-flow.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties({WarmFlowProperties.class})
public class WarmFlowBeanConfig {

    // ==================== DAO Beans ====================

    /**
     * 配置流程定义 DAO
     */
    @Bean
    public FlowDefinitionDao bpmDefinitionDao() {
        return new FlowDefinitionDaoImpl();
    }

    @Bean
    public FlowNodeDao bpmNodeDao() {
        return new FlowNodeDaoImpl();
    }

    @Bean
    public FlowInstanceDao instanceDao() {
        return new FlowInstanceDaoImpl();
    }


    @Bean
    public FlowTaskDao taskDao() {
        return new FlowTaskDaoImpl();
    }


    @Bean
    public FlowHisTaskDao hisTaskDao() {
        return new FlowHisTaskDaoImpl();
    }

    @Bean
    public FlowUserDao flowUserDao() {
        return new FlowUserDaoImpl();
    }

    @Bean
    public FlowSkipDao skipDao() {
        return new FlowSkipDaoImpl();
    }

    // ==================== Service Beans ====================

    /**
     * 配置流程定义服务
     */
    @Bean
    public DefService bpmDefService(FlowDefinitionDao definitionDao) {
        return new DefServiceImpl().setDao(definitionDao);
    }

    /**
     * 配置节点服务
     */
    @Bean
    public NodeService bpmNodeService(FlowNodeDao flowNodeDao) {
        return new NodeServiceImpl().setDao(flowNodeDao);
    }

    /**
     * 配置实例服务
     */
    @Bean
    public InsService bpmInsService(FlowInstanceDao flowInstanceDao) {
        return new InsServiceImpl().setDao(flowInstanceDao);
    }

    /**
     * 配置任务服务
     */
    @Bean
    public TaskService bpmTaskService(FlowTaskDao flowTaskDao) {
        return new TaskServiceImpl().setDao(flowTaskDao);
    }

    /**
     * 配置历史任务服务
     */
    @Bean
    public HisTaskService bpmHisTaskService(FlowHisTaskDao flowHisTaskDao) {
        return new HisTaskServiceImpl().setDao(flowHisTaskDao);
    }

    /**
     * 配置流程用户服务
     * 使用@Primary注解，让WarmFlow的UserService成为主要Bean
     */
    @Bean
    public UserService bpmUserService(FlowUserDao flowUserDao) {
        return new UserServiceImpl().setDao(flowUserDao);
    }

    /**
     * 配置流程跳过服务
     */
    @Bean
    public SkipService bpmSkipService(FlowSkipDao flowSkipDao) {
        return new SkipServiceImpl().setDao(flowSkipDao);
    }

//    /**
//     * 配置表单服务
//     */
//    @Bean
//    public FormService formService(FlowFormDao<?> flowFormDao) {
//        return new FormServiceImpl().setDao((FlowFormDao) flowFormDao);
//    }

//    @Bean
//
//    public FormService flowFormService(FlowFormDao formDao) {
//        return null;
//    }

    /**
     * 配置图表服务
     */
    @Bean
    public ChartService bpmChartService() {
        return new ChartServiceImpl();
    }

    @Bean
    public WarmFlow initFlow() {
        this.setNewEntity();
        FrameInvoker.setCfgFunction((key) -> {
            return Objects.requireNonNull(SpringUtil.getBean(Environment.class)).getProperty(key);
        });
        FrameInvoker.setBeanFunction(SpringUtil::getBean);
        WarmFlowProperties warmFlow = SpringUtil.getBean(WarmFlowProperties.class);
        warmFlow.init();
        FlowEngine.setFlowConfig(warmFlow);
        this.setExpression();
        this.after(warmFlow);
        log.info("【warm-flow】，加载完成");
        return warmFlow;
    }

    private void setExpression() {
        ExpressionUtil.setExpression(new ConditionStrategyDefault());
        ExpressionUtil.setExpression(new ConditionStrategySpel());
        ExpressionUtil.setExpression(new ListenerStrategySpel());
        ExpressionUtil.setExpression(new VariableStrategySpel());
    }

    public void setNewEntity() {
        FlowEngine.setNewDef(FlowDefinition::new);
        FlowEngine.setNewIns(FlowInstance::new);
        FlowEngine.setNewHisTask(FlowHisTask::new);
        FlowEngine.setNewNode(FlowNode::new);
        FlowEngine.setNewSkip(FlowSkip::new);
        FlowEngine.setNewTask(FlowTask::new);
        FlowEngine.setNewUser(FlowUser::new);
       //  FlowEngine.setNewForm(FlowForm::new);
    }

    public void after(WarmFlow flowConfig) {
    }
}
