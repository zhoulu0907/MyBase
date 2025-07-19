package com.cmsr.onebase.module.system.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.anyline.web.MyAnyLineService;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.LoginLogDO;
import com.cmsr.onebase.module.system.dal.mysql.logger.LoginLogMapper;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    static{
        ConfigTable.IS_AUTO_CHECK_METADATA = true;
        ConfigTable.IS_INSERT_NULL_COLUMN = false;
        ConfigTable.IS_INSERT_NULL_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_FIELD = false;
        ConfigTable.IS_INSERT_EMPTY_COLUMN = false;
    }
    private AnylineService<?> service = MyAnyLineService.getInstance().getService();
    private DataRepository dataRepository = new DataRepository(service);

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        dataRepository.insert(loginLog);
    }

}
