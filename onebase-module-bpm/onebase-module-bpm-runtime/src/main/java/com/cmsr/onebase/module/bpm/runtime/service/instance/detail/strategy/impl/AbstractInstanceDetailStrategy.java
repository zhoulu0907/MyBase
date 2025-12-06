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
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
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
     * @param tableName 实体ID
     * isTodo 是否待办
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

        // 构建字段映射：fieldName -> SemanticFieldSchemaDTO
        Map<String, Map<String, SemanticFieldSchemaDTO>> entityFieldMap = new HashMap<>();
        Map<String, Map<String, String>> fieldPermMap = new HashMap<>();

        // 默认所有字段都为只读
        // 先处理主表
        Map<String, String> mainFieldPermMap = new HashMap<>();
        Map<String, SemanticFieldSchemaDTO> mainEntityFieldMap = new HashMap<>();

        for (SemanticFieldSchemaDTO entityField : entityFields) {
            mainEntityFieldMap.put(entityField.getFieldName(), entityField);
            mainFieldPermMap.put(entityField.getFieldName(), FieldUiShowModeEnum.READ.getCode());
        }

        fieldPermMap.put(tableName, mainFieldPermMap);
        entityFieldMap.put(tableName, mainEntityFieldMap);

        // 再处理子表
        for (SemanticRelationSchemaDTO connector : entitySchema.getConnectors()) {
            String subTableName = connector.getTargetEntityTableName();
            Map<String, String> subFieldPermMap = new HashMap<>();
            // 构建子表字段映射
            Map<String, SemanticFieldSchemaDTO> subEntityFieldMap = new HashMap<>();

            for (SemanticFieldSchemaDTO subField : connector.getRelationAttributes()) {
                subEntityFieldMap.put(subField.getFieldName(), subField);
                subFieldPermMap.put(subField.getFieldName(), FieldUiShowModeEnum.READ.getCode());
            }

            fieldPermMap.put(subTableName, subFieldPermMap);
            entityFieldMap.put(subTableName, subEntityFieldMap);
        }

        vo.getFormData().setFieldPermMap(fieldPermMap);

        // 没有配置字段权限，则返回只读权限
        if (!CollectionUtils.isNotEmpty(fieldPermConfig.getFieldConfigs())) {
            return;
        }

        Map<String, String> hiddenFieldNameMap = new HashMap<>();

        // 处理节点配置的字段权限，todo: 先按照平铺处理，待优化
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String currTableName = fieldConfig.getTableName();

            // 通过 tableName 查找实体Map
            Map<String, SemanticFieldSchemaDTO> currEntityFieldMap = entityFieldMap.get(currTableName);
            if (MapUtils.isEmpty(currEntityFieldMap)) {
                continue;
            }

            // 通过 tableName 查找权限权限Map
            Map<String, String> currFildPermMap = fieldPermMap.get(currTableName);
            if (MapUtils.isEmpty(currFildPermMap)) {
                continue;
            }

            // 查找到对应的field
            SemanticFieldSchemaDTO entityField = currEntityFieldMap.get(fieldConfig.getFieldName());

            if (entityField == null) {
                continue;
            }

            // 处理隐藏字段
            if (Objects.equals(FieldPermTypeEnum.HIDDEN.getCode(), fieldConfig.getFieldPermType())) {
                hiddenFieldNameMap.put(currTableName, entityField.getFieldName());
                currFildPermMap.put(fieldConfig.getFieldName(), FieldUiShowModeEnum.HIDDEN.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.READ.getCode(), fieldConfig.getFieldPermType())) {
                currFildPermMap.put(fieldConfig.getFieldName(), FieldUiShowModeEnum.READ.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.WRITE.getCode(), fieldConfig.getFieldPermType())) {
                if (isTodo) {
                    currFildPermMap.put(fieldConfig.getFieldName(), FieldUiShowModeEnum.WRITE.getCode());
                } else {
                    // 非待办的情况下，都是只读
                    currFildPermMap.put(fieldConfig.getFieldName(), FieldUiShowModeEnum.READ.getCode());
                }
            }
        }

        // 移除隐藏字段在表单数据中的值
        if (MapUtils.isNotEmpty(hiddenFieldNameMap)) {
            hiddenFieldNameMap.forEach((k, v) -> {
                // 主表
                if (Objects.equals(k, tableName)) {
                    vo.getFormData().getData().remove(v);
                } else {
                    // 子表
                    Object subTableData = vo.getFormData().getData().get(k);

                    if (subTableData instanceof Map<?, ?> subTableMap) {
                        subTableMap.remove(v);
                    }
                }
            });
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

