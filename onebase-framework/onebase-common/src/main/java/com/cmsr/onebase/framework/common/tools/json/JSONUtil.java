package com.cmsr.onebase.framework.common.tools.json;


import com.cmsr.onebase.framework.common.tools.core.convert.NumberWithFormat;
import com.cmsr.onebase.framework.common.tools.core.map.MapWrapper;
import com.cmsr.onebase.framework.common.tools.core.util.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * JSON工具类
 *
 */
public class JSONUtil {

    /**
     * JSON字符串转为实体类对象，转换异常将被抛出
     *
     * @param <T>        Bean类型
     * @param jsonString JSON字符串
     * @param beanClass  实体类对象
     * @return 实体类对象
     * @since 3.1.2
     */
    public static <T> T toBean(String jsonString, Class<T> beanClass) {
        return toBean(parseObj(jsonString), beanClass);
    }

    /**
     * 转为实体类对象，转换异常将被抛出
     *
     * @param <T>       Bean类型
     * @param json      JSONObject
     * @param beanClass 实体类对象
     * @return 实体类对象
     */
    public static <T> T toBean(JSONObject json, Class<T> beanClass) {
        return null == json ? null : json.toBean(beanClass);
    }

    /**
     * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
     * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
     * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
     *
     * @param string 字符串
     * @return 适合在JSON中显示的字符串
     */
    public static String quote(String string) {
        return quote(string, true);
    }

    /**
     * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
     * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
     * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
     *
     * @param str    字符串
     * @param writer Writer
     * @return Writer
     * @throws IOException IO异常
     */
    public static Writer quote(String str, Writer writer) throws IOException {
        return quote(str, writer, true);
    }

    /**
     * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
     * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
     * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
     *
     * @param string 字符串
     * @param isWrap 是否使用双引号包装字符串
     * @return 适合在JSON中显示的字符串
     * @since 3.3.1
     */
    public static String quote(String string, boolean isWrap) {
        StringWriter sw = new StringWriter();
        try {
            return quote(string, sw, isWrap).toString();
        } catch (IOException ignored) {
            // will never happen - we are writing to a string writer
            return StrUtil.EMPTY;
        }
    }

