package com.cmsr.onebase.module.formula.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cmsr.onebase.module.formula.enums.FormulaConstants.SUPPORTED_FUNCTIONS;

public class FormulaValidate {


    public static final String REGEX = "REGEX";

    /**
     * 验证公式中是否包含受支持的函数
     *
     * @param formula 公式内容
     */
    public static String validateSupportedFunctions(String formula) {
        // 首先检查是否包含中文标点符号
        validateBrackets(formula);
        
        // 创建支持函数集合，便于快速查找
        Set<String> supportedFunctionSet = new HashSet<>(Arrays.asList(SUPPORTED_FUNCTIONS));

        // 使用循环逐步验证所有函数，从内层到外层
        String workingFormula = formula;
        int maxIterations = 100; // 防止无限循环
        int iteration = 0;

        while (iteration < maxIterations) {
            // 使用正则表达式提取最内层的函数（括号内不包含其他括号的函数）
            Pattern functionPattern = Pattern.compile("\\b([A-Z][A-Z0-9_]*)\\s*\\(([^()]*)\\)");
            Matcher matcher = functionPattern.matcher(workingFormula);

            boolean foundFunction = false;

            // 检查每个匹配的函数名
            while (matcher.find()) {
                foundFunction = true;
                String functionName = matcher.group(1);
                String params = matcher.group(2);

                // 如果函数不在支持列表中，则抛出异常
                if (!supportedFunctionSet.contains(functionName)) {
                    throw new IllegalArgumentException("不支持" + functionName + "函数");
                }

                // 验证参数个数
                validateFunctionParameters(functionName, params);

                // 验证参数类型
                validateFunctionParameterTypes(functionName, params);
            }

            // 如果没有找到任何函数，说明已经验证完成
            if (!foundFunction) {
                break;
            }

            // 将所有已验证的内层函数替换为占位符，以便外层函数可以被匹配
            // 使用特殊字符替代，确保不会再次被匹配
            workingFormula = workingFormula.replaceAll("\\b([A-Z][A-Z0-9_]*)\\s*\\([^()]*\\)", "@VALIDATED@");
            
            iteration++;
        }

        if (iteration >= maxIterations) {
            throw new IllegalArgumentException("公式验证失败：可能存在无效的嵌套结构");
        }

        return formula;
    }

