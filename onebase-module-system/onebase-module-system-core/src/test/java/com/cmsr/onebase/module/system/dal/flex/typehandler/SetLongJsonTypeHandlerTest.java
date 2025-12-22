package com.cmsr.onebase.module.system.dal.flex.typehandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * PostIdsJsonTypeHandler 单测。
 *
 * @author matianyu
 * @date 2025-12-22
 */
public class SetLongJsonTypeHandlerTest {

    @Test
    public void testParseJsonArray() throws Exception {
        Set<Long> result = invokeParse("[1,2,3]");
        Assertions.assertEquals(new LinkedHashSet<>(Set.of(1L, 2L, 3L)), result);
    }

    @Test
    public void testParseEmptyArray() throws Exception {
        Set<Long> result = invokeParse("[]");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testParseNull() throws Exception {
        Set<Long> result = invokeParse(null);
        Assertions.assertTrue(result.isEmpty());
    }

    private static Set<Long> invokeParse(String json) throws Exception {
        try {
            java.lang.reflect.Method m = SetLongJsonTypeHandler.class.getDeclaredMethod("parse", String.class);
            m.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Long> value = (Set<Long>) m.invoke(null, json);
            return value;
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof SQLException sqlException) {
                throw sqlException;
            }
            throw e;
        }
    }
}

