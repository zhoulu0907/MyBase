package com.cmsr.onebase.module.infra.dal.flex.typehandler;

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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map&lt;String, Object&gt; 与数据库 JSON/JSONB 字段之间的类型转换处理器。
 * <p>
 * 写入：将 Map 序列化为 JSON 字符串写入数据库。
 * 读取：将数据库中的 JSON 字符串反序列化为 Map。
 * <p>
 * 说明：这里不在 TypeHandler 内吞异常，解析/序列化失败会包装为 {@link SQLException} 上抛，便于上层统一处理。
 *
 * @author matianyu
 * @date 2025-12-23
 */
public class MapJsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType)
            throws SQLException {
        String json = toJson(parameter);
        if (jdbcType == null) {
            ps.setObject(i, json, Types.OTHER);
            return;
        }
        ps.setObject(i, json, jdbcType.TYPE_CODE);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static Map<String, Object> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> map = OBJECT_MAPPER.readValue(json, MAP_TYPE);
            if (map == null || map.isEmpty()) {
                return Collections.emptyMap();
            }
            // 拷贝一份，避免下游修改返回对象影响内部状态，并保持 key 顺序稳定
            return new LinkedHashMap<>(map);
        } catch (Exception e) {
            throw new SQLException("解析 JSON 为 Map 失败: " + json, e);
        }
    }

    private static String toJson(Map<String, Object> value) throws SQLException {
        Map<String, Object> safe = (value == null ? Collections.emptyMap() : value);
        try {
            return OBJECT_MAPPER.writeValueAsString(safe);
        } catch (Exception e) {
            throw new SQLException("序列化 Map 为 JSON 失败", e);
        }
    }
}
