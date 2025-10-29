package com.cmsr.onebase.module.system.service.corpAppRelation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationUpdateReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationVO;
import jakarta.validation.Valid;


/**
 * 企业应用关联表 Service 接口
 */
public interface CorpAppRelationService {

    /**
     * 创建企业应用关联表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    void createCorpAppRelation(@Valid CorpAppRelationInertReqVO createReqVO);

    /**
     * 更新企业应用关联
     *
     * @param updateReqVO 更新信息
     */
    void updateCorpAppRelation(@Valid CorpAppRelationUpdateReqVO updateReqVO);

    /**
     * 删除企业应用关联
     *
     * @param id 编号
     */
    void deleteCorpAppRelation(Long id);

    /**
     * 获得企业应用关联表
     *
     * @param id 编号
     * @return 企业应用关联表
     */
    CorpAppRelationVO getCorpAppRelation(Long id);

    /**
     * 获得企业应用关联表分页
     *
     * @param pageReqVO 分页查询
     * @return 企业应用关联分页
     */
    PageResult<CorpAppRelationVO> getCorpAppRelationPage(CorpAppRelationPageReqVO pageReqVO);
}