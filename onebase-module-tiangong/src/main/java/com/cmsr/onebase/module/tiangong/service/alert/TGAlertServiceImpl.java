package com.cmsr.onebase.module.tiangong.service.alert;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.tiangong.dal.dataflex.TGAlertDataRepository;
import com.cmsr.onebase.module.tiangong.dal.dataflexdo.TGAlertDO;
import com.cmsr.onebase.module.tiangong.vo.alert.AlertResVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天工提醒业务 Service 实现类
 *
 * @author matianyu
 * @date 2026-04-19
 */
@Service
@Validated
public class TGAlertServiceImpl implements TGAlertService {

    @Resource
    private TGAlertDataRepository tgAlertDataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AlertResVO> getLatestAlerts() {
        List<TGAlertDO> unclickedAlerts = tgAlertDataRepository.listUnclickedAlerts();
        if (CollUtil.isEmpty(unclickedAlerts)) {
            return Collections.emptyList();
        }

        List<Long> ids = unclickedAlerts.stream().map(TGAlertDO::getId).collect(Collectors.toList());
        tgAlertDataRepository.markClickedByIds(ids);

        return BeanUtils.toBean(unclickedAlerts, AlertResVO.class);
    }
}
