package com.cmsr.onebase.framework.common.tools.core.lang;

import com.cmsr.onebase.framework.common.tools.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.util.function.Supplier;

/**
 * 断言<br>
 * 断言某些对象或值是否符合规定，否则抛出异常。经常用于做变量检查
 *
 */
public class Assert {

    /**
     * 检查值是否在指定范围内
     *
     * @param value            值
     * @param min              最小值（包含）
     * @param max              最大值（包含）
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @return 经过检查后的值
     * @since 5.7.15
     */
    public static double checkBetween(double value, double min, double max, String errorMsgTemplate, Object... params) {
        return checkBetween(value, min, max, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param <X>           异常类型
     * @param value         值
     * @param min           最小值（包含）
     * @param max           最大值（包含）
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 经过检查后的值
     * @throws X if value is out of bound
     * @since 5.7.15
     */
    public static <X extends Throwable> double checkBetween(double value, double min, double max, Supplier<? extends X> errorSupplier) throws X {
        if (value < min || value > max) {
            throw errorSupplier.get();
        }

        return value;
    }

    /**
     * 断言两个对象是否相等,如果两个对象不相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.isEquals(obj1,obj2,"obj1 must be equals obj2");
     * </pre>
     *
     * @param obj1             对象1
     * @param obj2             对象2
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @throws IllegalArgumentException obj1 must be equals obj2
     */
    public static void equals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        equals(obj1, obj2, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言两个对象是否相等,如果两个对象不相等,抛出指定类型异常,并使用指定的函数获取错误信息返回
     *
     * @param obj1          对象1
     * @param obj2          对象2
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @param <X>           异常类型
     * @throws X obj1 must be equals obj2
     */
    public static <X extends Throwable> void equals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
        if (ObjUtil.notEqual(obj1, obj2)) {
            throw errorSupplier.get();
        }
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank(final T text) throws IllegalArgumentException {
        return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank(final T text, final String errorMsgTemplate, final Object... params) throws IllegalArgumentException {
        return notBlank(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出自定义异常。
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notBlank(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>              异常类型
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence, X extends Throwable> T notBlank(final T text, final Supplier<X> errorMsgSupplier) throws X {
        if (StrUtil.isBlank(text)) {
            throw errorMsgSupplier.get();
        }
        return text;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     * <pre>{@code
     *   Assert.notNull(clazz);
     * }</pre>
     *
     * @param <T>    被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(final T object) throws IllegalArgumentException {
        if (null == object) {
            throw new IllegalArgumentException("[Assertion failed] - this argument is required; it must not be null");
        }
        return object;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException}
     * <pre>{@code
     *   Assert.notNull(clazz, "The class must not be null");
     * }</pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param object           被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 被检查后的对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(final T object, final String errorMsgTemplate, final Object... params) throws IllegalArgumentException {
        if (null == object) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
        return object;
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0, "The value must not be negative");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(final boolean expression, final String errorMsgTemplate, final Object... params) throws IllegalArgumentException {
        isFalse(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出指定类型异常<br>
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     *  Assert.isFalse(i &gt; 0, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param expression    布尔值
     * @param errorSupplier 指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     * @since 5.4.5
     */
    public static <X extends Throwable> void isFalse(final boolean expression, final Supplier<X> errorSupplier) throws X {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空
     * @see StrUtil#isNotEmpty(CharSequence)
     */
    public static <T extends CharSequence> T notEmpty(final T text, final String errorMsgTemplate, final Object... params) throws IllegalArgumentException {
        return notEmpty(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空，为空抛出自定义异常，并使用指定的函数获取错误信息返回。
     * <pre class="code">
     * Assert.notEmpty(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param <T>           字符串类型
     * @param text          被检查字符串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空抛出此异常
     * @see StrUtil#isNotEmpty(CharSequence)
     * @since 5.4.5
     */
    public static <T extends CharSequence, X extends Throwable> T notEmpty(final T text, final Supplier<X> errorSupplier) throws X {
        if (StrUtil.isEmpty(text)) {
            throw errorSupplier.get();
        }
        return text;
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, IllegalArgumentException::new);
     * </pre>
     *
     * @param <X>        异常类型
     * @param expression 布尔值
     * @param supplier   指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
        if (false == expression) {
            throw supplier.get();
        }
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.notEquals(obj1,obj2,"obj1 must be not equals obj2");
     * </pre>
     *
     * @param obj1             对象1
     * @param obj2             对象2
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @throws IllegalArgumentException obj1 must be not equals obj2
     */
    public static void notEquals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        notEquals(obj1, obj2, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等,抛出指定类型异常,并使用指定的函数获取错误信息返回
     *
     * @param obj1          对象1
     * @param obj2          对象2
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @param <X>           异常类型
     * @throws X obj1 must be not equals obj2
     */
    public static <X extends Throwable> void notEquals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
        if (ObjUtil.equals(obj1, obj2)) {
            throw errorSupplier.get();
        }
    }

}
