package com.cmsr.onebase.module.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.cmsr.onebase.framework.common.consts.NumberConstant;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.module.system.dal.database.OAuth2ApproveDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;

/**
 * OAuth2 批准 Service 实现类
 *
 */
@Service
@Validated
public class OAuth2ApproveServiceImpl implements OAuth2ApproveService {

    /**
     * 批准的过期时间，默认 30 天
     */
    private static final Integer TIMEOUT = 30 * 24 * 60 * 60; // 单位：秒

    @Resource
    private OAuth2ClientService oauth2ClientService;

    @Resource
    private OAuth2ApproveDataRepository oauth2ApproveDataRepository;

    @Override
    @Transactional
    @LogRecord(type = SYSTEM_OAUTH_TYPE, subType = SYSTEM_OAUTH_APPROVE_SUB_TYPE, bizNo = "{{#clientDO.id}}",
            success = SYSTEM_OAUTH_APPROVE_SUCCESS)
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        // 第一步，基于 Client 的自动授权计算，如果 scopes 都在自动授权中，则返回 true 通过
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        Assert.notNull(clientDO, "客户端不能为空"); // 防御性编程
        if (CollUtil.containsAll(clientDO.getAutoApproveScopes(), requestedScopes)) {
            // gh-877 - if all scopes are auto approved, approvals still need to be added to the approval store.
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, NumberConstant.ONE, expireTime);
            }
            // 记录操作日志上下文
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

            LogRecordContext.putVariable("loginUser", loginUser);
            LogRecordContext.putVariable("client", clientDO);
            return true;
        }

        // 第二步，算上用户已经批准的授权。如果 scopes 都包含，则返回 true
        List<OAuth2ApproveDO> approveDOs = getApproveList(userId, userType, clientId);
        Set<String> scopes = convertSet(approveDOs, OAuth2ApproveDO::getScope,
                approve -> approve.getApproved() != null && approve.getApproved() == NumberConstant.ONE); // 只保留未过期的 + 同意的
        return CollUtil.containsAll(scopes, requestedScopes);
    }

    @Override
    @Transactional
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        // 如果 requestedScopes 为空，说明没有要求，则返回 true 通过
        if (CollUtil.isEmpty(requestedScopes)) {
            return true;
        }

        // 更新批准的信息
        boolean success = false; // 需要至少有一个同意
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
        for (Map.Entry<String, Boolean> entry : requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue()? NumberConstant.ONE: NumberConstant.ZERO, expireTime);
        }
        return success;
    }

    @Override
    public List<OAuth2ApproveDO> getApproveList(Long userId, Integer userType, String clientId) {
        List<OAuth2ApproveDO> approveDOs = oauth2ApproveDataRepository.findListByUserIdAndUserTypeAndClientId(
                userId, userType, clientId);
        approveDOs.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        return approveDOs;
    }

    @VisibleForTesting
    void saveApprove(Long userId, Integer userType, String clientId,
                     String scope, Integer approved, LocalDateTime expireTime) {
        // 先更新
        OAuth2ApproveDO approveDO = new OAuth2ApproveDO().setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScope(scope).setApproved(approved).setExpiresTime(expireTime);

        try {
            oauth2ApproveDataRepository.update(approveDO);
        } catch (Exception e) {
            oauth2ApproveDataRepository.insert(approveDO);
        }
    }

}
