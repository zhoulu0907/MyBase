package com.cmsr.onebase.module.flow.context.graph;

import java.time.format.DateTimeFormatter;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:16
 */
public class JsonGraphConstant {

    public static final String START_FORM = "startForm";

    public static final String START_ENTITY = "startEntity";

    public static final String START_TIME = "startTime";

    public static final String START_DATE_FIELD = "startDateField";

    public static final String PROCESS_ID = "processId";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
