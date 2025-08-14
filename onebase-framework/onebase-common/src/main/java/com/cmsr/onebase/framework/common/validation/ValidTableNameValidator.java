package com.cmsr.onebase.framework.common.validation;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 表名校验器
 * 校验规则：
 * 1. 不能为空
 * 2. 只能包含英文字母、数字和下划线
 * 3. 必须以字母或下划线开头
 * 4. 不能与PostgreSQL系统表名冲突
 *
 * @author bty418
 * @date 2025-08-14
 */
public class ValidTableNameValidator implements ConstraintValidator<ValidTableName, String> {

    /**
     * PostgreSQL系统表名列表（常见的系统表和关键字）
     */
    private static final Set<String> POSTGRES_SYSTEM_TABLES = new HashSet<>(Arrays.asList(
            // PostgreSQL系统表
            "user", "users", "role", "roles", "group", "groups",
            "database", "databases", "schema", "schemas", "table", "tables",
            "column", "columns", "index", "indexes", "sequence", "sequences",
            "view", "views", "function", "functions", "procedure", "procedures",
            "trigger", "triggers", "constraint", "constraints",
            "domain", "domains", "type", "types", "cast", "casts",
            
            // pg_catalog系统表
            "pg_class", "pg_attribute", "pg_type", "pg_proc", "pg_namespace",
            "pg_database", "pg_tablespace", "pg_authid", "pg_auth_members",
            "pg_constraint", "pg_index", "pg_trigger", "pg_rewrite",
            "pg_description", "pg_depend", "pg_cast", "pg_operator",
            "pg_aggregate", "pg_am", "pg_amop", "pg_amproc", "pg_attrdef",
            "pg_conversion", "pg_enum", "pg_inherits", "pg_language",
            "pg_largeobject", "pg_opclass", "pg_opfamily", "pg_policy",
            "pg_range", "pg_statistic", "pg_transform", "pg_ts_config",
            "pg_ts_dict", "pg_ts_parser", "pg_ts_template", "pg_user_mapping",
            
            // information_schema系统表
            "information_schema",
            
            // SQL标准保留字
            "all", "analyse", "analyze", "and", "any", "array", "as", "asc",
            "asymmetric", "both", "case", "check", "collate", "column",
            "constraint", "create", "current_catalog", "current_date",
            "current_role", "current_time", "current_timestamp", "current_user",
            "default", "deferrable", "desc", "distinct", "do", "else", "end",
            "except", "false", "fetch", "for", "foreign", "from", "grant",
            "group", "having", "in", "initially", "intersect", "into", "leading",
            "limit", "localtime", "localtimestamp", "not", "null", "offset",
            "on", "only", "or", "order", "placing", "primary", "references",
            "returning", "select", "session_user", "some", "symmetric", "table",
            "then", "to", "trailing", "true", "union", "unique", "user", "using",
            "variadic", "when", "where", "window", "with"
    ));

    @Override
    public void initialize(ValidTableName annotation) {
        // 初始化方法，可以在这里做一些准备工作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果表名为空，返回false（必须校验）
        if (CharSequenceUtil.isEmpty(value)) {
            setCustomMessage(context, "表名不能为空");
            return false;
        }

        // 校验表名格式：只能包含英文字母、数字和下划线，且必须以字母或下划线开头
        if (!value.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            setCustomMessage(context, "表名只能包含英文字母、数字和下划线，且必须以字母或下划线开头");
            return false;
        }

        // 校验是否与PostgreSQL系统表名冲突（不区分大小写）
        if (POSTGRES_SYSTEM_TABLES.contains(value.toLowerCase())) {
            setCustomMessage(context, "表名不能与PostgreSQL系统表名冲突：" + value);
            return false;
        }

        return true;
    }

    /**
     * 设置自定义错误消息
     */
    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
