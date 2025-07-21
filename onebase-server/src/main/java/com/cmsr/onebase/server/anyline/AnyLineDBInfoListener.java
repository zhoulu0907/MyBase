package com.cmsr.onebase.server.anyline;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import org.anyline.data.listener.DMListener;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.prepare.RunPrepare;
import org.anyline.data.run.Run;
import org.anyline.data.runtime.DataRuntime;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.metadata.ACTION.SWITCH;
import org.anyline.metadata.Table;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Component()
@SuppressWarnings("rawtypes") // AnyLine 框架的接口使用原始类型
public class AnyLineDBInfoListener implements DMListener {

    // 需要忽略租户过滤的表名列表
    private static final Set<String> TENANT_IGNORE_TABLES = new HashSet<>();

    static {
        // 添加不需要租户过滤的表
        TENANT_IGNORE_TABLES.add("system_dict_data");
        TENANT_IGNORE_TABLES.add("system_dict_type");
        TENANT_IGNORE_TABLES.add("system_config");
        TENANT_IGNORE_TABLES.add("system_tenant");
        TENANT_IGNORE_TABLES.add("system_tenant_package");
        // 可以根据需要添加更多表
    }

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
    public SWITCH prepareInsert(DataRuntime runtime, String random, int batch, Table dest, Object obj, List<String> columns) {
        // 加入租户标志
        autoInjectTenantID(obj);
        // 加入创建时间和创建人等参数
        if (Objects.nonNull(obj) && obj instanceof BaseDO baseDO) {
            LocalDateTime current = LocalDateTime.now();
            // 创建时间为空，则以当前时间为插入时间
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
                baseDO.setCreator(userId.toString());
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdater())) {
                baseDO.setUpdater(userId.toString());
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
    public SWITCH prepareQuery(DataRuntime runtime, String random, RunPrepare prepare, ConfigStore configs, String... conditions) {

        // 加入软删判断
        configs.and(Compare.EQUAL, "deleted", false);

        // 只有在不忽略租户的情况下才添加租户条件
        // 检查当前查询的表是否需要忽略租户过滤
        System.out.println("=== PrepareQuery called, TenantContextHolder.isIgnore(): " + TenantContextHolder.isIgnore());
        boolean shouldIgnore = isTableTenantIgnored2(prepare);
        System.out.println("=== Should ignore tenant filtering: " + shouldIgnore);
        if (!shouldIgnore) {
            configs.and("tenant_id = " + TenantContextHolder.getRequiredTenantId());
        }
        return SWITCH.CONTINUE;
    }

    /**
     * 检查表是否需要忽略租户过滤 by matianyu
     *
     * @param obj RunPrepare对象
     * @return 如果表需要忽略租户过滤则返回true
     */
    private boolean isTableTenantIgnored2(Object obj) {
        return obj != null && obj.getClass().isAnnotationPresent(TenantIgnore.class);
    }

    private boolean isTableTenantIgnored2(RunPrepare prepare) {
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        return prepare != null && TENANT_IGNORE_TABLES.contains(prepare.getTableName());
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
    public SWITCH prepareUpdate(DataRuntime runtime, String random, int batch, Table dest, Object obj, ConfigStore configs, List<String> columns) {
        // 加入软删判断
        configs.and(Compare.EQUAL, "deleted", false);
        // 加入租户标志
        autoInjectTenantID(obj);
        // 加入更新时间和更新人
        if (Objects.nonNull(obj) && obj instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) obj;

            LocalDateTime current = LocalDateTime.now();
            baseDO.setUpdateTime(current);

            Long userId = WebFrameworkUtils.getLoginUserId();
            baseDO.setUpdater(userId.toString());
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
     * @param dest    表
     * @param obj     entity或DataRow
     * @param columns 删除条件的我
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareDelete(DataRuntime runtime, String random, int batch, Table dest, Object obj, String... columns) {
        autoInjectTenantID(obj);
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
     * @param obj     obj
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareDelete(DataRuntime runtime, String random, int batch, Table table, String key, Object obj) {
        autoInjectTenantID(obj);
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
    public SWITCH afterQuery(DataRuntime runtime, String random, Run run, boolean success, DataSet set, long millis) {
        return SWITCH.CONTINUE;
    }

    /**
     * 注入租户标志
     * @param obj
     */
    private void autoInjectTenantID(Object obj) {
        boolean shouldIgnore = isTableTenantIgnored2(obj);
        System.out.println("=== Should ignore tenant filtering: " + shouldIgnore);
        if (shouldIgnore && obj instanceof TenantBaseDO tenantBaseDO) {
            tenantBaseDO.setTenantId(TenantContextHolder.getRequiredTenantId());
            System.out.println("tenantBaseDO id  ----------> " + tenantBaseDO.getTenantId());
        }
    }

}
