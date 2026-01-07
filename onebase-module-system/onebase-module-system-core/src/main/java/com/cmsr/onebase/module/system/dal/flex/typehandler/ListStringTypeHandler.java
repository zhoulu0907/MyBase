package com.cmsr.onebase.module.system.dal.flex.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * system_users.post_ids(json) 与 Java Set<Long> 之间的类型转换处理器。
 * <p>
 * 该字段在 PostgreSQL 中存储为 JSON 数组（如：[1,2] / []）。
 * MyBatis 默认会尝试用 LongTypeHandler 读取，导致 "不良的类型值 long : [1]"。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@MappedTypes(List.class)
@MappedJdbcTypes({JdbcType.VARCHAR})
public class ListStringTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
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
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static List<String> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> set = OBJECT_MAPPER.readValue(json, new TypeReference<>() {
            });
            if (set == null || set.isEmpty()) {
                return Collections.emptyList();
            }
            // 保持插入序；并避免下游不小心修改不可变集合
            return new ArrayList<>(set);
        } catch (Exception e) {
            // 这里不要吞异常，包装为 SQLException 交给上层统一处理
            throw new SQLException("解析 post_ids JSON 失败: " + json, e);
        }
    }

    private static String toJson(List<String> value) throws SQLException {
        List<String> safe = (value == null ? Collections.emptyList() : value);
        try {
            return OBJECT_MAPPER.writeValueAsString(safe);
        } catch (Exception e) {
            throw new SQLException("序列化 post_ids 为 JSON 失败", e);
        }
    }
}

