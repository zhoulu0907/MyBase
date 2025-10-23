package com.cmsr.onebase.module.formula.service.engine;

import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.dto.ContextData;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.permission.RoleApi;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公式引擎服务实现类
 * 基于GraalVM JavaScript引擎实现Excel公式计算功能
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Slf4j
@Service
public class FormulaEngineServiceImpl implements FormulaEngineService {

    private final FormulaEngineProperties properties;
    private final String formulaJsScript;
    private final Pattern dangerousPatternRegex;

    /**
     * 注入用户API
     */
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 注入用户API
     */
    @Resource
    private RoleApi RoleApi;

    /**
     * 注入部门API
     */
    @Resource
    private DeptApi deptApi;

    /**
     * 支持的Excel函数列表 - 基于FormulaJS v4.5.3完整函数库
     */
    private static final String[] SUPPORTED_FUNCTIONS = {
            // 文本函数
            "LEFT", "RIGHT", "MID", "LEN", "UPPER", "LOWER", "PROPER", "TRIM",
            "CONCATENATE", "FIND", "REPLACE", "SUBSTITUTE", "SEARCH", "EXACT",
            "CLEAN", "CODE", "CHAR", "REPT", "TEXT", "VALUE", "FIXED",

            // 数学和三角函数
            "SUM", "SUMIF", "SUMIFS", "AVERAGE", "AVERAGEIF", "AVERAGEIFS",
            "MAX", "MIN", "COUNT", "COUNTA", "COUNTIF", "COUNTIFS", "COUNTBLANK",
            "ROUND", "ROUNDUP", "ROUNDDOWN", "ABS", "POWER", "SQRT", "MOD",
            "CEILING", "FLOOR", "INT", "SIGN", "TRUNC", "EVEN", "ODD",
            "SIN", "COS", "TAN", "ASIN", "ACOS", "ATAN", "ATAN2",
            "SINH", "COSH", "TANH", "PI", "RADIANS", "DEGREES",
            "EXP", "LN", "LOG", "LOG10", "FACT", "COMBIN", "PERMUT",
            "GCD", "LCM", "RAND", "RANDBETWEEN",

            // 逻辑函数
            "IF", "AND", "OR", "NOT", "IFERROR", "IFNA", "TRUE", "FALSE",

            // 日期时间函数
            "TODAY", "NOW", "DATE", "TIME", "YEAR", "MONTH", "DAY",
            "HOUR", "MINUTE", "SECOND", "WEEKDAY", "WEEKNUM",
            "DATEDIF", "DATEVALUE", "TIMEVALUE", "DAYS", "DAYS360",
            "NETWORKDAYS", "WORKDAY", "EDATE", "EOMONTH",

            // 查找和引用函数
            "INDEX", "MATCH", "VLOOKUP", "HLOOKUP", "LOOKUP", "CHOOSE",
            "INDIRECT", "OFFSET", "ROW", "ROWS", "COLUMN", "COLUMNS",

            // 信息函数
            "ISNUMBER", "ISTEXT", "ISBLANK", "ISERROR", "ISNA", "ISLOGICAL",
            "ISEVEN", "ISODD", "TYPE", "N", "NA", "ERROR.TYPE",

            // 统计函数
            "MEDIAN", "MODE", "VAR", "VARP", "STDEV", "STDEVP",
            "QUARTILE", "PERCENTILE", "PERCENTRANK", "RANK",
            "LARGE", "SMALL", "FREQUENCY", "CORREL", "COVAR",

            // 财务函数
            "PV", "FV", "PMT", "RATE", "NPER", "NPV", "IRR",
            "CUMIPMT", "CUMPRINC", "IPMT", "PPMT", "EFFECT", "NOMINAL",

            // 工程函数
            "BIN2DEC", "BIN2HEX", "BIN2OCT", "DEC2BIN", "DEC2HEX", "DEC2OCT",
            "HEX2BIN", "HEX2DEC", "HEX2OCT", "OCT2BIN", "OCT2DEC", "OCT2HEX",
            "BITAND", "BITOR", "BITXOR", "BITLSHIFT", "BITRSHIFT",

            // 数据库函数
            "DGET", "DMAX", "DMIN", "DSUM", "DAVERAGE", "DCOUNT", "DCOUNTA",

            // 其他常用函数
            "TRANSPOSE", "UNIQUE", "SORT", "FILTER", "SUMPRODUCT"
    };

