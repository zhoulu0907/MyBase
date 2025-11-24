package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
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
import org.apache.commons.collections4.CollectionUtils;
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

        PageResult<BpmFlowAgentDO> pageResult = bpmFlowAgentRepository.findPageWithConditions(condition, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        // 结果不为空时 获取代理人，被代理人，创建人信息
        List<BpmFlowAgentDO> agentList = pageResult.getList() != null ?
                pageResult.getList() : Collections.emptyList();
        Map<Long, AdminUserRespDTO> agentUserInfoMap = queryAgentUser(agentList);

        List<BpmAgentPageResVO> list = pageResult.getList().stream().map(item -> {
            BpmAgentPageResVO vo = new BpmAgentPageResVO();
            // 代理人
            vo.setAgent(toAgentUser(agentUserInfoMap.get(item.getAgentId())));
            // 被代理人
            vo.setPrincipal(toAgentUser(agentUserInfoMap.get(item.getPrincipalId())));
            //创建人
            vo.setCreator(toAgentUser(agentUserInfoMap.get(item.getCreator())));
            vo.setStartTime(item.getStartTime());
            vo.setEndTime(item.getEndTime());
            vo.setCreateTime(item.getCreateTime());
            vo.setId(item.getId());

            // 代理状态
            BpmAgentStatus agentStatus = calculateAgentStatus(item.getStartTime(), item.getEndTime(),item.getRevokedTime());
            vo.setAgentStatus(agentStatus.getCode());

            return vo;
        }).toList();
       return new PageResult<>(list, pageResult.getTotal());
    }
    /**
     * 新增代理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BpmAgentInsertReqVO reqVO) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        String nickname = SecurityFrameworkUtils.getLoginUserNickname();
        LocalDateTime now = LocalDateTime.now();

        // 校验开始时间必须小于结束时间
        if (!reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.AGENT_START_TIME_AFTER_END_TIME);
        }

        if (!reqVO.getEndTime().isAfter(now)) {
            throw exception(ErrorCodeConstants.AGENT_END_TIME_BEFORE_NOW);
        }

        // 验证代理人是否存在
        AdminUserRespDTO agentUser = validateAgentExists(reqVO.getAgentId());

        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
        List<BpmFlowAgentDO> overlapAgentList = bpmFlowAgentRepository.findAllOverlapAgent(reqVO.getAppId(), userId, reqVO.getStartTime(), reqVO.getEndTime());
        if (CollectionUtils.isNotEmpty(overlapAgentList)) {
            throw exception(ErrorCodeConstants.AGENT_TIME_CONFLICT);
        }

        // 插入数据
        BpmFlowAgentDO bpmFlowAgentDO = new BpmFlowAgentDO();
        bpmFlowAgentDO.setPrincipalId(userId);
        bpmFlowAgentDO.setPrincipalName(nickname);
        bpmFlowAgentDO.setAgentId(reqVO.getAgentId());
        bpmFlowAgentDO.setAgentName(agentUser.getNickname());
        bpmFlowAgentDO.setStartTime(reqVO.getStartTime());
        bpmFlowAgentDO.setEndTime(reqVO.getEndTime());
        bpmFlowAgentDO.setAppId(reqVO.getAppId());

        // todo: 更新需要加锁，避免并发问题
        bpmFlowAgentDO = bpmFlowAgentRepository.insert(bpmFlowAgentDO);
        return bpmFlowAgentDO.getId();
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

        // 查询可撤销的代理记录
        BpmFlowAgentDO agent = bpmFlowAgentRepository.findById(reqVO.getId());
        if (agent == null) {
            throw exception(ErrorCodeConstants.AGENT_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能撤销
        if (!loginUserId.equals(agent.getCreator()) && !loginUserId.equals(agent.getPrincipalId())) {
            throw exception(ErrorCodeConstants.AGENT_REVOKE_NO_PERMISSION);
        }

        // 校验状态：只能撤销"待生效"或"代理中"的记录
        BpmAgentStatus agentStatus = calculateAgentStatus(agent.getStartTime(), agent.getEndTime(),agent.getRevokedTime());

        if (agentStatus != BpmAgentStatus.ACTIVE && agentStatus != BpmAgentStatus.INACTIVE) {
            throw exception(ErrorCodeConstants.AGENT_REVOKE_INVALID_STATUS);
        }

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
        LocalDateTime now = LocalDateTime.now();

        // 校验开始时间必须小于结束时间
        if (!reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.AGENT_START_TIME_AFTER_END_TIME);
        }

        if (!reqVO.getEndTime().isAfter(now)) {
            throw exception(ErrorCodeConstants.AGENT_END_TIME_BEFORE_NOW);
        }

        // 查询要更新的代理记录
        BpmFlowAgentDO agent = bpmFlowAgentRepository.findById(reqVO.getId());
        if (agent == null) {
            throw exception(ErrorCodeConstants.AGENT_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能编辑
        if (!loginUserId.equals(agent.getCreator()) && !loginUserId.equals(agent.getPrincipalId())) {
            throw exception(ErrorCodeConstants.AGENT_UPDATE_NO_PERMISSION);
        }

        // 校验状态：只能更新"待生效"或"代理中"的记录
        BpmAgentStatus agentStatus = calculateAgentStatus(agent.getStartTime(), agent.getEndTime(),agent.getRevokedTime());

        if (agentStatus != BpmAgentStatus.ACTIVE && agentStatus != BpmAgentStatus.INACTIVE) {
            throw exception(ErrorCodeConstants.AGENT_UPDATE_INVALID_STATUS);
        }

        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
        List<BpmFlowAgentDO> overlapAgentList = bpmFlowAgentRepository.findAllOverlapAgent(agent.getAppId(), loginUserId, reqVO.getStartTime(), reqVO.getEndTime());

        if (CollectionUtils.isNotEmpty(overlapAgentList)) {
            // 过滤掉当前记录，只保留重叠记录（排除自己）
            overlapAgentList = overlapAgentList.stream()
                    .filter(item -> !item.getId().equals(agent.getId()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(overlapAgentList)) {
                throw exception(ErrorCodeConstants.AGENT_TIME_CONFLICT);
            }
        }

        // 验证代理人是否存在
        AdminUserRespDTO agentUser = validateAgentExists(reqVO.getAgentId());

        // 执行更新操作
        agent.setAgentId(reqVO.getAgentId());
        agent.setAgentName(agentUser.getNickname());
        agent.setStartTime(reqVO.getStartTime());
        agent.setEndTime(reqVO.getEndTime());

        // todo: 更新需要加锁，避免并发问题
        bpmFlowAgentRepository.update(agent);
    }

    /**
     * 计算委托状态
     * @param startTime 代理生效时间
     * @param endTime 代理失效时间
     * @param revokedTime 撤销时间
     * @return 委托状态字符串
     */
    private BpmAgentStatus calculateAgentStatus(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime revokedTime) {
        // 如果撤销时间不为空，直接返回已撤销
        if (revokedTime != null) {
            return BpmAgentStatus.REVOKED;
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 判断待生效：当前时间 < 代理生效时间
        if (now.isBefore(startTime)) {
            return BpmAgentStatus.INACTIVE;
        }

        // 判断代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return BpmAgentStatus.ACTIVE;
        }

        // 判断已失效：当前时间 > 代理失效时间
        if (now.isAfter(endTime)) {
            return BpmAgentStatus.EXPIRED;
        }

        // 不应该出现其它状态
        return null;
    }

    private UserBasicInfoVO toAgentUser(AdminUserRespDTO user) {
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

        condition.and(Compare.EQUAL, BpmFlowAgentDO.APP_ID, reqVO.getAppId());
        ConfigStore userIdConfig = new DefaultConfigStore();
        userIdConfig.or(Compare.EQUAL, BpmFlowAgentDO.PRINCIPAL_ID, userId);
        userIdConfig.or(Compare.EQUAL, BpmFlowAgentDO.AGENT_ID, userId);
        condition.and(userIdConfig);

        // 动态添加其他查询条件
        if (StringUtils.isNotBlank(reqVO.getPersonName())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, BpmFlowAgentDO.PRINCIPAL_NAME, reqVO.getPersonName());
            orCondition.or(Compare.LIKE, BpmFlowAgentDO.AGENT_NAME, reqVO.getPersonName());
            condition.and(orCondition);
        }

        if (StringUtils.isNotBlank(reqVO.getAgentStatus())) {
            ConfigStore statusCondition = bpmFlowAgentRepository.buildConditionByAgentStatus(BpmAgentStatus.getByCode(reqVO.getAgentStatus()));
            condition.and(statusCondition);
        }

        condition.order(BpmFlowAgentDO.CREATE_TIME, "desc");

        return condition;
    }
}
