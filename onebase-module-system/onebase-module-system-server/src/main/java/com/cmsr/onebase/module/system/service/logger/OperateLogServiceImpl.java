package com.cmsr.onebase.module.system.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.anyline.web.MyAnyLineService;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.cmsr.onebase.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.cmsr.onebase.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.OperateLogDO;
import com.cmsr.onebase.module.system.dal.mysql.logger.OperateLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

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
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        dataRepository.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogMapper.selectPage(pageReqDTO);
    }

}
