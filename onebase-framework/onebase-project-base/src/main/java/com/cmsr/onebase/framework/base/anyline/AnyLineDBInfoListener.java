package com.cmsr.onebase.framework.base.anyline;

import com.cmsr.onebase.framework.common.consts.DeleteConstant;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessErrorCodes;
import com.cmsr.onebase.framework.common.exception.DatabaseAccessException;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.data.base.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.listener.DMListener;
import org.anyline.service.AnylineService;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.prepare.RunPrepare;
import org.anyline.data.run.Run;
import org.anyline.data.runtime.DataRuntime;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.metadata.ACTION.SWITCH;
import org.anyline.metadata.Table;
import org.anyline.util.ConfigTable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component()
@SuppressWarnings("rawtypes") // AnyLine 框架的接口使用原始类型
public class AnyLineDBInfoListener implements DMListener {


    // 需要忽略租户过滤的表名列表
    private static final Set<String> TENANT_IGNORE_TABLES = new HashSet<>();
    
    @Resource
    private UidGenerator uidGenerator;
    
    @Resource
    private AnylineService anylineService;

    static {
        // 添加不需要租户过滤的表
        TENANT_IGNORE_TABLES.add("system_tenant");
        TENANT_IGNORE_TABLES.add("system_tenant_package");
        TENANT_IGNORE_TABLES.add("system_dict_data");
        TENANT_IGNORE_TABLES.add("system_dict_type");
        TENANT_IGNORE_TABLES.add("system_config");
        TENANT_IGNORE_TABLES.add("system_mail_account");
        TENANT_IGNORE_TABLES.add("system_menu");
        TENANT_IGNORE_TABLES.add("system_notify_template");
        TENANT_IGNORE_TABLES.add("system_oauth2_client");
        TENANT_IGNORE_TABLES.add("system_license");
        TENANT_IGNORE_TABLES.add("infra_config");
        TENANT_IGNORE_TABLES.add("infra_data_source_config");
        TENANT_IGNORE_TABLES.add("infra_file_config");
        TENANT_IGNORE_TABLES.add("infra_file_content");
        TENANT_IGNORE_TABLES.add("infra_file");
        TENANT_IGNORE_TABLES.add("infra_api_access_log");
        TENANT_IGNORE_TABLES.add("infra_api_error_log");
        TENANT_IGNORE_TABLES.add("metadata_system_fields");
        TENANT_IGNORE_TABLES.add("metadata_field_type_mapping");
        TENANT_IGNORE_TABLES.add("metadata_data_system_method");
        TENANT_IGNORE_TABLES.add("metadata_validation_type");
        TENANT_IGNORE_TABLES.add("metadata_component_field_type");
        TENANT_IGNORE_TABLES.add("metadata_permit_ref_otft");
        TENANT_IGNORE_TABLES.add("flow_process");
        TENANT_IGNORE_TABLES.add("flow_process_date_field");
        TENANT_IGNORE_TABLES.add("flow_process_entity");
        TENANT_IGNORE_TABLES.add("flow_process_form");
        TENANT_IGNORE_TABLES.add("flow_process_stat");
        TENANT_IGNORE_TABLES.add("flow_process_time");
        TENANT_IGNORE_TABLES.add("flow_execution_log");
        TENANT_IGNORE_TABLES.add("flow_node_category");
        TENANT_IGNORE_TABLES.add("flow_node_type");
        TENANT_IGNORE_TABLES.add("etl_flink_mapping");
        // 可以根据需要添加更多表
    }

    /**
     * 创建插入相关的SQL之前调用<br/>
     * 要修改插入内容可以在这一步实现,注意不是在beforeInsert
     *
     * @param runtime 包含数据源(key)、适配器、JDBCTemplate、dao
     * @param random  用来标记同一组SQL、执行结构、参数等
     * @param dest    表
     * @param object  实体
     * @param columns 需要抛入的列 如果不指定  则根据实体属性解析
     * @return 如果返回false 则中断执行
     */
    @Override
    public SWITCH prepareInsert(DataRuntime runtime, String random, int batch, Table dest, Object object,
                                ConfigStore configs, List<String> columns) {
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareInsert--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        // 先看插入对象是否为空
        if (Objects.isNull(object)) {
            log.info("prepareInsert--------------> insert null object: {}", dest.getName());
            return SWITCH.CONTINUE;
        }
        // 加入租户标志
        injectTenantIdToEntity(object);

        // 加入创建时间和创建人等基础信息
        injectBaseInfoToEntity(object);
        
        // 检测并处理Map/List字段:转换为DataRow并序列化
        if (hasComplexFields(object)) {
            try {
                log.info("检测到实体{}包含Map/List字段,将转换为DataRow并序列化", object.getClass().getSimpleName());
                // 转换为DataRow并序列化复杂字段
                DataRow dataRow = convertEntityToDataRowWithSerialization(object, dest);
                // 手动插入DataRow
                anylineService.insert(dest.getName(), dataRow);
                log.info("通过DataRow插入成功,表: {}", dest.getName());
                // 返回BREAK阻止原Entity插入
                return SWITCH.BREAK;
            } catch (Exception e) {
                log.error("转换Entity为DataRow并插入失败,降级为原Entity插入流程", e);
                // 降级:继续原Entity插入
            }
        }
        
        return SWITCH.CONTINUE;
    }

