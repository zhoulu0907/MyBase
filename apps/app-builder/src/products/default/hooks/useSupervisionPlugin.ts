/**
 * 默认监督插件 hook
 * 返回空实现
 */
export function useSupervisionPlugin() {
  return {
    isInitialized: false,
    updatePageInfo: () => {},
    show: () => {},
    hide: () => {},
  };
}