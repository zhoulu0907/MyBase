package com.cmsr.onebase.module.bpm.runtime.service.predict.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.runtime.service.permission.BpmPermissionResolver;
import com.cmsr.onebase.module.bpm.runtime.service.predict.BpmPredictService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmPredictReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmPredictRespVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程预测服务实现类
 *
 * @author liyang
 * @date 2025-11-21
 */
@Slf4j
@Service
public class BpmPredictServiceImpl implements BpmPredictService {
    @Resource
    private BpmEngineDefExtService defExtService;

    @Resource
    private NodeService nodeService;

    @Resource
    private BpmPermissionResolver permissionResolver;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO) {
        Long businessId = reqVO.getBusinessId();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        List<BpmPredictRespVO.NodeInfo> nodes = new ArrayList<>();

        Definition definition = defExtService.getByFormPathAndStatus(String.valueOf(businessId), PublishStatus.PUBLISHED.getKey());
        if (definition == null) {
            log.error(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS.getMsg());
            throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
        }

        Node startNode = nodeService.getStartNode(definition.getId());

        if (startNode == null) {
            log.error("流程定义缺少开始节点");
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS.getCode(), "获取开始节点失败");
        }

        Node currentNode = startNode;
        Set<Long> allUserIds = new HashSet<>();

        while (true) {
            // todo：理论上只会有一条路径，需要结合实体信息预测下一步走向，主要是涉及到条件分支的
            Node nextNode = nodeService.getNextNode(definition.getId(), currentNode.getNodeCode(), null, SkipType.PASS.getKey());

            if (nextNode == null) {
                // 找不到下一个节点，结束，todo 是否应该抛出异常
                log.warn("没找到下一个节点");
                break;
            }

            if (NodeType.isEnd(nextNode.getNodeType())) {
                break;
            }

            currentNode = nextNode;

            // 设置预测节点信息
            BpmPredictRespVO.NodeInfo nodeInfo = new BpmPredictRespVO.NodeInfo();
            nodeInfo.setHandlers(new ArrayList<>());
            nodeInfo.setNodeCode(nextNode.getNodeCode());
            nodeInfo.setNodeName(nextNode.getNodeName());

            // 设置节点信息
            BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(nextNode.getExt(), BaseNodeExtDTO.class);

            // 获取业务节点类型
            String bizNodeType = nodeExtDTO.getNodeType();
            Set<Long> handlerIds = new HashSet<>();

            if (Objects.equals(bizNodeType, BpmNodeTypeEnum.INITIATION.getCode())) {
                // 发起节点的处理人是当前登录用户
                handlerIds.add(loginUserId);
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.APPROVER.getCode())) {
                // 解析权限标志
                Set<Long> approverUserIds = permissionResolver.resolveUserIds(currentNode.getPermissionFlag(), BpmConstants.MAX_NODE_APPROVER_USERS);

                if (CollectionUtils.isNotEmpty(approverUserIds)) {
                    handlerIds.addAll(approverUserIds);
                }
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.CC.getCode())) {
                // 解析权限标志
                Set<Long> ccUserIds = permissionResolver.resolveUserIds(currentNode.getPermissionFlag(), BpmConstants.MAX_NODE_CC_USERS);

                if (CollectionUtils.isNotEmpty(ccUserIds)) {
                    handlerIds.addAll(ccUserIds);
                }
            } else {
                // todo: 支持更多类型的节点
                log.warn("未知节点类型，bizNodeType: {}", bizNodeType);
                continue;
            }

            allUserIds.addAll(handlerIds);

            for (Long userId : handlerIds) {
                BpmPredictRespVO.HandlerInfo handlerInfo = new BpmPredictRespVO.HandlerInfo();
                handlerInfo.setHandlerId(userId);
                nodeInfo.getHandlers().add(handlerInfo);
            }

            nodes.add(nodeInfo);
        }

        // 获取用户的名称和头像
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(allUserIds);

        if (userResult.isSuccess()) {
            // 构建用户ID到用户信息的映射
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));

            for (BpmPredictRespVO.NodeInfo node : nodes) {
                for (BpmPredictRespVO.HandlerInfo handler : node.getHandlers()) {
                    AdminUserRespDTO user = userMap.get(handler.getHandlerId());
                    if (user != null) {
                        handler.setHandlerName(user.getNickname());
                        handler.setAvatar(user.getAvatar());
                    } else {
                        // todo：处理用户不存在的情况，名称先设置成 "-"
                        handler.setHandlerName("-");
                        log.warn("用户不存在，userId: {}", handler.getHandlerId());
                    }
                }
            }
        } else {
            log.warn("获取用户信息失败，userIds: {}", allUserIds);
            throw exception(ErrorCodeConstants.USER_API_CALL_FAILED);
        }

        return nodes;
    }
}
