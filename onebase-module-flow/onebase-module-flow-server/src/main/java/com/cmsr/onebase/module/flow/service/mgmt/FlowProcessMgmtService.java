package com.cmsr.onebase.module.flow.service.mgmt;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.CreateFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.FlowProcessVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.ListFlowProcessReqVO;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.UpdateFlowProcessReqVO;

import java.util.List;

/**
 * 流程管理服务接口
 */
public interface FlowProcessMgmtService {

    /**
     * 分页查询流程
     * @param reqVO 查询条件
     * @return 分页结果
     */
    PageResult<FlowProcessVO> pageList(ListFlowProcessReqVO reqVO);

    /**
     * 获取流程详情
     * @param id 流程ID
     * @return 流程详情
     */
    FlowProcessVO getDetail(Long id);

    /**
     * 创建流程
     * @param reqVO 创建参数
     * @return 流程ID
     */
    Long create(CreateFlowProcessReqVO reqVO);

    /**
     * 更新流程
     * @param reqVO 更新参数
     */
    void update(UpdateFlowProcessReqVO reqVO);

    /**
     * 删除流程
     * @param id 流程ID
     */
    void delete(Long id);

    /**
     * 批量删除流程
     * @param ids 流程ID列表
     */
    void batchDelete(List<Long> ids);
}
