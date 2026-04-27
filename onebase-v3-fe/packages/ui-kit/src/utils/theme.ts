import { envConfig } from '@onebase/common';

/** 主题加载器：主题名 -> 返回 Promise 的加载函数（如 () => import('xxx.less')），由调用方传入 */
export type ThemeLoaders = Record<string, () => Promise<unknown>>;

/**
 * @deprecated 已迁移到 `@onebase/common` 包，请使用 `import { loadTheme } from '@onebase/common'`
 * 根据 envConfig.THEME 加载对应主题样式，主题 less 地址由 themeLoaders 传参提供
 */
export async function loadTheme(themeLoaders: ThemeLoaders): Promise<void> {
  const rawTheme = envConfig?.THEME;
  console.log('[ThemeLoader] Raw theme:', rawTheme);
  const theme: string =
    typeof rawTheme === 'string' && rawTheme in themeLoaders ? rawTheme : 'tiangong';
    console.log('[ThemeLoader] Loading theme:', theme);

  const loader = themeLoaders[theme] ?? themeLoaders.default;
  if (loader) await loader();
}
