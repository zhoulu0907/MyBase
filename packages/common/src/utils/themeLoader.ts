type ThemeMap = Record<string, () => Promise<unknown>>;

export interface LoadThemeOptions {
  /** 主题名称 */
  theme: string;
  /** 主题映射，key 是主题名，value 是动态导入函数 */
  themeMap: ThemeMap;
  /** 插入位置的 CSS 选择器，主题样式会插入到该元素之后 */
  insertAfterSelector?: string;
  /** 默认主题名称，当 theme 不在 themeMap 中时使用 */
  defaultTheme?: string;
}

/**
 * 加载主题样式并插入到指定位置
 * 动态导入的 CSS 默认会追加到 head 末尾，此方法会将其移到指定位置
 */
export async function loadThemeAtPosition(options: LoadThemeOptions): Promise<void> {
  const { theme, themeMap, insertAfterSelector, defaultTheme } = options;

  // 确定要加载的主题
  const actualTheme = themeMap[theme] ? theme : defaultTheme;
  if (!actualTheme || !themeMap[actualTheme]) {
    console.warn(`[ThemeLoader] Theme "${theme}" not found in themeMap`);
    return;
  }

  console.log(`[ThemeLoader] Loading theme: ${actualTheme}`);

  // 找到插入位置
  let insertAfter: Element | null = null;
  if (insertAfterSelector) {
    insertAfter = document.head.querySelector(insertAfterSelector);
    if (!insertAfter) {
      // 尝试查找 style 元素
      insertAfter = document.head.querySelector('style');
    }
  } else {
    insertAfter = document.head.querySelector('style');
  }

  if (!insertAfter) {
    console.warn('[ThemeLoader] Insert position not found, loading theme without repositioning');
    await themeMap[actualTheme]();
    return;
  }

  // 记录当前 style 数量
  const styleCountBefore = document.head.querySelectorAll('style').length;

  // 加载主题
  await themeMap[actualTheme]();

  // 把新加载的主题样式移到指定位置之后
  const allStyles = document.head.querySelectorAll('style');
  const newStyles = Array.from(allStyles).slice(styleCountBefore);

  // 依次插入到目标位置之后（保持顺序）
  let currentInsertAfter = insertAfter;
  newStyles.forEach(style => {
    currentInsertAfter.after(style);
    currentInsertAfter = style;
  });
}