package com.cmsr.onebase.module.bpm.core.expression;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.edge.condition.BpmConditionItem;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.core.jsonconvert.FieldTypeConvertor;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.formula.api.formula.FormulaEngineApi;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.type.UserRefType;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.MapUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.strategy.ConditionStrategy;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.strategy.ExpressionStrategy;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 自定义 OneBase 条件表达式解析策略，解析流程设计器保存的 JSON 条件串。
 * <p>
 * 设计器会把条件保存为 `List<List<BpmConditionItem>>` 的 JSON 字符串，其中：
 *   - 外层集合表示 OR 关系
 *   - 内层集合表示 AND 关系
 * 本策略负责解析并根据当前变量(Map<String,Object>) 对条件进行计算，返回布尔结果。
 *
 * @author cascade
 */
public class ConditionStrategyOb implements ConditionStrategy {

    private static final class EvalContext {
        private final Map<String, Object> variable;
        private SemanticEntityValueDTO entity;
        private Map<String, Object> instanceMap;
        private Map<String, Object> preNodeMap;

        private EvalContext(Map<String, Object> variable) {
            this.variable = variable;
        }

        private SemanticEntityValueDTO getOrBuildEntity(ConditionStrategyOb owner) {
            if (entity == null) {
                entity = owner.getOrBuildEntityMap(variable);
            }
            return entity;
        }

        private Map<String, Object> getOrBuildInstanceMap(ConditionStrategyOb owner) {
            if (instanceMap == null) {
                instanceMap = owner.getOrBuildInstanceMap(variable);
            }
            return instanceMap;
        }

        private Map<String, Object> getOrBuildPreNodeMap(ConditionStrategyOb owner) {
            if (preNodeMap == null) {
                preNodeMap = owner.getOrBuildPreNodeMap(variable);
            }
            return preNodeMap;
        }
    }

    @Override
    public String getType() {
        return "ob";
    }

