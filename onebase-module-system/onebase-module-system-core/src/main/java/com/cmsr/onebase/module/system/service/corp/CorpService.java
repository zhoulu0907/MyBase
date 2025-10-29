// 6. 创建 enterprise 模块的 Service 接口
package com.cmsr.onebase.module.system.service.corp;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.CorpDO;
import com.cmsr.onebase.module.system.vo.corp.*;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;

import java.util.List;


/**
 * 企业服务接口
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface CorpService {

    /**
     * 更新企业
     *
     * @param reqVO 企业更新请求参数
     */
    void updateCorp(CorpUpdateReqVO reqVO);

    /**
     * 删除企业
     *
     * @param id 企业主键ID
     */
    void deleteCorp(Long id);

    /**
     * 获得企业分页
     * 入参 企业名称搜索、状态筛选、创建时间区间、行业类型筛选
     * @param pageReqVO 分页查询参数
     * @return 企业分页结果
     */
    PageResult<CorpRespVO> getCorpPage(CorpPageReqVO pageReqVO);


   List<CorpDO> findCorpAll();

    /**
     * 获得企业详情
     *
     * @param id 企业主键ID
     * @return 企业详情
     */
    CorpRespVO getCorp(Long id);
    /**
     * 创建企业管理员
     *
     */
    CorpUserRespVO createUser( CorpUserReqVO reqVO);
    /**
     * 创建企业管理员
     *
     */
    void updateStatus(Long id, Long status);
    /**
     * 创建企业对应的应用
     * 查询  入参 企业名称
     * 返回 带有授权时间 有效时间  版本
     */
    PageResult<CorpApplicationRespVO> selectCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO);

    /**
     * 一页式创建企业
     *
     */
    CorpUserRespVO createCorpCombined(CorpCombinedVo reqVO);
}
