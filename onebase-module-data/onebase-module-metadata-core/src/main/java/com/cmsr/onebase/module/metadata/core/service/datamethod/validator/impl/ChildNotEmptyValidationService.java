package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChildNotEmptyValidationService implements ValidationService {

    @Resource
    private MetadataValidationChildNotEmptyRepository childNotEmptyRepository;

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {

        ConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id",entityId);
        configStore.and("is_enabled",1);// 是否启用 1-启用 0-禁用
        configStore.and("deleted",0);
        List<MetadataValidationChildNotEmptyDO> rules = childNotEmptyRepository.findAllByConfig(configStore);
        if(ObjectUtils.isEmpty(rules)){
            log.info("该实体未配置子表空行校验，跳过校验，主实体ID：" + entityId);
        }else{
            for(MetadataValidationChildNotEmptyDO rule : rules){
                Long childChildEntityId = rule.getChildEntityId();
                if(ObjectUtils.isEmpty(subEntities)){
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage()
                            : "子表[" + childChildEntityId + "]数据行不能为空";
                    throw new IllegalArgumentException(errorMessage);
                }
                boolean childNotEmpty = subEntities.stream().anyMatch(metadataDataMethodSubEntityContext ->
                        childChildEntityId.equals(metadataDataMethodSubEntityContext.getEntityId()) && !ObjectUtils.isEmpty(metadataDataMethodSubEntityContext.getSubData()));
                if(!childNotEmpty){
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage()
                            : "子表[" + childChildEntityId + "]数据行不能为空";
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

    }

    @Override
    public String getValidationType() {
        return "CHILD_NOT_EMPTY";
    }

    @Override
    public boolean supports(String fieldType) {
        return false;
    }
}
