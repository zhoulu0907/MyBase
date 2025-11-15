package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.InstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.utils.PageViewUtil;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.service.impl.BpmConstants;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 抽象流程实例详情策略基类
 *
 * @author liyang
 * @date 2025-11-04
 */
public abstract class AbstractInstanceDetailStrategy<T extends BaseNodeExtDTO> implements InstanceDetailStrategy<T> {
    @Resource
    protected MetadataEntityFieldApi metadataEntityFieldApi;

    @Resource
    protected PageViewUtil pageViewUtil;

    @Resource
    protected BpmFlowInsBizExtRepository bpmFlowInsBizExtRepository;

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
    protected void fillFieldPermConfig(BpmTaskDetailRespVO vo, T extDTO, Long entityId, boolean isTodo) {
        // 由子类实现，默认什么都不做
    }

    /**
     * 填充视图页面信息
     *
     * @param instance 流程实例
     */
    protected void fillPageViewInfo(BpmTaskDetailRespVO vo, Instance instance, Long pageSetId, boolean isTodo) {
        PageViewGroupDTO viewGroupDTO = getPageViewGroupDTO(instance, pageSetId);

        // 默认获取详情视图
        vo.setPageView(viewGroupDTO.getDetailPageView());
    }

    protected PageViewGroupDTO getPageViewGroupDTO(Instance instance, Long pageSetId) {
        PageViewGroupDTO viewGroupDTO;

        String pageViewGroupJsonStr = MapUtils.getString(instance.getVariableMap(), BpmConstants.VAR_PAGE_VIEW_GROUP_KEY);

        if (StringUtils.isNotBlank(pageViewGroupJsonStr)) {
            return JsonUtils.parseObject(pageViewGroupJsonStr, PageViewGroupDTO.class);
        }

        // todo: 此处为了兼容旧数据，尝试获取最新页面视图，理论上应该是从实例里获取
        viewGroupDTO = pageViewUtil.findPageViewGroup(pageSetId);

        if (viewGroupDTO == null) {
            throw exception(ErrorCodeConstants.MISSING_EDIT_OR_DETAIL_PAGE_VIEW);
        }

        return viewGroupDTO;
    }

    protected Long getPageSetId(Instance instance) {
        Long pageSetId = MapUtils.getLong(instance.getVariableMap(), BpmConstants.VAR_BINDING_VIEW_ID_KEY);
        if (pageSetId == null) {
            // 兼容旧数据，再去查一次instanceExt表
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq(BpmFlowInsBizExtDO.INSTANCE_ID, instance.getId());
            BpmFlowInsBizExtDO bizExtDO = bpmFlowInsBizExtRepository.findOne(configStore);

            if (bizExtDO != null) {
                pageSetId = Long.valueOf(bizExtDO.getBindingViewId());
            }
        }

        if (pageSetId == null) {
            throw exception(ErrorCodeConstants.MISSING_BINDING_VIEW_ID);
        }

        return pageSetId;
    }

    /**
     * 填充详情（只处理节点类型相关的逻辑）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param currTask 待办任务（可能为null）
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     */
    @Override
    public void fillDetail(BpmTaskDetailRespVO vo, T extDTO, Task currTask, Instance instance, Long loginUserId) {
        Long entityId = MapUtils.getLong(instance.getVariableMap(), BpmConstants.VAR_ENTITY_ID_KEY);
        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        Long pageSetId = getPageSetId(instance);

        // 非当前待办，则没有按钮，且字段权限全部为只读
        if (currTask == null) {
            // 填充视图页面信息
            fillPageViewInfo(vo, instance, pageSetId, false);

            // 填充字段权限信息（节点类型相关）
            fillFieldPermConfig(vo, extDTO, entityId, false);
        } else {
            // 填充按钮信息（节点类型相关）
            fillButtonConfigs(vo, extDTO);

            // 填充视图页面信息
            fillPageViewInfo(vo, instance, pageSetId, true);

            // 填充字段权限信息（节点类型相关）
            fillFieldPermConfig(vo, extDTO, entityId, true);

            vo.setTaskId(currTask.getId());
        }
    }
}