    /**
     * 危险函数和操作的正则表达式
     */
    private static final String DANGEROUS_PATTERNS =
            "(?i)(\\beval\\b|\\bFunction\\b|\\bnew\\s+Function\\b|\\bimport\\b|\\brequire\\b|" +
                    "\\bprocess\\b|\\bsetTimeout\\b|\\bsetInterval\\b|\\bsetImmediate\\b|" +
                    "\\b__proto__\\b|\\bconstructor\\b|\\bprototype\\b)";

    /**
     * 人员函数常量
     */
    private static final String GETUSER = "GETUSER";
    private static final String GETDEPT = "GETDEPT";
    private static final String GETUPDEPT = "GETUPDEPT";
    private static final String GETROLE = "GETROLE";
    private static final String GETSUPERVISOR = "GETSUPERVISOR";
    private static final String ISINROLE = "ISINROLE";
    private static final String ISINDEPT = "ISINDEPT";

    public FormulaEngineServiceImpl(FormulaEngineProperties properties) {
        this.properties = properties;
        this.dangerousPatternRegex = Pattern.compile(DANGEROUS_PATTERNS);

        // 加载Formula.js脚本
        this.formulaJsScript = loadFormulaJsScript();

        log.info("公式引擎服务初始化完成，安全模式：{}，超时时间：{}ms",
                properties.isSecurityMode(), properties.getTimeoutMs());
    }

    @Override
    @Cacheable(value = "formulaResults", key = "#formula", condition = "#result != null")
    public Object executeFormula(String formula) {
        return executeFormulaWithParams(formula, new HashMap<>());
    }


