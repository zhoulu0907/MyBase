package com.cmsr.onebase.module.system.service.corpapprelation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.vo.corp.CorpApplicationRespVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.*;
import jakarta.validation.Valid;

import java.util.List;


/**
 * 企业应用关联表 Service 接口
 */
public interface CorpAppRelationService {

    /**
     * 创建企业应用关联表
     *
     * @param
     * @return 编号
     */
    void createCorpAppRelation(CorpAppRelationInertReqVO vo);

    void createListCorpAppRelation(List<AppAuthTimeReqVO> appAuthTimeReqVOs, Long corpId);
    /**
     * 更新企业应用关联
     * 通过id 可以更新关联关系，业可以更新授权时间
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
    PageResult<CorpApplicationRespVO> getCorpAppRelationPage(CorpAppPageReqVO pageReqVO);

    /**
     * 根据企业ID删除企业应用关联
     * @param corpID
     */
    void deleteCorpAppRelationByCorpId(Long corpID);

    /**
     * 获取企业应用关联列表
     * @param corpAppRelationPageReqVO
     * @return
     */
    List<CorpAppRelationDO> getCorpAppRelationList(CorpAppRelationPageReqVO corpAppRelationPageReqVO);


    /**
     * 企业启用/禁用
     * @param id
     * @param status
     */
    void updateStatus(Long id, Long status);

    /**
     * 获取排除企业已关联应用的应用列表
     * @param relationAppReqVO
     * @return
     */
    List<ApplicationDTO> getCorpNoRelationAppList(CorpRelationAppReqVO relationAppReqVO);
}