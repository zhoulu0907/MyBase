package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmTaskCenterMapper;
import com.cmsr.onebase.module.bpm.core.dto.BpmCcRecordDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmMyInstanceDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmFlowDefinitionRepositoryExt;
import com.cmsr.onebase.module.bpm.core.vo.*;
import com.cmsr.onebase.module.bpm.runtime.convert.BpmTaskCenterConvert;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class BpmFlowTaskCenterServiceImpl implements BpmFlowTaskCenterService {
    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource(name = "bpmUserService")
    private UserService userService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private BpmFlowDefinitionRepositoryExt defExtService;

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource
    private BpmFlowCcRecordRepository bpmFlowCcRecordRepository;

    @Resource
    private BpmTaskCenterMapper bpmTaskCenterMapper;

    private final BpmTaskCenterConvert bpmTaskCenterConvert = BpmTaskCenterConvert.INSTANCE;

    private List<String> splitToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }

        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<Long> splitToLongList(String str) {
       return splitToList(str).stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private List<String> splitToFlowStatusList(String str) {
        List<String> flowStatusList = splitToList(str);

        if (CollectionUtils.isEmpty(flowStatusList)) {
            return flowStatusList;
        }

        return flowStatusList.stream()
                .filter(s -> {
                    if (BpmBusinessStatusEnum.getByCode(s) == null) {
                        log.warn("忽略不支持的流程状态：{}", s);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private void fillAppId(BpmInsExtQueryPageVO queryPageVO) {
        // todo: 后续放到全局的Repository中
        Long appId = ApplicationManager.getApplicationId();

        if (appId == null) {
            throw new IllegalArgumentException("应用ID不能为空");
        }

        queryPageVO.setAppId(appId);
    }

    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmTodoTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        String loginUserStrId = String.valueOf(loginUserId);

        fillAppId(pageReqVO);

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        // 处理发起人参数
        pageReqVO.setInitiatorIdList(splitToList(pageReqVO.getInitiatorId()));

        com.github.pagehelper.Page<BpmTodoTaskDTO> pageResult = PageHelper
                .startPage(pageReqVO.getPageNo(), pageReqVO.getPageSize())
                .doSelectPage(() -> bpmTaskCenterMapper.getTodoTaskPage(pageReqVO, loginUserStrId));

        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (BpmTodoTaskDTO flowTaskExt : pageResult.getResult()) {
            BpmFlowTodoTaskVO todoTaskVO = bpmTaskCenterConvert.toTodoTaskVO(flowTaskExt);

            // 处理代理逻辑
            handleAgentLogic(todoTaskVO, flowTaskExt.getAgentId(), flowTaskExt.getBpmTitle(), loginUserId);
            todoTaskList.add(todoTaskVO);
        }

        // 返回新的PageResult
        return new PageResult<>(todoTaskList, pageResult.getTotal());
    }


    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowDoneTaskVO> getDonePage(BpmDoneTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        String loginUserStrId = String.valueOf(loginUserId);

        fillAppId(pageReqVO);

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        // 处理发起人参数
        pageReqVO.setInitiatorIdList(splitToList(pageReqVO.getInitiatorId()));

        com.github.pagehelper.Page<BpmDoneTaskDTO> pageResult = PageHelper
                .startPage(pageReqVO.getPageNo(), pageReqVO.getPageSize())
                .doSelectPage(() -> bpmTaskCenterMapper.getDoneTaskPage(pageReqVO, loginUserStrId));

        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (BpmDoneTaskDTO doneTaskDTO : pageResult.getResult()) {
            BpmFlowDoneTaskVO doneTaskVO = bpmTaskCenterConvert.toDoneTaskVO(doneTaskDTO);
            // 处理代理逻辑
            handleAgentLogic(doneTaskVO, doneTaskDTO.getAgentId(), doneTaskDTO.getBpmTitle(), loginUserId);
            doneTaskList.add(doneTaskVO);
        }

        return new PageResult<>(doneTaskList, pageResult.getTotal());
    }

    /**
     * 获取我创建的流程分页
     *
     * @param pageReqVO
     * @return
     */
    public PageResult<BpmMyCreatedVO> getMyCreatedPage(BpmMyCreatedPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        fillAppId(pageReqVO);

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        com.github.pagehelper.Page<BpmMyInstanceDTO> pageResult = PageHelper
                .startPage(pageReqVO.getPageNo(), pageReqVO.getPageSize())
                .doSelectPage(() -> bpmTaskCenterMapper.getMyCreatePage(pageReqVO, loginUserId));

        List<BpmMyCreatedVO> list = new ArrayList<>();
        for (BpmMyInstanceDTO flowInstance : pageResult.getResult()) {
            BpmMyCreatedVO bpmMyCreatedVO = bpmTaskCenterConvert.toMyCreatedVO(flowInstance);

            //设置当前节点处理人
            List<Task> flowTaskList = taskService.getByInsId(flowInstance.getId());

            if (CollectionUtils.isNotEmpty(flowTaskList)) {
                bpmMyCreatedVO.setTaskId(flowTaskList.get(0).getId());
                List<Long> taskIds = StreamUtils.toList(flowTaskList, Task::getId);
                List<User> userList = userService.getByAssociateds(taskIds);
                List<Long> processedByIds = userList.stream()
                        .map(user -> Long.valueOf(user.getProcessedBy()))
                        .collect(Collectors.toList());

                CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(processedByIds);
                List<Map<String, Object>> currentNodeHandler = new ArrayList<>();

                // todo：历史遗留，待修复
                dtos.getData().forEach(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", dto.getId());
                    map.put("userName", dto.getNickname());
                    map.put("avatar", dto.getAvatar());
                    currentNodeHandler.add( map);
                });

                bpmMyCreatedVO.setCurrentNodeHandler(currentNodeHandler);
            }

            list.add(bpmMyCreatedVO);
        }

        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<ListNodesRespVO.NodeVO> listNodes(String bindingViewId) {
        List<ListNodesRespVO.NodeVO> nodeVOs = new ArrayList<>();

        Definition def = defExtService.getByFormPathAndStatus(bindingViewId, PublishStatus.PUBLISHED.getKey());
        if (def == null) {
            // todo：无发布状态的定义，返回空列表，是否要返回历史状态的流程定义数据
            return nodeVOs;
        }

        DefJson defJson = defService.queryDesign(def.getId());

        if (defJson == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        for (NodeJson nodeJson : defJson.getNodeList()) {
            // 只取中间节点
            if (NodeType.isBetween(nodeJson.getNodeType())) {
                ListNodesRespVO.NodeVO nodeVO = new ListNodesRespVO.NodeVO();
                nodeVO.setNodeCode(nodeJson.getNodeCode());
                nodeVO.setNodeName(nodeJson.getNodeName());

                // 取实际的类型
                BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(nodeJson.getExt(), BaseNodeExtDTO.class);
                nodeVO.setNodeType(nodeExtDTO.getNodeType());

                nodeVOs.add(nodeVO);
            }
        }

        return nodeVOs;
    }

    /**
     * 获取流程抄送分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmCcTaskPageResVO> getCcPage(BpmCcTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        String loginUserStrId = String.valueOf(loginUserId);

        fillAppId(pageReqVO);

        // 处理节点编码参数
        pageReqVO.setNodeCodeList(splitToList(pageReqVO.getNodeCode()));

        // 处理流程状态参数
        pageReqVO.setFlowStatusList(splitToFlowStatusList(pageReqVO.getFlowStatus()));

        // 处理发起人参数
        pageReqVO.setInitiatorIdList(splitToList(pageReqVO.getInitiatorId()));

        com.github.pagehelper.Page<BpmCcRecordDTO> pageResult =  PageHelper
                .startPage(pageReqVO.getPageNo(), pageReqVO.getPageSize())
                .doSelectPage(() -> bpmFlowCcRecordRepository.getMapper().getCcPage(pageReqVO, loginUserStrId));

        // 转换 BpmCcRecordDTO 列表为 BpmCopyTaskPageResVO 列表
        List<BpmCcTaskPageResVO> copyTaskList = new ArrayList<>();

        for (BpmCcRecordDTO ccRecord : pageResult.getResult()) {
            BpmCcTaskPageResVO ccTaskVO = bpmTaskCenterConvert.toCcTaskVO(ccRecord);
            // 处理代理逻辑
            handleAgentLogic(ccTaskVO, ccRecord.getAgentId(), ccRecord.getBpmTitle(), loginUserId);
            copyTaskList.add(ccTaskVO);
        }

        return new PageResult<>(copyTaskList, pageResult.getTotal());
    }

    /**
     * 处理代理逻辑：如果是代理执行，则在流程标题前添加"【代理审批】"前缀
     *
     * @param vo 任务VO（待办/已办/抄送）
     * @param agentId 代理人ID
     * @param originalTitle 原始流程标题
     * @param loginUserId 当前登录用户ID
     */
    private void handleAgentLogic(Object vo, String agentId, String originalTitle, Long loginUserId) {
        if (agentId != null && Objects.equals(String.valueOf(loginUserId), agentId)) {
            String agentTitle = BpmConstants.AGENT_TITLE_PREFIX + originalTitle;
            if (vo instanceof BpmFlowTodoTaskVO) {
                ((BpmFlowTodoTaskVO) vo).setProcessTitle(agentTitle);
            } else if (vo instanceof BpmFlowDoneTaskVO) {
                ((BpmFlowDoneTaskVO) vo).setProcessTitle(agentTitle);
            } else if (vo instanceof BpmCcTaskPageResVO) {
                ((BpmCcTaskPageResVO) vo).setProcessTitle(agentTitle);
            }
        }
    }

}
