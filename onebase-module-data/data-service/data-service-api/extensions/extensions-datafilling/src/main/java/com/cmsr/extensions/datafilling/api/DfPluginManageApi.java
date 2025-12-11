package com.cmsr.extensions.datafilling.api;

import com.cmsr.extensions.datafilling.vo.XpackPluginsDfVO;

import java.util.List;

public interface DfPluginManageApi {
    List<XpackPluginsDfVO> queryPluginDf();
}
