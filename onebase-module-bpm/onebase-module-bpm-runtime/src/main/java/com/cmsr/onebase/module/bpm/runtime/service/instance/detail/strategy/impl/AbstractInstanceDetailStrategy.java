package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import com.cmsr.onebase.module.bpm.core.enums.FieldPermTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.FieldUiShowModeEnum;
import com.cmsr.onebase.module.bpm.runtime.helper.BpmEntityHelper;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.InstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
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

    @Resource
    protected BpmEntityHelper bpmEntityHelper;

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
        Map<String, Map<String, String>> fieldPermMap = new HashMap<>();

        // 非系统字段
        Map<String, Set<String>> nonSystemFields = bpmEntityHelper.getNonSystemFields(entitySchema);

        // 默认都readonly
        nonSystemFields.forEach((key, val) -> {
            fieldPermMap.putIfAbsent(key, new HashMap<>());

            // 如果是子表，再加上子表本身的权限
            if (!Objects.equals(key, tableName)) {
                fieldPermMap.get(key).put(key, FieldUiShowModeEnum.READ.getCode());
            }

            for (String s : val) {
                fieldPermMap.get(key).put(s, FieldUiShowModeEnum.READ.getCode());
            }
        });

        vo.getFormData().setFieldPermMap(fieldPermMap);

        // 没有配置字段权限，则返回只读权限
        if (CollectionUtils.isEmpty(fieldPermConfig.getFieldConfigs())) {
            return;
        }

        // 隐藏字段
        Map<String, Set<String>> hiddenFieldNameMap = new HashMap<>();

        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String permTableName = fieldConfig.getTableName();

            // 通过 tableName 查找权限Map
            Map<String, String> currFieldPermMap = fieldPermMap.get(permTableName);
            if (MapUtils.isEmpty(currFieldPermMap)) {
                continue;
            }

            String permFieldName = fieldConfig.getFieldName();

            // 实体本身不存在字段则直接跳过
            if (!currFieldPermMap.containsKey(permFieldName)) {
                continue;
            }

            // 处理隐藏字段
            if (Objects.equals(FieldPermTypeEnum.HIDDEN.getCode(), fieldConfig.getFieldPermType())) {
                hiddenFieldNameMap.putIfAbsent(permTableName, new HashSet<>());
                hiddenFieldNameMap.get(permTableName).add(permFieldName);

                currFieldPermMap.put(permFieldName, FieldUiShowModeEnum.HIDDEN.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.READ.getCode(), fieldConfig.getFieldPermType())) {
                currFieldPermMap.put(permFieldName, FieldUiShowModeEnum.READ.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.WRITE.getCode(), fieldConfig.getFieldPermType())) {
                if (isTodo) {
                    currFieldPermMap.put(permFieldName, FieldUiShowModeEnum.WRITE.getCode());
                } else {
                    // 非待办的情况下，都是只读
                    currFieldPermMap.put(permFieldName, FieldUiShowModeEnum.READ.getCode());
                }
            }
        }

        // 移除隐藏字段在表单数据中的值
        if (MapUtils.isNotEmpty(hiddenFieldNameMap)) {
            Map<String, Object> entityData = vo.getFormData().getData();

            hiddenFieldNameMap.forEach((k, v) -> {
                // 主表
                if (Objects.equals(k, tableName)) {
                    // 移除主表的隐藏字段值
                    for (String s : v) {
                        entityData.remove(s);
                    }
                } else {
                    // 子表
                    Object subTableData = entityData.get(k);

                    // 子表需要是列表类型，否则直接返回
                    if (!(subTableData instanceof List<?> subTableList)) {
                        return;
                    }

                    // 子表为空也不用处理
                    if (CollectionUtils.isEmpty(subTableList)) {
                        return;
                    }

                    Set<String> nonSystemFieldNames = nonSystemFields.get(k);

                    // 子表如果所有非系统字段全部隐藏，则直接移除数据
                    // 或者子表本身就是隐藏的，则直接移除数据
                    if (v.containsAll(nonSystemFieldNames) || v.contains(k)) {
                        entityData.remove(k);
                    } else {
                        // 移除指定的字段值
                        for (Object subTableItem : subTableList) {
                            if (subTableItem instanceof Map<?, ?> subTableItemMap) {
                                for (String s : v) {
                                    subTableItemMap.remove(s);
                                }
                            }
                        }
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

