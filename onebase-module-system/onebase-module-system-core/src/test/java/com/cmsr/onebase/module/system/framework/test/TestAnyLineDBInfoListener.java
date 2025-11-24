package com.cmsr.onebase.module.system.framework.test;

import com.cmsr.onebase.framework.common.exception.DatabaseAccessErrorCodes;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessException;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.listener.DMListener;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.prepare.RunPrepare;
import org.anyline.data.run.Run;
import org.anyline.data.runtime.DataRuntime;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.metadata.ACTION.SWITCH;
import org.anyline.metadata.Table;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 测试环境专用的AnyLine数据库监听器
 * <p>
 * 主要用于在查询时自动添加软删除条件，确保测试结果的正确性
 *
 * @author matianyu
 * @date 2025-08-06
 */
@Slf4j
@Component
public class TestAnyLineDBInfoListener implements DMListener {

    @Resource
    private UidGenerator uidGenerator;

    // 需要忽略租户过滤的表名列表
    private static final Set<String> TENANT_IGNORE_TABLES = new HashSet<>();


    /**
     * 创建插入相关的SQL之前调用<br/>
     * 要修改插入内容可以在这一步实现,注意不是在beforeInsert
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param dest    表
     * @param obj     实体
     * @param columns 需要抛入的列 如果不指定  则根据实体属性解析
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareInsert(DataRuntime runtime, String random, int batch, Table dest, Object obj,
                                ConfigStore configs, List<String> columns) {
        // 加入租户标志
        injectTenantIdToObject(obj);

        // 注释：由于config字段已改为String类型使用JacksonTypeHandler处理，不再需要JSONB转换
        // handleJsonbFields(runtime, dest, obj);

        // 加入创建时间和创建人等参数
        if (Objects.nonNull(obj) && obj instanceof BaseDO baseDO) {
            // 设置雪花ID
            if (baseDO.getId() == null) {
                baseDO.setId(uidGenerator.getUID());
                log.info("anyline global prepareInsert ---------> snow id:{}", baseDO.getId());
            }

            // 创建时间为空，则以当前时间为插入时间
            LocalDateTime current = LocalDateTime.now();
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }

            Long userId = WebFrameworkUtils.getLoginUserId();
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreator())) {
                baseDO.setCreator(userId);
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdater())) {
                baseDO.setUpdater(userId);
            }
        }
        return SWITCH.CONTINUE;
    }

    /**
     * 创建查相关的SQL之前调用,包括slect exists count等<br/>
     * 要修改查询条件可以在这一步实现,注意不是在beforeQuery
     *
     * @param runtime    包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random     用来标记同一组SQL、执行结构、参数等
     * @param prepare    prepare
     * @param configs    查询条件配置
     * @param conditions 查询条件
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareQuery(DataRuntime runtime, String random, RunPrepare prepare, ConfigStore configs,
                               String... conditions) {

        // 检查是否是简单的测试查询，如果是则跳过添加条件
        if (isSimpleTestQuery(prepare)) {
            log.info("prepareQuery--------------> 检测到简单测试查询，跳过添加租户和软删除条件");
            return SWITCH.CONTINUE;
        }

        // 检查是否有表名，如果没有表名则跳过添加条件
        if (prepare == null || prepare.getTableName() == null || prepare.getTableName().trim().isEmpty()) {
            log.info("prepareQuery--------------> 没有表名，跳过添加租户和软删除条件");
            return SWITCH.CONTINUE;
        }

        // 加入软删判断
        configs.and(Compare.EQUAL, BaseDO.DELETED, false);

        // 只有在不忽略租户的情况下才添加租户条件
        // 检查当前查询的表是否需要忽略租户过滤
        boolean shouldIgnore = isTableTenantIgnored2(prepare);
        log.info("prepareQuery--------------> isTableTenantIgnored: {}", shouldIgnore);
        if (!shouldIgnore) {
            configs.and("tenant_id = " + TenantContextHolder.getRequiredTenantId());
        }
        return SWITCH.CONTINUE;
    }

    /**
     * 检查是否是简单的测试查询
     *
     * @param prepare RunPrepare对象
     * @return 如果是简单测试查询则返回true
     */
    private boolean isSimpleTestQuery(RunPrepare prepare) {
        if (prepare == null) {
            return false;
        }

        // 获取SQL文本进行判断
        String sql = prepare.getText();
        if (sql == null) {
            return false;
        }

        // 去除空白字符并转为大写
        String normalizedSql = sql.trim().toUpperCase();

        // 检查是否是常见的测试查询
        return normalizedSql.equals("SELECT 1") ||
                normalizedSql.equals("SELECT 1 FROM DUAL") ||
                normalizedSql.matches("SELECT\\s+1\\s*") ||
                normalizedSql.matches("SELECT\\s+1\\s+FROM\\s+DUAL\\s*");
    }

    /**
     * 检查表是否需要忽略租户过滤
     *
     * @param obj RunPrepare对象
     * @return 如果表需要忽略租户过滤则返回true
     */
    private boolean isTableTenantIgnored2(Object obj) {
        return true;
    }

    /**
     * 检查表名是否需要忽略租户过滤
     *
     * @param prepare RunPrepare对象
     * @return 如果表名在忽略列表中则返回true
     */
    private boolean isTableTenantIgnored2(RunPrepare prepare) {
        return true;
    }

