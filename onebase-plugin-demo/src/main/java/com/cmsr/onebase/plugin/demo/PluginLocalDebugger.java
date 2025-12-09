package com.cmsr.onebase.plugin.demo;

import com.cmsr.onebase.plugin.demo.function.MathFunctions;
import com.cmsr.onebase.plugin.demo.function.StringFunctions;

/**
 * 插件本地调试运行器
 * <p>
 * 用于在IDE中直接运行和调试插件功能，无需打包部署到平台。
 * 提供快速的开发反馈循环，支持断点调试。
 * </p>
 * 
 * <h3>使用方法：</h3>
 * <ol>
 *     <li><b>方法1（推荐）：</b> 在IDE中右键运行本类的 main 方法</li>
 *     <li><b>方法2：</b> 命令行执行 <code>mvn compile exec:java</code></li>
 *     <li><b>调试技巧：</b> 在扩展点实现类（如MathFunctions）中打断点</li>
 *     <li><b>自定义测试：</b> 修改 main 方法中的测试用例</li>
 * </ol>
 * 
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>DataProcessor、EventListener、HttpHandler 需要完整的 PluginContext，</li>
 *     <li>建议在平台环境中进行集成测试，本地调试主要用于验证 CustomFunction 逻辑。</li>
 * </ul>
 *
 * @author matianyu
 * @date 2025-12-08
 */
public class PluginLocalDebugger {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   OneBase 插件本地调试模式                  ║");
        System.out.println("║   快速验证 CustomFunction 核心逻辑          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        System.out.println("💡 提示：可以在扩展点类中打断点进行调试\n");

        // 测试自定义函数
        testCustomFunctions();

        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║   ✅ 调试完成！");
        System.out.println("║   📝 建议：完整集成测试请部署到平台验证      ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    /**
     * 测试自定义函数
     */
    private static void testCustomFunctions() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 测试自定义函数（CustomFunction）");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // 测试数学函数
        System.out.println("【1】测试 MathFunctions");
        MathFunctions mathFunctions = new MathFunctions();
        System.out.println("  函数名称: " + mathFunctions.name());
        System.out.println("  函数说明: " + mathFunctions.description());
        
        try {
            Object result1 = mathFunctions.execute(null, "add", 10, 20, 2);
            System.out.println("  ✓ add(10, 20) = " + result1);

            Object result2 = mathFunctions.execute(null, "multiply", 5, 6, 2);
            System.out.println("  ✓ multiply(5, 6) = " + result2);

            Object result3 = mathFunctions.execute(null, "divide", 100, 3, 2);
            System.out.println("  ✓ divide(100, 3) = " + result3);

            Object result4 = mathFunctions.execute(null, "percent", 0.85, null, 2);
            System.out.println("  ✓ percent(0.85) = " + result4 + "%");
        } catch (Exception e) {
            System.err.println("  ✗ 执行失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 测试字符串函数
        System.out.println("\n【2】测试 StringFunctions");
        StringFunctions stringFunctions = new StringFunctions();
        System.out.println("  函数名称: " + stringFunctions.name());
        System.out.println("  函数说明: " + stringFunctions.description());
        
        try {
            Object result1 = stringFunctions.execute(null, "upper", "hello world");
            System.out.println("  ✓ upper('hello world') = " + result1);

            Object result2 = stringFunctions.execute(null, "concat", "Hello", " ", "OneBase");
            System.out.println("  ✓ concat('Hello', ' ', 'OneBase') = " + result2);

            Object result3 = stringFunctions.execute(null, "substring", "OneBase Platform", 0, 7);
            System.out.println("  ✓ substring('OneBase Platform', 0, 7) = " + result3);
        } catch (Exception e) {
            System.err.println("  ✗ 执行失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

}
