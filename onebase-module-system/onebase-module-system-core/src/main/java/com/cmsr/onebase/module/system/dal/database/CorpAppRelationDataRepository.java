package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.enums.CorpStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.corpapprelation.CorpAppRelationDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemCorpAppRelationMapper;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.module.system.dal.flex.table.SystemCorpAppRelationTableDef.SYSTEM_CORP_APP_RELATION;

/**
 * 企业应用关联 数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class CorpAppRelationDataRepository extends BaseDataServiceImpl<SystemCorpAppRelationMapper, CorpAppRelationDO> {

    /**
     * 分页查询企业应用关联
     *
     * @param pageReqVO 分页条件
     * @param appIds    应用ID列表
     * @return 分页数据
     */
    public PageResult<CorpAppRelationDO> selectPage(CorpAppPageReqVO pageReqVO, List<Long> appIds) {
        QueryWrapper queryWrapper = query().eq(CorpAppRelationDO.CORP_ID, pageReqVO.getCorpId());

        if (CollectionUtils.isNotEmpty(appIds)) {
            queryWrapper.in(CorpAppRelationDO.APPLICATION_ID, appIds);
        }

        if (pageReqVO.getStatus() != null) {
            Integer status = pageReqVO.getStatus();
            if (status.equals(CorpAppReationStatusEnum.DISABLE.getValue())) {
                queryWrapper.eq(CorpAppRelationDO.STATUS, CorpStatusEnum.DISABLE.getValue());
            } else {
                if (status.equals(CorpAppReationStatusEnum.ENABLE.getValue())) {
                    queryWrapper.gt(CorpAppRelationDO.EXPIRES_TIME, LocalDateTime.now());
                    queryWrapper.eq(CorpAppRelationDO.STATUS, CorpStatusEnum.ENABLE.getValue());
                } else if (status.equals(CorpAppReationStatusEnum.EXPIRES.getValue())) {
                    queryWrapper.le(CorpAppRelationDO.EXPIRES_TIME, LocalDateTime.now());
                }
            }
        }

        queryWrapper.orderBy(CREATE_TIME, false);

        Page<CorpAppRelationDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据企业ID删除关联记录
     *
     * @param corpId 企业ID
     */
    public void deleteCorpAppRelationByCorpId(Long corpId) {
        if (corpId == null) {
            return;
        }
        remove(query().eq(CorpAppRelationDO.CORP_ID, corpId));
    }

    /**
     * 批量查询关联
     *
     * @param reqVO 查询条件
     * @return 列表
     */
    public List<CorpAppRelationDO> getCorpAppRelationList(CorpAppRelationPageReqVO reqVO) {
        QueryWrapper queryWrapper = query();
        if (CollectionUtils.isNotEmpty(reqVO.getCorpIds())) {
            queryWrapper.in(CorpAppRelationDO.CORP_ID, reqVO.getCorpIds());
        }
        if (CollectionUtils.isNotEmpty(reqVO.getAppIds())) {
            queryWrapper.in(CorpAppRelationDO.APPLICATION_ID, reqVO.getAppIds());
        }
        List<CorpAppRelationDO> list = list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 根据企业ID查询关联列表
     *
     * @param corpId 企业ID
     * @return 列表
     */
    public List<CorpAppRelationDO> findCorpAppRelationByCorpId(Long corpId) {
        if (corpId == null) {
            return Collections.emptyList();
        }
        List<CorpAppRelationDO> list = list(query().eq(CorpAppRelationDO.CORP_ID, corpId));
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 按企业ID+应用ID查询未过期关联
     *
     * @param corpId 企业ID（可空）
     * @param appId  应用ID
     * @return 列表
     */
    public List<CorpAppRelationDO> findApplicationByCordIdAndAppId(Long corpId, Long appId) {
        QueryWrapper queryWrapper = query().eq(CorpAppRelationDO.APPLICATION_ID, appId)
                .gt(CorpAppRelationDO.EXPIRES_TIME, LocalDateTime.now());
        if (corpId != null) {
            queryWrapper.eq(CorpAppRelationDO.CORP_ID, corpId);
        }
        List<CorpAppRelationDO> list = list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 更新企业应用关联状态
     *
     * @param id     ID
     * @param status 状态
     */
    public void updateStatus(Long id, Integer status) {
        if (id == null) {
            return;
        }
        updateChain().set(SYSTEM_CORP_APP_RELATION.STATUS, status)
                .where(SYSTEM_CORP_APP_RELATION.ID.eq(id))
                .update();
    }
}

