export type ThemeLoaders = Record<string, () => Promise<unknown>>;

export interface LoadThemeOptions {
  /** 主题名称 */
  theme: string;
  /** 主题映射，key 是主题名，value 是动态导入函数 */
  themeMap: ThemeLoaders;
  /** 默认主题名称，当 theme 不在 themeMap 中时使用 */
  defaultTheme?: string;
}

// 跟踪上一次插入的样式元素，用于保持加载顺序
let lastInsertedElement: Element | null = null;

/**
 * 加载主题样式并插入到正确位置
 * 第一次调用会插入到 head 最前面，后续调用会插入到上一次加载的样式后面
 * 这样保证加载顺序：arco.css → 主题样式 → 本地覆盖样式
 */
export async function loadThemeAtPosition(options: LoadThemeOptions): Promise<void> {
  const { theme, themeMap, defaultTheme } = options;

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

  // 收集新加载的样式元素（style 和 link）
  const allStyles = document.head.querySelectorAll('style');
  const allLinks = document.head.querySelectorAll('link[rel="stylesheet"]');
  const newStyles = Array.from(allStyles).slice(styleCountBefore);
  const newLinks = Array.from(allLinks).slice(linkCountBefore);
  const newElements = [...newStyles, ...newLinks];

  if (newElements.length === 0) {
    return;
  }

  if (lastInsertedElement) {
    // 后续调用：插入到上一次插入的元素后面
    newElements.forEach(el => {
      lastInsertedElement!.after(el);
      lastInsertedElement = el;
    });
  } else {
    // 第一次调用：插入到 head 最前面
    const firstChild = document.head.firstChild;
    for (let i = newElements.length - 1; i >= 0; i--) {
      document.head.insertBefore(newElements[i], firstChild);
    }
    lastInsertedElement = newElements[newElements.length - 1];
  }
}

/**
 * 重置样式插入位置跟踪（用于切换主题时重新开始）
 */
export function resetThemeInsertPosition(): void {
  lastInsertedElement = null;
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