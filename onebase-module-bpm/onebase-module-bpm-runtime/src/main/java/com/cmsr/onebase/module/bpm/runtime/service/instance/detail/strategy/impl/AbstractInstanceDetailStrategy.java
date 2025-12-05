package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import com.cmsr.onebase.module.bpm.core.enums.FieldPermTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.FieldUiShowModeEnum;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.InstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.Instance;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 抽象流程实例详情策略基类
 *
 * @author liyang
 * @date 2025-11-04
 */
public abstract class AbstractInstanceDetailStrategy<T extends BaseNodeExtDTO> implements InstanceDetailStrategy<T> {
    @Resource
    protected SemanticDynamicDataApi semanticDynamicDataApi;

    /**
     * 填充按钮配置（节点类型相关）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     */
    protected void fillButtonConfigs(BpmTaskDetailRespVO vo, T extDTO) {
        // 由子类实现，默认什么都不做
    }

    /**
     * 填充字段权限配置（节点类型相关）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param entityId 实体ID
     */
    protected void fillFieldPermConfig(BpmTaskDetailRespVO vo, T extDTO, String tableName, boolean isTodo) {
        // 由子类实现，默认什么都不做
    }

    protected void fillFieldPermFromConfig(BpmTaskDetailRespVO vo, FieldPermCfgDTO fieldPermConfig, String tableName, boolean isTodo) {
        // 审批节点未配置字段权限，或未开启节点配置，则返回，使用表单默认权限
        if (fieldPermConfig == null || !fieldPermConfig.getUseNodeConfig()) {
            return;
        }

        SemanticEntitySchemaDTO entitySchema = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);
        List<SemanticFieldSchemaDTO> entityFields = entitySchema.getFields();

        Map<String, SemanticFieldSchemaDTO> entityFieldMap = new HashMap<>();
        Map<String, String> fieldPermMap = new HashMap<>();
        Map<String, String> fieldUuidNameMap = new HashMap<>();

        // 默认所有字段都为只读
        for (SemanticFieldSchemaDTO entityField : entityFields) {
            entityFieldMap.put(entityField.getFieldUuid(), entityField);
            fieldPermMap.put(entityField.getFieldUuid(), FieldUiShowModeEnum.READ.getCode());
            fieldUuidNameMap.put(entityField.getFieldUuid(), entityField.getFieldName());
        }

        vo.getFormData().setFieldPermMap(fieldPermMap);

        // 这里只是为了方便查看uuid和name的关联关系，业务上暂时没用上
        vo.getFormData().setFieldUuidName(fieldUuidNameMap);

        // 没有配置字段权限，则返回只读权限
        if (!CollectionUtils.isNotEmpty(fieldPermConfig.getFieldConfigs())) {
            return;
        }

        Set<String> hiddenFieldNames = new HashSet<>();

        // 处理节点配置的字段权限 todo: 处理子表字段权限
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            // 在实体字段中不存在的，直接跳过
            SemanticFieldSchemaDTO entityField = entityFieldMap.get(fieldConfig.getFieldUuid());
            if (entityField == null) {
                continue;
            }

            // 处理隐藏字段
            if (Objects.equals(FieldPermTypeEnum.HIDDEN.getCode(), fieldConfig.getFieldPermType())) {
                // 这里才是字段的英文名
                hiddenFieldNames.add(entityField.getFieldName());
                fieldPermMap.put(fieldConfig.getFieldUuid(), FieldUiShowModeEnum.HIDDEN.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.READ.getCode(), fieldConfig.getFieldPermType())) {
                fieldPermMap.put(fieldConfig.getFieldUuid(), FieldUiShowModeEnum.READ.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.WRITE.getCode(), fieldConfig.getFieldPermType())) {
                if (isTodo) {
                    fieldPermMap.put(fieldConfig.getFieldUuid(), FieldUiShowModeEnum.WRITE.getCode());
                } else {
                    // 非待办的情况下，都是只读
                    fieldPermMap.put(fieldConfig.getFieldUuid(), FieldUiShowModeEnum.READ.getCode());
                }
            }
        }

        // 移除隐藏字段在表单数据中的值
        if (CollectionUtils.isNotEmpty(hiddenFieldNames)) {
            for (String hiddenFieldName : hiddenFieldNames) {
                vo.getFormData().getData().remove(hiddenFieldName);
            }
        }
    }

    /**
     * 填充视图页面信息
     *
     * @param instance 流程实例
     */
    protected void fillPageViewInfo(BpmTaskDetailRespVO vo, Instance instance, boolean isTodo) {
        PageViewGroupDTO viewGroupDTO = getPageViewGroupDTO(instance);

        // todo 更新最新的pageId
        // 默认获取详情视图
        vo.setPageView(viewGroupDTO.getDetailPageView());
    }

    protected PageViewGroupDTO getPageViewGroupDTO(Instance instance) {
        String pageViewGroupJsonStr = MapUtils.getString(instance.getVariableMap(), BpmConstants.VAR_PAGE_VIEW_GROUP_KEY);

        if (StringUtils.isNotBlank(pageViewGroupJsonStr)) {
            return JsonUtils.parseObject(pageViewGroupJsonStr, PageViewGroupDTO.class);
        }

        throw exception(ErrorCodeConstants.MISSING_EDIT_OR_DETAIL_PAGE_VIEW);
    }

    /**
     * 填充详情（只处理节点类型相关的逻辑）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     * @param isTodo 是否待办
     */
    @Override
    public void fillDetail(BpmTaskDetailRespVO vo, T extDTO, Instance instance, Long loginUserId, boolean isTodo) {
        String tableName = MapUtils.getString(instance.getVariableMap(), BpmConstants.VAR_ENTITY_TABLE_NAME_KEY);
        if (tableName == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY);
        }

        // 非当前待办，则没有按钮，且字段权限全部为只读
        if (isTodo) {
            // 填充按钮信息（节点类型相关）
            fillButtonConfigs(vo, extDTO);
        }

        // 填充视图页面信息
        fillPageViewInfo(vo, instance, isTodo);

        // 填充字段权限信息（节点类型相关）
        fillFieldPermConfig(vo, extDTO, tableName, isTodo);
    }
}

