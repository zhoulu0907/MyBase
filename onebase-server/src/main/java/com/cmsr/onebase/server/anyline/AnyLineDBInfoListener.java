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

@Component()
public class AnyLineDBInfoListener implements DMListener {
    public SWITCH beforeExecute(DataRuntime runtime, String random, Run run) {
        System.out.println("----------> " + run.getFinalExecute());
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
        configs.and("tenant_id = " + TenantContextHolder.getRequiredTenantId());

        return SWITCH.CONTINUE;
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
