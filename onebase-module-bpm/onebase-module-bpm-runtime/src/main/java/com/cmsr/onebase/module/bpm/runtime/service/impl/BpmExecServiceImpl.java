package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.StartNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.runtime.service.BpmExecService;
import com.cmsr.onebase.module.bpm.runtime.vo.ListActButtonRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程执行服务实现类
 *
 * @author liyang
 * @date 2025-10-27
 */
@Slf4j
@Service
public class BpmExecServiceImpl implements BpmExecService {
    @Resource
    private BpmEngineDefExtService defExtService;

    @Resource
    private DefService defService;

    @Resource
    private InsService insService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ListActButtonRespVO getActButtons(String entityDataId, String businessId) {
        ListActButtonRespVO respVO = new ListActButtonRespVO();
        NodeJson currNodeJson = null;
        Instance instance = null;

        respVO.setButtons(new ArrayList<>());

        // businessId 业务ID是dataSetId，通过该ID能查到对应的流程定义信息
        // dataId是数据ID，通过该ID能查到对应的流程实例信息
        log.info("获取流程实例的操作按钮: {}, {}", entityDataId, businessId);

        // 查询已经发布的业务流程
        // 先判断dataId是否为空
        if (StringUtils.isBlank(entityDataId)) {
            // 为空则代表尚未发起流程，查询业务流程定义是否存在
            Definition flowDefinition = defExtService.getByFormPathAndStatus(businessId, PublishStatus.PUBLISHED.getKey());

            // 不存在已经发布的流程定义
            if (flowDefinition == null) {
                throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
            }

            // 获取开始节点对应的按钮
            DefJson defJson = defService.queryDesign(flowDefinition.getId());
            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (Objects.equals(nodeJson.getNodeType(), NodeType.START.getKey())) {
                    currNodeJson = nodeJson;
                    break;
                }
            }
        } else {
            // 数据存在则说明已经发起过流程，查询流程实例表
            instance = insService.getOne(FlowEngine.newIns().setBusinessId(entityDataId)
                    .setFormPath(businessId));

            // instance为空说明流程未发起，不存在流程实例
            if (instance == null) {
                throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
            }

            Integer nodeType = instance.getNodeType();

            // 流程节点类型不存在操作按钮
            if (!NodeType.isBetween(nodeType)) {
                throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
            }

            // 找到流程定义
            String defJsonStr = instance.getDefJson();
            DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);

            // 找到对应节点的配置
            String nodeCode = instance.getNodeCode();

            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (nodeJson.getNodeCode().equals(nodeCode)) {
                    currNodeJson = nodeJson;
                    break;
                }
            }
        }

        // 节点不存在
        if (currNodeJson == null) {
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
        }

        BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
        String userId = String.valueOf(WebFrameworkUtils.getLoginUserId());
        List<BaseNodeBtnCfgDTO> buttonConfigs = new ArrayList<>();

        if (nodeExtDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
            // todo：判断审批节点的权限

            // 审批节点
            if (approverNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(approverNodeExtDTO.getButtonConfigs());
            }
        } else if (nodeExtDTO instanceof StartNodeExtDTO startNodeExtDTO) {
            // 开始节点
            if (startNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(startNodeExtDTO.getButtonConfigs());
            }
        } else if (nodeExtDTO instanceof InitiationNodeExtDTO initiationNodeExtDTO) {
            // 发起节点
            // 判断是否是创建人
            if (!Objects.equals(instance.getCreateBy(), userId)) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }

            if (initiationNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(initiationNodeExtDTO.getButtonConfigs());
            }
        } else {
            // 未知节点
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_TYPE);
        }

        for (BaseNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            if (!buttonConfig.getEnabled()) {
                continue;
            }

            ListActButtonRespVO.ActionButton buttonVO = new ListActButtonRespVO.ActionButton();
            buttonVO.setButtonName(buttonConfig.getButtonName());
            buttonVO.setButtonType(buttonConfig.getButtonType());
            buttonVO.setDisplayName(buttonConfig.getDisplayName());
            respVO.getButtons().add(buttonVO);
        }

        respVO.setBusinessId(businessId);

        return respVO;
    }
}
