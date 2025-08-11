package com.cmsr.onebase.module.app.util.db;

import com.cmsr.onebase.module.app.dal.dataobject.version.VersionResourceDO;
import org.springframework.stereotype.Component;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationTagDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageRefRouterDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetLabelDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.dal.dataobject.version.VersionDO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TableInitializer
 * @Description 数据库表初始化工具类，用于创建和初始化数据库表结构
 * @Author mickey
 * @Date 2025/1/27 10:00
 */
@Slf4j
@Component
public class TableInitializer {

    @Resource
    private DataRepository dataRepository;

    /**
     * 初始化所有数据库表结构
     *
     * @param reset   是否重置表结构
     * @param execute 是否执行创建操作
     * @throws Exception 创建表时可能抛出的异常
     */
    public void initTables(Boolean reset, Boolean execute) throws Exception {
        log.info("开始初始化数据库表结构，reset: {}, execute: {}", reset, execute);

        try {
            dataRepository.createTable(ApplicationDO.class, reset, execute);
            dataRepository.createTable(ApplicationTagDO.class, reset, execute);
            dataRepository.createTable(VersionDO.class, reset, execute);
            dataRepository.createTable(VersionResourceDO.class, reset, execute);

            dataRepository.createTable(ComponentDO.class, reset, execute);
            dataRepository.createTable(PageDO.class, reset, execute);
            dataRepository.createTable(PageMetadataDO.class, reset, execute);
            dataRepository.createTable(PageRefRouterDO.class, reset, execute);
            dataRepository.createTable(PageSetDO.class, reset, execute);
            dataRepository.createTable(PageSetLabelDO.class, reset, execute);
            dataRepository.createTable(PageSetPageDO.class, reset, execute);

            log.info("数据库表结构初始化完成");
        } catch (Exception e) {
            log.error("初始化数据库表结构失败", e);
            throw e;
        }
    }

    /**
     * 初始化所有数据库表结构（默认重置并执行）
     *
     * @throws Exception 创建表时可能抛出的异常
     */
    public void initTables() throws Exception {
        initTables(true, true);
    }

    /**
     * 初始化核心演示数据（DML）
     *
     * @throws Exception 初始化数据时可能抛出的异常
     */
    public void initDML() throws Exception {
        // 以下为演示数据初始化逻辑

    }

}
