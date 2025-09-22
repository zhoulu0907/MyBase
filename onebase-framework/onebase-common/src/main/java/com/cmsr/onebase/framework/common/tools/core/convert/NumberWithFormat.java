package com.cmsr.onebase.framework.common.tools.core.convert;


import com.cmsr.onebase.framework.common.tools.core.convert.impl.DateConverter;
import com.cmsr.onebase.framework.common.tools.core.convert.impl.TemporalAccessorConverter;

import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 数字及其格式包装类
 *
 * @author yx6231
 * @date 2025-07-25
 */
public class NumberWithFormat extends Number implements TypeConverter {
    /**
     * 数字对象
     */
    private final Number number;

    /**
     * 格式字符串
     */
    private final String format;

    /**
     * 构造方法
     *
     * @param number 数字对象
     * @param format 格式字符串
     */
    public NumberWithFormat(Number number, String format) {
        this.number = number;
        this.format = format;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Type targetType, Object value) {
        // 自定义日期格式
        if (null != this.format && targetType instanceof Class) {
            final Class<?> clazz = (Class<?>) targetType;
            // https://gitee.com/dromara/hutool/issues/I6IS5B
            if (Date.class.isAssignableFrom(clazz)) {
                return new DateConverter((Class<? extends Date>) clazz, format).convert(this.number, null);
            } else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
                return new TemporalAccessorConverter(clazz, format).convert(this.number, null);
            } else if (String.class == clazz) {
                return toString();
            }

            // 其他情况按照正常数字转换
        }

        // 按照正常数字转换
        return Convert.convert(targetType, this.number,false);
    }

    /**
     * 获取数字对象
     *
     * @return 数字对象
     */
    public Number getNumber() {
        return number;
    }

    /**
     * 获取格式字符串
     *
     * @return 格式字符串
     */
    public String getFormat() {
        return format;
    }

    @Override
    public int intValue() {
        return this.number.intValue();
    }

    @Override
    public long longValue() {
        return this.number.longValue();
    }

    @Override
    public float floatValue() {
        return this.number.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.number.doubleValue();
    }

    @Override
    public String toString() {
        return this.number.toString();
    }
}