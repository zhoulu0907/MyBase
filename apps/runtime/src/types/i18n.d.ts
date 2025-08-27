declare module '*.json' {
  const value: any;
  export default value;
}

declare module 'i18next' {
  interface CustomTypeOptions {
    defaultNS: 'translation';
    resources: {
      'zh-CN': typeof import('../i18n/locales/zh-CN.json');
      'en-US': typeof import('../i18n/locales/en-US.json');
    };
  }
}
