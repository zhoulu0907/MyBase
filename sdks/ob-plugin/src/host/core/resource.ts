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
    const prevRequire = (window as any).require;
    (window as any).require = (name: string) => {
      if (name === 'react') return (window as any).React;
      if (name === '@arco-design/web-react') return (window as any).Arco;
      if (name === 'react-router-dom') return (window as any).ReactRouterDOM;
      throw new Error(`Unsupported require: ${name}`);
    };
    script.onload = () => {
      const base = url.split('/').pop()?.split('.')[0] ?? '';
      const snake = base.replace(/-/g, '_');
      const camel = base.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
      const plugin = (window as any)[base] || (window as any)[snake] || (window as any)[camel];
      (window as any).require = prevRequire;
      if (!plugin) {
        reject(new Error(`Plugin ${base} not found in window`));
      } else {
        resolve(plugin);
      }
    };
    script.onerror = () => {
      (window as any).require = prevRequire;
      reject(new Error(`Failed to load JS: ${url}`));
    };
    document.body.appendChild(script);
  });
}
