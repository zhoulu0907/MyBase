package com.cmsr.onebase.module.system.dal.flex.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * system_users.post_ids(json) 与 Java Set<Long> 之间的类型转换处理器。
 * <p>
 * 该字段在 PostgreSQL 中存储为 JSON 数组（如：[1,2] / []）。
 * MyBatis 默认会尝试用 LongTypeHandler 读取，导致 "不良的类型值 long : [1]"。
 *
 * @author matianyu
 * @date 2025-12-22
 */
public class SetLongJsonTypeHandler extends BaseTypeHandler<Set<Long>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Set<Long>> SET_LONG_TYPE = new TypeReference<>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Set<Long> parameter, JdbcType jdbcType)
            throws SQLException {
        // PostgreSQL json/jsonb 字段写入通常用字符串即可
        String json = toJson(parameter);
        if (jdbcType == null) {
            ps.setObject(i, json, Types.OTHER);
            return;
        }
        ps.setObject(i, json, jdbcType.TYPE_CODE);
    }

    @Override
    public Set<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Set<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Set<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static Set<Long> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) {
            return Collections.emptySet();
        }
        try {
            Set<Long> set = OBJECT_MAPPER.readValue(json, SET_LONG_TYPE);
            if (set == null || set.isEmpty()) {
                return Collections.emptySet();
            }
            // 保持插入序；并避免下游不小心修改不可变集合
            return new LinkedHashSet<>(set);
        } catch (Exception e) {
            // 这里不要吞异常，包装为 SQLException 交给上层统一处理
            throw new SQLException("解析 post_ids JSON 失败: " + json, e);
        }
    }

    private static String toJson(Set<Long> value) throws SQLException {
        Set<Long> safe = (value == null ? Collections.emptySet() : value);
        try {
            return OBJECT_MAPPER.writeValueAsString(safe);
        } catch (Exception e) {
            throw new SQLException("序列化 post_ids 为 JSON 失败", e);
        }
    }
}