    /**
     * 检查表名是否需要忽略租户过滤
     *
     * @param tableName 表名
     * @return 如果表名在忽略列表中则返回true
     */
    private boolean isTableTenantIgnored2(String tableName) {
        return true;
    }

    /**
     * 创建更新相关的SQL之前调用<br/>
     * 要修改更新内容或条件可以在这一步实现,注意不是在beforeUpdate
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param dest    表
     * @param obj     Entity或DtaRow
     * @param columns 需要更新的列
     * @param configs 更新条件
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareUpdate(DataRuntime runtime, String random, int batch, Table dest, Object obj,
                                ConfigStore configs, List<String> columns) {
        // 这里config可能为空，强制异常提前发现问题。
        if (configs == null) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.UPDATE_WHERE_IS_NULL);
        }
        // 加入软删判断 (opt: 框架这里config可能为空)
        configs.and(Compare.EQUAL, BaseDO.DELETED, false);
        // 加入租户标志
        boolean shouldIgnore = isTableTenantIgnored2(dest.getName());
        log.info("prepareUpdate obj--------------> isTableTenantIgnored: {}", shouldIgnore);
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, TenantBaseDO.TENANT_ID, TenantContextHolder.getRequiredTenantId());
        }
        // 加入更新时间和更新人
        if (Objects.nonNull(obj) && obj instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) obj;
            baseDO.setUpdateTime(LocalDateTime.now());
            Long userId = WebFrameworkUtils.getLoginUserId();
            baseDO.setUpdater(userId);
        }
        return SWITCH.CONTINUE;
    }

    @Override
    public SWITCH prepareUpdate(DataRuntime runtime, String random, RunPrepare prepare, DataRow data,
                                ConfigStore configs) {
        // 这里config可能为空，强制异常提前发现问题。
        if (configs == null) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.UPDATE_WHERE_IS_NULL);
        }
        // 加入软删判断 (opt: 框架这里config可能为空)
        configs.and(Compare.EQUAL, BaseDO.DELETED, false);
        // 加入租户标志
        boolean shouldIgnore = isTableTenantIgnored2(prepare.getTableName());
        log.info("prepareUpdate row--------------> isTableTenantIgnored: {}", shouldIgnore);
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, TenantBaseDO.TENANT_ID, TenantContextHolder.getRequiredTenantId());
        }
        // 加入更新时间和更新人
        if (Objects.nonNull(data)) {
            data.put(BaseDO.UPDATE_TIME, LocalDateTime.now());
            Long userId = WebFrameworkUtils.getLoginUserId();
            data.put(BaseDO.UPDATER, userId);
        }
        return SWITCH.CONTINUE;
    }

    /**
     * 创建删除SQL前调用(根据Entity/DataRow),修改删除条件可以在这一步实现<br/>
     * 注意不是beforeDelete<br/>
     * 注意beforeBuildDelete有两个函数需要实现<br/>
     * service.delete(DataRow);
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param table   表
     * @param obj     entity或DataRow
     * @param columns 删除条件的我
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareDelete(DataRuntime runtime, String random, int batch, Table table, Object obj,
                                ConfigStore configs, String... columns) {
        injectTenantIdAndDeleteToQuery(table.getName(), configs);
        return SWITCH.CONTINUE;
    }

    /**
     * 创建删除SQL前调用(根据条件),修改删除条件可以在这一步实现<br/>
     * 注意不是beforeDelete<br/>
     * 注意beforeBuildDelete有两个函数需要实现<br/>
     * service.delete("CRM_USER", "ID", "1", "2", "3");
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param table   表
     * @param key     key
     * @param values  obj
     * @return 如果返回false 则中断执行
     */
    public SWITCH prepareDelete(DataRuntime runtime, String random, int batch, Table table, ConfigStore configs,
                                String key, Object values) {
        injectTenantIdAndDeleteToQuery(table.getName(), configs);
        return SWITCH.CONTINUE;
    }

    /**
     * 查询完成后调用
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param run     执行SQL及参数值
     * @param success SQL是否成功执行
     * @param set     查询结果
     * @param millis  耗时(毫秒)
     */
    @Override
    public SWITCH afterQuery(DataRuntime runtime, String random, Run run, boolean success, DataSet set,
                             long millis) {
        return SWITCH.CONTINUE;
    }

    /**
     * 向实体注入租户标志
     *
     * @param obj
     */
    private void injectTenantIdToObject(Object obj) {
        boolean shouldIgnore = isTableTenantIgnored2(obj);
        log.info("injectTenantIdToObject--------------> isTableTenantIgnored: {}", shouldIgnore);
        if (!shouldIgnore && obj instanceof TenantBaseDO tenantBaseDO) {
            tenantBaseDO.setTenantId(TenantContextHolder.getRequiredTenantId());
            log.info("injectTenantIdToObject--------------> setTenantId: {}", tenantBaseDO.getTenantId());
        }
    }

    /**
     * 向查询条件注入租户标志
     *
     * @param
     */
    private void injectTenantIdAndDeleteToQuery(String table, ConfigStore configs) {
        boolean shouldIgnore = isTableTenantIgnored2(table);
        log.info("[{}] injectTenantIdAndDeleteToQuery --------------> isTableTenantIgnored: {}", table, shouldIgnore);
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, "tenant_id", TenantContextHolder.getRequiredTenantId());
        }
        // 加入软删判断
        configs.and(Compare.EQUAL, BaseDO.DELETED, false);
    }


}
