package com.cmsr.onebase.plugin.api;

import com.cmsr.onebase.plugin.context.PluginContext;
import org.pf4j.ExtensionPoint;

import java.util.Map;

/**
 * 数据处理器扩展点
 * <p>
 * 用于数据导入、导出、转换等场景。
 * 可以在数据流水线中作为处理节点使用。
 * </p>
 *
 * <pre>
 * 使用示例：
 * {@code
 * public class ExcelParser implements DataProcessor {
 *     @Override
 *     public String type() { return "COMPLEX_EXCEL_PARSER"; }
 *
 *     @Override
 *     public Object process(PluginContext ctx, Object input, Map<String, Object> config) {
 *         InputStream stream = (InputStream) input;
 *         return parseComplexExcel(stream, config);
 *     }
 * }
 * }
 * </pre>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public interface DataProcessor extends ExtensionPoint {

    /**
     * 处理器类型标识（唯一）
     *
     * @return 类型标识
     */
    String type();

    /**
     * 处理器描述
     *
     * @return 描述信息
     */
    default String description() {
        return "";
    }

    /**
     * 支持的输入类型
     * <p>
     * 如：InputStream、byte[]、String、Map 等
     * </p>
     *
     * @return 输入类型Class
     */
    default Class<?> inputType() {
        return Object.class;
    }

    /**
     * 输出类型
     *
     * @return 输出类型Class
     */
    default Class<?> outputType() {
        return Object.class;
    }

    /**
     * 处理数据
     *
     * @param ctx    插件上下文
     * @param input  输入数据
     * @param config 配置参数
     * @return 处理后的数据
     * @throws Exception 处理异常
     */
    Object process(PluginContext ctx, Object input, Map<String, Object> config) throws Exception;
}
