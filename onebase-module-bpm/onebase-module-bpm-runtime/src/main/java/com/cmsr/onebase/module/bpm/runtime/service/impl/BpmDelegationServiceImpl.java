package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowDelegationRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowDelegationDO;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmDelegationService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationInsertReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageResVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationUpdateReqVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import com.cmsr.onebase.module.bpm.core.enums.BpmDelegationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
@Service
public class BpmDelegationServiceImpl implements BpmDelegationService {
    @Resource
    private BpmFlowDelegationRepository bpmFlowDelegationRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public PageResult<BpmDelegationPageResVO> getDelegationPage(BpmDelegationPageReqVO pageReqVO) {
        // 获取当前登录用户ID
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 构建查询条件
        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));

        PageResult<BpmFlowDelegationDO> pageResult =bpmFlowDelegationRepository.findPageWithConditions(condition, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        // 结果不为空时 获取代理人，被代理人，创建人信息
        List<BpmFlowDelegationDO> delegationList = pageResult.getList() != null ?
                pageResult.getList() : Collections.emptyList();
        Map<Long, AdminUserRespDTO> delegateUserInfoMap = queryDelegateUser(delegationList);

        List<BpmDelegationPageResVO> list = pageResult.getList().stream().map(item -> {
            BpmDelegationPageResVO vo = new BpmDelegationPageResVO();
            // 代理人
            vo.setDelegate(createDelegateUser(delegateUserInfoMap.get(item.getDelegateId())));
            // 被代理人
            vo.setPrincipal(createDelegateUser(delegateUserInfoMap.get(item.getPrincipalId())));
            //创建人
            vo.setCreator(createDelegateUser(delegateUserInfoMap.get(item.getCreator())));
            vo.setStartTime(item.getStartTime());
            vo.setEndTime(item.getEndTime());
            vo.setCreateTime(item.getCreateTime());
            vo.setId(item.getId());
            // 代理状态
            vo.setDelegateStatus(calculateDelegateStatus(item.getStartTime(), item.getEndTime(),item.getRevokedTime()));
            return vo;
        }).toList();
       return new PageResult<>(list, pageResult.getTotal());
    }
    /**
     * 新增代理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(BpmDelegationInsertReqVO reqVO) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        String nickname =SecurityFrameworkUtils.getLoginUserNickname();
        // 校验开始时间必须小于结束时间
        if (reqVO.getStartTime() != null && reqVO.getEndTime() != null &&
                !reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.DELEGATION_START_TIME_AFTER_END_TIME);
        }
        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
        validateExistingDelegation(userId, reqVO.getStartTime(), reqVO.getEndTime());
        // 插入数据
        BpmFlowDelegationDO bpmFlowDelegationDO = new BpmFlowDelegationDO();
        bpmFlowDelegationDO.setPrincipalId(userId);
        bpmFlowDelegationDO.setPrincipalName(nickname);
        bpmFlowDelegationDO.setDelegateId(reqVO.getDelegateId());
        bpmFlowDelegationDO.setDelegateName(reqVO.getDelegateName());
        bpmFlowDelegationDO.setStartTime(reqVO.getStartTime());
        bpmFlowDelegationDO.setEndTime(reqVO.getEndTime());
        bpmFlowDelegationDO.setAppId(reqVO.getAppId());
        bpmFlowDelegationRepository.insert(bpmFlowDelegationDO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long delegationId) {
        // 获取当前登录用户信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查询要撤销的代理记录
        BpmFlowDelegationDO delegation = bpmFlowDelegationRepository.findById(delegationId);
        if (delegation == null) {
            throw exception(ErrorCodeConstants.DELEGATION_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能撤销
        validateRevokePermission(delegation, loginUserId);

        // 校验状态：只能撤销"待生效"或"代理中"的记录
        validateRevokeStatus(delegation);

        // 执行撤销操作：设置撤销时间
        delegation.setRevokedTime(LocalDateTime.now());
        delegation.setRevokerId(loginUserId);
        bpmFlowDelegationRepository.update(delegation);
    }
    /**
     * 更新代理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmDelegationUpdateReqVO reqVO) {
        // 获取当前登录用户信息
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查询要更新的代理记录
        BpmFlowDelegationDO delegation = bpmFlowDelegationRepository.findById(reqVO.getId());
        if (delegation == null) {
            throw exception(ErrorCodeConstants.DELEGATION_NOT_EXISTS);
        }

        // 校验权限：只有创建人或被代理人才能编辑
        validateRevokePermission(delegation, loginUserId);

        // 校验状态：只能编辑"待生效"或"代理中"的记录
        validateRevokeStatus(delegation);

        // 校验开始时间必须小于结束时间
        if (reqVO.getStartTime() != null && reqVO.getEndTime() != null &&
                !reqVO.getStartTime().isBefore(reqVO.getEndTime())) {
            throw exception(ErrorCodeConstants.DELEGATION_START_TIME_AFTER_END_TIME);
        }

        // 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系（排除自己）
        validateUpdateDelegation(delegation.getId(), delegation.getPrincipalId(), reqVO.getStartTime(), reqVO.getEndTime());

        // 执行更新操作
        delegation.setDelegateId(reqVO.getDelegateId());
        delegation.setDelegateName(reqVO.getDelegateName());
        delegation.setStartTime(reqVO.getStartTime());
        delegation.setEndTime(reqVO.getEndTime());

        bpmFlowDelegationRepository.update(delegation);
    }

    /**
     * 校验更新时被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
     * @param id 当前记录ID
     * @param principalId 被代理人ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateUpdateDelegation(Long id, Long principalId, LocalDateTime startTime, LocalDateTime endTime) {
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

        List<BpmFlowDelegationDO> existingDelegations = bpmFlowDelegationRepository.findAllByConfig(condition);

        // 检查这些记录是否处于"待生效"或"代理中"状态
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveOrPending = existingDelegations.stream().anyMatch(delegation ->
                (delegation.getStartTime() != null && now.isBefore(delegation.getStartTime())) || // 待生效
                        (delegation.getStartTime() != null && delegation.getEndTime() != null &&
                                now.isAfter(delegation.getStartTime()) && now.isBefore(delegation.getEndTime())) // 代理中
        );

        if (hasActiveOrPending) {
            throw exception(ErrorCodeConstants.DELEGATION_TIME_CONFLICT);
        }
    }


    /**
     * 校验撤销权限：只有创建人或被代理人才能操作
     * @param delegation 代理记录
     * @param loginUserId 当前登录用户ID
     */
    private void validateRevokePermission(BpmFlowDelegationDO delegation, Long loginUserId) {
        if (!loginUserId.equals(delegation.getCreator()) && !loginUserId.equals(delegation.getPrincipalId())) {
            throw exception(ErrorCodeConstants.DELEGATION_REVOKE_NO_PERMISSION);
        }
    }

    /**
     * 校验撤销状态：只能撤销"待生效"或"代理中"的记录
     * @param delegation 代理记录
     */
    private void validateRevokeStatus(BpmFlowDelegationDO delegation) {
        String status = calculateDelegateStatus(delegation.getStartTime(), delegation.getEndTime(), delegation.getRevokedTime());
        if (!BpmDelegationStatus.INACTIVE.getName().equals(status) &&
                !BpmDelegationStatus.ACTIVE.getName().equals(status)) {
            throw exception(ErrorCodeConstants.DELEGATION_REVOKE_INVALID_STATUS);
        }
    }


    /**
     * 校验被代理人在指定时间段内是否已存在"待生效"或"代理中"的代理关系
     * @param principalId 被代理人ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateExistingDelegation(Long principalId, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件：查找指定被代理人的未撤销记录
        DefaultConfigStore condition = new DefaultConfigStore();
        condition.and(Compare.EQUAL, "principal_id", principalId);
        condition.and(Compare.NULL, "revoked_time");

        // 查找时间有重叠的记录
        ConfigStore timeOverlapCondition = new DefaultConfigStore();
        timeOverlapCondition.and(Compare.LESS, "start_time", endTime);  // 记录开始时间 < 新记录结束时间
        timeOverlapCondition.and(Compare.GREAT, "end_time", startTime); // 记录结束时间 > 新记录开始时间
        condition.and(timeOverlapCondition);

        List<BpmFlowDelegationDO> existingDelegations = bpmFlowDelegationRepository.findAllByConfig(condition);

        // 检查这些记录是否处于"待生效"或"代理中"状态
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveOrPending = existingDelegations.stream().anyMatch(delegation ->
                (delegation.getStartTime() != null && now.isBefore(delegation.getStartTime())) || // 待生效
                        (delegation.getStartTime() != null && delegation.getEndTime() != null &&
                                now.isAfter(delegation.getStartTime()) && now.isBefore(delegation.getEndTime())) // 代理中
        );

        if (hasActiveOrPending) {
            throw exception(ErrorCodeConstants.DELEGATION_TIME_CONFLICT);
        }
    }

    /**
     * 计算委托状态
     * @param startTime 代理生效时间
     * @param endTime 代理失效时间
     * @param revokedTime 撤销时间
     * @return 委托状态字符串
     */
    private String calculateDelegateStatus(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime revokedTime) {
        // 如果撤销时间不为空，直接返回已撤销
        if (revokedTime != null) {
            return BpmDelegationStatus.REVOKED.getName();
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 判断待生效：当前时间 < 代理生效时间
        if (startTime != null && now.isBefore(startTime)) {
            return BpmDelegationStatus.INACTIVE.getName();
        }

        // 判断代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
        if (startTime != null && endTime != null &&
                now.isAfter(startTime) && now.isBefore(endTime)) {
            return BpmDelegationStatus.ACTIVE.getName();
        }

        // 判断已失效：当前时间 > 代理失效时间
        if (endTime != null && now.isAfter(endTime)) {
            return BpmDelegationStatus.EXPIRED.getName();
        }

        return "未知状态";
    }



    private UserBasicInfoVO createDelegateUser(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        UserBasicInfoVO operationUser = new UserBasicInfoVO();
        operationUser.setUserId(user.getId());
        operationUser.setName(user.getNickname());
        operationUser.setAvatar(user.getAvatar());
        return operationUser;
    }

    private Map<Long, AdminUserRespDTO> queryDelegateUser(List<BpmFlowDelegationDO>  list) {
        // 获取创建人和代理人被代理人去重后一次性查出
        Set<Long> userIds = list.stream()
                .flatMap(item -> Stream.of(item.getCreator(), item.getPrincipalId(), item.getDelegateId()))
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

    private ConfigStore buildDynamicCondition(BpmDelegationPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();
        // 填充条件
        fillCondition(condition, reqVO,userId);

        return condition;
    }
    private void fillCondition(ConfigStore condition, BpmDelegationPageReqVO queryVO,String userId) {
        condition.and(Compare.EQUAL, "app_id", queryVO.getAppId());
        ConfigStore userIdConfig = new DefaultConfigStore();
        userIdConfig.or(Compare.EQUAL, "principal_id", userId);
        userIdConfig.or(Compare.EQUAL, "delegate_id", userId);
        condition.and(userIdConfig);
        // 动态添加其他查询条件
        if (StringUtils.isNotBlank(queryVO.getDelegatePersonName())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "principal_name", queryVO.getDelegatePersonName());
            orCondition.or(Compare.LIKE, "delegate_name", queryVO.getDelegatePersonName());
            condition.and(orCondition);
        }
        if (StringUtils.isNotBlank(queryVO.getDelegateStatus())) {
            String status = queryVO.getDelegateStatus();
            LocalDateTime now = LocalDateTime.now();

            ConfigStore statusCondition = new DefaultConfigStore();
            if (BpmDelegationStatus.INACTIVE.getCode().equals(status)) {
                // 待生效：当前时间 < 代理生效时间
                statusCondition.and(Compare.GREAT, "start_time", now);
            } else if (BpmDelegationStatus.ACTIVE.getCode().equals(status)) {
                // 代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
                statusCondition.and(Compare.LESS_EQUAL, "start_time", now);
                statusCondition.and(Compare.GREAT_EQUAL, "end_time", now);
            } else if (BpmDelegationStatus.EXPIRED.getCode().equals(status)) {
                // 已失效：当前时间 > 代理失效时间
                statusCondition.and(Compare.LESS, "end_time", now);
            } else if (BpmDelegationStatus.REVOKED.getCode().equals(status)) {
                // 已撤销：撤销时间不为空
                statusCondition.and(Compare.NOT_NULL, "revoked_time");
            }
            condition.and(statusCondition);
        }
        condition.order("create_time", "desc");
    }

}
