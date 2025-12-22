package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.OwnerTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemCorpMapper;
import com.cmsr.onebase.module.system.vo.corp.CorpPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.framework.data.base.BaseDO.ID;
import static com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO.CORP_CODE;
import static com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO.CORP_NAME;
import static com.cmsr.onebase.module.system.dal.flex.table.SystemCorpTableDef.SYSTEM_CORP;

/**
 * 企业数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class CorpDataRepository extends BaseDataServiceImpl<SystemCorpMapper, CorpDO> {

    /**
     * 分页查询企业列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页数据
     */
    public PageResult<CorpDO> selectPage(CorpPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .like(CORP_NAME, pageReqVO.getCorpName(), StringUtils.isNotBlank(pageReqVO.getCorpName()))
                .eq(CorpDO.STATUS, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .eq(CorpDO.INDUSTRY_TYPE, pageReqVO.getIndustryType(), pageReqVO.getIndustryType() != null)
                .ge(CREATE_TIME, pageReqVO.getBeginCreateTime(), pageReqVO.getBeginCreateTime() != null)
                .le(CREATE_TIME, pageReqVO.getEndCreateTime(), pageReqVO.getEndCreateTime() != null)
                .orderBy(CREATE_TIME, false);

        if (pageReqVO.getOwnerTag() != null && pageReqVO.getOwnerTag().equals(OwnerTagEnum.MY.getValue())) {
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (loginUser != null && loginUser.getId() != null) {
                queryWrapper.eq(CorpDO.CREATOR, loginUser.getId());
            }
        }

        Page<CorpDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据企业名称查找企业
     *
     * @param name 企业名称
     * @return 企业对象
     */
    public CorpDO findCorpByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(CORP_NAME, name));
    }

    /**

     * 根据企业编码查找企业
     *
     * @param corpCode 企业编码
     * @return 企业对象
     */
    public CorpDO findCorpByCorpCode(String corpCode) {
        if (StringUtils.isBlank(corpCode)) {
            return null;
        }
        return getOne(query().eq(CORP_CODE, corpCode));
    }

    /**
     * 获取简单企业列表
     *
     * @param status 状态
     * @return 企业列表
     */
    public List<CorpDO> getSimpleCorpList(Integer status) {
        return list(query()
                .eq(CorpDO.STATUS, status, status != null)
                .orderBy(CREATE_TIME, false));
    }

    /**
     * 获取所有启用的企业
     *
     * @return 启用企业列表
     */
    public List<CorpDO> getAllEnableCorp() {
        return list(query().eq(CorpDO.STATUS, CommonStatusEnum.ENABLE.getStatus()));
    }

    /**
     * 获取全部企业列表
     *
     * @return 企业列表
     */
    public List<CorpDO> getAllCorpList() {
        List<CorpDO> corpList = list();
        return corpList == null ? Collections.emptyList() : corpList;
    }

    /**
     * 更新企业管理员ID
     *
     * @param corpId  企业ID
     * @param adminId 管理员用户ID
     */
    public void updateCorpAdminId(Long corpId, Long adminId) {
        updateChain().set(SYSTEM_CORP.ADMIN_ID, adminId)
                .where(SYSTEM_CORP.ID.eq(corpId))
                .update();
    }

    /**
     * 更新企业状态
     *
     * @param corpId 企业ID
     * @param status 状态
     */
    public void updateStatus(Long corpId, Integer status) {
        updateChain().set(SYSTEM_CORP.STATUS, status)
                .where(SYSTEM_CORP.ID.eq(corpId))
                .update();
    }

    /**
     * 根据ID集合查询企业
     *
     * @param corpIds 企业ID集合
     * @return 企业列表
     */
    public List<CorpDO> findByIds(List<Long> corpIds) {
        if (corpIds == null || corpIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(ID, corpIds));
    }
}

