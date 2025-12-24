package com.cmsr.onebase.module.infra.service.logger;

import com.cmsr.onebase.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.StrUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.infra.dal.dataflex.ApiAccessLogDataRepository;
import com.cmsr.onebase.module.infra.dal.dataflexdo.logger.ApiAccessLogDO;
import com.cmsr.onebase.module.infra.dal.vo.logger.apiaccesslog.ApiAccessLogPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiAccessLogDO.REQUEST_PARAMS_MAX_LENGTH;
import static com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiAccessLogDO.RESULT_MSG_MAX_LENGTH;

/**
 * API 访问日志 Service 实现类
 *
 */
@Slf4j
@Service
@Validated
public class ApiAccessLogServiceImpl implements ApiAccessLogService {

    @Resource
    private ApiAccessLogDataRepository apiAccessLogDataRepository;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        ApiAccessLogDO apiAccessLog = BeanUtils.toBean(createDTO, ApiAccessLogDO.class);
        apiAccessLog.setRequestParams(StrUtils.maxLength(apiAccessLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        apiAccessLog.setResultMsg(StrUtils.maxLength(apiAccessLog.getResultMsg(), RESULT_MSG_MAX_LENGTH));
        
        // 设置默认租户ID为0，确保不违反数据库约束
        if (apiAccessLog.getTenantId() == null) {
            apiAccessLog.setTenantId(0L);
        }
        
        if (TenantContextHolder.getTenantId() != null) {
            apiAccessLogDataRepository.save(apiAccessLog);
        } else {
            // 极端情况下，上下文中没有租户时，此时忽略租户上下文，避免插入失败！
            TenantUtils.executeIgnore(() -> apiAccessLogDataRepository.save(apiAccessLog));
        }
    }

    @Override
    public PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO) {
        return apiAccessLogDataRepository.findPage(pageReqVO);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit) {
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        return apiAccessLogDataRepository.cleanExpiredLogs(expireDate, deleteLimit);
    }

}