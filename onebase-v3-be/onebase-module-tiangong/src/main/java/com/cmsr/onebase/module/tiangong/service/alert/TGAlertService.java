package com.cmsr.onebase.module.tiangong.service.alert;

import com.cmsr.onebase.module.tiangong.vo.alert.AlertResVO;

import java.util.List;

/**
 * 天工提醒业务 Service
 *
 * @author matianyu
 * @date 2026-04-19
 */
public interface TGAlertService {

    /**
     * 获取最新未读提醒，并将其标记为已读
     *
     * @return 未读提醒列表
     */
    List<AlertResVO> getLatestAlerts();
}

