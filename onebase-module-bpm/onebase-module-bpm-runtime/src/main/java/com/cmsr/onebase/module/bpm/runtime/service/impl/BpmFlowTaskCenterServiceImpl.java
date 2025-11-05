package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmHisTaskExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmInstanceExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmTaskExtRepository;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmDoneTaskPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmMyCreatedPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ListNodesRespVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class BpmFlowTaskCenterServiceImpl implements BpmFlowTaskCenterService {


    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Resource
    private FlowHisTaskRepository flowHisTaskRepository;

    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Resource
    private BpmTaskExtRepository taskExtRepository;

    @Resource
    private BpmHisTaskExtRepository hisTaskExtRepository;

    @Resource
    private BpmInstanceExtRepository insExtRepository;

    @Resource
    private UserService flowUserservice;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private InsService insService;

    @Resource
    private BpmFlowInsBizExtRepository insBizExtRepository;

    @Resource
    private AdminUserApi adminUserApi;
    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmTodoTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        PageResult<BpmTodoTaskDTO> pageResult = taskExtRepository.getTodoTaskPage(pageReqVO, String.valueOf(loginUserId));
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (BpmTodoTaskDTO flowTaskExt : pageResult.getList()) {
            try {
                BpmFlowTodoTaskVO todoTaskVO = getBpmFlowTodoTaskVO(flowTaskExt);
                todoTaskList.add(todoTaskVO);
            } catch (Exception e) {
                // 记录日志并跳过异常项
                throw new RuntimeException(e);
            }
        }
        // 返回新的PageResult
        return new PageResult<>(todoTaskList, pageResult.getTotal());
    }

    @NotNull
    private static BpmFlowTodoTaskVO getBpmFlowTodoTaskVO(BpmTodoTaskDTO flowTaskExt) {
        BpmFlowTodoTaskVO todoTaskVO = new BpmFlowTodoTaskVO();
        todoTaskVO.setTaskId(flowTaskExt.getId());
        todoTaskVO.setInstanceId(flowTaskExt.getInstanceId());
        todoTaskVO.setFlowStatus(flowTaskExt.getFlowStatus());
        todoTaskVO.setTaskId(flowTaskExt.getId());
        todoTaskVO.setNodeCode(flowTaskExt.getNodeCode());
        todoTaskVO.setProcessTitle(flowTaskExt.getBusinessTitle());
        todoTaskVO.setInitiator(flowTaskExt.getInitiatorName());
        todoTaskVO.setSubmitTime(flowTaskExt.getSubmitTime());
        todoTaskVO.setFormSummary(flowTaskExt.getFormSummary());
        todoTaskVO.setArrivalTime(flowTaskExt.getCreateTime());
        todoTaskVO.setBusinessId(flowTaskExt.getBusinessId());
        return todoTaskVO;
    }

    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowDoneTaskVO> getDonePage(BpmDoneTaskPageReqVO pageReqVO) {
        PageResult<BpmDoneTaskDTO> pageResult = hisTaskExtRepository.getDoneTaskPage(pageReqVO, WebFrameworkUtils.getLoginUserId());  //todo WebFrameworkUtils.getLoginUserId()需改为运行态
        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (BpmDoneTaskDTO flowHisTaskExt : pageResult.getList()) {
            BpmFlowDoneTaskVO doneTaskVO = new BpmFlowDoneTaskVO();
            doneTaskVO.setTaskId(flowHisTaskExt.getId());
            doneTaskVO.setInstanceId(flowHisTaskExt.getInstanceId());
            doneTaskVO.setProcessTitle(flowHisTaskExt.getBusinessTitle());
            doneTaskVO.setInitiator(flowHisTaskExt.getInitiatorName());
            doneTaskVO.setFormSummary(flowHisTaskExt.getFormSummary());
            doneTaskVO.setHandleTime(flowHisTaskExt.getUpdateTime());
            doneTaskVO.setTaskStatus(flowHisTaskExt.getFlowStatus());
            doneTaskVO.setBusinessId(flowHisTaskExt.getBusinessId());
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
        PageResult<BpmInstanceDTO> pageResult = insExtRepository.getMyCreatePage(pageReqVO, WebFrameworkUtils.getLoginUserId());
        List<BpmMyCreatedVO> list = new ArrayList<>();
        for (BpmInstanceDTO flowInstance : pageResult.getList()) {
            BpmMyCreatedVO bpmMyCreatedVO = new BpmMyCreatedVO();
            bpmMyCreatedVO.setId(flowInstance.getId());
            bpmMyCreatedVO.setProcessTitle(flowInstance.getBusinessTitle());
            bpmMyCreatedVO.setFlowStatus(flowInstance.getFlowStatus());
            bpmMyCreatedVO.setFormSummary(flowInstance.getFormSummary());
            bpmMyCreatedVO.setSubmitTime(flowInstance.getSubmitTime());
            bpmMyCreatedVO.setCreateTime(flowInstance.getCreateTime());
            bpmMyCreatedVO.setUpdateTime(flowInstance.getUpdateTime());
            bpmMyCreatedVO.setInstanceId(flowInstance.getId());
            bpmMyCreatedVO.setBusinessId(flowInstance.getBusinessId());

            //设置当前节点处理人
            List<Task> flowTaskList = taskService.getByInsId(flowInstance.getId());

            if (CollectionUtils.isNotEmpty(flowTaskList)) {
                bpmMyCreatedVO.setTaskId(flowTaskList.get(0).getId());
                List<Long> taskIds = StreamUtils.toList(flowTaskList, Task::getId);
                List<User> userList = flowUserservice.getByAssociateds(taskIds);
                List<Long> processedByIds = userList.stream()
                        .map(user -> Long.valueOf(user.getProcessedBy()))
                        .collect(Collectors.toList());
                CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(processedByIds);
                List<Map<String, Object>> currentNodeHandler = new ArrayList<>();
                dtos.getData().forEach(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", dto.getId());
                    map.put("userName", dto.getNickname());
                    currentNodeHandler.add( map);
                });
                bpmMyCreatedVO.setCurrentNodeHandler(currentNodeHandler);
            }
            list.add(bpmMyCreatedVO);

        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<ListNodesRespVO.NodeVO> listNodes(Long instanceId) {
        List<ListNodesRespVO.NodeVO> nodeVOs = new ArrayList<>();

        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        String defJsonStr = instance.getDefJson();

        DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);
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
}
