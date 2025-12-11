package com.cmsr.extensions.datasource.api;

import com.cmsr.extensions.datasource.vo.XpackPluginsDatasourceVO;

import java.util.List;

/**
 * @Author Junjun
 */
public interface PluginManageApi {
    List<XpackPluginsDatasourceVO> queryPluginDs();
}
