// 支持的语言列表
export const SUPPORTED_LANGUAGES = {
  'zh-CN': {
    name: '中文',
    flag: '🇨🇳',
    direction: 'ltr'
  },
  'en-US': {
    name: 'English',
    flag: '🇺🇸',
    direction: 'ltr'
  }
} as const;

export type SupportedLanguage = keyof typeof SUPPORTED_LANGUAGES;

// 获取语言信息
export const getLanguageInfo = (language: SupportedLanguage) => {
  return SUPPORTED_LANGUAGES[language];
};

// 格式化数字
export const formatNumber = (value: number, language: string, options?: Intl.NumberFormatOptions): string => {
  return new Intl.NumberFormat(language, options).format(value);
};

// 格式化日期
export const formatDate = (date: Date, language: string, options?: Intl.DateTimeFormatOptions): string => {
  return new Intl.DateTimeFormat(language, options).format(date);
};

// 格式化货币
export const formatCurrency = (value: number, language: string, currency = 'CNY'): string => {
  return new Intl.NumberFormat(language, {
    style: 'currency',
    currency
  }).format(value);
};
