package com.cmsr.onebase.module.bpm.core.service.impl;

import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.service.DefService;
import org.springframework.stereotype.Service;

/**
 * BPM流程定义扩展服务
 *
 * 提供根据formPath查询流程定义的便捷方法，供build和runtime模块使用
 *
 * @author liyang
 * @date 2025-10-27
 */
@Service
public class BpmEngineDefExtServiceImpl implements BpmEngineDefExtService {

    @Resource
    private DefService defService;

    /**
     * 根据表单路径查询流程定义（查询最新的）
     *
     * @param formPath 表单路径（业务ID）
     * @return Definition 返回最新的流程定义，如果不存在返回null
     */
    public Definition getByFormPath(String formPath) {
        FlowDefinition query = new FlowDefinition();
        query.setFormPath(formPath);
        return defService.getOne(query);
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
        return defService.getOne(query);
    }
}
