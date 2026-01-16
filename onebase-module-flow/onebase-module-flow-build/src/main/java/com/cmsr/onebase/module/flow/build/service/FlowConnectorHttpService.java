package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;

import javax.validation.Valid;

/**
 * HTTP连接器动作Service接口
 *
 * @author zhoulu
 * @since 2026-01-16
 */
public interface FlowConnectorHttpService {

    /**
     * 创建HTTP动作
     *
     * @param createReqVO 创建请求
     * @return HTTP动作ID
     */
    Long createHttpAction(@Valid CreateHttpActionReqVO createReqVO);

    /**
     * 更新HTTP动作
     *
     * @param updateReqVO 更新请求
     */
    void updateHttpAction(@Valid UpdateHttpActionReqVO updateReqVO);

    /**
     * 删除HTTP动作
     *
     * @param id HTTP动作ID
     */
    void deleteHttpAction(Long id);

    /**
     * 获取HTTP动作详情
     *
     * @param id HTTP动作ID
     * @return HTTP动作VO
     */
    HttpActionVO getHttpAction(Long id);

    /**
     * 分页查询HTTP动作列表
     *
     * @param pageReqVO 分页查询请求
     * @return 分页结果
     */
    PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO);
}
