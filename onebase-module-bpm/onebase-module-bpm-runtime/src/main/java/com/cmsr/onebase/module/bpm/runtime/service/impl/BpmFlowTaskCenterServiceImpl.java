package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowInstanceExtVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmFlowTaskCenterService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.UserType;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmFlowTodoTaskPageReqVO pageReqVO) {
        //        SysUser sysUser = SecurityUtils.getLoginUser().getUser();  todo 获取当前登录人运行态的权限待开发
//        List<String> permissionList = permissionList(String.valueOf(sysUser.getUserId()), sysUser.getDeptId(), sysUser);
//        reqVO.setPermissionList(permissionList);
        List<String> permissionList = new ArrayList<>();
        PageResult<FlowTaskExt> pageResult = flowTaskRepository.getTodoTaskPage(pageReqVO, permissionList);
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (FlowTaskExt flowTaskExt : pageResult.getList()) {
            try {
                // 创建BpmFlowTodoTaskVO实例
                BpmFlowTodoTaskVO todoTaskVO = new BpmFlowTodoTaskVO();
                todoTaskVO.setId(flowTaskExt.getId());
                // 解析ext字段中的JSON数据
                if (StringUtils.isNotBlank(flowTaskExt.getExt())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    BpmFlowInstanceExtVO extVO = objectMapper.readValue(
                            flowTaskExt.getExt(),
                            BpmFlowInstanceExtVO.class
                    );
                    if (extVO != null && extVO.getProcessInfo() != null) {
                        BpmFlowInstanceExtVO.ProcessInfo processInfo = extVO.getProcessInfo();
                        todoTaskVO.setProcessTitle(processInfo.getProcessTitle());
                        todoTaskVO.setInitiator(processInfo.getInitiator());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        todoTaskVO.setSubmitTime(LocalDateTime.parse(processInfo.getSubmitTime(), formatter));
                        todoTaskVO.setFormSummary(processInfo.getFormSummary());
                    }
                }
                todoTaskVO.setArrivalTime(flowTaskExt.getCreateTime());
                todoTaskList.add(todoTaskVO);
            } catch (Exception e) {
                // 记录日志并跳过异常项
                throw new RuntimeException(e);
            }
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
    public PageResult<BpmFlowDoneTaskVO> getDonePage(BpmFlowDoneTaskPageReqVO pageReqVO) {
        PageResult<FlowHisTask> pageResult = flowHisTaskRepository.getDoneTaskPage(pageReqVO, WebFrameworkUtils.getLoginUserId());  //todo WebFrameworkUtils.getLoginUserId()需改为运行态
        List<BpmFlowDoneTaskVO> doneTaskList = new ArrayList<>();
        for (FlowHisTask flowHisTask : pageResult.getList()){
            BpmFlowDoneTaskVO doneTaskVO = new BpmFlowDoneTaskVO();
            if (StringUtils.isNotBlank(flowHisTask.getExt())){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    BpmFlowInstanceExtVO extVO = objectMapper.readValue(
                            flowHisTask.getExt(),
                            BpmFlowInstanceExtVO.class
                    );
                    if (extVO != null && extVO.getProcessInfo() != null) {
                        BpmFlowInstanceExtVO.ProcessInfo processInfo = extVO.getProcessInfo();
                        doneTaskVO.setProcessTitle(processInfo.getProcessTitle());
                        doneTaskVO.setInitiator(processInfo.getInitiator());
                        doneTaskVO.setFormSummary(processInfo.getFormSummary());
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                doneTaskVO.setId(flowHisTask.getId());
                doneTaskVO.setHandleTime(flowHisTask.getUpdateTime());
                doneTaskVO.setHandleOperation(flowHisTask.getSkipType());
            }
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
        PageResult<FlowInstance> pageResult = flowInstanceRepository.findPage(pageReqVO, WebFrameworkUtils.getLoginUserId());
        List<BpmMyCreatedVO>   list  = new ArrayList<>();
        for (FlowInstance flowInstance : pageResult.getList()){
            BpmMyCreatedVO bpmMyCreatedVO = new BpmMyCreatedVO();
            // 复制BpmFlowInstanceVO的属性
            BeanUtils.copyProperties(flowInstance,bpmMyCreatedVO);
            if (StringUtils.isNotBlank(flowInstance.getExt())){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    BpmFlowInstanceExtVO extVO = objectMapper.readValue(
                            flowInstance.getExt(),
                            BpmFlowInstanceExtVO.class
                    );
                    if (extVO != null && extVO.getProcessInfo() != null) {
                        BpmFlowInstanceExtVO.ProcessInfo processInfo = extVO.getProcessInfo();
                        bpmMyCreatedVO.setProcessTitle(processInfo.getProcessTitle());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        bpmMyCreatedVO.setSubmitTime(LocalDateTime.parse(processInfo.getSubmitTime(), formatter));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
        }
            //设置当前节点处理人
            List<FlowTask>  flowTaskList =flowTaskRepository.getByInsId(flowInstance.getId());
            if(CollectionUtils.isNotEmpty(flowTaskList)){
                List<Long> taskIds = StreamUtils.toList(flowTaskList, FlowTask::getId);
                List<User> userList = flowUserservice.getByAssociateds(taskIds);
                Map<Long, List<User>> map = StreamUtils.groupByKey(userList, User::getAssociated);
                List<User> users = map.get(flowTaskList.get(0).getId());
                if (CollectionUtils.isNotEmpty(users)) {
                    for (User user : users) {
                        if (UserType.APPROVAL.getKey().equals(user.getType())) {
                            if (StringUtils.isEmpty(bpmMyCreatedVO.getCurrentNodeHandler())) {
                                bpmMyCreatedVO.setCurrentNodeHandler("");
                            }
                            //String name = executeService.getName(user.getProcessedBy()); //todo 获取用户名称
                            String name="";
                            if(user.getProcessedBy().contains("role")){
                                name = "角色"+name;
                            }else{
                                name = "用户"+name;
                            }
                            if (StringUtils.isNotEmpty(name)) {
                                bpmMyCreatedVO.setCurrentNodeHandler(bpmMyCreatedVO.getCurrentNodeHandler().concat(name).concat(";"));
                            }
                        }
                    }
                }
            }
            list.add(bpmMyCreatedVO);

        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<FlowHisTask> getHisTaskByInstanceId(Long instanceId) {
        return flowHisTaskRepository.getHisTaskByInstanceId(instanceId);
    }
}
