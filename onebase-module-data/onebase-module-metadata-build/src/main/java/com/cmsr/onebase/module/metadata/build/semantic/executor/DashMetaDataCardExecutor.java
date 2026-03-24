package com.cmsr.onebase.module.metadata.build.semantic.executor;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.core.config.ApplicationDataSourceManager;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticCombinatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionTypeEnum;
import com.cmsr.onebase.module.metadata.core.dialect.SqlDialects;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticMergeRecordAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticQueryConditionBuilder;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.Db;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("buildDashMetaDataCardExecutor")
public class DashMetaDataCardExecutor {

    private static final String CALC_COUNT = "count";
    private static final String CALC_SUM = "sum";
    private static final String CALC_AVG = "avg";
    private static final String CALC_MAX = "max";
    private static final String CALC_MIN = "min";
    private static final String CALC_COUNT_DISTINCT = "count_distinct";

    @Resource
    private SemanticMergeRecordAssembler semanticMergeRecordAssembler;

    @Resource
    private SemanticQueryConditionBuilder semanticQueryConditionBuilder;

    private static boolean ciEquals(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    public List<Map<String, Object>> execute(List<Map<String, Object>> cards, String traceId) {
        if (CollectionUtils.isEmpty(cards)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>(cards.size());
        for (Map<String, Object> card : cards) {
            result.add(executeOne(card, traceId));
        }
        return result;
    }

    private Map<String, Object> executeOne(Map<String, Object> card, String traceId) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (card == null) {
            out.put("success", false);
            out.put("message", "empty card config");
            return out;
        }
        out.put("labelText", extractLabelText(card));
        out.put("describe", safeToString(card.get("describe")));
        out.put("metaData", safeToString(card.get("metaData")));
        out.put("tableName", safeToString(card.get("tableName")));
        out.put("calculateType", safeToString(card.get("calculateType")));
        out.put("unit", safeToString(card.get("unit")));

        String calculateType = normalizeCalculateType(safeToString(card.get("calculateType")));
        if (!isSupportedCalculateType(calculateType)) {
            out.put("success", false);
            out.put("message", "unsupported calculateType: " + calculateType);
            return out;
        }

        try {
            Object absoluteValue = card.get("absoluteValue");
            boolean compareLimit = boolValue(card.get("compareLimit"));
            String timeField = safeToString(card.get("timeField"));
            String timeDimension = safeToString(card.get("timeDimension"));
            boolean hasTime = compareLimit && StringUtils.isNotBlank(timeField) && StringUtils.isNotBlank(timeDimension);

            Object value;
            if (hasTime) {
                SemanticEntitySchemaDTO schema0 = resolveEntitySchema(safeToString(card.get("tableName")), safeToString(card.get("metaData")));
                boolean timeFieldOk = isSafeIdentifier(timeField)
                        && schema0 != null && schema0.getFields() != null
                        && schema0.getFields().stream().map(SemanticFieldSchemaDTO::getFieldName).anyMatch(timeField::equals);
                if (timeFieldOk) {
                    LocalDateTime[] curr = currentRange(timeDimension);
                    value = queryAggregateWithTimeRange(card, calculateType, schema0, timeField, curr[0], curr[1]);
                } else {
                    value = queryAggregate(card, calculateType);
                }
            } else {
                value = queryAggregate(card, calculateType);
            }

            value = applyPrecision(card, value);
            if (absoluteValue instanceof Boolean b && b) {
                value = applyAbsolute(value);
            }
            out.put("value", value == null ? 0 : value);
            out.put("displayValue", formatDisplayValue(card, value, calculateType));

            if (hasTime) {
                SemanticEntitySchemaDTO schema1 = resolveEntitySchema(safeToString(card.get("tableName")), safeToString(card.get("metaData")));
                boolean timeFieldOk = isSafeIdentifier(timeField)
                        && schema1 != null && schema1.getFields() != null
                        && schema1.getFields().stream().map(SemanticFieldSchemaDTO::getFieldName).anyMatch(timeField::equals);
                if (timeFieldOk) {
                    boolean yoy = isYoy(card);
                    LocalDateTime[] base = yoy ? previousYearRange(timeDimension) : previousRange(timeDimension);
                    Object baseValRaw = queryAggregateWithTimeRange(card, calculateType, schema1, timeField, base[0], base[1]);
                    BigDecimal currNumber = ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)
                            ? BigDecimal.valueOf(toLongValue(value))
                            : toBigDecimalValue(value);
                    if (currNumber == null) currNumber = BigDecimal.ZERO;
                    BigDecimal baseNumber = ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)
                            ? BigDecimal.valueOf(toLongValue(baseValRaw))
                            : toBigDecimalValue(baseValRaw);
                    if (baseNumber == null) baseNumber = BigDecimal.ZERO;
                    BigDecimal delta = currNumber.subtract(baseNumber);
                    BigDecimal rate = null;
                    if (baseNumber.compareTo(BigDecimal.ZERO) != 0) {
                        rate = delta.divide(baseNumber, 6, RoundingMode.HALF_UP);
                    }
                    String compareCalculateType = safeToString(card.get("compareCalculateType"));
                    out.put("compareBaseValue", ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)
                            ? baseNumber.longValue()
                            : baseNumber);
                    out.put("compareDelta", delta);
                    out.put("compareRate", rate);
                    out.put("compareType", delta.signum() > 0 ? "up" : (delta.signum() < 0 ? "down" : "equal"));
                    out.put("compareDescribe", safeToString(card.get("compareDescribe")));
                    out.put("compareMode", yoy ? "yoy" : "mom");
                    if ("rate".equalsIgnoreCase(compareCalculateType)) {
                        out.put("compareDisplay", rate == null ? null : formatPercent(rate));
                    } else {
                        out.put("compareDisplay", formatNumber(delta, card));
                    }
                    out.put("compareAvailable", rate != null || delta.signum() == 0);
                }
            }
            out.put("success", true);
            return out;
        } catch (Exception ex) {
            out.put("success", false);
            out.put("message", ex.getMessage());
            return out;
        }
    }

    private String formatDisplayValue(Map<String, Object> card, Object value, String calculateType) {
        BigDecimal number;
        if (ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)) {
            number = BigDecimal.valueOf(toLongValue(value));
        } else {
            number = toBigDecimalValue(value);
        }
        if (number == null) {
            number = BigDecimal.ZERO;
        }

        boolean percent = boolValue(card.get("percent"));
        boolean precisionLimit = boolValue(card.get("precisionLimit"));
        Integer precision = intValue(card.get("precision"));
        boolean thousandsSeparator = boolValue(card.get("thousandsSeparator"));
        boolean unitLimit = boolValue(card.get("unitLimit"));
        String unit = safeToString(card.get("unit"));

        BigDecimal finalNumber = number;
        if (percent) {
            finalNumber = finalNumber.multiply(BigDecimal.valueOf(100));
        }

        Integer scale = null;
        if (precisionLimit) {
            if (precision != null && precision >= 0) {
                scale = precision;
            } else {
                scale = 0;
            }
        } else if (percent) {
            scale = 2;
        }

        if (scale != null) {
            finalNumber = finalNumber.setScale(scale, RoundingMode.HALF_UP);
        }

        String text;
        if (thousandsSeparator) {
            int s = scale == null ? Math.max(0, finalNumber.stripTrailingZeros().scale()) : scale;
            StringBuilder pattern = new StringBuilder("#,##0");
            if (s > 0) {
                pattern.append(".");
                for (int i = 0; i < s; i++) {
                    pattern.append("0");
                }
            }
            DecimalFormat df = new DecimalFormat(pattern.toString());
            df.setRoundingMode(RoundingMode.HALF_UP);
            text = df.format(finalNumber);
        } else {
            text = finalNumber.stripTrailingZeros().toPlainString();
        }

        if (percent) {
            text = text + "%";
        }
        if (unitLimit && StringUtils.isNotBlank(unit)) {
            text = text + unit;
        }
        return text;
    }

    private boolean boolValue(Object raw) {
        if (raw instanceof Boolean b) {
            return b;
        }
        if (raw instanceof String s) {
            return "true".equalsIgnoreCase(s) || "1".equals(s);
        }
        if (raw instanceof Number n) {
            return n.intValue() != 0;
        }
        return false;
    }

    private Integer intValue(Object raw) {
        if (raw instanceof Number n) {
            return n.intValue();
        }
        if (raw instanceof String s && StringUtils.isNumeric(s)) {
            return Integer.parseInt(s);
        }
        return null;
    }

    private String formatPercent(BigDecimal rate) {
        BigDecimal r = rate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        return r.stripTrailingZeros().toPlainString() + "%";
    }

    private String formatNumber(BigDecimal number, Map<String, Object> card) {
        boolean thousandsSeparator = boolValue(card.get("thousandsSeparator"));
        Integer precision = intValue(card.get("precision"));
        boolean precisionLimit = boolValue(card.get("precisionLimit"));
        BigDecimal n = number == null ? BigDecimal.ZERO : number;
        if (precisionLimit && precision != null && precision >= 0) {
            n = n.setScale(precision, RoundingMode.HALF_UP);
        }
        if (thousandsSeparator) {
            int s = Math.max(0, n.stripTrailingZeros().scale());
            StringBuilder pattern = new StringBuilder("#,##0");
            if (s > 0) {
                pattern.append(".");
                for (int i = 0; i < s; i++) pattern.append("0");
            }
            DecimalFormat df = new DecimalFormat(pattern.toString());
            df.setRoundingMode(RoundingMode.HALF_UP);
            return df.format(n);
        }
        return n.stripTrailingZeros().toPlainString();
    }

    private boolean isYoy(Map<String, Object> card) {
        String desc = safeToString(card.get("compareDescribe"));
        if (StringUtils.isBlank(desc)) return false;
        String s = desc.toLowerCase();
        return s.contains("同比") || s.contains("去年") || s.contains("yoy");
    }

    private LocalDateTime[] currentRange(String dimension) {
        LocalDateTime now = LocalDateTime.now();
        if ("day".equalsIgnoreCase(dimension)) {
            LocalDate d = now.toLocalDate();
            return new LocalDateTime[]{d.atStartOfDay(), d.plusDays(1).atStartOfDay()};
        }
        if ("week".equalsIgnoreCase(dimension)) {
            LocalDate d = now.toLocalDate();
            LocalDate start = d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = start.plusWeeks(1);
            return new LocalDateTime[]{start.atStartOfDay(), end.atStartOfDay()};
        }
        if ("year".equalsIgnoreCase(dimension)) {
            LocalDate d = now.toLocalDate();
            LocalDate start = LocalDate.of(d.getYear(), 1, 1);
            LocalDate end = start.plusYears(1);
            return new LocalDateTime[]{start.atStartOfDay(), end.atStartOfDay()};
        }
        LocalDate d = now.toLocalDate();
        LocalDate start = LocalDate.of(d.getYear(), d.getMonth(), 1);
        LocalDate end = start.plusMonths(1);
        return new LocalDateTime[]{start.atStartOfDay(), end.atStartOfDay()};
    }

    private LocalDateTime[] previousRange(String dimension) {
        LocalDateTime[] curr = currentRange(dimension);
        LocalDateTime start = curr[0];
        LocalDateTime pStart;
        LocalDateTime pEnd = start;
        if ("day".equalsIgnoreCase(dimension)) {
            pStart = start.minusDays(1);
        } else if ("week".equalsIgnoreCase(dimension)) {
            pStart = start.minusWeeks(1);
        } else if ("year".equalsIgnoreCase(dimension)) {
            pStart = start.minusYears(1);
        } else {
            pStart = start.minusMonths(1);
        }
        return new LocalDateTime[]{pStart, pEnd};
    }

    private LocalDateTime[] previousYearRange(String dimension) {
        LocalDateTime[] curr = currentRange(dimension);
        LocalDateTime start = curr[0].minusYears(1);
        LocalDateTime end = curr[1].minusYears(1);
        return new LocalDateTime[]{start, end};
    }

    private Object queryAggregateWithTimeRange(Map<String, Object> card,
                                               String calculateType,
                                               SemanticEntitySchemaDTO schema,
                                               String timeField,
                                               LocalDateTime start,
                                               LocalDateTime end) {
        String actualTableName = schema.getTableName();
        if (!isSafeIdentifier(actualTableName)) {
            throw new IllegalArgumentException("unsafe tableName");
        }
        String calcField = safeToString(card.get("calculateField"));
        SemanticFieldSchemaDTO calcFieldSchema = resolveCalcFieldSchema(calcField, schema);
        QueryWrapper whereQw = buildWhereQuery(schema, card.get("filterCondition"));
        if (!isSafeIdentifier(timeField)) {
            throw new IllegalArgumentException("unsafe timeField");
        }
        whereQw.and(" " + timeField + " >= ? ", start);
        whereQw.and(" " + timeField + " < ? ", end);
        QueryWrapper aggQw = QueryWrapper.create();
        applyAggregateSelect(aggQw, calculateType, calcFieldSchema, calcField);
        aggQw.where(CPI.getWhereQueryCondition(whereQw));
        Long appId = ApplicationManager.getApplicationId();
        if (appId == null) {
            throw new IllegalStateException("missing applicationId");
        }
        ApplicationDataSourceManager.useBizDatasourceByAppId(appId);
        try {
            Row row = Db.selectOneByQuery(actualTableName, aggQw);
            if (row == null) {
                return 0;
            }
            Object raw = row.get("value");
            if (raw == null) {
                return 0;
            }
            if (ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)) {
                return toLongValue(raw);
            }
            return toBigDecimalValue(raw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    private Object queryAggregate(Map<String, Object> card, String calculateType) {
        String tableName = safeToString(card.get("tableName"));
        String metaData = safeToString(card.get("metaData"));
        SemanticEntitySchemaDTO schema = resolveEntitySchema(tableName, metaData);
        String actualTableName = schema.getTableName();
        if (!isSafeIdentifier(actualTableName)) {
            throw new IllegalArgumentException("unsafe tableName");
        }

        String calcField = safeToString(card.get("calculateField"));
        SemanticFieldSchemaDTO calcFieldSchema = resolveCalcFieldSchema(calcField, schema);
        QueryWrapper whereQw = buildWhereQuery(schema, card.get("filterCondition"));
        QueryWrapper aggQw = QueryWrapper.create();
        applyAggregateSelect(aggQw, calculateType, calcFieldSchema, calcField);
        aggQw.where(CPI.getWhereQueryCondition(whereQw));

        Long appId = ApplicationManager.getApplicationId();
        if (appId == null) {
            throw new IllegalStateException("missing applicationId");
        }

        ApplicationDataSourceManager.useBizDatasourceByAppId(appId);
        try {
            Row row = Db.selectOneByQuery(actualTableName, aggQw);
            if (row == null) {
                return 0;
            }
            Object raw = row.get("value");
            if (raw == null) {
                return 0;
            }
            if (ciEquals(CALC_COUNT, calculateType) || ciEquals(CALC_COUNT_DISTINCT, calculateType)) {
                return toLongValue(raw);
            }
            return toBigDecimalValue(raw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    private SemanticEntitySchemaDTO resolveEntitySchema(String tableName, String metaData) {
        if (StringUtils.isNotBlank(metaData)) {
            return semanticMergeRecordAssembler.buildEntitySchemaByUuid(metaData);
        }
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("missing tableName/metaData");
        }
        return semanticMergeRecordAssembler.buildEntitySchemaByTableName(tableName);
    }

    private QueryWrapper buildWhereQuery(SemanticEntitySchemaDTO schema, Object filterConditionRaw) {
        QueryWrapper whereQw = QueryWrapper.create();
        SemanticConditionDTO semanticCondition = buildSemanticCondition(schema, filterConditionRaw);
        semanticQueryConditionBuilder.apply(whereQw, schema == null ? null : schema.getFields(), semanticCondition, null);
        return whereQw;
    }

    private SemanticConditionDTO buildSemanticCondition(SemanticEntitySchemaDTO schema, Object filterConditionRaw) {
        List<Map<String, Object>> groups = normalizeToMapList(filterConditionRaw);
        if (CollectionUtils.isEmpty(groups)) {
            return null;
        }
        List<String> allowedFields = schema == null || schema.getFields() == null
                ? List.of()
                : schema.getFields().stream()
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(StringUtils::isNotBlank)
                .toList();

        List<SemanticConditionDTO> groupNodes = new ArrayList<>();
        for (Map<String, Object> group : groups) {
            if (group == null) {
                continue;
            }
            List<Map<String, Object>> conditions = normalizeToMapList(group.get("conditions"));
            if (CollectionUtils.isEmpty(conditions)) {
                continue;
            }
            List<SemanticConditionDTO> children = new ArrayList<>();
            for (Map<String, Object> cond : conditions) {
                if (cond == null) {
                    continue;
                }
                String fieldKey = safeToString(cond.get("fieldKey"));
                if (StringUtils.isBlank(fieldKey) || !isSafeIdentifier(fieldKey) || !allowedFields.contains(fieldKey)) {
                    continue;
                }
                SemanticOperatorEnum op = mapOperator(safeToString(cond.get("op")));
                List<Object> values = mapValues(op, cond.get("value"));

                SemanticConditionDTO leaf = new SemanticConditionDTO();
                leaf.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
                leaf.setConditionType(SemanticConditionTypeEnum.SUB_CONDITION);
                leaf.setFieldName(fieldKey);
                leaf.setOperator(op);
                leaf.setFieldValue(values);
                children.add(leaf);
            }
            if (!children.isEmpty()) {
                SemanticConditionDTO groupNode = new SemanticConditionDTO();
                groupNode.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
                groupNode.setCombinator(SemanticCombinatorEnum.AND);
                groupNode.setChildren(children);
                groupNodes.add(groupNode);
            }
        }
        if (groupNodes.isEmpty()) {
            return null;
        }
        SemanticConditionDTO root = new SemanticConditionDTO();
        root.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
        root.setCombinator(groupNodes.size() > 1 ? SemanticCombinatorEnum.OR : SemanticCombinatorEnum.AND);
        root.setChildren(groupNodes);
        root.setConditionType(SemanticConditionTypeEnum.MAIN_CONDITION);
        return root;
    }

    private SemanticOperatorEnum mapOperator(String opRaw) {
        if (StringUtils.isBlank(opRaw)) {
            return null;
        }
        if ("GREATER_THAN_OR_EQUALS".equalsIgnoreCase(opRaw)) {
            return SemanticOperatorEnum.GREATER_EQUALS;
        }
        if ("LESS_THAN_OR_EQUALS".equalsIgnoreCase(opRaw)) {
            return SemanticOperatorEnum.LESS_EQUALS;
        }
        return SemanticOperatorEnum.getByName(opRaw);
    }

    private List<Object> mapValues(SemanticOperatorEnum op, Object raw) {
        if (op == SemanticOperatorEnum.EXISTS_IN || op == SemanticOperatorEnum.NOT_EXISTS_IN) {
            Collection<?> c = normalizeToCollection(raw);
            if (CollectionUtils.isEmpty(c)) {
                return List.of();
            }
            return new ArrayList<>(c);
        }
        if (op == SemanticOperatorEnum.IS_EMPTY || op == SemanticOperatorEnum.IS_NOT_EMPTY) {
            return List.of();
        }
        if (raw == null) {
            return List.of();
        }
        return List.of(raw);
    }

    private boolean isSupportedCalculateType(String calculateType) {
        return ciEquals(CALC_COUNT, calculateType)
                || ciEquals(CALC_SUM, calculateType)
                || ciEquals(CALC_AVG, calculateType)
                || ciEquals(CALC_MAX, calculateType)
                || ciEquals(CALC_MIN, calculateType)
                || ciEquals(CALC_COUNT_DISTINCT, calculateType);
    }

    private SemanticFieldSchemaDTO resolveCalcFieldSchema(String calcField, SemanticEntitySchemaDTO schema) {
        if (StringUtils.isBlank(calcField)) {
            return null;
        }
        if (!isSafeIdentifier(calcField)) {
            throw new IllegalArgumentException("unsafe calculateField");
        }
        if (schema == null || schema.getFields() == null || schema.getFields().isEmpty()) {
            throw new IllegalArgumentException("missing schema fields");
        }
        return schema.getFields().stream()
                .filter(f -> calcField.equals(f.getFieldName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("calculateField not exists in entity"));
    }

    private void assertNumericField(SemanticFieldSchemaDTO fieldSchema, String calculateType) {
        if (fieldSchema == null) {
            throw new IllegalArgumentException("missing calculateField for " + calculateType);
        }
        SemanticFieldTypeEnum fieldType = fieldSchema.getFieldTypeEnum();
        if (fieldType == null || !fieldType.isNumberType()) {
            throw new IllegalArgumentException(
                    calculateType + " 仅支持数值类型字段，当前字段 " + fieldSchema.getFieldName() + " 类型为 " + fieldSchema.getFieldType());
        }
    }

    private void applyAggregateSelect(QueryWrapper qw, String calculateType, SemanticFieldSchemaDTO fieldSchema, String calcField) {
        // count 不需要字段
        if (ciEquals(CALC_COUNT, calculateType)) {
            qw.select(QueryMethods.count().as("value"));
            return;
        }
        // count_distinct 需要字段但不限类型
        if (ciEquals(CALC_COUNT_DISTINCT, calculateType)) {
            if (StringUtils.isBlank(calcField)) {
                throw new IllegalArgumentException("missing calculateField");
            }
            if (!isSafeIdentifier(calcField)) {
                throw new IllegalArgumentException("unsafe calculateField");
            }
            qw.select(SqlDialects.countDistinct(calcField) + " AS value");
            return;
        }
        // sum/avg/max/min 需要数值类型字段
        if (ciEquals(CALC_SUM, calculateType) || ciEquals(CALC_AVG, calculateType)
                || ciEquals(CALC_MAX, calculateType) || ciEquals(CALC_MIN, calculateType)) {
            assertNumericField(fieldSchema, calculateType);
            if (StringUtils.isBlank(calcField) || !isSafeIdentifier(calcField)) {
                throw new IllegalArgumentException("missing or unsafe calculateField");
            }
            // 使用工具类生成 SQL
            String sql;
            if (ciEquals(CALC_SUM, calculateType)) {
                sql = SqlDialects.sum(calcField);
            } else if (ciEquals(CALC_AVG, calculateType)) {
                sql = SqlDialects.avg(calcField);
            } else if (ciEquals(CALC_MAX, calculateType)) {
                sql = SqlDialects.max(calcField);
            } else {
                sql = SqlDialects.min(calcField);
            }
            qw.select(sql + " AS value");
            return;
        }
        throw new IllegalArgumentException("unsupported calculateType: " + calculateType);
    }

    private Object applyAbsolute(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bd) {
            return bd.abs();
        }
        if (value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long) {
            long v = ((Number) value).longValue();
            return Math.abs(v);
        }
        if (value instanceof Float || value instanceof Double) {
            double v = ((Number) value).doubleValue();
            return Math.abs(v);
        }
        if (value instanceof Number n) {
            BigDecimal bd = toBigDecimalValue(n);
            return bd == null ? value : bd.abs();
        }
        return value;
    }

    private Object applyPrecision(Map<String, Object> card, Object value) {
        if (value == null) {
            return null;
        }
        Object precisionLimit = card.get("precisionLimit");
        if (!(precisionLimit instanceof Boolean b) || !b) {
            return value;
        }
        Integer scale = intValue(card.get("precision"));
        if (scale == null || scale < 0) {
            return value;
        }
        BigDecimal bd = toBigDecimalValue(value);
        return bd == null ? value : bd.setScale(scale, java.math.RoundingMode.HALF_UP);
    }

    private long toLongValue(Object raw) {
        if (raw == null) {
            return 0L;
        }
        if (raw instanceof Number n) {
            return n.longValue();
        }
        try {
            return new BigDecimal(String.valueOf(raw)).longValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private BigDecimal toBigDecimalValue(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof BigDecimal bd) {
            return bd;
        }
        if (raw instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(raw));
        } catch (Exception ignored) {
            return null;
        }
    }

    static String extractLabelText(Map<String, Object> card) {
        if (card == null) {
            return null;
        }
        Object labelObj = card.get("label");
        if (!(labelObj instanceof Map<?, ?> label)) {
            return null;
        }
        Object text = label.get("text");
        return safeToString(text);
    }

    static boolean isSafeIdentifier(String s) {
        if (StringUtils.isBlank(s)) {
            return false;
        }
        return s.matches("^[A-Za-z_][A-Za-z0-9_]*$");
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> normalizeToMapList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object it : list) {
                if (it instanceof Map<?, ?> map) {
                    out.add((Map<String, Object>) map);
                }
            }
            return out;
        }
        if (raw instanceof String s && JsonUtils.isJson(s)) {
            List<Map<String, Object>> parsed = JsonUtils.parseObjectQuietly(s, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            return parsed == null ? List.of() : parsed;
        }
        return List.of();
    }

    static Collection<?> normalizeToCollection(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof Collection<?> c) {
            return c;
        }
        if (raw.getClass().isArray()) {
            Object[] arr = (Object[]) raw;
            List<Object> out = new ArrayList<>(arr.length);
            for (Object v : arr) {
                out.add(v);
            }
            return out;
        }
        String s = safeToString(raw);
        if (StringUtils.isBlank(s)) {
            return List.of();
        }
        if (JsonUtils.isJson(s)) {
            List<Object> parsed = JsonUtils.parseObjectQuietly(s, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            return parsed == null ? List.of() : parsed;
        }
        String[] parts = s.split(",");
        List<String> out = new ArrayList<>();
        for (String part : parts) {
            if (StringUtils.isNotBlank(part)) {
                out.add(part.trim());
            }
        }
        return out;
    }

    static String safeToString(Object v) {
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v);
        return StringUtils.isBlank(s) ? null : s;
    }

    private String normalizeCalculateType(String raw) {
        if (StringUtils.isBlank(raw)) {
            return raw;
        }
        String v = raw.trim().toLowerCase();
        if ("distinctcount".equals(v) || "countdistinct".equals(v) || "distinct_count".equals(v)) {
            return CALC_COUNT_DISTINCT;
        }
        if ("count_distinct".equals(v)) {
            return CALC_COUNT_DISTINCT;
        }
        return v;
    }
}
