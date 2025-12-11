package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    public void validate(String entityUuid, String fieldUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {

        QueryWrapper queryWrapper = childNotEmptyRepository.query()
                .eq(MetadataValidationChildNotEmptyDO::getEntityUuid, entityUuid)
                .eq(MetadataValidationChildNotEmptyDO::getIsEnabled, 1);
        List<MetadataValidationChildNotEmptyDO> rules = childNotEmptyRepository.list(queryWrapper);
        if(ObjectUtils.isEmpty(rules)){
            log.info("该实体未配置子表空行校验，跳过校验，主实体UUID：" + entityUuid);
        }else{
            for(MetadataValidationChildNotEmptyDO rule : rules){
                String childEntityUuid = rule.getChildEntityUuid();
                if(ObjectUtils.isEmpty(subEntities)){
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage()
                            : "子表[" + childEntityUuid + "]数据行不能为空";
                    throw new IllegalArgumentException(errorMessage);
                }
                // 注意：这里需要修改MetadataDataMethodSubEntityContext使用entityUuid
                // 暂时保持使用entityId进行匹配，后续需要统一改造
                boolean childNotEmpty = subEntities.stream().anyMatch(metadataDataMethodSubEntityContext ->
                        childEntityUuid.equals(metadataDataMethodSubEntityContext.getEntityUuid()) && !ObjectUtils.isEmpty(metadataDataMethodSubEntityContext.getSubData()));
                if(!childNotEmpty){
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage()
                            : "子表[" + childEntityUuid + "]数据行不能为空";
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
