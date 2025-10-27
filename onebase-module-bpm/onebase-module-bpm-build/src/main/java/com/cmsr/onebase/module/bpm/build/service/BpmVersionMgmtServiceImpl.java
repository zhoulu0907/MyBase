package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowDefinitionRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.SkipService;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程设计服务实现类
 *
 * @author liyang
 * @date 2025-10-20
 */
@Service
@Slf4j
public class BpmVersionMgmtServiceImpl implements BpmVersionMgmtService {

    @Resource
    private DefService defService;

    @Resource
    private NodeService nodeService;

    @Resource
    private SkipService skipService;

    @Resource
    private InsService insService;

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    /**
     * 可对指定的流程版本进行删除，但已发布版本及含有尚未完结的历史版本流程无法删除。
     *
     * @param reqVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(BpmDeleteReqVo reqVo) {
        List<Long> ids = new ArrayList<>();
        ids.add(reqVo.getId());

        // 校验流程是否存在
        Definition existDef = defService.getById(String.valueOf(reqVo.getId()));

        if (existDef == null) {
            return;
        }

        // 已发布流程无法删除
        if (existDef.getIsPublish().equals(PublishStatus.PUBLISHED.getKey())) {
            throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED_FOR_PUBLISHED);
        }

        // 包含历史未完结的历史版本无法删除
        List<Instance> instanceList = insService.getByDefId(reqVo.getId());

        if (CollUtil.isNotEmpty(instanceList)) {
            for (Instance instance : instanceList) {
                if (!instance.getFlowStatus().equals(FlowStatus.FINISHED.getKey())) {
                    throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED_FOR_INS_NOT_FINISHED);
                }
            }
        }

        // 删除流程节点和跳转
        nodeService.deleteNodeByDefIds(ids);
        skipService.deleteSkipByDefIds(ids);

        boolean success = defService.removeById(reqVo.getId());

        if (!success) {
            throw exception(ErrorCodeConstants.DELETE_FLOW_FAILED);
        }
    }
}
