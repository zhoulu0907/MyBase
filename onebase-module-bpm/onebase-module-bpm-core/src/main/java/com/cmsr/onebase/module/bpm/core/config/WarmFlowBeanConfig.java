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
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.impl.ChartServiceImpl;
import org.dromara.warm.flow.core.service.impl.DefServiceImpl;
import org.dromara.warm.flow.core.utils.ExpressionUtil;
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
        return new FlowDefinitionDaoImpl();
    }

    /**
     * 重写父类方法，返回 null 避免创建不存在的 DAO
     */
    @Bean
    public FlowNodeDao<?> nodeDao() {
        return new FlowNodeDaoImpl();
    }

    @Bean
    
    public FlowInstanceDao instanceDao() {
        return new FlowInstanceDaoImpl();
    }

    
    @Bean
    public FlowTaskDao<?> taskDao() {
        return new FlowTaskDaoImpl();
    }

    
    @Bean
    public FlowHisTaskDao<?> hisTaskDao() {
        return new FlowHisTaskDaoImpl();
    }

    
    @Bean
    public FlowUserDao<?> flowUserDao() {
        return new FlowUserDaoImpl();
    }

    
    @Bean
    public FlowSkipDao<?> skipDao() {
        return new FlowSkipDaoImpl();
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
