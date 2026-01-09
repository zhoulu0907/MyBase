/**
 * 插件方法集合（Plugin Methods）
 *
 * 作用：对外暴露可复用的业务方法，宿主或其他插件可通过 `registerMethods` 调用。
 * 建议：方法需纯净、可测试，避免直接依赖 UI。
 */
export const pluginMethods = {
  /**
   * 文本格式化：在文本前添加当前时间戳
   * @param text 原始文本
   * @returns 带时间前缀的文本，例如 "[12:00:00] hello"
   */
  formatText: (text: string) => `[${new Date().toLocaleTimeString()}] ${text}`,

  /**
   * 数据处理示例：模拟异步加工并返回带标记的数据
   * @param data 原始数据对象
   * @returns 处理后的数据 `{ ...data, processed: true, from: 'ob-plugin-template' }`
   */
  processData: async (data: any) => new Promise(resolve => {
    setTimeout(() => {
      resolve({ ...data, processed: true, from: 'ob-plugin-template' })
    }, 1000)
  })
}
