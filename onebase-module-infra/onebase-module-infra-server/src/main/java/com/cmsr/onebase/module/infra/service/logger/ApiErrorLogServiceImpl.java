package com.cmsr.onebase.module.infra.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.StrUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogPageReqVO;
import com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import com.cmsr.onebase.module.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiErrorLogDO.REQUEST_PARAMS_MAX_LENGTH;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.API_ERROR_LOG_PROCESSED;

/**
 * API 错误日志 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class ApiErrorLogServiceImpl implements ApiErrorLogService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        ApiErrorLogDO apiErrorLog = BeanUtils.toBean(createDTO, ApiErrorLogDO.class)
                .setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        apiErrorLog.setRequestParams(StrUtils.maxLength(apiErrorLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        if (TenantContextHolder.getTenantId() != null) {
            dataRepository.insert(apiErrorLog);
        } else {
            // 极端情况下，上下文中没有租户时，此时忽略租户上下文，避免插入失败！
            TenantUtils.executeIgnore(() -> dataRepository.insert(apiErrorLog));
        }
    }

    @Override
    public PageResult<ApiErrorLogDO> getApiErrorLogPage(ApiErrorLogPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(ApiErrorLogDO.COLUMN_USER_ID, pageReqVO.getUserId())
                .eq(ApiErrorLogDO.COLUMN_USER_TYPE, pageReqVO.getUserType())
                .eq(ApiErrorLogDO.COLUMN_APPLICATION_NAME, pageReqVO.getApplicationName())
                .eq(ApiErrorLogDO.COLUMN_REQUEST_URL, pageReqVO.getRequestUrl())
                .eq(ApiErrorLogDO.COLUMN_PROCESS_STATUS, pageReqVO.getProcessStatus())
                .eq("exceptionTime",pageReqVO.getExceptionTime());
        return dataRepository.findPageWithConditions(ApiErrorLogDO.class, configStore,pageReqVO.getPageNo(),pageReqVO.getPageSize());
    }

    @Override
    public void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId) {
        ApiErrorLogDO errorLog = dataRepository.findById(ApiErrorLogDO.class,id);
        if (errorLog == null) {
            throw exception(API_ERROR_LOG_NOT_FOUND);
        }
        if (!ApiErrorLogProcessStatusEnum.INIT.getStatus().equals(errorLog.getProcessStatus())) {
            throw exception(API_ERROR_LOG_PROCESSED);
        }
        // 标记处理
        ApiErrorLogDO logDO = ApiErrorLogDO.builder().processStatus(processStatus).processUserId(processUserId)
            .processTime(LocalDateTime.now()).build();
        logDO.setId(id);
        dataRepository.update(logDO);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanErrorLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = (int) dataRepository.deleteByConfigReturn(ApiAccessLogDO.class, new DefaultConfigStore()
                    .le(ApiAccessLogDO.CREATE_TIME, expireDate).limit(deleteLimit));
            count += deleteCount;
            // 达到删除预期条数，说明到底了
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}