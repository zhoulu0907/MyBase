import type { LoadedPlugin } from '../../sdk/types';

/** 加载 CSS 资源 */
export function loadCss(url: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = url;
    link.onload = () => resolve();
    link.onerror = () => reject(new Error(`Failed to load CSS: ${url}`));
    document.head.appendChild(link);
  });
}

/** 加载 UMD JS 资源并从 window 获取导出对象 */
export function loadJs(url: string): Promise<Omit<LoadedPlugin, 'meta'>> {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = url;
    script.type = 'text/javascript';
    script.onload = () => {
      const base = url.split('/').pop()?.split('.')[0] ?? '';
      const plugin = (window as any)[base];
      if (!plugin) {
        reject(new Error(`Plugin ${base} not found in window`));
      } else {
        resolve(plugin);
      }
    };
    script.onerror = () => reject(new Error(`Failed to load JS: ${url}`));
    document.body.appendChild(script);
  });
}

