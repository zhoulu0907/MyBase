package com.cmsr.onebase.module.app.build.util.db;

import com.cmsr.onebase.framework.aynline.DataDDLRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private DataDDLRepository dataDDLRepository;

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

//            dataDDLRepository.createTable(AppApplicationDO.class, reset, execute);
//            dataDDLRepository.createTable(AppApplicationTagDO.class, reset, execute);
//            dataDDLRepository.createTable(AppMenuDO.class, reset, execute);
//            dataDDLRepository.createTable(AppTagDO.class, reset, execute);
//            dataDDLRepository.createTable(AppApplicationTagDO.class, reset, execute);
//            dataDDLRepository.createTable(AppAuthRoleDO.class, reset, execute);

//            dataDDLRepository.createTable(AppVersionDO.class, reset, execute);
//            dataDDLRepository.createTable(AppVersionResourceDO.class, reset, execute);

            dataDDLRepository.createTable(AppResourceComponentDO.class, reset, execute);
            dataDDLRepository.createTable(AppResourcePageDO.class, reset, execute);
            dataDDLRepository.createTable(AppResourcePagesetDO.class, reset, execute);
            dataDDLRepository.createTable(AppResourcePagesetPageDO.class, reset, execute);

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
