package com.cmsr.onebase.module.bpm.core.api.impl;

import com.cmsr.onebase.module.bpm.api.app.BpmApplicationApi;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowNodeRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;



/**
 * 流程应用服务实现类
 *
 * @author liyang
 * @date 2025-12-15
 */
@Slf4j
@Service
public class BpmApplicationApiImpl implements BpmApplicationApi {
    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Resource
    private FlowNodeRepository flowNodeRepository;

    @Override
    public boolean existsEntityRelation(String entityUuid, String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return false;
        }

        // 使用 % 作为通配，允许 key 与 value 之间存在任意空白或其它字符
        String pattern = String.format("\"%s\"%%:%%\"%s\"", BpmConstants.VAR_ENTITY_TABLE_NAME_KEY, entityName);

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.like(FlowInstance::getVariable, pattern);

        return flowInstanceRepository.exists(queryWrapper);
    }

    @Override
    public boolean existsEntityFieldRelation(String entityUuid, String entityName, String fieldUuid, String fieldName) {
        if (StringUtils.isBlank(entityName) || StringUtils.isBlank(fieldName)) {
            return false;
        }

        // 使用 % 作为通配，允许 key 与 value 之间存在任意空白或其它字符
        String pattern = String.format("\"%s\"%%:%%\"%s\"", entityName, fieldName);

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.like(FlowNode::getExt, pattern);
        return flowNodeRepository.exists(queryWrapper);
    }
}
