/**
 * 天工主题加载器
 * 返回天工主题的加载函数
 */

export const themeLoader = async () => {
  // 加载天工主题样式
  // 实际主题文件位于 apps/app-builder/src/themes/theme_tiangong.less
  // 由 main.tsx 根据平台配置加载
  console.log('[Tiangong] Theme config loaded');
};

// 天工主题名称
export const themeName = 'tiangong';