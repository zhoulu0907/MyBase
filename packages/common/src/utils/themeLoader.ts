export type ThemeLoaders = Record<string, () => Promise<unknown>>;

export interface LoadThemeOptions {
  /** 主题名称 */
  theme: string;
  /** 主题映射，key 是主题名，value 是动态导入函数 */
  themeMap: ThemeLoaders;
  /** 插入到 head 最前面，默认 true */
  insertAtStart?: boolean;
  /** 默认主题名称，当 theme 不在 themeMap 中时使用 */
  defaultTheme?: string;
}

/**
 * 加载主题样式并插入到 head 最前面
 * 动态导入的 CSS 默认会追加到 head 末尾，此方法会将其移到最前面
 */
export async function loadThemeAtPosition(options: LoadThemeOptions): Promise<void> {
  const { theme, themeMap, insertAtStart = true, defaultTheme } = options;

  // 确定要加载的主题
  const actualTheme = themeMap[theme] ? theme : defaultTheme;
  if (!actualTheme || !themeMap[actualTheme]) {
    console.warn(`[ThemeLoader] Theme "${theme}" not found in themeMap`);
    return;
  }

  console.log(`[ThemeLoader] Loading theme: ${actualTheme}`);

  // 记录当前 style 和 link 数量
  const styleCountBefore = document.head.querySelectorAll('style').length;
  const linkCountBefore = document.head.querySelectorAll('link[rel="stylesheet"]').length;

  // 加载主题
  await themeMap[actualTheme]();

  // 如果不需要插入到最前面，直接返回
  if (!insertAtStart) {
    return;
  }

  // 收集新加载的样式元素（style 和 link）
  const allStyles = document.head.querySelectorAll('style');
  const allLinks = document.head.querySelectorAll('link[rel="stylesheet"]');
  const newStyles = Array.from(allStyles).slice(styleCountBefore);
  const newLinks = Array.from(allLinks).slice(linkCountBefore);
  const newElements = [...newStyles, ...newLinks];

  if (newElements.length === 0) {
    return;
  }

  // 将新加载的样式插入到 head 最前面（倒序插入保持原有顺序）
  const firstChild = document.head.firstChild;
  for (let i = newElements.length - 1; i >= 0; i--) {
    document.head.insertBefore(newElements[i], firstChild);
  }
}

/**
 * 加载应用主题样式
 * @param themeLoaders - 主题加载器映射，key 是主题名（如 'tiangong', 'lingji', 'default'），value 是动态导入函数
 * @param theme - 当前主题名称，如果不传则从 window.global_config.THEME 读取
 */
export async function loadTheme(themeLoaders: ThemeLoaders, theme?: string): Promise<void> {
  // 获取主题名称：优先使用传入的 theme，否则从 global_config 读取
  const rawTheme = theme ?? (typeof window !== 'undefined'
    ? (window as unknown as { global_config?: { THEME?: string } }).global_config?.THEME
    : undefined);

  // 确定要加载的主题：优先使用 rawTheme，如果不存在则依次尝试 'tiangong', 'default'
  let actualTheme: string | undefined;
  if (rawTheme && rawTheme in themeLoaders) {
    actualTheme = rawTheme;
  } else if (themeLoaders.tiangong) {
    actualTheme = 'tiangong';
  } else if (themeLoaders.default) {
    actualTheme = 'default';
  }

  if (!actualTheme) {
    console.warn(`[ThemeLoader] No valid theme loader found`);
    return;
  }

  console.log(`[ThemeLoader] Loading app theme: ${actualTheme}`);
  await themeLoaders[actualTheme]();
}