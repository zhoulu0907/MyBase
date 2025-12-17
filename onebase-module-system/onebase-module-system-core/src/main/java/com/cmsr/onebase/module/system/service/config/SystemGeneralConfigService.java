package com.cmsr.onebase.module.system.service.config;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.config.SystemGeneralConfigDO;
import com.cmsr.onebase.module.system.vo.config.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 参数配置 Service 接口
 *
 */
public interface SystemGeneralConfigService {

    /**
     * 创建参数配置
     *
     * @param createReqVO 创建信息
     * @return 配置编号
     */
    Long createConfig(@Valid SystemGeneralConfigSaveReqVO createReqVO);

    /**
     * 更新参数配置
     *
     * @param updateReqVO 更新信息
     */
    void updateConfig(@Valid SystemGeneralConfigUpdateReqVO updateReqVO);

    /**
     * 删除参数配置
     *
     * @param id 配置编号
     */
    void deleteConfig(Long id);

    /**
     * 获得参数配置
     *
     * @param id 配置编号
     * @return 参数配置
     */
    SystemGeneralConfigDO getConfig(Long id);

    /**
     * 获得参数配置分页
     *
     * @param pageReqVO 分页参数
     * @return 参数配置分页
     */
    List<SystemGeneralConfigDO> getConfigList(SystemConfigPageReqVO pageReqVO);

    /**
     * 更新参数配置状态
     *
     * @param
     */
    void updateStatus(Long id, Integer status);
}
