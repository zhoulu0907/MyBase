package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.dromara.warm.flow.core.entity.Definition;
import org.springframework.stereotype.Repository;

/**
 * BPM流程定义扩展仓储，基于FlowDefinitionRepository实现
 *
 * 由于FlowDefinitionRepository是warmflow的内置仓储，其逻辑与业务无关
 *
 * 所以单独创建一个BpmFlowDefinitionRepositoryExt，进行业务功能的扩展
 *
 * 提供根据formPath查询流程定义的便捷方法，供build和runtime模块使用
 *
 * @author liyang
 * @date 2025-10-27
 */
@Getter
@Repository
public class BpmFlowDefinitionRepositoryExt {
    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    /**
     * 根据表单路径查询流程定义（查询最新的）
     *
     * @param formPath 表单路径（业务ID）
     * @return Definition 返回最新的流程定义，如果不存在返回null
     */
    public Definition getByFormPath(String formPath) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(FlowDefinition::getFormPath,formPath);
        queryWrapper.orderBy(FlowDefinition::getCreateTime, false);
        return flowDefinitionRepository.getOne(queryWrapper);
    }

    /**
     * 根据表单路径查询已发布的流程定义
     *
     * @param formPath 表单路径（业务ID）
     * @return Definition 返回已发布的流程定义，如果不存在返回null
     */
    public Definition getByFormPathAndStatus(String formPath, Integer isPublish) {
        FlowDefinition query = new FlowDefinition();
        query.setFormPath(formPath);
        query.setIsPublish(isPublish);

        QueryWrapper queryWrapper = QueryWrapper.create(query);
        queryWrapper.orderBy(FlowDefinition::getCreateTime, false);

        return flowDefinitionRepository.getOne(queryWrapper);
    }
}
