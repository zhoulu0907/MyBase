package com.cmsr.onebase.framework.ds.exception;

import org.junit.jupiter.api.Test;

/**
 * @Author：huangjie
 * @Date：2025/11/24 16:26
 */
class DolphinschedulerExceptionTest {

    @Test
    void testMessage() {
        DolphinschedulerException dolphinschedulerException = DolphinschedulerException.of("message{} 参数 {} {}", "abb", 333);
        System.out.println(dolphinschedulerException.getMessage());
    }
}