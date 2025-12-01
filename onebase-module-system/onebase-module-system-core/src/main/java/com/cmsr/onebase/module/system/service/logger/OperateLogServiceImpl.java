package com.cmsr.onebase.module.system.service.logger;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.cmsr.onebase.module.system.vo.log.OperateLogPageReqVO;
import com.cmsr.onebase.module.system.dal.database.OperateLogRepository;
import com.cmsr.onebase.module.system.dal.dataobject.logger.OperateLogDO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogRepository operateLogRepository;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        if (log.getUserId() == null) {
            log.setUserId(0L);
        }
        operateLogRepository.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogRepository.findPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogRepository.findPage(pageReqDTO);
    }

}