    /**
     * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
     * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
     * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
     *
     * @param str    字符串
     * @param writer Writer
     * @param isWrap 是否使用双引号包装字符串
     * @return Writer
     * @throws IOException IO异常
     * @since 3.3.1
     */
    public static Writer quote(String str, Writer writer, boolean isWrap) throws IOException {
        if (StrUtil.isEmpty(str)) {
            if (isWrap) {
                writer.write("\"\"");
            }
            return writer;
        }

        char c; // 当前字符
        int len = str.length();
        if (isWrap) {
            writer.write('"');
        }
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    writer.write("\\");
                    writer.write(c);
                    break;
                default:
                    writer.write(escape(c));
            }
        }
        if (isWrap) {
            writer.write('"');
        }
        return writer;
    }

    /**
     * 在需要的时候包装对象<br>
     * 包装包括：
     * <ul>
     * <li>{@code null} =》 {@code JSONNull.NULL}</li>
     * <li>array or collection =》 JSONArray</li>
     * <li>map =》 JSONObject</li>
     * <li>standard property (Double, String, et al) =》 原对象</li>
     * <li>来自于java包 =》 字符串</li>
     * <li>其它 =》 尝试包装为JSONObject，否则返回{@code null}</li>
     * </ul>
     *
     * @param object     被包装的对象
     * @param jsonConfig JSON选项
     * @return 包装后的值，null表示此值需被忽略
     */
    public static Object wrap(Object object, JSONConfig jsonConfig) {
        if (object == null) {
            return jsonConfig.isIgnoreNullValue() ? null : JSONNull.NULL;
        }
        if (object instanceof JSON //
                || ObjUtil.isNull(object) //
                || object instanceof JSONString //
                || object instanceof CharSequence //
                || object instanceof Number //
                || ObjUtil.isBasicType(object) //
        ) {
            if(object instanceof Number && null != jsonConfig.getDateFormat()){
                // 当JSONConfig中设置了日期格式，则包装为NumberWithFormat，以便在Converter中使用自定义格式转换日期时间
                return new NumberWithFormat((Number) object, jsonConfig.getDateFormat());
            }
            return object;
        }

        try {
            // fix issue#1399@Github
            if(object instanceof SQLException){
                return object.toString();
            }

            // JSONArray
            if (object instanceof Iterable || ArrayUtil.isArray(object)) {
                return new JSONArray(object, jsonConfig);
            }
            // JSONObject
            if (object instanceof Map || object instanceof Map.Entry) {
                return new JSONObject(object, jsonConfig);
            }

            // 日期类型原样保存，便于格式化
            if (object instanceof Date
                    || object instanceof Calendar
                    || object instanceof TemporalAccessor
            ) {
                return object;
            }
            // 枚举类保存其字符串形式（4.0.2新增）
            if (object instanceof Enum) {
                return object.toString();
            }

            // pr#3507
            // Class类型保存类名
            if (object instanceof Class<?>) {
                return ((Class<?>) object).getName();
            }

            // Java内部类不做转换
            if (ClassUtil.isJdkClass(object.getClass())) {
                return object.toString();
            }

            // 默认按照JSONObject对待
            return new JSONObject(object, jsonConfig);
        } catch (final Exception exception) {
            return null;
        }
    }

    /**
     * 是否为JSONObject类型字符串，首尾都为大括号判定为JSONObject字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     * @since 5.7.22
     */
    public static boolean isTypeJSONObject(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return StrUtil.isWrap(StrUtil.trim(str), '{', '}');
    }

    /**
     * 是否为JSON类型字符串，首尾都为大括号或中括号判定为JSON字符串
     *
     * @param str 字符串
     * @return 是否为JSON类型字符串
     * @since 5.7.22
     */
    public static boolean isTypeJSON(String str) {
        return isTypeJSONObject(str) || isTypeJSONArray(str);
    }

    /**
     * 是否为JSONArray类型的字符串，首尾都为中括号判定为JSONArray字符串
     *
     * @param str 字符串
     * @return 是否为JSONArray类型字符串
     * @since 5.7.22
     */
    public static boolean isTypeJSONArray(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return StrUtil.isWrap(StrUtil.trim(str), '[', ']');
    }

    /**
     * JSON字符串转JSONArray
     *
     * @param jsonStr JSON字符串
     * @return JSONArray
     */
    public static JSONArray parseArray(String jsonStr) {
        return new JSONArray(jsonStr);
    }

    /**
     * JSON字符串转JSONObject对象
     *
     * @param jsonStr JSON字符串
     * @return JSONObject
     */
    public static JSONObject parseObj(String jsonStr) {
        return new JSONObject(jsonStr);
    }

    /**
     * 转换为JSON字符串
     *
     * @param obj 被转为JSON的对象
     * @return JSON字符串
     */
    public static String toJsonStr(Object obj) {
        return toJsonStr(obj, (JSONConfig) null);
    }

    /**
     * 转换为JSON字符串
     *
     * @param obj 被转为JSON的对象
     * @param jsonConfig JSON配置
     * @return JSON字符串
     * @since 5.7.12
     */
    public static String toJsonStr(Object obj, JSONConfig jsonConfig) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return StrUtil.str((CharSequence) obj);
        }
        return toJsonStr(parse(obj, jsonConfig));
    }

    /**
     * 转为JSON字符串
     *
     * @param json JSON
     * @return JSON字符串
     */
    public static String toJsonStr(JSON json) {
        if (null == json) {
            return null;
        }
        return json.toJSONString(0);
    }

    /**
     * 转换对象为JSON，如果用户不配置JSONConfig，则JSON的有序与否与传入对象有关。<br>
     * 支持的对象：
     * <ul>
     *     <li>String: 转换为相应的对象</li>
     *     <li>Array、Iterable、Iterator：转换为JSONArray</li>
     *     <li>Bean对象：转为JSONObject</li>
     * </ul>
     *
     * @param obj 对象
     * @return JSON
     */
    public static JSON parse(Object obj) {
        return parse(obj, null);
    }

    /**
     * 转换对象为JSON，如果用户不配置JSONConfig，则JSON的有序与否与传入对象有关。<br>
     * 支持的对象：
     * <ul>
     *     <li>String: 转换为相应的对象</li>
     *     <li>Array、Iterable、Iterator：转换为JSONArray</li>
     *     <li>Bean对象：转为JSONObject</li>
     * </ul>
     *
     * @param obj    对象
     * @param config JSON配置，{@code null}使用默认配置
     * @return JSON
     * @since 5.3.1
     */
    public static JSON parse(Object obj, JSONConfig config) {
        if (null == obj) {
            return null;
        }
        JSON json;
        if (obj instanceof JSON) {
            json = (JSON) obj;
        } else if (obj instanceof CharSequence) {
            final String jsonStr = StrUtil.trim((CharSequence) obj);
            json = isTypeJSONArray(jsonStr) ? parseArray(jsonStr, config) : parseObj(jsonStr, config);
        } else if (obj instanceof MapWrapper) {
            // MapWrapper实现了Iterable会被当作JSONArray，此处做修正
            json = parseObj(obj, config);
        } else if (obj instanceof Iterable || obj instanceof Iterator || ArrayUtil.isArray(obj)) {// 列表
            json = parseArray(obj, config);
        } else {// 对象
            json = parseObj(obj, config);
        }

        return json;
    }

    /**
     * JSON字符串转JSONObject对象<br>
     * 此方法会忽略空值，但是对JSON字符串不影响
     *
     * @param obj    Bean对象或者Map
     * @param config JSON配置
     * @return JSONObject
     * @since 5.3.1
     */
    public static JSONObject parseObj(Object obj, JSONConfig config) {
        return new JSONObject(obj, ObjUtil.defaultIfNull(config, JSONConfig::create));
    }

    /**
     * JSON字符串转JSONArray
     *
     * @param arrayOrCollection 数组或集合对象
     * @param config            JSON配置
     * @return JSONArray
     * @since 5.3.1
     */
    public static JSONArray parseArray(Object arrayOrCollection, JSONConfig config) {
        return new JSONArray(arrayOrCollection, config);
    }

    /**
     * 是否为null对象，null的情况包括：
     *
     * <pre>
     * 1. {@code null}
     * 2. {@link JSONNull}
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     * @since 4.5.7
     */
    public static boolean isNull(Object obj) {
        return null == obj || obj instanceof JSONNull;
    }

    /**
     * 转义显示不可见字符
     *
     * @param str 字符串
     * @return 转义后的字符串
     */
    public static String escape(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }

        final int len = str.length();
        final StringBuilder builder = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            builder.append(escape(c));
        }
        return builder.toString();
    }

    /**
     * 转义不可见字符<br>
     * 见：<a href="https://en.wikibooks.org/wiki/Unicode/Character_reference/0000-0FFF">https://en.wikibooks.org/wiki/Unicode/Character_reference/0000-0FFF</a>
     *
     * @param c 字符
     * @return 转义后的字符串
     */
    private static String escape(char c) {
        switch (c) {
            case '\b':
                return "\\b";
            case '\t':
                return "\\t";
            case '\n':
                return "\\n";
            case '\f':
                return "\\f";
            case '\r':
                return "\\r";
            default:
                if (c < StrUtil.C_SPACE || //
                        (c >= '\u0080' && c <= '\u00a0') || //
                        (c >= '\u2000' && c <= '\u2010') || //
                        (c >= '\u2028' && c <= '\u202F') || //
                        (c >= '\u2066' && c <= '\u206F')//
                ) {
                    return HexUtil.toUnicodeHex(c);
                } else {
                    return Character.toString(c);
                }
        }
    }

    /**
     * 创建 JSONArray
     *
     * @param config JSON配置
     * @return JSONArray
     * @since 5.2.5
     */
    public static JSONArray createArray(JSONConfig config) {
        return new JSONArray(config);
    }
}
