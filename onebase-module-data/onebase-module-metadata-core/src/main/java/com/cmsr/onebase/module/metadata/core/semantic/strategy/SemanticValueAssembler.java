package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.*;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.mybatisflex.core.row.Row;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语义值装配工具类。
 * <p>
 * 负责在语义层 DTO 与底层数据行 {@link Row} 之间进行双向转换与装配：
 * </p>
 * <ul>
 *   <li>根据实体语义模型与值模型，构建主表数据行（含系统字段）。</li>
 *   <li>构建子表数据行与关系表数据行（含系统字段）。</li>
 *   <li>将数据库数据行解析为语义实体值或语义行值对象。</li>
 *   <li>在未知类型场景下对原始值进行类型猜测，生成对应的语义字段类型。</li>
 * </ul>
 * <p>
 * 线程安全说明：本类仅进行数据的读写与简单转换，不持有可变共享状态；方法均为静态方法，
 * 只在入参范围内操作，因此可被并发安全地调用。
 * </p>
 */
@Component
@Slf4j
public class SemanticValueAssembler {
    /**
     * 基于语义实体模型与实体值，构建主表数据行。
     * <p>
     * 遍历实体模型字段，将对应的存储值写入 {@link Row}，并在最后补齐系统字段。
     * 对于主键字段，若存储值为空则跳过（避免覆盖由系统生成的主键）。
     * </p>
     *
     * @param entity       语义实体模型，包含表名与字段模型
     * @param value        语义实体值，包含各字段的原始值/存储值
     * @param uidGenerator 主键生成器，用于生成系统字段中的 `id`
     * @return 构建完成的主表 {@link Row}
     */
    public Row buildMainRow(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value, UidGenerator uidGenerator) {
        Row row = new Row();
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        if (fields != null) {
            for (SemanticFieldSchemaDTO field : fields) {
                SemanticFieldValueDTO fieldValue = value.getFieldValueByTableAndField(entity.getTableName(), field.getFieldName());
                if (fieldValue == null) { continue; }
                String name = field.getFieldName();
                if (name == null) { continue; }
                Object storeValue = fieldValue.getStoreValue();
                if (storeValue == null && Boolean.TRUE.equals(field.getIsPrimaryKey())) { continue; }
                row.put(name, storeValue);
            }
        }
        fillSystemFields(row, uidGenerator);
        return row;
    }

    /**
     * 为数据行填充系统字段。
     * <p>
     * 包括：`id`、审计字段（创建/更新人与时间）、删除标记、乐观锁、所有者信息等。
     * 对除 `id` 外的系统字段使用空值占位，交由持久层或触发器在后续阶段填充。
     * </p>
     *
     * @param row          需填充的 {@link Row}
     * @param uidGenerator 主键生成器，用于生成 `id`
     */
    public void fillSystemFields(Row row, UidGenerator uidGenerator) {
        row.set("id", uidGenerator.getUID());
        row.put("creator", null);
        row.put("created_time", null);
        row.put("updater", null);
        row.put("updated_time", null);
        row.put("deleted", null);
        row.put("lock_version", null);
        row.put("owner_id", null);
        row.put("owner_dept", null);
    }

    /**
     * 将数据库数据行转换为语义实体值对象。
     * <p>
     * 根据实体模型中定义的字段，读取数据行中的对应值，构建 {@link SemanticEntityValueDTO}。
     * </p>
     *
     * @param entity 语义实体模型
     * @param row    数据库数据行
     * @return 语义实体值 {@link SemanticEntityValueDTO}
     */
    public SemanticEntityValueDTO toEntityValue(SemanticEntitySchemaDTO entity, Row row) {
        SemanticEntityValueDTO resultVal = new SemanticEntityValueDTO();
        resultVal.setId(row.get("id"));
        Map<String, SemanticFieldValueDTO<Object>> fvMap = new HashMap<>();
        List<SemanticFieldSchemaDTO> schemaFields = entity.getFields();
        if (schemaFields != null) {
            for (SemanticFieldSchemaDTO sf : schemaFields) {
                String name = sf.getFieldName();
                if (name == null) { continue; }
                SemanticFieldValueDTO<Object> dto = SemanticFieldValueDTO.ofType(sf.getFieldTypeEnum());
                dto.setFieldId(sf.getId());
                dto.setFieldUuid(sf.getFieldUuid());
                dto.setTableName(entity.getTableName());
                dto.setFieldName(name);
                dto.setRawValue(row.get(name));
                fvMap.put(name, dto);
            }
        }
        resultVal.setFieldValueMap(fvMap);
        return resultVal;
    }

