package com.cmsr.onebase.module.infra.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.StrUtils;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import com.cmsr.onebase.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import com.cmsr.onebase.module.infra.dal.mysql.logger.ApiAccessLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
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

//    @Resource
//    private ApiAccessLogMapper apiAccessLogMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        ApiAccessLogDO apiAccessLog = BeanUtils.toBean(createDTO, ApiAccessLogDO.class);
        apiAccessLog.setRequestParams(StrUtils.maxLength(apiAccessLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        apiAccessLog.setResultMsg(StrUtils.maxLength(apiAccessLog.getResultMsg(), RESULT_MSG_MAX_LENGTH));
        if (TenantContextHolder.getTenantId() != null) {
            dataRepository.insert(apiAccessLog);
//            apiAccessLogMapper.insert(apiAccessLog);
        } else {
            // 极端情况下，上下文中没有租户时，此时忽略租户上下文，避免插入失败！
            TenantUtils.executeIgnore(() -> dataRepository.insert(apiAccessLog));
//            TenantUtils.executeIgnore(() -> apiAccessLogMapper.insert(apiAccessLog));
        }
    }

    @Override
    public PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("user_id", pageReqVO.getUserId())
                .eq("user_type", pageReqVO.getUserType())
                .eq("application_name", pageReqVO.getApplicationName())
                .like("request_url", pageReqVO.getRequestUrl())
                .eq("duration", pageReqVO.getDuration())
                .eq("result_code", pageReqVO.getResultCode());
        if (pageReqVO.getBeginTime() != null && pageReqVO.getBeginTime().length == 2) {
            configStore.ge("create_time", pageReqVO.getBeginTime()[0]);
            configStore.le("create_time", pageReqVO.getBeginTime()[1]);
        }
        return dataRepository.findPageWithConditions(ApiAccessLogDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
//        return apiAccessLogMapper.selectPage(pageReqVO);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        // 循环删除，直到没有满足条件的数据
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = (int) dataRepository.deleteByConfigReturn(ApiAccessLogDO.class, new DefaultConfigStore()
                    .le("create_time", expireDate).limit(deleteLimit));
//            int deleteCount = apiAccessLogMapper.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            // 达到删除预期条数，说明到底了
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

}