    /**
     * 向实体注入租户标志
     *
     * @param obj
     */
    private void injectTenantIdToEntity(Object obj) {
        if (obj instanceof Collection) {
            for (Object item : (Collection<?>) obj) {
                injectTenantIdToSingleEntity(item);
            }
        } else {
            injectTenantIdToSingleEntity(obj);
        }
    }

    /**
     * 向实体注入租户标志
     *
     * @param obj
     */
    private void injectTenantIdToSingleEntity(Object obj) {
        boolean shouldIgnore = isTableTenantIgnored(obj);
        if (ConfigTable.IS_DEBUG) {
            log.info("injectTenantIdToOneEntity--------------> isTableTenantIgnored: {}", shouldIgnore);
        }
        if (!shouldIgnore) {
            if (obj instanceof TenantBaseDO tenantBaseDO) {
                tenantBaseDO.setTenantId(TenantContextHolder.getRequiredTenantId());
                if (ConfigTable.IS_DEBUG) {
                    log.info("injectTenantIdToOneEntity--------------> class: {} , setTenantId: {}", obj.getClass().getSimpleName(), tenantBaseDO.getTenantId());
                }
            } else if (obj instanceof BaseEntity baseEntity) {
                baseEntity.setTenantIdByListener(TenantContextHolder.getRequiredTenantId());
                if (ConfigTable.IS_DEBUG) {
                    log.info("injectTenantIdToOneEntity--------------> class: {} , setTenantId: {}", obj.getClass().getSimpleName(), baseEntity.getTenantId());
                }
            }
        }
    }


    private void injectBaseInfoToEntity(Object object) {
        if (object instanceof Collection) {
            for (Object item : (Collection<?>) object) {
                injectBaseInfoToSingleEntity(item);
            }
        } else {
            injectBaseInfoToSingleEntity(object);
        }
    }

    private void injectBaseInfoToSingleEntity(Object obj) {
        if (obj instanceof BaseDO baseDO) {
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
            // 新增数据，删除状态为未删除。解决批量插入数据是插入deleted为null的问题
            baseDO.setDeleted(DeleteConstant.NOT_DELETED);

            // 设置乐观锁默认值
            if (Objects.isNull(baseDO.getLockVersion())) {
                baseDO.setLockVersion(0L);
            }
        } else if (obj instanceof BaseEntity baseEntity) {
            // 设置雪花ID
            if (baseEntity.getId() == null) {
                baseEntity.setIdByListener(uidGenerator.getUID());
                log.info("anyline global prepareInsert ---------> snow id:{}", baseEntity.getId());
            }

            // 创建时间为空，则以当前时间为插入时间
            LocalDateTime current = LocalDateTime.now();
            if (Objects.isNull(baseEntity.getCreateTime())) {
                baseEntity.setCreateTimeByListener(current);
            }

            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseEntity.getUpdateTime())) {
                baseEntity.setUpdateTimeByListener(current);
            }

