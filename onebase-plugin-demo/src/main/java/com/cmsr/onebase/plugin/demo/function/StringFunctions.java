package com.cmsr.onebase.plugin.demo.function;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.ParamDef;
import org.pf4j.Extension;

import java.util.Arrays;
import java.util.List;

/**
 * 字符串处理函数示例
 * <p>
 * 演示如何实现自定义函数扩展点，提供字符串相关的处理功能
 * </p>
 *
 * @author OneBase Team
 * @date 2025-11-29
 */
@Extension
public class StringFunctions implements CustomFunction {

    @Override
    public String name() {
        return "STR_UTILS";
    }

    @Override
    public String description() {
        return "字符串工具函数集，提供字符串格式化、截取、替换等功能";
    }

    @Override
    public List<ParamDef> params() {
        return Arrays.asList(
            ParamDef.required("operation", "操作类型: format/substring/replace/reverse", ParamDef.ParamType.STRING),
            ParamDef.required("input", "输入字符串", ParamDef.ParamType.STRING),
            new ParamDef("param1", "参数1", ParamDef.ParamType.STRING, false),
            new ParamDef("param2", "参数2", ParamDef.ParamType.STRING, false)
        );
    }

    @Override
    public String returnType() {
        return "string";
    }

    @Override
    public Object execute(PluginContext ctx, Object... args) throws Exception {
        String operation = args.length > 0 ? String.valueOf(args[0]) : "";
        String input = args.length > 1 ? String.valueOf(args[1]) : "";
        String param1 = args.length > 2 ? String.valueOf(args[2]) : null;
        String param2 = args.length > 3 ? String.valueOf(args[3]) : null;

        // 演示如何获取上下文信息
        String tenantId = ctx.getTenantId();
        System.out.println("当前租户: " + tenantId);

        return switch (operation) {
            case "format" -> formatString(input, param1);
            case "substring" -> substringString(input, param1, param2);
            case "replace" -> replaceString(input, param1, param2);
            case "reverse" -> reverseString(input);
            default -> throw new IllegalArgumentException("不支持的操作类型: " + operation);
        };
    }

    private String formatString(String template, String args) {
        if (args == null || args.isEmpty()) {
            return template;
        }
        String[] argArray = args.split(",");
        return String.format(template, (Object[]) argArray);
    }

    private String substringString(String input, String startStr, String endStr) {
        int start = startStr != null ? Integer.parseInt(startStr) : 0;
        int end = endStr != null ? Integer.parseInt(endStr) : input.length();
        return input.substring(start, Math.min(end, input.length()));
    }

    private String replaceString(String input, String target, String replacement) {
        if (target == null || replacement == null) {
            return input;
        }
        return input.replace(target, replacement);
    }

    private String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }
}
