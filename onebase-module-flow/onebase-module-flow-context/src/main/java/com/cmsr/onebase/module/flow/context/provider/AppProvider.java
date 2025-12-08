package com.cmsr.onebase.module.flow.context.provider;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/8 21:21
 */
public interface AppProvider {

    Menu findPageByMenuUuid(String menuUuid);

    @Data
    class Menu {

        private Long menuId;

        private String menuUuid;

        private String entityUuid;

    }

}
