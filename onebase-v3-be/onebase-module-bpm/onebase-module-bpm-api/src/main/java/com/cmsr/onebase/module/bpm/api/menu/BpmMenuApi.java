package com.cmsr.onebase.module.bpm.api.menu;

/**
 * 用于给菜单调用的接口
 *
 * @author liyang
 * @date 2025-12-16
 */
public interface BpmMenuApi {

    /**
     * 删除菜单所关联的流程定义数据
     * @param menuUuid 菜单UUID
     */
    void removeAppMenu(String menuUuid);
}
