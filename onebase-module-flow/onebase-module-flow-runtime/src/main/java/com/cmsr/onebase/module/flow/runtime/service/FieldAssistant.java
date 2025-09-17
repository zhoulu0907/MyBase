package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.module.flow.core.rule.ConditionItem;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/17 13:23
 */
@Slf4j
@Setter
@Component
public class FieldAssistant {

    private MetadataEntityFieldApi metadataEntityFieldApi;

    public void fillFieldData(List<ConditionItem> conditions) {

    }
}
