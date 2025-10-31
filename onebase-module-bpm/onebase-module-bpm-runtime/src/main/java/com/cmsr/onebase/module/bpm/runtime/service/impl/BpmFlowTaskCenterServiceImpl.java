package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsExtRepository;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowInstanceExtVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowHisTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowInstanceExt;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.UserType;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpmFlowTaskCenterServiceImpl implements BpmFlowTaskCenterService {


    @Resource
    FlowInstanceRepository flowInstanceRepository;

    @Resource
    FlowHisTaskRepository flowHisTaskRepository;

    @Resource
    FlowTaskRepository flowTaskRepository;

    @Resource
    private UserService flowUserservice;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private BpmFlowInsExtRepository insExtRepository;

    @Resource
    AdminUserApi adminUserApi;
    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmFlowTodoTaskPageReqVO pageReqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        PageResult<FlowTaskExt> pageResult = flowTaskRepository.getTodoTaskPage(pageReqVO, String.valueOf(loginUserId));
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (FlowTaskExt flowTaskExt : pageResult.getList()) {
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
    private static BpmFlowTodoTaskVO getBpmFlowTodoTaskVO(FlowTaskExt flowTaskExt) {
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
        return todoTaskVO;
    }

    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowDoneTaskVO> getDonePage(BpmFlowDoneTaskPageReqVO pageReqVO) {
        PageResult<FlowHisTaskExt> pageResult = flowHisTaskRepository.getDoneTaskPage(pageReqVO, WebFrameworkUtils.getLoginUserId());  //todo WebFrameworkUtils.getLoginUserId()需改为运行态
        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (FlowHisTaskExt flowHisTaskExt : pageResult.getList()) {
            BpmFlowDoneTaskVO doneTaskVO = new BpmFlowDoneTaskVO();
            doneTaskVO.setTaskId(flowHisTaskExt.getId());
            doneTaskVO.setInstanceId(flowHisTaskExt.getInstanceId());
            doneTaskVO.setProcessTitle(flowHisTaskExt.getBusinessTitle());
            doneTaskVO.setInitiator(flowHisTaskExt.getInitiatorName());
            doneTaskVO.setFormSummary(flowHisTaskExt.getFormSummary());
            doneTaskVO.setHandleTime(flowHisTaskExt.getUpdateTime());
            doneTaskVO.setTaskStatus(flowHisTaskExt.getFlowStatus());
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
        PageResult<FlowInstanceExt> pageResult = flowInstanceRepository.getMyCreatePage(pageReqVO, WebFrameworkUtils.getLoginUserId());
        List<BpmMyCreatedVO> list = new ArrayList<>();
        for (FlowInstanceExt flowInstance : pageResult.getList()) {
            BpmMyCreatedVO bpmMyCreatedVO = new BpmMyCreatedVO();
            bpmMyCreatedVO.setId(flowInstance.getId());
            bpmMyCreatedVO.setProcessTitle(flowInstance.getBusinessTitle());
            bpmMyCreatedVO.setFlowStatus(flowInstance.getFlowStatus());
            bpmMyCreatedVO.setFormSummary(flowInstance.getFormSummary());
            bpmMyCreatedVO.setSubmitTime(flowInstance.getSubmitTime());
            bpmMyCreatedVO.setCreateTime(flowInstance.getCreateTime());
            bpmMyCreatedVO.setUpdateTime(flowInstance.getUpdateTime());
            //设置当前节点处理人
            List<FlowTask> flowTaskList = flowTaskRepository.getByInsId(flowInstance.getId());
            if (CollectionUtils.isNotEmpty(flowTaskList)) {
                bpmMyCreatedVO.setTaskId(flowTaskList.get(0).getId());
                List<Long> taskIds = StreamUtils.toList(flowTaskList, FlowTask::getId);
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
    public List<FlowHisTask> getHisTaskByInstanceId(Long instanceId, String appId) {
        return flowHisTaskRepository.getHisTaskByInstanceId(instanceId, appId);
    }
}
