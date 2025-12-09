package com.cmsr.onebase.plugin.api;

import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.ParamDef;
import org.pf4j.ExtensionPoint;

import java.util.Collections;
import java.util.List;

/**
 * 自定义函数扩展点
 * <p>
 * 用于在公式引擎、流程条件等场景中使用自定义函数。
 * 插件开发者只需实现此接口即可扩展平台的函数能力。
 * </p>
 *
 * <pre>
 * 使用示例：
 * {@code
 * public class OcrFunction implements CustomFunction {
 *     @Override
 *     public String name() { return "OCR_RECOGNIZE"; }
 *
 *     @Override
 *     public Object execute(PluginContext ctx, Object... args) {
 *         String imageUrl = (String) args[0];
 *         return doOcrRecognize(imageUrl);
 *     }
 * }
 * }
 * </pre>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public interface CustomFunction extends ExtensionPoint {

    /**
     * 函数名称（唯一标识）
     * <p>
     * 在公式中使用此名称调用函数，如：MY_FUNC(arg1, arg2)
     * 建议使用大写字母和下划线命名，如：OCR_RECOGNIZE、CALCULATE_TAX
     * </p>
     *
     * @return 函数名称
     */
    String name();

    /**
     * 函数描述
     * <p>
     * 用于在UI界面展示函数说明，帮助用户理解函数用途
     * </p>
     *
     * @return 函数描述
     */
    default String description() {
        return "";
    }

    /**
     * 函数分类
     * <p>
     * 用于在UI界面对函数进行分组展示
     * </p>
     *
     * @return 分类名称
     */
    default String category() {
        return "自定义";
    }

    /**
     * 参数定义列表
     * <p>
     * 定义函数的入参信息，用于UI展示和参数校验
     * </p>
     *
     * @return 参数定义列表
     */
    default List<ParamDef> params() {
        return Collections.emptyList();
    }

    /**
     * 返回值类型描述
     *
     * @return 返回值类型，如：string、number、boolean、object、array
     */
    default String returnType() {
        return "object";
    }

    /**
     * 执行函数
     *
     * @param ctx  插件上下文，包含租户、用户、请求等信息
     * @param args 函数入参
     * @return 函数执行结果
     * @throws Exception 执行异常
     */
    Object execute(PluginContext ctx, Object... args) throws Exception;
}
