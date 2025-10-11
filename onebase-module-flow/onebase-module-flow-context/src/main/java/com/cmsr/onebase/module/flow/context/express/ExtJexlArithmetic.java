package com.cmsr.onebase.module.flow.context.express;

import org.apache.commons.jexl3.JexlArithmetic;
import org.apache.commons.jexl3.JexlOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @Author：huangjie
 * @Date：2025/9/28 14:05
 */
public class ExtJexlArithmetic extends JexlArithmetic {

    public ExtJexlArithmetic(boolean astrict) {
        super(astrict);
    }

    @Override
    protected int compare(Object left, Object right, JexlOperator operator) {
        if (left instanceof LocalDate && right instanceof String) {
            try {
                LocalDate leftLocalDate = (LocalDate) left;
                LocalDate rightLocalDate = LocalDate.parse((String) right, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return super.compare(leftLocalDate.toEpochDay(), rightLocalDate.toEpochDay(), operator);
            } catch (Exception e) {
                // 解析失败，使用默认比较逻辑
            }
        }
        if (right instanceof LocalDateTime && left instanceof String) {
            try {
                LocalDateTime leftLocalDateTime = LocalDateTime.parse((String) left, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime rightLocalDateTime = (LocalDateTime) right;
                return super.compare(leftLocalDateTime.toEpochSecond(ZoneOffset.UTC), rightLocalDateTime.toEpochSecond(ZoneOffset.UTC), operator);
            } catch (Exception e) {
                // 解析失败，使用默认比较逻辑
            }
        }
        return super.compare(left, right, operator);
    }
}
