package com.cmsr.onebase.module.system.service.logger;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.cmsr.onebase.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.cmsr.onebase.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.OperateLogDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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

    //@Resource
    //private OperateLogMapper operateLogMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        dataRepository.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        try {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "deleted", false);
            
            // 构建查询条件
            if (pageReqVO.getUserId() != null) {
                cs.and(Compare.EQUAL, "user_id", pageReqVO.getUserId());
            }
            if (pageReqVO.getBizId() != null) {
                cs.and(Compare.EQUAL, "biz_id", pageReqVO.getBizId());
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getType())) {
                cs.and(Compare.LIKE, "type", pageReqVO.getType());
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getSubType())) {
                cs.and(Compare.LIKE, "sub_type", pageReqVO.getSubType());
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getAction())) {
                cs.and(Compare.LIKE, "action", pageReqVO.getAction());
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
                    OperateLogDO.class, 
                    cs, 
                    pageReqVO.getPageNo(), 
                    pageReqVO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询操作日志失败", e);
            throw new RuntimeException("分页查询操作日志失败", e);
        }
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        try {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "deleted", false);
            
            // 构建查询条件
            if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqDTO.getType())) {
                cs.and(Compare.EQUAL, "type", pageReqDTO.getType());
            }
            if (pageReqDTO.getBizId() != null) {
                cs.and(Compare.EQUAL, "biz_id", pageReqDTO.getBizId());
            }
            if (pageReqDTO.getUserId() != null) {
                cs.and(Compare.EQUAL, "user_id", pageReqDTO.getUserId());
            }
            
            // 添加排序条件，按ID降序排列
            cs.order("id", "DESC");
            
            return dataRepository.findPageWithConditions(
                    OperateLogDO.class, 
                    cs, 
                    pageReqDTO.getPageNo(), 
                    pageReqDTO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询操作日志失败", e);
            throw new RuntimeException("分页查询操作日志失败", e);
        }
    }

}