    /**
     * 计算条件表达式是否成立。
     * <p>
     * expression 是设计器保存的 JSON 条件串（结构为 List&lt;List&lt;BpmConditionItem&gt;&gt;）：
     * - 外层：OR
     * - 内层：AND
     * variable 为流程运行时上下文变量（包含实体数据、实例数据等）。
     *
     * @param expression JSON 条件表达式
     * @param variable   流程上下文变量
     * @return 任意一组 AND 条件成立则返回 true；解析失败或不成立返回 false
     */
    @Override
    public Boolean eval(String expression, Map<String, Object> variable) {
        if (StringUtils.isEmpty(expression)) {
            return false;
        }
        expression = expression.trim();
        List<List<BpmConditionItem>> orList = JsonUtils.parseObject(
                expression,
                new TypeReference<List<List<BpmConditionItem>>>() {}
        );

        EvalContext ctx = new EvalContext(variable);

        for (List<BpmConditionItem> andList : orList) {
            if (evaluateAndList(ctx, andList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算一组 AND 条件。
     *
     * @param andList   同一组内的条件（AND 关系）
     * @param variable  流程上下文变量
     * @return 组内所有条件都成立返回 true，否则 false
     */
    private boolean evaluateAndList(EvalContext ctx, List<BpmConditionItem> andList) {
        for (BpmConditionItem cond : andList) {
            if (!evaluateItem(ctx, cond)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算单个条件项。
     * <p>
     * 负责：
     * - 解析运算符/右值类型
     * - 取左值（来自实体/实例/前置节点/变量）
     * - 取右值（静态值/变量/公式）
     * - 按字段类型做比较
     */
    private boolean evaluateItem(EvalContext ctx, BpmConditionItem cond) {
        if (cond == null) {
            return false;
        }

        String fieldScope = cond.getFieldScope();
        String fieldName = cond.getFieldName();
        OpEnum op = OpEnum.getByName(cond.getOp());
        OperatorTypeEnum  operatorType = OperatorTypeEnum.getByCode(cond.getOperatorType());

        String fieldType = cond.getFieldType();

        Object leftVal = resolveLeftValue(ctx, fieldScope, fieldName);
        Object rightVal = resolveRightValue(ctx, fieldType, op, operatorType, cond.getValue());

        return compareByFieldType(leftVal, rightVal, op, fieldType);
    }

    /**
     * 根据字段作用域解析左值。
     * <p>
     * 支持作用域：
     * - ENTITY：实体数据（通过语义动态数据接口获取）
     * - INSTANCE：流程实例扩展数据
     * - PRE_NODE：前置节点（最近一次历史任务）数据
     * - 其他：直接从 variable 取
     */
    private Object resolveLeftValue(EvalContext ctx, String fieldScope, String fieldName) {
        Map<String, Object> variable = ctx.variable;
        // 表单属性
        if (FieldScopeEnum.ENTITY.getCode().equalsIgnoreCase(fieldScope)) {
            SemanticEntityValueDTO entity = ctx.getOrBuildEntity(this);
            if (entity == null || entity.getGlobalRawMap() == null) {
                return null;
            }
            Map<String, Object> entityMap = entity.getGlobalRawMap();
            if (entityMap != null) {
                Object v = entityMap.get(fieldName);
                // 如果是系统字段UserRefType，不能直接返回
                if (v instanceof UserRefType){
                    return ((UserRefType) v).getId();
                }
                if (v != null) {
                    return v;
                }
            }
            return variable.get(fieldName);
        }
        // 当前实例属性
        if (FieldScopeEnum.INSTANCE.getCode().equalsIgnoreCase(fieldScope)) {
            Map<String, Object> instanceMap = ctx.getOrBuildInstanceMap(this);
            if (instanceMap != null) {
                Object v = instanceMap.get(fieldName);
                if (v != null) {
                    return v;
                }
            }
            return variable.get(fieldName);
        }
        // 如果类型选择是上一节点
        if (FieldScopeEnum.PRE_NODE.getCode().equalsIgnoreCase(fieldScope)) {
            Map<String, Object> preNodeMap = ctx.getOrBuildPreNodeMap(this);
            if (preNodeMap != null) {
                Object v = preNodeMap.get(fieldName);
                if (v != null) {
                    return v;
                }
            }
            return variable.get(fieldName);
        }

        return variable.get(fieldName);
    }

    /**
     * 获取或构建实体数据映射。
     * <p>
     * 从上下文变量中取出表名和实体数据 ID，调用语义动态数据接口获取完整实体数据。
     */
    private SemanticEntityValueDTO getOrBuildEntityMap(Map<String, Object> variable) {

        Object tableNameObj = firstNotNull(variable.get(BpmConstants.VAR_ENTITY_TABLE_NAME_KEY));
        // 提交的时候存放的是entityDataId 审核的时候是便利数据，所以放的是id
        Object entityDataIdObj = firstNotNull(variable.get(BpmConstants.VAR_ENTITY_DATA_ID_KEY), variable.get(BpmConstants.VAR_ENTITY_DATA_ID));
        if (tableNameObj == null || entityDataIdObj == null) {
            return null;
        }

        SemanticDynamicDataApi semanticDynamicDataApi = FrameInvoker.getBean(SemanticDynamicDataApi.class);
        if (semanticDynamicDataApi == null) {
            return null;
        }

        SemanticTargetBodyVO reqVO = new SemanticTargetBodyVO();
        String traceId = UuidUtils.getUuid();
        reqVO.setTableName(String.valueOf(tableNameObj));
        reqVO.setId(String.valueOf(entityDataIdObj));
        reqVO.setTraceId(traceId);
        SemanticEntityValueDTO entityValueDTO;
        try {
            entityValueDTO = semanticDynamicDataApi.getDataById(reqVO);
        } catch (Exception e) {
            return null;
        }
        return entityValueDTO;
    }

    /**
     * 获取或构建流程实例扩展数据映射。
     * <p>
     * 包含标题、发起人、发起部门、提交时间、创建时间、更新时间等。
     */
    private Map<String, Object> getOrBuildInstanceMap(Map<String, Object> variable) {

        Long instanceId = toLong(variable.get(BpmConstants.VAR_INSTANCE_ID_KEY));


        BpmFlowInsBizExtDO ext = null;
        BpmFlowInsBizExtRepository repo = FrameInvoker.getBean(BpmFlowInsBizExtRepository.class);
        if (repo != null) {
            if (instanceId != null) {
                ext = repo.findOneByInstanceId(instanceId);
            } else {
                Object entityDataId = firstNotNull(variable.get(BpmConstants.VAR_ENTITY_DATA_ID_KEY));
                if (entityDataId != null) {
                    ext = repo.findOneByBusinessDataId(String.valueOf(entityDataId));
                }
            }
        }
        Instance instance = FlowEngine.insService().getById(instanceId);
        if (ext == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(InstanceEnum.BPMT_ITLE.getCode(), ext.getBpmTitle());
        map.put(InstanceEnum.INITIATOR_ID.getCode(), ext.getInitiatorId());
        map.put(InstanceEnum.INITIATOR_DEPT_ID.getCode(), ext.getInitiatorDeptId());
        map.put(InstanceEnum.SUBMIT_TIME.getCode(), ext.getSubmitTime());
        LocalDateTime createTime = instance.getCreateTime();
        map.put(InstanceEnum.CREATE_TIME.getCode(), createTime);
        // 更新时间ext表没有更新
        LocalDateTime updateTime = instance.getUpdateTime();
        map.put(InstanceEnum.UPDATE_TIME.getCode(), updateTime);

        return map;
    }

    /**
     * 获取或构建“前置节点”数据映射。
     * <p>
     * 通过实例 ID 获取历史任务列表，取最近一次任务作为“上一节点”，并输出其审批结果/审批人/审批时间等字段。
     */
    private Map<String, Object> getOrBuildPreNodeMap(Map<String, Object> variable) {

//        Long instanceId = toLong(variable.get(BpmConstants.VAR_INSTANCE_ID_KEY));
//        if (instanceId == null) {
//            return null;
//        }
//        Instance instance = FlowEngine.insService().getById(instanceId);
//        if (instance == null) {
//            return null;
//        }
//
//        List<HisTask> hisTasks = FlowEngine.hisTaskService().getByInsId(instanceId);
//
//        if (hisTasks == null || hisTasks.isEmpty()) {
//            return null;
//        }
//        if (hisTasks != null && !hisTasks.isEmpty()) {
//            hisTasks = hisTasks.stream()
//                    .filter(Objects::nonNull)
//                    .sorted(Comparator.comparing(HisTask::getCreateTime,
//                            Comparator.nullsLast(Comparator.naturalOrder())).reversed())
//                    .toList();
//        }
//        HisTask last = null;
//        for(HisTask his : hisTasks){
//            BaseNodeExtDTO nodeExt = BpmUtil.getNodeExtDTOByNodeCode(his.getNodeCode(), instance.getDefJson());
//            if (nodeExt != null && BpmNodeTypeEnum.APPROVER.getCode().equalsIgnoreCase(nodeExt.getNodeType())
//                    // 如果hisTask的node_code 和instance的 NodeCode相同，说明是并行节点
//                    && !his.getNodeCode().equals(instance.getNodeCode())) {
//                last = his;
//                break;
//            }
//        }
//        if (null == last ){
//            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
//        }

        Map<String, Object> map = new HashMap<>();
        map.put(PreNodeEnum.APPROVAL_RESULT.getCode(), variable.get(BpmConstants.VAR_FLOW_STATUS_KEY));
        map.put(PreNodeEnum.APPROVER_ID.getCode(), variable.get(BpmConstants.VAR_HANDLER_KEY));
        map.put(PreNodeEnum.APPROVAL_TIME.getCode(), LocalDateTime.now());

        Long deptId = tryExtractApproverDeptId(String.valueOf(variable.get(BpmConstants.VAR_HANDLER_KEY)));



        if (deptId != null) {
            map.put(PreNodeEnum.APPROVER_DEPT_ID.getCode(), deptId);
        }

        return map;
    }

    /**
     * 尝试从审批人 ID 提取其部门 ID。
     * <p>
     * 调用用户接口获取用户详情，返回部门 ID；失败则返回 null。
     */
    private Long tryExtractApproverDeptId(String approverIdStr) {
        Long approverId = toLong(approverIdStr);
        if (approverId != null) {
            AdminUserApi adminUserApi = FrameInvoker.getBean(AdminUserApi.class);
            if (adminUserApi != null) {
                AdminUserRespDTO user = adminUserApi.getUser(approverId).getCheckedData();
                return user.getDeptId();
            }
        }

        return null;
    }

    /**
     * 解析右值。
     * <p>
     * 右值来源由 {@link OperatorTypeEnum} 决定：
     * - VALUE：静态值，直接使用；日期范围会被归一化为 [begin,end]
     * - VARIABLE：引用表单/实体字段（rawRight 通常是字段 uuid），从实体数据中取值
     * - FORMULA：公式，调用公式引擎计算并按字段类型进行转换
     */
    private Object resolveRightValue(EvalContext ctx, String fieldType, OpEnum op, OperatorTypeEnum operatorType, Object rawRight) {
        // 如果是静态值就是存什么取什么
        if (operatorType == OperatorTypeEnum.VALUE) {
            if (op == OpEnum.RANGE
                    && (fieldType == SemanticFieldTypeEnum.DATE.getCode() || fieldType == SemanticFieldTypeEnum.DATETIME.getCode())
                    && rawRight instanceof Map) {
                String begin = MapUtils.getString((Map) rawRight, BpmConstants.VAR_BEGIN_KEY);
                String end = MapUtils.getString((Map) rawRight, BpmConstants.VAR_END_KEY);
                return List.of(begin, end);
            }
            return rawRight;
        }
        // 如果是变量就是表单的数据
        if (operatorType == OperatorTypeEnum.VARIABLE) {
            SemanticEntityValueDTO entity = ctx.getOrBuildEntity(this);
            if (entity == null || entity.getGlobalRawMap() == null) {
                return null;
            }
            Map<String, Object> entityMap = entity.getGlobalRawMap();
            // todo:如果是多选数据，这里需要改
//            SemanticFieldValueDTO<Object> fieldValueByUuid = entity.getFieldValueByUuid((String) rawRight);
//            if (fieldValueByUuid == null || fieldValueByUuid.getFieldName() == null) {
//                return null;
//            }
            if (entityMap != null) {
                Object v = entityMap.get(rawRight);
                // 如果是系统字段UserRefType，不能直接返回
                if (v instanceof UserRefType){
                    return ((UserRefType) v).getId();
                }
                if (v != null) {
                    return v;
                }
            }
            return null;
        }
        // 如果是公式
        if (operatorType == OperatorTypeEnum.FORMULA) {
            Map<String, Object> variable = ctx.variable;
            SemanticEntityValueDTO entity = ctx.getOrBuildEntity(this);
            if (entity == null) {
                return null;
            }
            Map<String, Object> currentEntityRawMap = entity.getCurrentEntityRawMap();
            String formula = MapUtils.getString((Map)rawRight, BpmConstants.VAR_FORMULA_KEY);
            Map parameters = MapUtils.getMap((Map)rawRight, BpmConstants.VAR_PARAMETERS_KEY);
            parameters.forEach((key, value) -> {
                String fieldName = String.valueOf(value);
                if (StringUtils.isNotEmpty(fieldName)) {
                    // 从实体数据 Map 中按字段名取值
                    Object actualVal = currentEntityRawMap.get(fieldName);
                    // 处理日期格式化
                    actualVal = dateFormat(actualVal);
                    parameters.put(key, actualVal);
                }
            });
            FormulaExecuteReqDTO reqDTO = new FormulaExecuteReqDTO();
            reqDTO.setFormula(formula);
            reqDTO.setParameters(parameters);
            reqDTO.setContextData(variable);
            FormulaEngineApi formulaEngineApi = FrameInvoker.getBean(FormulaEngineApi.class);
            CommonResult<FormulaExecuteRespDTO> respDTO = formulaEngineApi.executeFormula(reqDTO);
            if (respDTO.getData() == null) {
                throw new IllegalCallerException("调用公式错误: " + reqDTO.getFormula() + ", 错误信息: " + respDTO.getMsg());
            }
            Object result = respDTO.getData().getResult();
            if (StringUtils.isNotEmpty(fieldType)) {
                return FieldTypeConvertor.convert(SemanticFieldTypeEnum.ofCode(fieldType), result);
            } else {
                return result;
            }
        }
        return null;
    }

    //如果是时间格式转一下
    private static Object dateFormat(Object actualVal) {
        if (actualVal instanceof LocalDateTime ldt) {
            actualVal = ldt.format(DateTimeFormatter.ofPattern(com.cmsr.onebase.framework.uid.utils.DateUtils.DATETIME_PATTERN));
        } else if (actualVal instanceof Date d) {
            actualVal = com.cmsr.onebase.framework.uid.utils.DateUtils.formatDate(d, com.cmsr.onebase.framework.uid.utils.DateUtils.DATETIME_PATTERN);
        } else if (actualVal instanceof java.time.LocalDate ld) {
            actualVal = ld.format(DateTimeFormatter.ofPattern(com.cmsr.onebase.framework.uid.utils.DateUtils.DAY_PATTERN));
        }
        return actualVal;
    }

    /**
     * 按字段类型分发比较逻辑。
     * <p>
     * - IS_EMPTY/IS_NOT_EMPTY 统一处理
     * - 日期/时间字段走 compareDateTime
     * - 其他走 compareGeneric
     */
    private boolean compareByFieldType(Object left, Object right, OpEnum op, String fieldType) {
        // 是否为空全部都有，所以放在这里统一校验
        if (op == OpEnum.IS_EMPTY) {
            return isEmpty(left);
        }
        if (op == OpEnum.IS_NOT_EMPTY) {
            return !isEmpty(left);
        }

        boolean dateTime = isDateTimeField(fieldType);
        if (dateTime) {
            return compareDateTime(left, right, op, fieldType);
        }
        return compareGeneric(left, right, op);
    }

    /**
     * 通用比较逻辑（非日期类型）。
     * <p>
     * 支持：相等、不等、包含、存在性、数值比较、集合包含/不包含、范围等。
     */
    private boolean compareGeneric(Object left, Object right, OpEnum op) {
        switch (op) {
            case EQUALS:
                return Objects.equals(left, right);
            case NOT_EQUALS:
                return !Objects.equals(left, right);
            case CONTAINS:
                return containsAnyDirection(left, right);
            case NOT_CONTAINS:
                return !containsAnyDirection(left, right);
            case EXISTS_IN:
                return existsIn(left, right);
            case NOT_EXISTS_IN:
                return !existsIn(left, right);
            case GREATER_THAN:
                return toBigDecimal(left).compareTo(toBigDecimal(right)) > 0;
            case GREATER_EQUALS:
                return toBigDecimal(left).compareTo(toBigDecimal(right)) >= 0;
            case LESS_THAN:
                return toBigDecimal(left).compareTo(toBigDecimal(right)) < 0;
            case LESS_EQUALS:
                return toBigDecimal(left).compareTo(toBigDecimal(right)) <= 0;
            case CONTAINS_ALL:
                return containsAll(left, right);
            case NOT_CONTAINS_ALL:
                return !containsAll(left, right);
            case CONTAINS_ANY:
                return containsAny(left, right);
            case NOT_CONTAINS_ANY:
                return !containsAny(left, right);
            case RANGE: {
                if (left == null || right == null) {
                    return false;
                }

                Object startO = null;
                Object endO = null;

                if (right instanceof List<?> list && list.size() == 2) {
                    startO = list.get(0);
                    endO = list.get(1);
                }

                if (startO == null || endO == null) {
                    return false;
                }

                BigDecimal leftBd = toBigDecimal(left);
                BigDecimal startBd = toBigDecimal(startO);
                BigDecimal endBd = toBigDecimal(endO);
                if (startBd.compareTo(endBd) > 0) {
                    BigDecimal tmp = startBd;
                    startBd = endBd;
                    endBd = tmp;
                }
                return leftBd.compareTo(startBd) >= 0 && leftBd.compareTo(endBd) <= 0;
            }
            default:
                return false;
        }
    }

    /**
     * 日期/时间字段比较。
     * <p>
     * 左右值会先转换为时间戳（毫秒）。
     * - RANGE：右值为 [begin,end] 的日期字符串列表，使用当天起止时间戳做闭区间比较
     * - LATER_THAN / EARLIER_THAN：比较时间先后
     * - EQUALS：按“日期”粒度比较（忽略时分秒）
     */
    private boolean compareDateTime(Object left, Object right, OpEnum op, String fieldType) {
        Long leftMs = toEpochMillis(left);
        if (leftMs == null) {
            return false;
        }

        // 范围校验提前，减少重复代码
        if (op == OpEnum.RANGE) {
            Long[] range = toRangeMillis(right);
            if (range == null) {
                return false;
            }
            return leftMs >= range[0] && leftMs <= range[1];
        }

        Long rightMs = toEpochMillis(right);
        if (rightMs == null) {
            return false;
        }

        switch (op) {
            case LATER_THAN:
                // 如果是时间格式，需要大于今天的最后一秒
                if (fieldType.equalsIgnoreCase(SemanticFieldTypeEnum.DATE.getCode())){
                    return leftMs > DateUtils.getEndOfDayTimestamp1(rightMs);
                }
                return leftMs > rightMs;
            case EARLIER_THAN:
                return leftMs < rightMs;
            case EQUALS:
                java.time.LocalDate leftDate = java.time.Instant.ofEpochMilli(leftMs).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalDate rightDate = java.time.Instant.ofEpochMilli(rightMs).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                return Objects.equals(leftDate, rightDate);
            default:
                return false;
        }
    }

    /**
     * 判断字段类型是否为日期/时间类型。
     */
    private boolean isDateTimeField(String fieldType) {
        if (fieldType == null) {
            return false;
        }
        String t = fieldType.trim().toLowerCase(Locale.ROOT);
        return fieldType.equalsIgnoreCase(SemanticFieldTypeEnum.DATE.getCode()) || fieldType.equalsIgnoreCase(SemanticFieldTypeEnum.DATETIME.getCode());
    }

    /**
     * “包含”判断（双向兜底）。
     * <p>
     * - 若 left 是集合：判断集合是否包含 right
     * - 否则转为字符串，判断 left 是否包含 right 或 right 是否包含 left
     */
    private boolean containsAnyDirection(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if (left instanceof Collection<?> lc) {
            return lc.contains(right);
        }
        String ls = String.valueOf(left);
        String rs = String.valueOf(right);
        return ls.contains(rs) || rs.contains(ls);
    }

    /**
     * 判断 left 是否存在于 right 中。
     * <p>
     * right 支持：
     * - Collection：直接 contains
     * - String：按英文逗号分隔后逐个匹配（trim 后与 left 的字符串值比较）
     */
    private boolean existsIn(Object left, Object right) {
        if (right == null) {
            return false;
        }
        Collection<?> rightCol = toCollection(right);
        if (rightCol != null) {
            return rightCol.contains(left);
        }
        String s = String.valueOf(right);
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        String[] parts = s.split(",");
        for (String part : parts) {
            if (Objects.equals(String.valueOf(left), part.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 left 集合是否包含 right 集合的全部元素。
     */
    private boolean containsAll(Object left, Object right) {
        Collection<?> leftCol = toCollection(left);
        Collection<?> rightCol = toCollection(right);
        if (leftCol == null || rightCol == null) {
            return false;
        }
        return leftCol.containsAll(rightCol);
    }

    /**
     * 判断 left 集合是否包含 right 集合的任意元素。
     */
    private boolean containsAny(Object left, Object right) {
        Collection<?> leftCol = toCollection(left);
        Collection<?> rightCol = toCollection(right);
        if (leftCol == null || rightCol == null) {
            return false;
        }
        for (Object r : rightCol) {
            if (leftCol.contains(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判空工具：支持 null/Collection/Map。
     */
    private boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Collection<?> c) {
            return c.isEmpty();
        }
        if (o instanceof Map<?, ?> m) {
            return m.isEmpty();
        }
        return false;
    }

    /**
     * 将对象转换为 BigDecimal。
     * <p>
     * - null/解析失败：返回 0
     * - Number：使用其字符串形式构造，避免精度丢失
     */
    private BigDecimal toBigDecimal(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        }
        if (o instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        try {
            return new BigDecimal(String.valueOf(o));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 将对象尽量转换为集合。
     * <p>
     * 支持：
     * - Collection：原样返回
     * - Array：转为 List
     */
    private Collection<?> toCollection(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Collection<?> c) {
            return c;
        }
        if (o.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(o);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                list.add(java.lang.reflect.Array.get(o, i));
            }
            return list;
        }
        return null;
    }

    /**
     * 从参数列表中返回第一个非 null 的值。
     */
    private Object firstNotNull(Object... objs) {
        if (objs == null) {
            return null;
        }
        for (Object o : objs) {
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    /**
     * 将对象转换为 Long（允许 String/Number）。
     *
     * @return 转换失败返回 null
     */
    private Long toLong(Object o) {
        if (o == null) {
            return null;
        }
        try {
            String s = String.valueOf(o).trim();
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将多种日期类型转换为毫秒时间戳。
     * <p>
     * 支持：
     * - {@link Date}
     * - {@link LocalDateTime}
     * - {@link java.time.LocalDate}（按当天 00:00）
     * - 字符串 yyyy-MM-dd
     */
    private Long toEpochMillis(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date d) {
            return d.getTime();
        }
        if (o instanceof LocalDateTime ldt) {
            return ldt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        if (o instanceof java.time.LocalDate ld) {
            return ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        // 支持仅日期格式：yyyy-MM-dd，例如 2026-02-01
        String s = String.valueOf(o).trim();
        try {
            java.time.LocalDate ld = java.time.LocalDate.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            return ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 将右值范围转换为 [start,end] 毫秒时间戳。
     * <p>
     * 当前支持的 right 格式：List(2) 且元素为 yyyy-MM-dd 字符串。
     */
    private Long[] toRangeMillis(Object right) {
        if (right == null) {
            return null;
        }
        if (right instanceof List<?> list && list.size() == 2) {
            Long start = DateUtils.getStartTimestampOfDate((String)list.get(0), DateUtils.FORMAT_YEAR_MONTH_DAY);
            Long end = DateUtils.getEndTimestampOfDate((String)list.get(1), DateUtils.FORMAT_YEAR_MONTH_DAY);
            if (start == null || end == null) {
                return null;
            }
            return new Long[]{start, end};
        }
        return null;
    }

    @Override
    public void setExpression(ExpressionStrategy<Boolean> expressionStrategy) {
        ConditionStrategy.EXPRESSION_STRATEGY_LIST.add(expressionStrategy);
    }
}
