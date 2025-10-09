package com.cmsr.onebase.module.flow.context.express;

import org.apache.commons.jexl3.JexlArithmetic;
import org.apache.commons.jexl3.JexlOperator;

import java.time.LocalDate;

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
                LocalDate rightLocalDate = LocalDate.parse((String) right);
                return super.compare(leftLocalDate.toEpochDay(), rightLocalDate.toEpochDay(), operator);
            } catch (Exception e) {
                // 解析失败，使用默认比较逻辑
            }
        }
        return super.compare(left, right, operator);
    }
}
