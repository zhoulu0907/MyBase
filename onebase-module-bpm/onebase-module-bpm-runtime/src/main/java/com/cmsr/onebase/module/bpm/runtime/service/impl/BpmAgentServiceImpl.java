package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmAgentService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import com.cmsr.onebase.module.bpm.core.enums.BpmAgentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
@Service
public class BpmAgentServiceImpl implements BpmAgentService {
    @Resource
    private BpmFlowAgentRepository bpmFlowAgentRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public PageResult<BpmAgentPageResVO> getAgentPage(BpmAgentPageReqVO pageReqVO) {
        // 获取当前登录用户ID
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 构建查询条件
        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));

        PageResult<BpmFlowAgentDO> pageResult =bpmFlowAgentRepository.findPageWithConditions(condition, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        // 结果不为空时 获取代理人，被代理人，创建人信息
        List<BpmFlowAgentDO> agentList = pageResult.getList() != null ?
                pageResult.getList() : Collections.emptyList();
        Map<Long, AdminUserRespDTO> agentUserInfoMap = queryAgentUser(agentList);

        List<BpmAgentPageResVO> list = pageResult.getList().stream().map(item -> {
            BpmAgentPageResVO vo = new BpmAgentPageResVO();
            // 代理人
            vo.setAgent(createAgentUser(agentUserInfoMap.get(item.getAgentId())));
            // 被代理人
            vo.setPrincipal(createAgentUser(agentUserInfoMap.get(item.getPrincipalId())));
            //创建人
            vo.setCreator(createAgentUser(agentUserInfoMap.get(item.getCreator())));
            vo.setStartTime(item.getStartTime());
            vo.setEndTime(item.getEndTime());
            vo.setCreateTime(item.getCreateTime());
            vo.setId(item.getId());
            // 代理状态
            vo.setAgentStatus(calculateAgentStatus(item.getStartTime(), item.getEndTime(),item.getRevokedTime()));
            return vo;
        }).toList();
       return new PageResult<>(list, pageResult.getTotal());
    }
    /**
     * 新增代理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(BpmAgentInsertReqVO reqVO) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        String nickname =SecurityFrameworkUtils.getLoginUserNickname();
        // 校验开始时间必须小于结束时间
        if (reqVO.getStartTime() != null && reqVO.getEndTime() != null &&
                !reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.AGENT_START_TIME_AFTER_END_TIME);
        }

        // 验证代理人是否存在
        AdminUserRespDTO agentUser = validateAgentExists(reqVO.getAgentId());

        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
        validateExistingAgent(userId, reqVO.getStartTime(), reqVO.getEndTime());

        // 插入数据
        BpmFlowAgentDO bpmFlowAgentDO = new BpmFlowAgentDO();
        bpmFlowAgentDO.setPrincipalId(userId);
        bpmFlowAgentDO.setPrincipalName(nickname);
        bpmFlowAgentDO.setAgentId(reqVO.getAgentId());
        bpmFlowAgentDO.setAgentName(agentUser.getNickname());
        bpmFlowAgentDO.setStartTime(reqVO.getStartTime());
        bpmFlowAgentDO.setEndTime(reqVO.getEndTime());
        bpmFlowAgentDO.setAppId(reqVO.getAppId());
        bpmFlowAgentRepository.insert(bpmFlowAgentDO);

    }
    /**
     * 验证代理人是否存在
     * @param agentId 代理人ID
     * @return 代理人用户信息
     */
    private AdminUserRespDTO validateAgentExists(Long agentId) {

        CommonResult<AdminUserRespDTO> agentUserResult = adminUserApi.getUser(agentId);
        if (!agentUserResult.isSuccess() || agentUserResult.getData() == null) {
            throw exception(ErrorCodeConstants.AGENT_USER_NOT_EXISTS);
        }
        return agentUserResult.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke( BpmAgentRevokeReqVO reqVO) {
        // 获取当前登录用户信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查询要撤销的代理记录
        BpmFlowAgentDO agent = bpmFlowAgentRepository.findById(reqVO.getAgentId());
        if (agent == null) {
            throw exception(ErrorCodeConstants.AGENT_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能撤销
        validateRevokePermission(agent, loginUserId);

        // 校验状态：只能撤销"待生效"或"代理中"的记录
        validateRevokeStatus(agent);

        // 执行撤销操作：设置撤销时间
        agent.setRevokedTime(LocalDateTime.now());
        agent.setRevokerId(loginUserId);
        bpmFlowAgentRepository.update(agent);
    }
    /**
     * 更新代理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmAgentUpdateReqVO reqVO) {
        // 获取当前登录用户信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查询要更新的代理记录
        BpmFlowAgentDO agent = bpmFlowAgentRepository.findById(reqVO.getId());
        if (agent == null) {
            throw exception(ErrorCodeConstants.AGENT_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能编辑
        validateRevokePermission(agent, loginUserId);

        // 校验状态：只能编辑"待生效"或"代理中"的记录
        validateRevokeStatus(agent);

        // 校验开始时间必须小于结束时间
        if (reqVO.getStartTime() != null && reqVO.getEndTime() != null &&
                !reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.AGENT_START_TIME_AFTER_END_TIME);
        }

        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系（排除自己）
        validateUpdateAgent(agent.getId(), agent.getPrincipalId(), reqVO.getStartTime(), reqVO.getEndTime());

        // 验证代理人是否存在
        AdminUserRespDTO agentUser = validateAgentExists(reqVO.getAgentId());

        // 执行更新操作
        agent.setAgentId(reqVO.getAgentId());
        agent.setAgentName(agentUser.getNickname());
        agent.setStartTime(reqVO.getStartTime());
        agent.setEndTime(reqVO.getEndTime());

        bpmFlowAgentRepository.update(agent);
    }

    /**
     * 校验更新时被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
     * @param id 当前记录ID
     * @param principalId 被代理人ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateUpdateAgent(Long id, Long principalId, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件：查找指定被代理人的未撤销记录，排除当前记录
        DefaultConfigStore condition = new DefaultConfigStore();
        condition.and(Compare.EQUAL, "principal_id", principalId);
        condition.and(Compare.NULL, "revoked_time");
        condition.and(Compare.NOT_EQUAL, "id", id);

        // 查找时间有重叠的记录
        ConfigStore timeOverlapCondition = new DefaultConfigStore();
        timeOverlapCondition.and(Compare.LESS, "start_time", endTime);  // 记录开始时间 < 新记录结束时间
        timeOverlapCondition.and(Compare.GREAT, "end_time", startTime); // 记录结束时间 > 新记录开始时间
        condition.and(timeOverlapCondition);

        List<BpmFlowAgentDO> existingAgents = bpmFlowAgentRepository.findAllByConfig(condition);

        // 检查这些记录是否处于"待生效"或"代理中"状态
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveOrPending = existingAgents.stream().anyMatch(agent ->
                (agent.getStartTime() != null && now.isBefore(agent.getStartTime())) || // 待生效
                        (agent.getStartTime() != null && agent.getEndTime() != null &&
                                now.isAfter(agent.getStartTime()) && now.isBefore(agent.getEndTime())) // 代理中
        );

        if (hasActiveOrPending) {
            throw exception(ErrorCodeConstants.AGENT_TIME_CONFLICT);
        }
    }


    /**
     * 校验撤销权限：只有创建人或被代理人才能操作
     * @param agent 代理记录
     * @param loginUserId 当前登录用户ID
     */
    private void validateRevokePermission(BpmFlowAgentDO agent, Long loginUserId) {
        if (!loginUserId.equals(agent.getCreator()) && !loginUserId.equals(agent.getPrincipalId())) {
            throw exception(ErrorCodeConstants.AGENT_REVOKE_NO_PERMISSION);
        }
    }

    /**
     * 校验撤销状态：只能撤销"待生效"或"代理中"的记录
     * @param agent 代理记录
     */
    private void validateRevokeStatus(BpmFlowAgentDO agent) {
        String status = calculateAgentStatus(agent.getStartTime(), agent.getEndTime(), agent.getRevokedTime());
        if (!BpmAgentStatus.INACTIVE.getCode().equals(status) &&
                !BpmAgentStatus.ACTIVE.getCode().equals(status)) {
            throw exception(ErrorCodeConstants.AGENT_REVOKE_INVALID_STATUS);
        }
    }


    /**
     * 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
     * @param principalId 被代理人ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateExistingAgent(Long principalId, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件：查找指定被代理人的未撤销记录
        DefaultConfigStore condition = new DefaultConfigStore();
        condition.and(Compare.EQUAL, "principal_id", principalId);
        condition.and(Compare.NULL, "revoked_time");

        // 查找时间有重叠的记录
        ConfigStore timeOverlapCondition = new DefaultConfigStore();
        timeOverlapCondition.and(Compare.LESS, "start_time", endTime);  // 记录开始时间 < 新记录结束时间
        timeOverlapCondition.and(Compare.GREAT, "end_time", startTime); // 记录结束时间 > 新记录开始时间
        condition.and(timeOverlapCondition);

        List<BpmFlowAgentDO> existingAgents = bpmFlowAgentRepository.findAllByConfig(condition);

        // 检查这些记录是否处于"待生效"或"代理中"状态
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveOrPending = existingAgents.stream().anyMatch(agent ->
                (agent.getStartTime() != null && now.isBefore(agent.getStartTime())) || // 待生效
                        (agent.getStartTime() != null && agent.getEndTime() != null &&
                                now.isAfter(agent.getStartTime()) && now.isBefore(agent.getEndTime())) // 代理中
        );

        if (hasActiveOrPending) {
            throw exception(ErrorCodeConstants.AGENT_TIME_CONFLICT);
        }
    }

    /**
     * 计算委托状态
     * @param startTime 代理生效时间
     * @param endTime 代理失效时间
     * @param revokedTime 撤销时间
     * @return 委托状态字符串
     */
    private String calculateAgentStatus(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime revokedTime) {
        // 如果撤销时间不为空，直接返回已撤销
        if (revokedTime != null) {
            return BpmAgentStatus.REVOKED.getCode();
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 判断待生效：当前时间 < 代理生效时间
        if (startTime != null && now.isBefore(startTime)) {
            return BpmAgentStatus.INACTIVE.getCode();
        }

        // 判断代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
        if (startTime != null && endTime != null &&
                now.isAfter(startTime) && now.isBefore(endTime)) {
            return BpmAgentStatus.ACTIVE.getCode();
        }

        // 判断已失效：当前时间 > 代理失效时间
        if (endTime != null && now.isAfter(endTime)) {
            return BpmAgentStatus.EXPIRED.getCode();
        }

        return "unknowStatus";
    }



    private UserBasicInfoVO createAgentUser(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        UserBasicInfoVO operationUser = new UserBasicInfoVO();
        operationUser.setUserId(user.getId());
        operationUser.setName(user.getNickname());
        operationUser.setAvatar(user.getAvatar());
        return operationUser;
    }

    private Map<Long, AdminUserRespDTO> queryAgentUser(List<BpmFlowAgentDO>  list) {
        // 获取创建人和代理人被代理人去重后一次性查出
        Set<Long> userIds = list.stream()
                .flatMap(item -> Stream.of(item.getCreator(), item.getPrincipalId(), item.getAgentId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);
        if (userResult.isSuccess()) {
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, user -> user));
            return userMap;
        }else{
            throw exception(ErrorCodeConstants.USER_API_CALL_FAILED);
        }
    }

    private ConfigStore buildDynamicCondition(BpmAgentPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();
        // 填充条件
        fillCondition(condition, reqVO,userId);

        return condition;
    }
    private void fillCondition(ConfigStore condition, BpmAgentPageReqVO queryVO, String userId) {
        condition.and(Compare.EQUAL, "app_id", queryVO.getAppId());
        ConfigStore userIdConfig = new DefaultConfigStore();
        userIdConfig.or(Compare.EQUAL, "principal_id", userId);
        userIdConfig.or(Compare.EQUAL, "agent_id", userId);
        condition.and(userIdConfig);
        // 动态添加其他查询条件
        if (StringUtils.isNotBlank(queryVO.getPersonName())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "principal_name", queryVO.getPersonName());
            orCondition.or(Compare.LIKE, "agent_name", queryVO.getPersonName());
            condition.and(orCondition);
        }
        if (StringUtils.isNotBlank(queryVO.getAgentStatus())) {
            String status = queryVO.getAgentStatus();
            LocalDateTime now = LocalDateTime.now();

            ConfigStore statusCondition = new DefaultConfigStore();
            if (BpmAgentStatus.INACTIVE.getCode().equals(status)) {
                // 待生效：当前时间 < 代理生效时间
                statusCondition.and(Compare.GREAT, "start_time", now);
            } else if (BpmAgentStatus.ACTIVE.getCode().equals(status)) {
                // 代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
                statusCondition.and(Compare.LESS_EQUAL, "start_time", now);
                statusCondition.and(Compare.GREAT_EQUAL, "end_time", now);
            } else if (BpmAgentStatus.EXPIRED.getCode().equals(status)) {
                // 已失效：当前时间 > 代理失效时间
                statusCondition.and(Compare.LESS, "end_time", now);
            } else if (BpmAgentStatus.REVOKED.getCode().equals(status)) {
                // 已撤销：撤销时间不为空
                statusCondition.and(Compare.NOT_NULL, "revoked_time");
            }
            condition.and(statusCondition);
        }
        condition.order("create_time", "desc");
    }

}
