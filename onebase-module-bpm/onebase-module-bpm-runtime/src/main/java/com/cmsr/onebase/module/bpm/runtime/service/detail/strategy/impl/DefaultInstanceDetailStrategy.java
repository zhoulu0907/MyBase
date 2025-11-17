package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import org.dromara.warm.flow.core.entity.Instance;
import org.springframework.stereotype.Component;

/**
 * 兜底节点详情策略
 *
 * @author liyang
 * @date 2025-11-15
 */
@Component
public class DefaultInstanceDetailStrategy  extends AbstractInstanceDetailStrategy<BaseNodeExtDTO> {
    @Override
    public void fillDetail(BpmTaskDetailRespVO vo, BaseNodeExtDTO extDTO, Instance instance, Long loginUserId, boolean isTodo) {
        Long pageSetId = getPageSetId(instance);

        // 默认策略始终返回详情视图
        fillPageViewInfo(vo, instance, pageSetId, false);
    }

    @Override
    public boolean supports(String bizNodeType) {
        return false;
    }
}
