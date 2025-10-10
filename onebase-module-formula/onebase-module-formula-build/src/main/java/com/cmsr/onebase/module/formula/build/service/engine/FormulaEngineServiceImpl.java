package com.cmsr.onebase.module.formula.build.service.engine;

import com.cmsr.onebase.module.formula.build.config.FormulaEngineProperties;
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
import java.util.HashMap;
import java.util.Map;
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

                    // 4. 将exports中的所有函数暴露到���局作用域
                    if (exportsObj != null && exportsObj.hasMembers()) {
                        for (String memberName : exportsObj.getMemberKeys()) {
                            Value member = exportsObj.getMember(memberName);
                            if (member != null && member.canExecute()) {
                                bindings.putMember(memberName, member);
                            }
                        }
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
            log.error("公式执行异常：{}, 公式：{}", e.getMessage(), formula);
            throw new RuntimeException("公式执行失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("公式引擎执行异常", e);
            throw e;
        }
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
     * 注入参数到JavaScript上下文
     *
     * @param context JavaScript上下文
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
}
