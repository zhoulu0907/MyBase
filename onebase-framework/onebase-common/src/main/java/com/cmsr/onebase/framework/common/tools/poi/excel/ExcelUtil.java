package com.cmsr.onebase.framework.common.tools.poi.excel;

import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

public class ExcelUtil {

    /**
     * xls的ContentType
     */
    public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * xlsx的ContentType
     */
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * 将Sheet列号变为列名
     *
     * @param index 列号, 从0开始
     * @return 0-》A; 1-》B...26-》AA
     * @since 4.1.20
     */
    public static String indexToColName(int index) {
        if (index < 0) {
            return null;
        }
        final StringBuilder colName = StrUtil.builder();
        do {
            if (colName.length() > 0) {
                index--;
            }
            int remainder = index % 26;
            colName.append((char) (remainder + 'A'));
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName.reverse().toString();
    }

}
