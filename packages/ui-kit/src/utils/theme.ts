/** 主题加载器：主题名 -> 返回 Promise 的加载函数（如 () => import('xxx.less')），由调用方传入 */
export type ThemeLoaders = Record<string, () => Promise<unknown>>;

/** 根据 global_config.THEME 加载对应主题样式，主题 less 地址由 themeLoaders 传参提供 */
export async function loadTheme(themeLoaders: ThemeLoaders): Promise<void> {
  const rawTheme =
    typeof window !== 'undefined'
      ? (window as unknown as { global_config?: { THEME?: string } }).global_config?.THEME
      : undefined;
  const theme: string =
    typeof rawTheme === 'string' && rawTheme in themeLoaders ? rawTheme : 'default';
  const loader = themeLoaders[theme] ?? themeLoaders.default;
  if (loader) await loader();
}
