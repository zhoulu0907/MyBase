package com.cmsr.onebase.module.tiangong.dal.dataflex;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.module.tiangong.dal.dataflexdo.TGAlertDO;
import com.cmsr.onebase.module.tiangong.dal.mapper.TGAlertMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天工提醒数据访问层
 *
 * @author matianyu
 * @date 2026-04-19
 */
@Repository
public class TGAlertDataRepository extends ServiceImpl<TGAlertMapper, TGAlertDO> {

    private static final Integer CLICKED_NO = 0;
    private static final Integer CLICKED_YES = 1;

    /**
     * 查询全部未读提醒
     *
     * @return 未读提醒列表
     */
    public List<TGAlertDO> listUnclickedAlerts() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(TGAlertDO.FIELD_CLICKED, CLICKED_NO)
                .orderBy("create_time", false);
        return this.list(queryWrapper);
    }

    /**
     * 根据主键批量标记为已读
     *
     * @param ids 主键集合
     */
    public void markClickedByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<TGAlertDO> updateList = ids.stream().map(id -> {
            TGAlertDO alertDO = new TGAlertDO();
            alertDO.setId(id);
            alertDO.setClicked(CLICKED_YES);
            return alertDO;
        }).collect(Collectors.toList());
        this.updateBatch(updateList, true);
    }
}

