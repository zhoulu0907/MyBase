package com.cmsr.onebase.module.formula.service.engine;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.extendsion.FormulaExtendsService;
import com.cmsr.onebase.module.formula.util.FormulaValidate;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cmsr.onebase.module.formula.enums.FormulaConstants.DANGEROUS_PATTERNS;
import static com.cmsr.onebase.module.formula.enums.FormulaConstants.SUPPORTED_FUNCTIONS;

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

    @Resource
    private FormulaExtendsService formulaExtendsService;

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

        // 检查的函数是否存在，函数参数个数，参数类型，并返回报错提示
        FormulaValidate.validateSupportedFunctions(formula);
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


                    formulaExtendsService.buildParametersWithSystemInfo(formula, parameters);

                    // 检查公式中是否包含$字符，如果包含则进行参数替换
                    if (formula.contains("$")) {
                        formula = processFormulaParameters(formula, parameters);
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
        formula = handleFormulaParameters(formula, parameters);
        formula = handleFormulaContextData(formula, contextData);
        return executeFormulaWithParams(formula, parameters);
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

        // 检查是否包含恶意字符 （部分字符串函数传入正则表达式，需放过这些字符校验）
        // if (formula.contains("..") || formula.contains("//") ||
        //         formula.contains("\\") || formula.contains("file:")) {
        //     log.warn("公式包含可疑字符：{}", formula);
        //     return false;
        // }

        return true;
    }

    /**
     * 将Java对象转换为JS字面量字符串
     * - 字符串自动加引号并转义
     * - 数字/布尔/空值保持本型
     *
     * @param v 值
     * @return JS字面量字符串
     */
    private String toJsLiteral(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof Number || v instanceof Boolean) {
            return String.valueOf(v);
        }
        String s = String.valueOf(v);
        s = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + s + "\"";
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
            // 优先检查是否适合long类型（整数）
            if (result.fitsInLong() || result.fitsInInt()) {
                return result.asLong();
            }
            // 然后检查是否适合double类型（小数）
            if (result.fitsInDouble() || result.fitsInFloat()) {
                return result.asDouble();
            }
            return result.asDouble();
        }

        if (result.isDate()) {
            return LocalDateTime.parse(result.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        }
        
        // 处理JavaScript数组
        if (result.hasArrayElements()) {
            List<Object> resultList = new ArrayList<>();
            long arraySize = result.getArraySize();
            for (long i = 0; i < arraySize; i++) {
                Value element = result.getArrayElement(i);
                resultList.add(convertResult(element)); // 递归转换数组元素
            }
            return resultList;
        }
        // 对于JavaScript对象，检查是否包含value属性，如果包含则返回value的值
        if (result.hasMembers()) {
            // 如果没有value属性，按原逻辑处理
            Map<String, Object> resultMap = new HashMap<>();
            for (String key : result.getMemberKeys()) {
                Value member = result.getMember(key);
                resultMap.put(key, convertResult(member)); // 递归转换成员
            }
            return resultMap;
        }
        if (result.isString()&&result.asString().startsWith("[{")) {
            return JsonUtils.parseObject(result.toString(), new TypeReference<List<Map<String, Object>>>() {});
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


    /**
     * 替换公式中的参数占位符，例如：
     * {
     * "formula": "COUNT($数据查询节点(多条).关联主表ID)",
     * "parameters":
     * {
     * "$数据查询节点(多条)": "r_id",
     * "$数据查询节点(多条).关联主表ID": "f_id"
     * }
     * }
     * 执行完成后 "formula": "COUNT($r_id.f_id)",
     *
     * @param formula    包含$占位符的公式
     * @param parameters 参数映射
     * @return 替换占位符后的新公式
     */
    private String handleFormulaParameters(String formula, Map<String, Object> parameters) {
        if (!StringUtils.hasText(formula) || parameters == null || parameters.isEmpty()) {
            return formula;
        }

        String result = formula;

        // 收集所有以$开头的键，并按长度倒序，避免短键先替换污染长键
        List<String> keys = new ArrayList<>();
        for (String key : parameters.keySet()) {
            if (key != null && key.startsWith("$")) {
                keys.add(key);
            }
        }
        if (keys.isEmpty()) {
            return result;
        }
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String key : keys) {
            Object valObj = parameters.get(key);
            if (valObj == null) {
                continue;
            }
            String val = String.valueOf(valObj).trim();
            if (val.isEmpty()) {
                continue;
            }

            String replacement;
            int dotIdx = key.indexOf('.', 1); // 从$之后开始找.
            if (dotIdx > 0) {
                // 形式: $node.field
                String nodeName = key.substring(1, dotIdx);
                // String fieldName = key.substring(dotIdx + 1); // 原始字段名，不再直接使用

                // $node 对应的映射值作为节点映射
                Object mappedNodeObj = parameters.get("$" + nodeName);
                String mappedNode = mappedNodeObj == null ? nodeName : String.valueOf(mappedNodeObj).trim();
                if (mappedNode.startsWith("$")) {
                    mappedNode = mappedNode.substring(1);
                }
                // 当前键的值作为字段映射
                String mappedField = val;

                replacement = "$" + mappedNode + "." + mappedField;
            } else {
                // 形式: $node
                String mappedNode = val;
                if (mappedNode.startsWith("$")) {
                    mappedNode = mappedNode.substring(1);
                }
                replacement = "$" + mappedNode;
            }

            // 精确替换该键出现的所有位置
            result = result.replace(key, replacement);
        }

        return result;
    }

    /**
     * 新的解析方法，用于处理流程公式中的上下文数据
     * 该方法会将字段引用直接替换为值列表，例如将 COUNT(f_id) 转换为 COUNT(1,2,3)，例如：
     * {
     * "formula": "COUNT($r_id1.f_id)",
     * "contextData":
     * {
     * "r_id1": [
     * {"f_id": 1},
     * {"f_id": 2},
     * {"f_id": 3}
     * ],
     * "r_id2": [
     * {"f_id": 4},
     * {"f_id": 5},
     * {"f_id": 6}
     * ],
     * "r_id3": {"f_id": 4}
     * }
     * }
     * 执行完成后 "formula": "COUNT(1,2,3)"
     *
     * @param formula     原始公式
     * @param contextData 上下文数据
     * @return 解析替换后的公式
     */
    private String handleFormulaContextData(String formula, Map<String, Object> contextData) {
        if (!StringUtils.hasText(formula) || contextData == null || contextData.isEmpty()) {
            return formula;
        }

        String result = formula;

        // 匹配占位符: $recordKey.fieldKey （recordKey和fieldKey均为字母/数字/下划线，recordKey以字母或下划线开头）
        Pattern p = Pattern.compile("\\$([A-Za-z_][\\w]*)\\.([A-Za-z_][\\w]*)");
        Matcher m = p.matcher(result);

        // 收集唯一占位符，避免重复处理
        Set<String> placeholders = new LinkedHashSet<>();
        while (m.find()) {
            placeholders.add(m.group(0));
        }
        if (placeholders.isEmpty()) {
            return result;
        }

        for (String placeholder : placeholders) {
            Matcher pm = p.matcher(placeholder);
            if (!pm.matches()) {
                continue;
            }
            String recordKey = pm.group(1);
            String fieldKey = pm.group(2);

            Object data = contextData.get(recordKey);
            List<Object> values = new ArrayList<>();

            if (data instanceof List) {
                List<?> list = (List<?>) data;
                for (Object item : list) {
                    if (item instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) item;
                        values.add(map.get(fieldKey));
                    } else {
                        // 若为非Map元素，直接当作值使用
                        values.add(item);
                    }
                }
            } else if (data instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) data;
                values.add(map.get(fieldKey));
            } else if (data != null) {
                // 其他类型，直接当作单值
                values.add(data);
            }

            // 将值拼接为逗号分隔的JS字面量参数列表；空集用 null 占位，避免出现 COUNT()
            StringBuilder sb = new StringBuilder();
            if (values.isEmpty()) {
                sb.append("null");
            } else {
                for (int i = 0; i < values.size(); i++) {
                    sb.append(toJsLiteral(values.get(i)));
                    if (i < values.size() - 1) {
                        sb.append(",");
                    }
                }
            }

            result = result.replace(placeholder, sb.toString());
        }

        return result;
    }

    /**
     * 处理公式中的参数映射，将形如"$数据查询节点(多条).关联主表ID"的参数替换为对应的字段名"f_id"
     * 例如：将"COUNT($数据查询节点(多条).关联主表ID)"转换为"COUNT(值1, 值2，...)"
     *
     * @param formula    公式字符串
     * @param parameters 参数映射
     * @return 处理后的公式
     */
    public String processFormulaParameters(String formula, Map<String, Object> parameters) {
        if (!StringUtils.hasText(formula) || parameters == null || parameters.isEmpty()) {
            return formula;
        }

        String result = formula;

        // 收集所有以$开头的键
        List<String> keys = new ArrayList<>();
        for (String key : parameters.keySet()) {
            if (key != null && key.startsWith("$")) {
                keys.add(key);
            }
        }

        if (keys.isEmpty()) {
            return result;
        }

        // 按键长度倒序排列，避免短键先替换影响长键
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String key : keys) {
            Object valObj = parameters.get(key);
            if (valObj == null) {
                continue;
            }

            String val = String.valueOf(valObj).trim();
            if (val.isEmpty()) {
                continue;
            }

            // 如果是字段引用形式 ($nodeName.fieldName)
            int dotIdx = key.indexOf('.', 1); // 从$之后开始找.
            if (dotIdx > 0) {
                // 提取字段名，用于替换
                result = result.replace(key, val);
            }
        }

        return result;
    }

}
