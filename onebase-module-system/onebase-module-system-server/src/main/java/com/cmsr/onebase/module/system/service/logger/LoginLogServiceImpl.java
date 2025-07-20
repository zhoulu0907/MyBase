package com.cmsr.onebase.module.system.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.LoginLogDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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

    //@Resource
    //private LoginLogMapper loginLogMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        try {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "deleted", false);
            
            // 构建查询条件
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getUserIp())) {
                cs.and(Compare.LIKE, "user_ip", pageReqVO.getUserIp());
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getUsername())) {
                cs.and(Compare.LIKE, "username", pageReqVO.getUsername());
            }
            if (pageReqVO.getStatus() != null) {
                cs.and(Compare.EQUAL, "status", pageReqVO.getStatus());
            }
            if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
                if (pageReqVO.getCreateTime()[0] != null) {
                    cs.and(Compare.GREAT_EQUAL, "create_time", pageReqVO.getCreateTime()[0]);
                }
                if (pageReqVO.getCreateTime()[1] != null) {
                    cs.and(Compare.LESS_EQUAL, "create_time", pageReqVO.getCreateTime()[1]);
                }
            }
            
            // 添加排序条件，按ID降序排列
            cs.order("id", "DESC");
            
            return dataRepository.findPageWithConditions(
                    LoginLogDO.class, 
                    cs, 
                    pageReqVO.getPageNo(), 
                    pageReqVO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询登录日志失败", e);
            throw new RuntimeException("分页查询登录日志失败", e);
        }
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        dataRepository.insert(loginLog);
    }

}
