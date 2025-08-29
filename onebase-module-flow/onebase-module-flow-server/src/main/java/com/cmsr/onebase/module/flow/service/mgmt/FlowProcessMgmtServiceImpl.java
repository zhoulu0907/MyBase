package com.cmsr.onebase.module.flow.service.mgmt;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.CreateFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.FlowProcessVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.ListFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.UpdateFlowProcessReqVO;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessRepository;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程管理服务实现类
 */
@Slf4j
@Service
public class FlowProcessMgmtServiceImpl implements FlowProcessMgmtService {

    private static final Logger logger = LoggerFactory.getLogger(FlowProcessMgmtServiceImpl.class);

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Override
    public PageResult<FlowProcessVO> pageList(ListFlowProcessReqVO reqVO) {
        logger.info("分页查询流程列表，请求参数：{}", reqVO);
        // 分页查询
        PageResult<FlowProcessDO> pageResult = flowProcessRepository.findPageByQuery(reqVO);

        // DO转换为VO
        List<FlowProcessVO> voList = pageResult.getList().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public FlowProcessVO getDetail(Long id) {
        logger.info("获取流程详情，流程ID：{}", id);

        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        if (flowProcessDO == null) {
            logger.warn("未找到流程，流程ID：{}", id);
            return null;
        }
        return convertToVO(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateFlowProcessReqVO reqVO) {
        logger.info("创建流程，请求参数：{}", reqVO);
        // 转换为DO对象
        FlowProcessDO flowProcessDO = new FlowProcessDO();
        BeanUtils.copyProperties(reqVO, flowProcessDO);
        // 保存到数据库
        FlowProcessDO saved = flowProcessRepository.insert(flowProcessDO);
        logger.info("流程创建成功，流程ID：{}", saved.getId());

        return saved.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateFlowProcessReqVO reqVO) {
        logger.info("更新流程，请求参数：{}", reqVO);

        // 检查流程是否存在
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(reqVO.getId());
        if (flowProcessDO == null) {
            logger.warn("未找到流程，流程ID：{}", reqVO.getId());
            throw new RuntimeException("流程不存在");
        }

        // 更新字段
        BeanUtils.copyProperties(reqVO, flowProcessDO);

        // 保存更新
        flowProcessRepository.update(flowProcessDO);
        logger.info("流程更新成功，流程ID：{}", reqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        logger.info("删除流程，流程ID：{}", id);

        // 检查流程是否存在
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        if (flowProcessDO == null) {
            logger.warn("未找到流程，流程ID：{}", id);
            throw new RuntimeException("流程不存在");
        }

        // 删除流程
        flowProcessRepository.deleteById(id);
        logger.info("流程删除成功，流程ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        logger.info("批量删除流程，流程ID列表：{}", ids);

        // 批量删除
        flowProcessRepository.deleteByIds(ids);
        logger.info("批量删除流程成功，删除数量：{}", ids.size());
    }



    /**
     * DO转换为VO
     */
    private FlowProcessVO convertToVO(FlowProcessDO flowProcessDO) {
        FlowProcessVO flowProcessVO = new FlowProcessVO();
        BeanUtils.copyProperties(flowProcessDO, flowProcessVO);
        return flowProcessVO;
    }
}