    /**
     * 将数据库数据行转换为语义行值对象。
     * <p>
     * 优先使用实体字段元数据 {@link MetadataEntityFieldDO} 进行解析；若未提供则回退到遍历 {@link Row}
     * 的键值并通过 {@link #guessEnumByRaw(Object)} 推断字段类型。可选的属性列表 `attrs` 用于限定需解析的字段集合。
     * </p>
     *
     * @param r          数据库数据行
     * @param fields     字段元数据列表（可为空）
     * @param attrs      字段属性模型列表（可为空，用于按名称过滤）
     * @param tableName  表名，用于填充字段的来源信息
     * @return 语义行值 {@link SemanticRowValueDTO}
     */
    public SemanticRowValueDTO toRowValue(Row r, List<SemanticFieldSchemaDTO> attrs, String tableName) {
        SemanticRowValueDTO rowDto = new SemanticRowValueDTO();
        rowDto.setId(r.get("id"));
        Object del = r.get("deleted");
        rowDto.setDeleted(del == null ? null : ("1".equals(String.valueOf(del)) || Integer.valueOf(1).equals(del)));
        Map<String, SemanticFieldValueDTO<Object>> fv = new java.util.LinkedHashMap<>();
        
        for (SemanticFieldSchemaDTO a : attrs) {
            if (a == null) { continue; }
            String name = a.getFieldName();
            if (name == null) { continue; }
            Object raw = r.get(name);
            SemanticFieldTypeEnum type = a.getFieldTypeEnum() != null ? a.getFieldTypeEnum() : guessEnumByRaw(raw);
            if (type == null) { type = SemanticFieldTypeEnum.TEXT; }
            SemanticFieldValueDTO<Object> v = SemanticFieldValueDTO.ofType(type);
            v.setFieldId(a.getId());
            v.setFieldUuid(a.getFieldUuid());
            v.setFieldName(name);
            v.setRawValue(raw);
            v.setTableName(tableName);
            fv.put(name, v);
        }
        
        rowDto.setFields(fv);
        return rowDto;
    }

    /**
     * 基于原始值进行语义字段类型的简单推断。
     * <p>
     * 布尔、日期、日期时间、数字、列表类型被映射到相应的枚举；其他情况默认返回文本类型。
     * </p>
     *
     * @param raw 原始值
     * @return 推断得到的 {@link SemanticFieldTypeEnum}，若入参为空返回 {@code null}
     */
    private SemanticFieldTypeEnum guessEnumByRaw(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Boolean) return SemanticFieldTypeEnum.BOOLEAN;
        if (raw instanceof java.time.LocalDate) return SemanticFieldTypeEnum.DATE;
        if (raw instanceof java.time.LocalDateTime) return SemanticFieldTypeEnum.DATETIME;
        if (raw instanceof Number) return SemanticFieldTypeEnum.NUMBER;
        if (raw instanceof java.util.List<?>) return SemanticFieldTypeEnum.MULTI_SELECT;
        return SemanticFieldTypeEnum.TEXT;
    }

    /**
     * 构建子表数据行。
     * <p>
     * 将入参数据映射为行字段，并补齐系统字段及父主键 `parent_id`。
     * </p>
     *
     * @param data        字段名到语义字段值的映射
     * @param parentId    父主键ID
     * @param uidGenerator 主键生成器，用于生成 `id`
     * @return 子表 {@link Row}
     */
    public Row buildSubRow(Map<String, SemanticFieldValueDTO<Object>> data, Long parentId, UidGenerator uidGenerator) {
        Row subRow = new Row();
        if (data != null) {
            for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : data.entrySet()) {
                Object store = e.getValue() == null ? null : e.getValue().getStoreValue();
                subRow.put(e.getKey(), store);
            }
        }
        subRow.set("id", uidGenerator.getUID());
        subRow.put("parent_id", parentId);
        subRow.put("deleted", null);
        subRow.put("lock_version", null);
        subRow.put("creator", null);
        subRow.put("created_time", null);
        subRow.put("updater", null);
        subRow.put("updated_time", null);
        subRow.put("owner_id", null);
        subRow.put("owner_dept", null);
        return subRow;
    }

    /**
     * 构建关系表数据行。
     * <p>
     * 将入参数据映射为行字段，并补齐系统字段（不包含 `parent_id`）。
     * </p>
     *
     * @param data         字段名到语义字段值的映射
     * @param uidGenerator 主键生成器，用于生成 `id`
     * @return 关系表 {@link Row}
     */
    public Row buildRelationRow(Map<String, SemanticFieldValueDTO<Object>> data, UidGenerator uidGenerator) {
        Row relRow = new Row();
        if (data != null) {
            for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : data.entrySet()) {
                Object store = e.getValue() == null ? null : e.getValue().getStoreValue();
                relRow.put(e.getKey(), store);
            }
        }
        relRow.set("id", uidGenerator.getUID());
        relRow.put("deleted", null);
        relRow.put("lock_version", null);
        relRow.put("creator", null);
        relRow.put("created_time", null);
        relRow.put("updater", null);
        relRow.put("updated_time", null);
        relRow.put("owner_id", null);
        relRow.put("owner_dept", null);
        return relRow;
    }
}
