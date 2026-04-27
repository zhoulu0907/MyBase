package com.cmsr.onebase.module.flow.context.enums;

import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FieldTypeConvertorTest {

    @Test
    void testConvertString() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.TEXT, "test");
        assertEquals("test", result);
        assertTrue(result instanceof String);
    }

    @Test
    void testConvertLong() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.ID, "123");
        assertEquals(123L, result);
        assertTrue(result instanceof Long);
    }

    @Test
    void testConvertBoolean() {
        Object result1 = FieldTypeConvertor.convert(SemanticFieldTypeEnum.BOOLEAN, "true");
        assertEquals(true, result1);
        assertTrue(result1 instanceof Boolean);

        Object result2 = FieldTypeConvertor.convert(SemanticFieldTypeEnum.BOOLEAN, "false");
        assertEquals(false, result2);
        assertTrue(result2 instanceof Boolean);
    }

    @Test
    void testConvertDate() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.DATE, "2023-10-15");
        assertEquals(LocalDate.of(2023, 10, 15), result);
        assertTrue(result instanceof LocalDate);
    }

    @Test
    void testConvertDateTime() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.DATETIME, "2023-10-15 14:30:45");
        assertEquals(LocalDateTime.of(2023, 10, 15, 14, 30, 45), result);
        assertTrue(result instanceof LocalDateTime);
    }

    @Test
    void testConvertBigDecimal() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.NUMBER, "123.45");
        assertEquals(new BigDecimal("123.45"), result);
        assertTrue(result instanceof BigDecimal);
    }

    @Test
    void testConvertNull() {
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.TEXT, null);
        assertNull(result);
    }

    @Test
    void testConvertSameType() {
        LocalDate date = LocalDate.now();
        Object result = FieldTypeConvertor.convert(SemanticFieldTypeEnum.DATE, date);
        assertEquals(date, result);
    }
}