    @Override
    public Object executeFormulaWithParams(String formula, Map<String, Object> parameters) {

        if (!StringUtils.hasText(formula)) {
            throw new IllegalArgumentException("公式不能为空");
        }

        // 验证公式长度
        if (formula.length() > properties.getMaxFormulaLength()) {
            throw new IllegalArgumentException("公式长度超过最大限制：" + properties.getMaxFormulaLength());
        }

        // 安全性检查
        if (properties.isSecurityMode() && !validateFormulaSecurity(formula)) {
            throw new SecurityException("公式包含不安全的操作或函数");
        }


        // 创建GraalVM JavaScript上下文
        Context.Builder contextBuilder = Context.newBuilder("js")
                .allowHostAccess(HostAccess.CONSTRAINED)
                .allowPolyglotAccess(org.graalvm.polyglot.PolyglotAccess.NONE)
                .allowNativeAccess(false)
                .allowCreateThread(false)
                .allowIO(false)
                .allowEnvironmentAccess(org.graalvm.polyglot.EnvironmentAccess.NONE)
                // 禁用解释器模式警告
                .option("engine.WarnInterpreterOnly", "false");

        try (Context context = contextBuilder.build()) {
            // 设置执行超时时间
            if (properties.getTimeoutMs() > 0) {
                context.enter();
                try {
                    // 1. 首先创建module和exports环境
                    context.eval("js", "var module = { exports: {} }; var exports = module.exports;");

                    // 2. 加载FormulaJS库，函数将被导出到exports对象
                    context.eval("js", formulaJsScript);

                    // 3. 获取exports对象并将所有函数暴露到全局作用域
                    Value bindings = context.getBindings("js");
                    Value moduleObj = bindings.getMember("module");
                    Value exportsObj = moduleObj.getMember("exports");

                    // 4. 将exports中的所有函数暴露到全局作用域
                    if (exportsObj != null && exportsObj.hasMembers()) {
                        for (String memberName : exportsObj.getMemberKeys()) {
                            Value member = exportsObj.getMember(memberName);
                            if (member != null && member.canExecute()) {
                                bindings.putMember(memberName, member);
                            }
                        }
                    }


                    enrichParametersWithUserInfo(formula, parameters);

                    // 检查公式中是否包含$字符，如果包含则进行参数替换
                    if (formula.contains("$")) {
                        formula = replaceParametersInFormula(formula, parameters);
                    }

                    // 5. 注入参数
                    injectParameters(context, parameters);

                    // 6. 执行公式
                    Value formulaResult = context.eval("js", formula);

                    return convertResult(formulaResult);
                } finally {
                    context.leave();
                }
            } else {
                throw new IllegalStateException("超时时间配置无效");
            }
        } catch (PolyglotException e) {
            log.error("公式执行异常：{}, 公式：{}, 参数：{}", e.getMessage(), formula, parameters);
            throw new RuntimeException("公式执行失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("公式引擎执行异常", e);
            throw e;
        }
    }

    @Override
    public Object executeFormulaWithParamsForFlow(String formula, Map<String, Object> parameters, Map<String, Object> contextData) {
        formula = replaceParametersInFormula(formula,parameters);
        formula = resolveFormulaWithContextData(formula, contextData);
        return executeFormulaWithParams(formula, parameters);
    }

    @Override
    public Object executeFormulaWithParams(String formula, Map<String, Object> parameters, ContextData contextData) {
        // 从contextData解析占位符并替换为可执行的JS数组字面量，再复用已有方法执行
        String resolved = resolveFormulaWithContext(formula, contextData);
        return executeFormulaWithParams(resolved, parameters);
    }

    @Override
    public boolean validateFormula(String formula) {
        if (!StringUtils.hasText(formula)) {
            return false;
        }

        // 长度检查
        if (formula.length() > properties.getMaxFormulaLength()) {
            return false;
        }

        // 安全性检查
        if (properties.isSecurityMode() && !validateFormulaSecurity(formula)) {
            return false;
        }

        // 语法检查
        try {
            Context.Builder contextBuilder = Context.newBuilder("js")
                    .allowHostAccess(HostAccess.CONSTRAINED)
                    .allowPolyglotAccess(org.graalvm.polyglot.PolyglotAccess.NONE)
                    .allowNativeAccess(false)
                    .allowCreateThread(false)
                    .allowIO(false)
                    // 禁用解释器模式警告
                    .option("engine.WarnInterpreterOnly", "false");

            try (Context context = contextBuilder.build()) {
                // 使用与executeFormula相同的加载逻辑
                context.eval("js", "var module = { exports: {} }; var exports = module.exports;");
                context.eval("js", formulaJsScript);

                // 获取exports对象并将所有函数暴露到全局作用域
                Value bindings = context.getBindings("js");
                Value moduleObj = bindings.getMember("module");
                Value exportsObj = moduleObj.getMember("exports");

                if (exportsObj != null && exportsObj.hasMembers()) {
                    for (String memberName : exportsObj.getMemberKeys()) {
                        Value member = exportsObj.getMember(memberName);
                        if (member != null && member.canExecute()) {
                            bindings.putMember(memberName, member);
                        }
                    }
                }

                // 尝试执行公式
                context.eval("js", formula);
                return true;
            }
        } catch (Exception e) {
            log.debug("公式验证失败：{}, 公式：{}", e.getMessage(), formula);
            return false;
        }
    }

    @Override
    public String[] getSupportedFunctions() {
        return SUPPORTED_FUNCTIONS.clone();
    }

    /**
     * 加载Formula.js脚本
     *
     * @return JavaScript脚本内容
     */
    private String loadFormulaJsScript() {
        try {
            ClassPathResource resource = new ClassPathResource("js/formula.js");
            // ClassPathResource resource = new ClassPathResource("js/formula_test.js");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("加载Formula.js脚本失败", e);
            throw new RuntimeException("无法加载公式计算脚本", e);
        }
    }

    /**
     * 验证公式安全性
     *
     * @param formula 公式内容
     * @return 是否安全
     */
    private boolean validateFormulaSecurity(String formula) {
        // 检查是否包含危险的函数或操作
        if (dangerousPatternRegex.matcher(formula).find()) {
            log.warn("公式包含不安全的操作：{}", formula);
            return false;
        }

        // 检查是否包含恶意字符
        if (formula.contains("..") || formula.contains("//") ||
                formula.contains("\\") || formula.contains("file:")) {
            log.warn("公式包含可疑字符：{}", formula);
            return false;
        }

        return true;
    }

    /**
     * 将ContextData中的数据注入公式
     * 当前支持占位符：
     * - $recordList.fieldName  => 由ContextData.recordList提取该字段，替换为JS数组字面量，如 [1,2,"完成"]
     *
     * @param formula     原始公式
     * @param contextData 上下文数据
     * @return 已替换占位符的公式
     */
    private String resolveFormulaWithContext(String formula, ContextData contextData) {
        if (contextData == null || !StringUtils.hasText(formula)) {
            return formula;
        }
        // 仅支持解析 $recordList.xxx 模式
        Pattern p = Pattern.compile("\\$recordList\\.([a-zA-Z_][\\w]*)");
        Matcher m = p.matcher(formula);
        Set<String> fields = new LinkedHashSet<>();
        while (m.find()) {
            fields.add(m.group(1));
        }
        if (fields.isEmpty()) {
            return formula;
        }

        List<Map<String, Object>> recordList = contextData.getRecordList();
        if (recordList == null) {
            recordList = new ArrayList<>();
        }

        String resolved = formula;
        for (String field : fields) {
            List<Object> values = new ArrayList<>();
            for (Map<String, Object> rec : recordList) {
                Object v = rec != null ? rec.get(field) : null;
                values.add(v);
            }
            String arrayLiteral = toJsArrayLiteral(values);
            // 精确替换占位符
            resolved = resolved.replace("$recordList." + field, arrayLiteral);
        }
        return resolved;
    }

    /**
     * 新的解析方法，用于处理流程公式中的上下文数据
     * 该方法会将字段引用直接替换为值列表，例如将 COUNT(f_id) 转换为 COUNT(1,2,3)
     *
     * @param formula    原始公式
     * @param contextData 上下文数据
     * @return 解析替换后的公式
     */
    private String resolveFormulaWithContextData(String formula, Map<String, Object> contextData) {
        if (contextData == null || contextData.isEmpty()) {
            return formula;
        }

        String result = formula;

        // 遍历contextData中的每个键值对
        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            Object value = entry.getValue();

            // 如果值是List类型，处理数组数据
            if (value instanceof List) {
                List<?> listValue = (List<?>) value;

                // 处理字段引用，如 f_id
                for (Object item : listValue) {
                    if (item instanceof Map) {
                        Map<?, ?> mapItem = (Map<?, ?>) item;

                        // 遍历Map中的所有键，查找公式中匹配的字段名
                        for (Map.Entry<?, ?> mapEntry : mapItem.entrySet()) {
                            String fieldName = mapEntry.getKey().toString();

                            // 如果公式中包含字段名
                            if (result.contains(fieldName)) {
                                // 构造字段值列表
                                StringBuilder fieldValues = new StringBuilder();

                                // 收集所有元素中该字段的值
                                for (int i = 0; i < listValue.size(); i++) {
                                    Object listItem = listValue.get(i);
                                    if (listItem instanceof Map) {
                                        Map<?, ?> listItemMap = (Map<?, ?>) listItem;
                                        Object fieldValue = listItemMap.get(fieldName);

                                        if (i > 0) {
                                            fieldValues.append(",");
                                        }

                                        // 根据值的类型进行处理，数字和布尔值不加引号
                                        if (fieldValue instanceof String) {
                                            fieldValues.append("\"").append(fieldValue).append("\"");
                                        } else if (fieldValue instanceof Number || fieldValue instanceof Boolean) {
                                            fieldValues.append(fieldValue.toString());
                                        } else if (fieldValue == null) {
                                            fieldValues.append("null");
                                        } else {
                                            fieldValues.append(fieldValue.toString());
                                        }
                                    }
                                }

                                // 替换公式中的字段名为值列表
                                result = result.replace(fieldName, fieldValues.toString());
                            }
                        }
                        // 处理完第一个元素后跳出，因为我们假设所有元素具有相同的结构
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 将Java List转换为JS数组字面量字符串
     * 规则：
     * - 字符串自动加引号并转义
     * - 数字/布尔/空值保持本型
     */
    private String toJsArrayLiteral(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object v = list.get(i);
            if (v == null) {
                sb.append("null");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(String.valueOf(v));
            } else {
                // 统一按字符串处理并转义
                String s = String.valueOf(v);
                s = s.replace("\\", "\\\\").replace("\"", "\\\"");
                sb.append("\"").append(s).append("\"");
            }
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 注入参数到JavaScript上下文
     *
     * @param context    JavaScript上下文
     * @param parameters 参数映射
     */
    private void injectParameters(Context context, Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            Value bindings = context.getBindings("js");
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                bindings.putMember(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 包装公式执行，添加错误处理和结果返回
     *
     * @param formula 原始公式
     * @return 包装后的JavaScript代码
     */
    private String wrapFormulaExecution(String formula) {
        return String.format(
                "(function() { " +
                        "try { " +
                        "    // 创建module和exports对象来支持UMD模块加载" +
                        "    var module = { exports: {} }; " +
                        "    var exports = module.exports; " +
                        "    " +
                        "    // 重新执行FormulaJS库以获取exports对象" +
                        "    eval(arguments[0]); " +
                        "    " +
                        "    // 将所有函数暴露到全局作用域" +
                        "    for (var key in exports) { " +
                        "        if (typeof exports[key] === 'function') { " +
                        "            global[key] = exports[key]; " +
                        "        } " +
                        "    } " +
                        "    " +
                        "    // 执行用户公式" +
                        "    var result = %s; " +
                        "    return result; " +
                        "} catch (error) { " +
                        "    throw new Error('公式执行错误: ' + error.message); " +
                        "} " +
                        "})",
                formula
        );
    }

    /**
     * 转换JavaScript执行结果为Java对象
     *
     * @param result JavaScript执行结果
     * @return Java对象
     */
    private Object convertResult(Value result) {
        if (result == null || result.isNull()) {
            return null;
        }

        if (result.isBoolean()) {
            return result.asBoolean();
        }

        if (result.isNumber()) {
            if (result.fitsInDouble()) {
                return result.asDouble();
            }
            if (result.fitsInLong()) {
                return result.asLong();
            }
            if (result.fitsInInt()) {
                return result.asInt();
            }
        }

        if (result.isString()) {
            return result.asString();
        }

        // 对于其他类型，转换为字符串
        return result.toString();
    }

    /**
     * 根据公式类型丰富参数，注入用户、部门等信息
     *
     * @param formula    公式
     * @param parameters 参数映射
     */
    private void enrichParametersWithUserInfo(String formula, Map<String, Object> parameters) {
        // 获取当前登录用户信息
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return;
        }

        // 检查公式是否包含用户相关函数
        if (formula.contains(GETUSER)) {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
                if (user != null) {
                    parameters.put("id", user.getId());
                    parameters.put("name", user.getNickname());
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败，loginUserId: {}", loginUserId, e);
                // 即使获取用户信息失败，也要确保id和name变量在JS环境中存在默认值
                parameters.putIfAbsent("id", 0L);
                parameters.putIfAbsent("name", "");
            }
            return;
        }

        // 检查公式是否包含部门相关函数
        if (formula.contains(GETDEPT)) {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
                if (user != null && user.getDeptId() != null) {
                    DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();
                    if (dept != null) {
                        parameters.put("deptId", dept.getId());
                        parameters.put("name", dept.getName());
                    }
                }
            } catch (Exception e) {
                log.warn("获取部门信息失败，loginUserId: {}", loginUserId, e);
                parameters.putIfAbsent("deptId", 0L);
                parameters.putIfAbsent("name", "");
            }
            return;
        }

        // 检查公式是否包含上级部门相关函数
        if (formula.contains(GETUPDEPT)) {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
                if (user != null && user.getDeptId() != null) {
                    DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();
                    if (dept != null && dept.getParentId() != null) {
                        DeptRespDTO parentDept = deptApi.getDept(dept.getParentId()).getCheckedData();
                        if (parentDept != null) {
                            parameters.put("parentDeptId", parentDept.getId());
                            parameters.put("parentDeptName", parentDept.getName());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("获取上级部门信息失败，loginUserId: {}", loginUserId, e);
                parameters.putIfAbsent("parentDeptId", 0L);
                parameters.putIfAbsent("parentDeptName", "");
            }
            return;
        }

        // 检查公式是否包含角色相关函数
        if (formula.contains(GETROLE)) {
            // 角色信息已在PermissionService中处理，此处可扩展具体实现
        }

        // 检查公式是否包含直属上级相关函数
        if (formula.contains(GETSUPERVISOR)) {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(loginUserId).getCheckedData();
                if (user != null && user.getDeptId() != null) {
                    DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();
                    if (dept != null && dept.getLeaderUserId() != null) {
                        AdminUserRespDTO supervisor = adminUserApi.getUser(dept.getLeaderUserId()).getCheckedData();
                        if (supervisor != null) {
                            parameters.put("supervisorId", supervisor.getId());
                            parameters.put("supervisorName", supervisor.getNickname());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("获取直属上级信息失败，loginUserId: {}", loginUserId, e);
                parameters.putIfAbsent("supervisorId", 0L);
                parameters.putIfAbsent("supervisorName", "");
            }
            return;
        }
    }

    /**
     * 替换公式中的参数占位符
     *
     * @param formula    包含$占位符的公式
     * @param parameters 参数映射
     * @return 替换占位符后的新公式
     */
    private String replaceParametersInFormula(String formula, Map<String, Object> parameters) {

        String result = formula;

        // 按参数名长度降序排列，确保长参数名优先匹配
        List<Map.Entry<String, Object>> sortedParameters = new ArrayList<>(parameters.entrySet());
        sortedParameters.sort((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()));

        // 处理复杂参数名（包含点号等特殊字符）
        for (Map.Entry<String, Object> entry : sortedParameters) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 如果参数键包含在公式中
            if (result.contains(key)) {
                String replacement;
                // 根据参数值的类型进行适当格式化
                if (value instanceof String) {
                    // 检查这个参数是否是字段引用（不带引号），还是普通字符串值（带引号）
                    // 如果key中包含"."，则认为是字段引用，不需要加引号
                    if (key.contains(".")) {
                        replacement = value.toString();
                    } else {
                        // 普通字符串值需要加引号
                        replacement = "\"" + value.toString() + "\"";
                    }
                } else if (value instanceof Number || value instanceof Boolean) {
                    // 数字和布尔值直接转换为字符串
                    replacement = value.toString();
                } else if (value == null) {
                    // null值替换为JavaScript的null
                    replacement = "null";
                } else {
                    // 其他类型如果是字段引用则不加引号，否则加引号
                    if (key.contains(".")) {
                        replacement = value.toString();
                    } else {
                        replacement = "\"" + value.toString() + "\"";
                    }
                }

                // 替换公式中的参数
                result = result.replace(key, replacement);
            }
        }

        return result;
    }
}