            Long userId = WebFrameworkUtils.getLoginUserId();
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getCreatorByListener())) {
                baseEntity.setCreatorByListener(userId);
            }

            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseEntity.getUpdaterByListener())) {
                baseEntity.setUpdaterByListener(userId);
            }

            // 新增数据，删除状态为未删除。解决批量插入数据是插入deleted为null的问题
            baseEntity.setDeletedByListener(DeleteConstant.NOT_DELETED);

            // 设置乐观锁默认值
            if (Objects.isNull(baseEntity.getLockVersion())) {
                baseEntity.setLockVersion(0L);
            }
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
    public SWITCH prepareQuery(DataRuntime runtime, String random, RunPrepare prepare, ConfigStore configs,
                               String... conditions) {
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareQuery--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        if (!TenantContextHolder.isIgnore() && TenantContextHolder.getTenantId() != null) {
            configs.param(TenantBaseDO.TENANT_ID, TenantContextHolder.getTenantId());
        }
        // 检查是否有表名，如果没有表名则跳过添加条件
        if (prepare == null || prepare.getTableName() == null || prepare.getTableName().trim().isEmpty()) {
            log.info("prepareQuery--------------> 没有表名，跳过添加租户和软删除条件");
            return SWITCH.CONTINUE;
        }


        // 检查是否是简单的测试查询，如果是则跳过添加条件
        if (isSimpleTestQuery(prepare)) {
            log.info("prepareQuery--------------> 检测到简单测试查询，跳过添加租户和软删除条件");
            return SWITCH.CONTINUE;
        }

        // 加入软删判断
        configs.and(Compare.EQUAL, BaseDO.DELETED, 0);

        // 只有在不忽略租户的情况下才添加租户条件
        // 检查当前查询的表是否需要忽略租户过滤
        boolean shouldIgnore = isTableTenantIgnored(prepare) || TenantContextHolder.isIgnore();
        if (ConfigTable.IS_DEBUG) {
            log.info("prepareQuery--------------> isTableTenantIgnored: {}", shouldIgnore);
        }
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, TenantBaseDO.TENANT_ID, TenantContextHolder.getRequiredTenantId());
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
     * 检查是否是系统数据源
     *
     * @param runtime DataRuntime对象
     * @return 如果是系统数据源则返回true
     */
    private boolean isSystemDataSource(DataRuntime runtime) {
        if (runtime == null) {
            return true; // 默认认为是系统数据源
        }

        String dataSourceKey = getDataSourceKey(runtime);

        // 系统默认数据源通常是 "default" 或为空
        // 临时数据源通常是 "temporary" 或包含临时标识
        return dataSourceKey == null ||
                "default".equals(dataSourceKey) ||
                dataSourceKey.trim().isEmpty() ||
                (!dataSourceKey.contains("temporary") && !dataSourceKey.contains("temp"));
    }

    /**
     * 获取数据源标识
     *
     * @param runtime DataRuntime对象
     * @return 数据源标识字符串
     */
    private String getDataSourceKey(DataRuntime runtime) {
        if (runtime == null) {
            return "default";
        }

        try {
            // 尝试获取数据源key，这可能因AnyLine版本而有所不同
            String key = runtime.getKey();
            return key != null ? key : "default";
        } catch (Exception e) {
            log.debug("获取数据源key失败: {}", e.getMessage());
            return "default";
        }
    }

    /**
     * 检查表是否需要忽略租户过滤
     *
     * @param obj RunPrepare对象
     * @return 如果表需要忽略租户过滤则返回true
     */
    private boolean isTableTenantIgnored(Object obj) {
        return obj != null && obj.getClass().isAnnotationPresent(TenantIgnore.class);
    }

    /**
     * 检查表名是否需要忽略租户过滤
     *
     * @param prepare RunPrepare对象
     * @return 如果表名在忽略列表中则返回true
     */
    private boolean isTableTenantIgnored(RunPrepare prepare) {
        return prepare != null && isTableTenantIgnored(prepare.getTableName());
    }

    /**
     * 检查表名是否需要忽略租户过滤
     *
     * @param tableName 表名
     * @return 如果表名在忽略列表中则返回true
     */
    private boolean isTableTenantIgnored(String tableName) {
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        return TENANT_IGNORE_TABLES.contains(tableName);
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
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareUpdate--------------> 检测到非系统数据源，跳过添加租户和软删除条件，table:{} 数据源: {}", dest.getName(), getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        // 这里config可能为空，强制异常提前发现问题。
        if (configs == null) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.UPDATE_WHERE_IS_NULL);
        }
        // 加入软删判断 (opt: 框架这里config可能为空)
        configs.and(Compare.EQUAL, BaseDO.DELETED, 0);
        // 加入租户标志
        boolean shouldIgnore = isTableTenantIgnored(dest.getName());
        if (ConfigTable.IS_DEBUG) {
            log.info("prepareUpdate obj--------------> isTableTenantIgnored: {}", shouldIgnore);
        }
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, TenantBaseDO.TENANT_ID, TenantContextHolder.getRequiredTenantId());
        }
        // 加入更新时间和更新人
        if (Objects.nonNull(obj)) {
            Long userId = WebFrameworkUtils.getLoginUserId();
            LocalDateTime now = LocalDateTime.now();

            if (obj instanceof BaseDO) {
                BaseDO baseDO = (BaseDO) obj;
                baseDO.setUpdateTime(now);
                baseDO.setUpdater(userId);
            } else if (obj instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) obj;
                baseEntity.setUpdateTimeByListener(now);
                baseEntity.setUpdaterByListener(userId);
            }
        }
        return SWITCH.CONTINUE;
    }

    @Override
    public SWITCH prepareUpdate(DataRuntime runtime, String random, RunPrepare prepare, DataRow data,
                                ConfigStore configs) {
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareUpdate--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        // 这里config可能为空，强制异常提前发现问题。(opt: 框架这里config可能为空)
        if (configs == null) {
            throw new DatabaseAccessException(DatabaseAccessErrorCodes.UPDATE_WHERE_IS_NULL);
        }
        // 加入软删判断
        configs.and(Compare.EQUAL, BaseDO.DELETED, 0);
        // 加入租户标志
        boolean shouldIgnore = isTableTenantIgnored(prepare.getTableName());
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
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareDelete--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        injectTenantIdAndDeleteToConfigs(table.getName(), configs);
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
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("prepareDelete--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        injectTenantIdAndDeleteToConfigs(table.getName(), configs);
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
        // 检查是否是非系统数据源，如果是则跳过添加条件
        if (!isSystemDataSource(runtime)) {
            log.info("afterQuery--------------> 检测到非系统数据源，跳过添加租户和软删除条件，数据源: {}", getDataSourceKey(runtime));
            return SWITCH.CONTINUE;
        }
        return SWITCH.CONTINUE;
    }

    /**
     * 向查询条件注入租户标志
     *
     * @param
     */
    private void injectTenantIdAndDeleteToConfigs(String table, ConfigStore configs) {
        boolean shouldIgnore = isTableTenantIgnored(table);
        log.info("[{}] injectTenantIdAndDeleteToConfigs --------------> isTableTenantIgnored: {}", table, shouldIgnore);
        if (!shouldIgnore) {
            configs.and(Compare.EQUAL, TenantBaseDO.TENANT_ID, TenantContextHolder.getRequiredTenantId());
        }
        // 加入软删判断
        configs.and(Compare.EQUAL, BaseDO.DELETED, 0);
    }

    /**
     * 检测Entity是否有需要序列化的Map/List字段
     * 
     * @param entity 实体对象
     * @return 是否包含Map/List类型的字段
     */
    private boolean hasComplexFields(Object entity) {
        if (entity == null) {
            return false;
        }
        
        // 处理集合的情况
        if (entity instanceof Collection) {
            Collection<?> collection = (Collection<?>) entity;
            if (collection.isEmpty()) {
                return false;
            }
            // 检查集合中第一个元素
            return hasComplexFields(collection.iterator().next());
        }
        
        Class<?> clazz = entity.getClass();
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        
        for (java.lang.reflect.Field field : fields) {
            // 只检查带@Column注解的字段(表示需要持久化)
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            
            Class<?> fieldType = field.getType();
            // 检查是否是Map或List类型
            if (Map.class.isAssignableFrom(fieldType) || List.class.isAssignableFrom(fieldType)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 将Entity转换为DataRow,并对Map/List字段进行JSON序列化
     * 
     * @param entity 实体对象
     * @param table 表信息
     * @return 转换后的DataRow
     * @throws Exception 转换异常
     */
    private DataRow convertEntityToDataRowWithSerialization(Object entity, Table table) throws Exception {
        DataRow dataRow = new DataRow();
        Class<?> clazz = entity.getClass();
        
        // 获取所有字段,包括父类字段
        List<java.lang.reflect.Field> allFields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            allFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        
        for (java.lang.reflect.Field field : allFields) {
            try {
                // 跳过没有@Column注解的字段
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(entity);
                
                // 获取列名
                Column columnAnnotation = field.getAnnotation(Column.class);
                String columnName = columnAnnotation.name();
                if (columnName == null || columnName.isEmpty()) {
                    // 如果@Column没有指定name,使用字段名
                    columnName = field.getName();
                }
                
                // 处理Map/List字段:序列化为JSON字符串
                if (value != null && (value instanceof Map || value instanceof List)) {
                    String jsonString = JsonUtils.toJsonString(value);
                    dataRow.put(columnName, jsonString);
                    log.info("序列化字段 {}.{} : {} -> JSON字符串(长度:{})", 
                        clazz.getSimpleName(), columnName, value.getClass().getSimpleName(), jsonString.length());
                } else {
                    // 其他字段直接设置
                    dataRow.put(columnName, value);
                }
                
            } catch (Exception e) {
                log.error("转换字段失败: {}", field.getName(), e);
                // 继续处理其他字段
            }
        }
        
        log.info("Entity转DataRow完成,表:{}, 字段数:{}", table.getName(), dataRow.size());
        return dataRow;
    }

}