    /**
     * 验证公式中的括号是否为英文半角括号，并且成对出现
     *
     * @param formula 公式内容
     */
    private static void validateBrackets(String formula) {
        // 首先提取所有字符串字面量（双引号和单引号），并标记它们的位置
        Set<String> stringLiterals = new HashSet<>();
            
        // 匹配双引号字符串
        Pattern doubleQuotePattern = Pattern.compile("\"[^\"]*\"");
        Matcher doubleQuoteMatcher = doubleQuotePattern.matcher(formula);
        while (doubleQuoteMatcher.find()) {
            stringLiterals.add(doubleQuoteMatcher.group());
        }
            
        // 匹配单引号字符串
        Pattern singleQuotePattern = Pattern.compile("'[^']*'");
        Matcher singleQuoteMatcher = singleQuotePattern.matcher(formula);
        while (singleQuoteMatcher.find()) {
            stringLiterals.add(singleQuoteMatcher.group());
        }
            
        // 创建一个临时公式，将字符串字面量替换为占位符
        String tempFormula = formula;
        int placeholderIndex = 0;
        for (String literal : stringLiterals) {
            tempFormula = tempFormula.replace(literal, "@STRING_" + placeholderIndex++ + "@");
        }
            
        // 在临时公式中检查中文标点符号（只检查字符串外部的内容）
        if (tempFormula.contains("（") || tempFormula.contains("）")) {
            throw new IllegalArgumentException("公式中包含中文括号，请使用英文半角括号 () ");
        }
        
        // 检查中文逗号（只检查字符串外部的内容）
        if (tempFormula.contains("，")) {
            throw new IllegalArgumentException("公式中包含中文逗号，请使用英文半角逗号 , ");
        }
        
        // 检查中文引号（只检查字符串外部的内容）
        if (tempFormula.contains("‘") || tempFormula.contains("’") ||
            tempFormula.contains("“") || tempFormula.contains("”")) {
            throw new IllegalArgumentException("公式中包含中文引号，请使用英文半角引号 ‘ ’ 或 “ ” ");
        }
            
        // 验证原始公式中的字符串字面量是否使用英文引号（双引号或单引号）
        for (String literal : stringLiterals) {
            boolean isDoubleQuoted = literal.startsWith("\"") && literal.endsWith("\"");
            boolean isSingleQuoted = literal.startsWith("'") && literal.endsWith("'");
                
            if (!isDoubleQuoted && !isSingleQuoted) {
                throw new IllegalArgumentException("字符串字面量必须使用英文双引号或单引号包裹");
            }
        }
        
        // 验证英文括号是否成对出现
        int leftParenCount = 0;
        int rightParenCount = 0;
            
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                leftParenCount++;
            } else if (c == ')') {
                rightParenCount++;
            }
        }
        
        if (leftParenCount != rightParenCount) {
            throw new IllegalArgumentException(
                String.format("公式中括号不匹配：左括号 (%d 个，右括号 )%d 个", 
                            leftParenCount, rightParenCount));
        }
    }

    /**
     * 验证函数参数个数是否符合要求
     *
     * @param functionName 函数名
     * @param params       参数字符串
     */
    private static void validateFunctionParameters(String functionName, String params) {
        // 解析参数个数
        int actualParamCount = parseParameterCount(params);

        // 根据函数名验证参数个数
        switch (functionName) {
            // 无参数函数
            case "NA":
            case "NOW":
            case "TODAY":
            case "PI":
            case "RAND":
                if (actualParamCount != 0) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为0个参数");
                }
                break;

            // 单参数函数
            case "LEN":
            case "UPPER":
            case "LOWER":
            case "PROPER":
            case "TRIM":
            case "CLEAN":
            case "CODE":
            case "VALUE":
            case "DAY":
            case "MONTH":
            case "YEAR":
            case "HOUR":
            case "MINUTE":
            case "SECOND":
            case "ISBLANK":
            case "ISERR":
            case "ISERROR":
            case "ISEVEN":
            case "ISLOGICAL":
            case "ISNA":
            case "ISNONTEXT":
            case "ISNUMBER":
            case "ISODD":
            case "ISTEXT":
            case "N":
            case "TYPE":
            case "SQRT":
            case "ABS":
            case "SIN":
            case "COS":
            case "TAN":
            case "ASIN":
            case "ACOS":
            case "ATAN":
            case "EXP":
            case "LN":
            case "LOG10":
            case "INT":
            case "FACT":
            case "SIGN":
            case "ROMAN":
                if (actualParamCount != 1) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为1个参数");
                }
                break;

            // 特殊处理可以有多个参数数量的函数
            case "ROUND":
                if (actualParamCount != 1 && actualParamCount != 2) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为1个或2个参数");
                }
                break;

            case "LEFT":
            case "RIGHT":
                if (actualParamCount != 1 && actualParamCount != 2) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为1个或2个参数");
                }
                break;

            case "MID":
                if (actualParamCount != 3) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为3个参数");
                }
                break;

            case "REPLACE":
                if (actualParamCount != 4) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为4个参数");
                }
                break;

            case "SUBSTITUTE":
                if (actualParamCount != 3 && actualParamCount != 4) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为3个或4个参数");
                }
                break;

            // 可变参数函数
            case "SUM":
            case "AVERAGE":
            case "COUNT":
            case "COUNTA":
            case "MAX":
            case "MIN":
            case "CONCATENATE":
                // 这些函数至少需要一个参数
                if (actualParamCount < 1) {
                    throw new IllegalArgumentException(functionName + "函数至少需要1个参数");
                }
                break;

            case "IF":
                if (actualParamCount != 2 && actualParamCount != 3) {
                    throw new IllegalArgumentException(functionName + "函数参数个数不符合要求，应该为2个或3个参数");
                }
                break;

            // 可以根据需要添加更多函数的验证
        }
    }

    /**
     * 验证函数参数类型是否符合要求
     *
     * @param functionName 函数名
     * @param params       参数字符串
     */
    private static void validateFunctionParameterTypes(String functionName, String params) {
        // 根据函数名验证参数类型
        switch (functionName) {
            // 字符串函数
            case "LEN":
            case "UPPER":
            case "LOWER":
            case "PROPER":
            case "TRIM":
            case "CLEAN":
            case "CODE":
            case "VALUE":
                validateStringParameter(params, 1, functionName);
                break;

            // 数值函数
            case "SQRT":
            case "ABS":
            case "SIN":
            case "COS":
            case "TAN":
            case "ASIN":
            case "ACOS":
            case "ATAN":
            case "EXP":
            case "LN":
            case "LOG10":
            case "INT":
            case "FACT":
            case "SIGN":
                validateNumericParameter(params, 1, functionName);
                break;

            case "LEFT":
            case "RIGHT":
                validateStringAndNumericParameters(params, functionName);
                break;

            case "MID":
                validateMidParameters(params, functionName);
                break;

            case "ROUND":
                validateRoundParameters(params, functionName);
                break;

            case "REPLACE":
                validateReplaceParameters(params, functionName);
                break;

            case "SUBSTITUTE":
                validateSubstituteParameters(params, functionName);
                break;

            // 日期函数
            case "DAY":
            case "MONTH":
            case "YEAR":
            case "HOUR":
            case "MINUTE":
            case "SECOND":
                // 这些函数接受日期类型的参数
                break;

            // 逻辑判断函数
            case "ISBLANK":
            case "ISERR":
            case "ISERROR":
            case "ISEVEN":
            case "ISLOGICAL":
            case "ISNA":
            case "ISNONTEXT":
            case "ISNUMBER":
            case "ISODD":
            case "ISTEXT":
                // 这些函数可以接受任意类型的参数
                break;

            // 可以根据需要添加更多函数的类型验证
        }
    }

    /**
     * 验证只有一个字符串参数的函数
     */
    private static void validateStringParameter(String params, int expectedCount, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= expectedCount) {
            // 对于第一个参数，检查是否可能为字符串
            String firstParam = paramArray[0].trim();
            // 如果参数看起来像数字，则报错
            if (firstParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数参数应该是字符串类型");
            }
        }
    }

    /**
     * 验证只有一个数值参数的函数
     */
    private static void validateNumericParameter(String params, int expectedCount, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= expectedCount) {
            // 对于第一个参数，检查是否可能为数值
            String firstParam = paramArray[0].trim();
            // 如果参数是字符串字面量，则报错
            if (firstParam.startsWith("\"") && firstParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数参数应该是数值类型");
            }
        }
    }

    /**
     * 验证LEFT/RIGHT函数参数（字符串, 数值）
     */
    private static void validateStringAndNumericParameters(String params, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= 1) {
            // 第一个参数应该是字符串
            String firstParam = paramArray[0].trim();
            if (firstParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第一个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 2) {
            // 第二个参数应该是数值
            String secondParam = paramArray[1].trim();
            if (secondParam.startsWith("\"") && secondParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第二个参数应该是数值类型");
            }
        }
    }

    /**
     * 验证MID函数参数（字符串, 数值, 数值）
     */
    private static void validateMidParameters(String params, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= 1) {
            // 第一个参数应该是字符串
            String firstParam = paramArray[0].trim();
            if (firstParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第一个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 2) {
            // 第二个参数应该是数值
            String secondParam = paramArray[1].trim();
            if (secondParam.startsWith("\"") && secondParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第二个参数应该是数值类型");
            }
        }
        if (paramArray.length >= 3) {
            // 第三个参数应该是数值
            String thirdParam = paramArray[2].trim();
            if (thirdParam.startsWith("\"") && thirdParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第三个参数应该是数值类型");
            }
        }
    }

    /**
     * 验证ROUND函数参数（数值, [数值]）
     */
    private static void validateRoundParameters(String params, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= 1) {
            // 第一个参数应该是数值
            String firstParam = paramArray[0].trim();
            if (firstParam.startsWith("\"") && firstParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第一个参数应该是数值类型");
            }
        }
        if (paramArray.length >= 2) {
            // 第二个参数应该是数值
            String secondParam = paramArray[1].trim();
            if (secondParam.startsWith("\"") && secondParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第二个参数应该是数值类型");
            }
        }
    }

    /**
     * 验证REPLACE函数参数（字符串, 数值, 数值, 字符串）
     */
    private static void validateReplaceParameters(String params, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= 1) {
            // 第一个参数应该是字符串
            String firstParam = paramArray[0].trim();
            if (firstParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第一个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 2) {
            // 第二个参数应该是数值
            String secondParam = paramArray[1].trim();
            if (secondParam.startsWith("\"") && secondParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第二个参数应该是数值类型");
            }
        }
        if (paramArray.length >= 3) {
            // 第三个参数应该是数值
            String thirdParam = paramArray[2].trim();
            if (thirdParam.startsWith("\"") && thirdParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第三个参数应该是数值类型");
            }
        }
        if (paramArray.length >= 4) {
            // 第四个参数应该是字符串
            String fourthParam = paramArray[3].trim();
            if (fourthParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第四个参数应该是字符串类型");
            }
        }
    }

    /**
     * 验证SUBSTITUTE函数参数（字符串, 字符串, 字符串, [数值]）
     */
    private static void validateSubstituteParameters(String params, String functionName) {
        String[] paramArray = parseParameters(params);
        if (paramArray.length >= 1) {
            // 第一个参数应该是字符串
            String firstParam = paramArray[0].trim();
            if (firstParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第一个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 2) {
            // 第二个参数应该是字符串
            String secondParam = paramArray[1].trim();
            if (secondParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第二个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 3) {
            // 第三个参数应该是字符串
            String thirdParam = paramArray[2].trim();
            if (thirdParam.matches("-?\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException(functionName + "函数第三个参数应该是字符串类型");
            }
        }
        if (paramArray.length >= 4) {
            // 第四个参数应该是数值
            String fourthParam = paramArray[3].trim();
            if (fourthParam.startsWith("\"") && fourthParam.endsWith("\"")) {
                throw new IllegalArgumentException(functionName + "函数第四个参数应该是数值类型");
            }
        }
    }

    /**
     * 解析参数个数
     *
     * @param params 参数字符串
     * @return 参数个数
     */
    private static int parseParameterCount(String params) {
        if (params == null || params.trim().isEmpty()) {
            return 0;
        }

        // 简单按逗号分割计算参数个数
        String[] paramArray = params.split(",");
        int count = paramArray.length;

        // 处理末尾逗号的情况
        if (params.trim().endsWith(",")) {
            count--;
        }

        return count;
    }

    /**
     * 解析参数数组
     *
     * @param params 参数字符串
     * @return 参数数组
     */
    private static String[] parseParameters(String params) {
        if (params == null || params.trim().isEmpty()) {
            return new String[0];
        }

        // 简单按逗号分割参数（不处理嵌套函数等情况）
        return params.split(",");
    }

}
