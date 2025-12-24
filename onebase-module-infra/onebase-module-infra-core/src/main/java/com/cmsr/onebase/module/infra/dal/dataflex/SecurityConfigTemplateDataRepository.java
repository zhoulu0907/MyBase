package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity.SecurityConfigTemplateDO;
import com.cmsr.onebase.module.infra.dal.mapper.ssecurity.SecurityConfigTemplateMapper;
import com.cmsr.onebase.module.infra.dal.vo.app.AppTenantVO;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 安全配置模板数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigTemplateDataRepository extends ServiceImpl<SecurityConfigTemplateMapper, SecurityConfigTemplateDO> {

    /**
     * 根据租户ID和分类ID查询安全配置项（带兜底策略）
     * 优先从租户配置表查询，若不存在则从模板表获取默认值
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 配置项列表，包含租户配置或模板默认值
     */
    @TenantIgnore
    public List<SecurityConfigTemplateDO> findByTenantIdAndCategoryId(Long tenantId, Long categoryId) {
        String sql = """
                SELECT
                    t.id,
                    t.category_id,
                    t.config_key,
                    t.config_name,
                    t.data_type,
                    t.description,
                    t.sort_order,
                    t.options,
                    t.max_value,
                    t.min_value,
                    t.required,
                    t.widget_type,
                    COALESCE(c.config_value, t.default_value) AS config_value_effective
                FROM infra_security_config_template t
                LEFT JOIN infra_security_config c
                    ON t.config_key = c.config_key
                    AND c.tenant_id = ?
                    AND c.deleted = 0
                WHERE t.category_id = ?
                    AND t.deleted = 0
                ORDER BY t.sort_order ASC
                """;
        List<Row> rows = Db.selectListBySql(sql, tenantId, categoryId);
        return getSecurityConfigTemplateDOS(rows);
    }

    /**
     * 根据租户ID和分类ID列表查询安全配置项（带兜底策略）
     * 优先从租户配置表查询，若不存在则从模板表获取默认值
     *
     * @param tenantId   租户ID
     * @param categoryIds 分类ID列表
     * @return 配置项列表，包含租户配置或模板默认值
     */
    @TenantIgnore
    public List<SecurityConfigTemplateDO> findByTenantIdAndCategoryIdList(Long tenantId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < categoryIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sqlPrefix = """
                SELECT
                    t.id,
                    t.category_id,
                    t.config_key,
                    t.config_name,
                    t.data_type,
                    t.description,
                    t.sort_order,
                    t.options,
                    t.max_value,
                    t.min_value,
                    t.required,
                    t.widget_type,
                    COALESCE(c.config_value, t.default_value) AS config_value_effective
                FROM infra_security_config_template t
                LEFT JOIN infra_security_config c
                    ON t.config_key = c.config_key
                    AND c.tenant_id = ?
                    AND c.deleted = 0
                WHERE t.category_id IN (
                """;
        String sqlSuffix = """
                )
                    AND t.deleted = 0
                ORDER BY t.sort_order ASC
                """;

        String sql = sqlPrefix + placeholders + sqlSuffix;

        List<Object> params = new ArrayList<>(1 + categoryIds.size());
        params.add(tenantId);
        params.addAll(categoryIds);

        List<Row> rows = Db.selectListBySql(sql, params.toArray());
        return getSecurityConfigTemplateDOS(rows);
    }

    @NotNull
    private static List<SecurityConfigTemplateDO> getSecurityConfigTemplateDOS(List<Row> rows) {
        List<SecurityConfigTemplateDO> list = rows.stream().map(dataRow -> {
            SecurityConfigTemplateDO templateDO = new SecurityConfigTemplateDO();
            templateDO.setId(dataRow.getLong("id"));
            templateDO.setCategoryId(dataRow.getLong("category_id"));
            templateDO.setConfigKey(dataRow.getString("config_key"));
            templateDO.setConfigName(dataRow.getString("config_name"));
            templateDO.setDataType(dataRow.getString("data_type"));
            templateDO.setConfigValue(dataRow.getString("config_value_effective"));
            templateDO.setDescription(dataRow.getString("description"));
            templateDO.setSortOrder(dataRow.getInt("sort_order"));
            templateDO.setOptions(dataRow.getString("options"));
            templateDO.setMaxValue(dataRow.getLong("max_value"));
            templateDO.setMinValue(dataRow.getLong("min_value"));
            templateDO.setRequired(dataRow.getString("required"));
            templateDO.setWidgetType(dataRow.getString("widget_type"));
            return templateDO;
        }).toList();
        return list;
    }

    /**
     * 根据租户ID查询所有安全配置项（带兜底策略）
     * 优先从租户配置表查询，若不存在则从模板表获取默认值
     *
     * @param tenantId 租户ID
     * @return 配置项列表，包含租户配置或模板默认值
     */
    @TenantIgnore
    public List<SecurityConfigTemplateDO> findByTenantId(Long tenantId) {
        String sql = """
                SELECT
                    t.id,
                    t.category_id,
                    t.config_key,
                    t.config_name,
                    t.data_type,
                    t.description,
                    t.sort_order,
                    t.options,
                    t.max_value,
                    t.min_value,
                    t.required,
                    COALESCE(c.config_value, t.default_value) AS config_value_effective
                FROM infra_security_config_template t
                LEFT JOIN infra_security_config c
                    ON t.config_key = c.config_key
                    AND c.tenant_id = ?
                    AND c.deleted = 0
                WHERE t.deleted = 0
                ORDER BY t.category_id ASC, t.sort_order ASC
                """;

        List<Row> rows = Db.selectListBySql(sql, tenantId);
        List<SecurityConfigTemplateDO> list = getSecurityConfigTemplateDOS(rows);
        return list;
    }

    public AppTenantVO findAppTenantIdById(Long appId) {
        String sql = """
                SELECT  t.id,  t.tenant_id
                FROM app_application t
                WHERE t.id = ?   AND t.deleted = 0
                """;
        List<Row> rows = Db.selectListBySql(sql, appId);
        List<AppTenantVO> volist= rows.stream().map(dataRow -> {
            AppTenantVO templateDO = new AppTenantVO();
            templateDO.setAppId(dataRow.getLong("id"));
            templateDO.setTenantId(dataRow.getLong("tenant_id"));
            return templateDO;
        }).toList();
        if(!volist.isEmpty()){
            return volist.get(0);
        }
        return null;
    }

}
