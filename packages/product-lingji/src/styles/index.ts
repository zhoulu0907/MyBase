/**
 * 灵畿主题加载器
 * 返回灵畿主题的加载函数
 */

export const themeLoader = async () => {
  // 加载灵畿主题样式
  // 实际主题文件位于 apps/app-builder/src/themes/theme_lingji.less
  // 由 main.tsx 根据平台配置加载
  console.log('[Lingji] Theme config loaded');
};

// 灵畿主题名称
export const themeName = 'lingji';