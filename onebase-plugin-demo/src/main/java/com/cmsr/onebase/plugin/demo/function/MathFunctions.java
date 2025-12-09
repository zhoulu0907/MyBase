package com.cmsr.onebase.plugin.demo.function;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.ParamDef;
import org.pf4j.Extension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * 数学计算函数示例
 * <p>
 * 演示如何实现复杂的数学运算函数
 * </p>
 *
 * @author OneBase Team
 * @date 2025-11-29
 */
@Extension
public class MathFunctions implements CustomFunction {

    @Override
    public String name() {
        return "MATH_CALC";
    }

    @Override
    public String description() {
        return "数学计算函数，支持四则运算、百分比计算、精确小数运算等";
    }

    @Override
    public List<ParamDef> params() {
        return Arrays.asList(
            ParamDef.required("operation", "操作类型: add/subtract/multiply/divide/percent/round", ParamDef.ParamType.STRING),
            ParamDef.required("num1", "第一个数字", ParamDef.ParamType.NUMBER),
            ParamDef.optional("num2", ParamDef.ParamType.NUMBER, 0),
            ParamDef.optional("scale", ParamDef.ParamType.NUMBER, 2)
        );
    }

    @Override
    public String returnType() {
        return "number";
    }

    @Override
    public Object execute(PluginContext ctx, Object... args) throws Exception {
        String operation = args.length > 0 ? String.valueOf(args[0]) : "";
        BigDecimal num1 = args.length > 1 ? toBigDecimal(args[1]) : BigDecimal.ZERO;
        BigDecimal num2 = args.length > 2 ? toBigDecimal(args[2]) : BigDecimal.ZERO;
        int scale = args.length > 3 ? toInt(args[3]) : 2;

        return switch (operation) {
            case "add" -> num1.add(num2).setScale(scale, RoundingMode.HALF_UP);
            case "subtract" -> num1.subtract(num2).setScale(scale, RoundingMode.HALF_UP);
            case "multiply" -> num1.multiply(num2).setScale(scale, RoundingMode.HALF_UP);
            case "divide" -> {
                if (num2.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("除数不能为零");
                }
                yield num1.divide(num2, scale, RoundingMode.HALF_UP);
            }
            case "percent" -> num1.multiply(BigDecimal.valueOf(100)).setScale(scale, RoundingMode.HALF_UP);
            case "round" -> num1.setScale(num2.intValue(), RoundingMode.HALF_UP);
            default -> throw new IllegalArgumentException("不支持的操作类型: " + operation);
        };
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
}
