import { signal } from '@preact/signals-react';

/**
 * 页面布局配置 Signal
 * 用于控制运行时页面的 Header 和 Sidebar 显示状态
 */

export interface PageLayoutConfig {
  showHeader: boolean;
  showSidebar: boolean;
}

export const createPageLayoutSignal = () => {
  const pageLayout = signal<PageLayoutConfig>({
    showHeader: true,
    showSidebar: true
  });

  const setPageLayout = (config: Partial<PageLayoutConfig>) => {
    pageLayout.value = { ...pageLayout.value, ...config };
  };

  const resetPageLayout = () => {
    pageLayout.value = { showHeader: true, showSidebar: true };
  };

  return {
    pageLayout,
    setPageLayout,
    resetPageLayout
  };
};

export const pageLayoutSignal = createPageLayoutSignal();

