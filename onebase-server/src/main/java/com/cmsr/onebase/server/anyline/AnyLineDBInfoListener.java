package com.cmsr.onebase.server.anyline;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import org.anyline.data.listener.DMListener;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.prepare.RunPrepare;
import org.anyline.data.run.Run;
import org.anyline.data.runtime.DataRuntime;
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
        // 可以根据需要添加更多表
    }
    public SWITCH beforeExecute(DataRuntime runtime, String random, Run run) {
        String sql = run.getFinalExecute();
        System.out.println("=== beforeExecute SQL: " + sql);
        
        // 检查是否是查询语句且包含需要忽略租户过滤的表
        if (sql != null && !TenantContextHolder.isIgnore()) {
            String lowerSql = sql.toLowerCase().trim();
            if (lowerSql.startsWith("select")) {
                // 检查是否包含需要忽略租户过滤的表名
                for (String tableName : TENANT_IGNORE_TABLES) {
                    if (lowerSql.contains("from " + tableName.toLowerCase()) || 
                        lowerSql.contains("from `" + tableName.toLowerCase() + "`") ||
                        lowerSql.contains("from \"" + tableName.toLowerCase() + "\"")) {
                        System.out.println("=== Found tenant-ignored table " + tableName + " in SQL, blocking execution");
                        // 找到需要忽略租户过滤的表，但SQL已经包含了tenant_id条件
                        // 我们需要在这里修改SQL或者阻止执行
                        if (lowerSql.contains("tenant_id")) {
                            System.out.println("=== SQL contains tenant_id but should not for table " + tableName + ", this will cause error");
                            // 这里我们发现了问题SQL，但在beforeExecute阶段修改SQL比较复杂
                            // 我们需要回到prepareQuery阶段解决
                        }
                        break;
                    }
                }
            }
        }
        
        return SWITCH.CONTINUE;
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
        autoInjectTenantID(obj);
        if (Objects.nonNull(obj) && obj instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) obj;

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

    private void autoInjectTenantID(Object obj) {
        if (Objects.nonNull(obj) && obj instanceof TenantBaseDO) {
            TenantBaseDO tenantBaseDO = (TenantBaseDO) obj;
            tenantBaseDO.setTenantId(TenantContextHolder.getRequiredTenantId());
            System.out.println("tenantBaseDO id  ----------> " + tenantBaseDO.getTenantId());
        }
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
        System.out.println("=== PrepareQuery called, TenantContextHolder.isIgnore(): " + TenantContextHolder.isIgnore());
        
        // 只有在不忽略租户的情况下才添加租户条件
        if (!TenantContextHolder.isIgnore()) {
            // 检查当前查询的表是否需要忽略租户过滤
            boolean shouldIgnore = isTableTenantIgnored(prepare);
            System.out.println("=== Should ignore tenant filtering: " + shouldIgnore);
            
            if (!shouldIgnore) {
                System.out.println("=== Adding tenant_id condition");
                configs.and("tenant_id = " + TenantContextHolder.getRequiredTenantId());
            } else {
                System.out.println("=== Skipping tenant_id condition for this table");
            }
        } else {
            System.out.println("=== TenantContextHolder.isIgnore() is true, skipping tenant filtering");
        }

        return SWITCH.CONTINUE;
    }
    
    /**
     * 检查表是否需要忽略租户过滤
     * 
     * @param prepare RunPrepare对象
     * @return 如果表需要忽略租户过滤则返回true
     */
    private boolean isTableTenantIgnored(RunPrepare prepare) {
        try {
            // 方法1: 检查调用栈中是否有@TenantIgnore注解的实体类
            boolean ignoredByAnnotation = checkStackTraceForTenantIgnore();
            if (ignoredByAnnotation) {
                System.out.println("=== Found @TenantIgnore annotation in call stack, ignoring tenant filtering");
                return true;
            }
            
            // 方法2: 尝试从 prepare 对象中获取表名
            String sql = prepare.getText();
            System.out.println("=== PrepareQuery: SQL = " + sql);
            
            if (sql != null) {
                String lowerSql = sql.toLowerCase().trim();
                System.out.println("=== Checking SQL for tenant filtering: " + lowerSql);
                
                // 检查SQL中是否包含需要忽略的表名
                for (String tableName : TENANT_IGNORE_TABLES) {
                    if (lowerSql.contains(tableName.toLowerCase())) {
                        System.out.println("=== Table " + tableName + " found in SQL, ignoring tenant filtering");
                        return true;
                    }
                }
            } else {
                System.out.println("=== PrepareQuery: SQL is null, checking other methods");
                
                // 尝试获取表信息的其他方式
                try {
                    // 检查 prepare 对象的其他方法
                    System.out.println("=== Prepare object class: " + prepare.getClass().getName());
                    System.out.println("=== Prepare object toString: " + prepare.toString());
                    
                    // 尝试反射获取表名
                    try {
                        java.lang.reflect.Method getTableMethod = prepare.getClass().getMethod("getTable");
                        Object table = getTableMethod.invoke(prepare);
                        if (table != null) {
                            String tableName = table.toString();
                            System.out.println("=== Found table name via getTable(): " + tableName);
                            if (TENANT_IGNORE_TABLES.contains(tableName.toLowerCase())) {
                                System.out.println("=== Table " + tableName + " is in ignore list, ignoring tenant filtering");
                                return true;
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("=== getTable() method not available: " + ex.getMessage());
                    }
                    
                    // 尝试反射获取dest信息
                    try {
                        java.lang.reflect.Method getDestMethod = prepare.getClass().getMethod("getDest");
                        Object dest = getDestMethod.invoke(prepare);
                        if (dest != null) {
                            String destName = dest.toString();
                            System.out.println("=== Found dest name via getDest(): " + destName);
                            if (TENANT_IGNORE_TABLES.contains(destName.toLowerCase())) {
                                System.out.println("=== Dest " + destName + " is in ignore list, ignoring tenant filtering");
                                return true;
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("=== getDest() method not available: " + ex.getMessage());
                    }
                    
                } catch (Exception ex) {
                    System.out.println("=== Error during reflection: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            // 如果无法获取表名，则不忽略（安全做法）
            System.err.println("Error checking table name for tenant filtering: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * 检查调用栈中是否有@TenantIgnore注解的实体类
     */
    private boolean checkStackTraceForTenantIgnore() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                
                // 跳过系统类和框架类
                if (className.startsWith("java.") || 
                    className.startsWith("org.springframework.") ||
                    className.startsWith("org.anyline.") ||
                    className.startsWith("com.cmsr.onebase.framework.") ||
                    className.contains("$")) {
                    continue;
                }
                
                try {
                    Class<?> clazz = Class.forName(className);
                    // 检查类是否有@TenantIgnore注解
                    if (clazz.isAnnotationPresent(com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore.class)) {
                        System.out.println("=== Found @TenantIgnore annotation on class: " + className);
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    // 忽略找不到的类
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("=== Error checking stack trace for @TenantIgnore: " + e.getMessage());
        }
        
        return false;
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
        autoInjectTenantID(obj);
        if (Objects.nonNull(obj) && obj instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) obj;

            LocalDateTime current = LocalDateTime.now();
            baseDO.setUpdateTime(current);

            Long userId = WebFrameworkUtils.getLoginUserId();
            baseDO.setUpdater(userId.toString());
        }
        // 更新时间为空，则以当前时间为更新时间
//        Object modifyTime = getFieldValByName("updateTime", metaObject);
//        if (Objects.isNull(modifyTime)) {
//            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
//        }
//
//        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
//        Object modifier = getFieldValByName("updater", metaObject);
//        Long userId = WebFrameworkUtils.getLoginUserId();
//        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
//            setFieldValByName("updater", userId.toString(), metaObject);
//        }
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
//        if(obj instanceof DataRow){
//            DataRow row = (DataRow)obj;
//            row.put("UPT_TIME", DateUtil.format());
//            if(row.getInt("ROLE_ID", 0) == 99){
//                return SWITCH.BREAK;
//            }
//        }
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
        System.out.println(run.getFinalQuery());
        System.out.println(run.getValues());
        return SWITCH.CONTINUE;
    }
}
