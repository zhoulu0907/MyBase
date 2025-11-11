package com.cmsr.onebase.module.formula.enums;

public class FormulaConstants {

    /**
     * 支持的Excel函数列表 - 基于FormulaJS v4.5.3完整函数库
     */
    public static final String[] SUPPORTED_FUNCTIONS = {
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
            "TRANSPOSE", "UNIQUE", "SORT", "FILTER", "SUMPRODUCT",

            // 正则表达式函数
            "REGEXEXTRACTALL", "REGEXEXTRACT", "REGEXMATCH", "REGEXREPLACE"
    };

    /**
     * 危险函数和操作的正则表达式
     */
    public static final String DANGEROUS_PATTERNS =
            "(?i)(\\beval\\b|\\bFunction\\b|\\bnew\\s+Function\\b|\\bimport\\b|\\brequire\\b|" +
                    "\\bprocess\\b|\\bsetTimeout\\b|\\bsetInterval\\b|\\bsetImmediate\\b|" +
                    "\\b__proto__\\b|\\bconstructor\\b|\\bprototype\\b)";

    /**
     * 人员函数常量
     */
    public static final String GETUSER       = "GETUSER";
    public static final String GETDEPT       = "GETDEPT";
    public static final String GETUPDEPT     = "GETUPDEPT";
    public static final String GETROLE       = "GETROLE";
    public static final String GETSUPERVISOR = "GETSUPERVISOR";
    public static final String ISINROLE      = "ISINROLE";
    public static final String ISINDEPT      = "ISINDEPT";

}
