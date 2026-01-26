// 6. 创建 crop 模块的 Service 接口
package com.cmsr.onebase.module.system.service.corp;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.vo.corp.*;
import jakarta.validation.Valid;

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
     * 获得企业应用分页
     * 入参 企业名称搜索、状态筛选、创建时间区间、行业类型筛选
     *
     * @param pageReqVO 分页查询参数
     * @return 企业应用分页结果
     */
    PageResult<CorpRespVO> getCorpAppsPage(CorpPageReqVO pageReqVO);

    /**
     * 获得所有企业
     *
     */
    List<CorpDO> findCorpAll();

    /**
     * 获得企业详情
     *
     */
    CorpRespVO getCorp(Long id);

    /**
     * 创建企业管理员
     *
     */
    void updateStatus(Long id, Long status);

    /**
     * 一页式创建企业
     *
     */
    CorpAdminUserRespVO createCorpCombined(CorpCombinedVo reqVO);

    /**
     * 获取企业精简信息列表-不分页
     *
     */
    List<CorpDO> getSimpleCorpList(Integer staus);
    /**
     * 验证企业基本信息
     *
     */
    void checkCorp(@Valid CorpReqVO corpReqVO);
    /**
     * 验证企业管理员
     *
     */
    void checkCorpAdminUser(@Valid CorpAdminReqVO corpAdminReqVO);
    /**
     * 获取所有企业
     *
     */
    List<CorpDO> getAllCorpList();
}
