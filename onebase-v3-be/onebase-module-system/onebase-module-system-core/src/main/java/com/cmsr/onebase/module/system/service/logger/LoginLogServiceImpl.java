package com.cmsr.onebase.module.system.service.logger;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.vo.log.LoginLogPageReqVO;
import com.cmsr.onebase.module.system.dal.database.LoginLogRepository;
import com.cmsr.onebase.module.system.dal.dataobject.logger.LoginLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
@Slf4j
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogRepository loginLogRepository;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogRepository.findPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogRepository.insert(loginLog);
    }

}
