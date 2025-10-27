package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.FlowTaskDO;
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
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
        PageResult<FlowTaskDO> pageResult = flowTaskRepository.getTodoTaskPage(pageReqVO, permissionList);
        List<BpmFlowTodoTaskVO> todoTaskList = new ArrayList<>();

        for (FlowTaskDO flowTaskDO : pageResult.getList()) {
            try {
                // 创建BpmFlowTodoTaskVO实例
                BpmFlowTodoTaskVO todoTaskVO = new BpmFlowTodoTaskVO();

                // 复制BpmFlowInstanceVO的属性
                BeanUtils.copyProperties(flowTaskDO,todoTaskVO);
                // 解析ext字段中的JSON数据
                if (StringUtils.isNotBlank(flowTaskDO.getExt())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode extNode = objectMapper.readTree(flowTaskDO.getExt());
                    // 提取processInfo对象
                    if (extNode.has("processInfo")) {
                        JsonNode processInfoNode = extNode.get("processInfo");
                        // 设置BpmFlowTodoTaskVO特有字段
                        if (processInfoNode.has("processTitle")) {
                            todoTaskVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }
                        if (processInfoNode.has("initiator")) {
                            todoTaskVO.setInitiator(processInfoNode.get("initiator").asText());
                        }
                        if (processInfoNode.has("formSummary")) {
                            todoTaskVO.setFormSummary(processInfoNode.get("formSummary").asText());
                        }
                        // 时间字段转换
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        if (processInfoNode.has("submitTime")) {
                            String submitTimeStr = processInfoNode.get("submitTime").asText();
                            if (StringUtils.isNotBlank(submitTimeStr)) {
                                todoTaskVO.setSubmitTime(LocalDateTime.parse(submitTimeStr, formatter));
                            }
                        }
                    }
                }
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
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode extNode = objectMapper.readTree(flowHisTask.getExt());
                    if (extNode.has("processInfo")){
                        JsonNode processInfoNode = extNode.get("processInfo");
                        if (processInfoNode.has("processTitle")) {
                            doneTaskVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }
                        if (processInfoNode.has("initiator")) {
                            doneTaskVO.setInitiator(processInfoNode.get("initiator").asText());
                        }
                        if (processInfoNode.has("formSummary")) {
                            doneTaskVO.setFormSummary(processInfoNode.get("formSummary").asText());
                        }
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
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode extNode = objectMapper.readTree(flowInstance.getExt());
                    if (extNode.has("processInfo")){
                        JsonNode processInfoNode = extNode.get("processInfo");
                        if (processInfoNode.has("processTitle")) {
                            bpmMyCreatedVO.setProcessTitle(processInfoNode.get("processTitle").asText());
                        }
                        // 时间字段转换
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        if (processInfoNode.has("submitTime")) {
                            String submitTimeStr = processInfoNode.get("submitTime").asText();
                            if (StringUtils.isNotBlank(submitTimeStr)) {
                                bpmMyCreatedVO.setSubmitTime(LocalDateTime.parse(submitTimeStr, formatter));
                            }
                        }

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
}
