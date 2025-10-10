package com.cmsr.onebase.module.bpm.core.warmflow.anyline.config;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl.*;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.dao.*;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.impl.ChartServiceImpl;
import org.dromara.warm.flow.core.service.impl.DefServiceImpl;
import org.dromara.warm.flow.core.utils.ExpressionUtil;
import org.dromara.warm.plugin.modes.sb.config.WarmFlowProperties;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategyDefault;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.ListenerStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.VariableStrategySpel;
import org.dromara.warm.plugin.modes.sb.helper.SpelHelper;
import org.dromara.warm.plugin.modes.sb.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * WarmFlow 流程引擎配置
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
    @Primary
    public FlowDefinitionDao definitionDao() {
        return new WfFlowDefinitionDaoImpl();
    }

    /**
     * 重写父类方法，返回 null 避免创建不存在的 DAO
     */
    @Bean
    public FlowNodeDao<?> nodeDao() {
        return new WfFlowNodeDaoImpl();
    }

    @Bean
    
    public FlowInstanceDao instanceDao() {
        return new WfFlowInstanceDaoImpl();
    }

    
    @Bean
    public FlowTaskDao<?> taskDao() {
        return new WfFlowTaskDaoImpl();
    }

    
    @Bean
    public FlowHisTaskDao<?> hisTaskDao() {
        return new WfFlowHisTaskDaoImpl();
    }

    
    @Bean
    public FlowUserDao<?> flowUserDao() {
        return new WfFlowUserDaoImpl();
    }

    
    @Bean
    public FlowSkipDao<?> skipDao() {
        return new WfFlowSkipDaoImpl();
    }

    
//    @Bean
//    public FlowFormDao<?> formDao() {
//        return null;
//    }

    // ==================== Service Beans ====================

    /**
     * 配置流程定义服务
     */
    @Bean
    public DefService definitionService(FlowDefinitionDao<?> definitionDao) {
        return new DefServiceImpl().setDao((FlowDefinitionDao) definitionDao);
    }

    /**
     * 配置节点服务
     */
//    @Bean
//    public NodeService nodeService(FlowNodeDao<?> flowNodeDao) {
//        return new NodeServiceImpl().setDao((FlowNodeDao) flowNodeDao);
//    }
//
//    /**
//     * 配置实例服务
//     */
//    @Bean
//    public InsService insService(FlowInstanceDao<?> flowInstanceDao) {
//        return new InsServiceImpl().setDao((FlowInstanceDao) flowInstanceDao);
//    }
//
//    /**
//     * 配置任务服务
//     */
//    @Bean
//    public TaskService taskService(FlowTaskDao<?> flowTaskDao) {
//        return new TaskServiceImpl().setDao((FlowTaskDao) flowTaskDao);
//    }
//
//    /**
//     * 配置历史任务服务
//     */
//    @Bean
//    public HisTaskService hisTaskService(FlowHisTaskDao<?> flowHisTaskDao) {
//        return new HisTaskServiceImpl().setDao((FlowHisTaskDao) flowHisTaskDao);
//    }

    /**
     * 配置用户服务
     */
//    @Bean
//    public UserService userService(FlowUserDao<?> flowUserDao) {
//        return new UserServiceImpl().setDao((FlowUserDao) flowUserDao);
//    }
//
//    /**
//     * 配置跳过服务
//     */
//    @Bean
//    public SkipService skipService(FlowSkipDao<?> flowSkipDao) {
//        return new SkipServiceImpl().setDao((FlowSkipDao) flowSkipDao);
//    }
//
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
    
    public ChartService chartService() {
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
        FlowEngine.setNewDef(WfFlowDefinition::new);
        FlowEngine.setNewIns(WfFlowInstance::new);
        FlowEngine.setNewHisTask(WfFlowHisTask::new);
        FlowEngine.setNewNode(WfFlowNode::new);
        FlowEngine.setNewSkip(WfFlowSkip::new);
        FlowEngine.setNewTask(WfFlowTask::new);
        FlowEngine.setNewUser(WfFlowUser::new);
       //  FlowEngine.setNewForm(FlowForm::new);
    }

    public void after(WarmFlow flowConfig) {
    }
}
