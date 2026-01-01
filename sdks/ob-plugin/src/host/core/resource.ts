import type { LoadedPlugin } from '../../sdk/types';
// 资源缓存：避免重复加载同一 URL，提升稳定性与性能
const cssCache = new Set<string>();
const jsCache = new Map<string, Promise<Omit<LoadedPlugin, 'meta'>>>();

/** 加载 CSS 资源 */
export function loadCss(url: string): Promise<void> {
  if (cssCache.has(url)) return Promise.resolve();
  return new Promise((resolve, reject) => {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = url;
    // 预注册，失败时回滚
    cssCache.add(url);
    link.onload = () => resolve();
    link.onerror = () => {
      cssCache.delete(url);
      reject(new Error(`Failed to load CSS: ${url}`));
    };
    document.head.appendChild(link);
  });
}

/** 加载 UMD JS 资源并从 window 获取导出对象 */
export function loadJs(url: string): Promise<Omit<LoadedPlugin, 'meta'>> {
  const cached = jsCache.get(url);
  if (cached) return cached;
  const promise = new Promise<Omit<LoadedPlugin, 'meta'>>((resolve, reject) => {
    const script = document.createElement('script');
    script.src = url;
    script.type = 'text/javascript';
    script.async = true;
    // 临时覆盖 require 以支持 UMD 插件依赖的公共包
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
      // 恢复原环境，避免影响宿主其它逻辑
      (window as any).require = prevRequire;
      if (!plugin) {
        jsCache.delete(url);
        reject(new Error(`Plugin ${base} not found in window`));
      } else {
        resolve(plugin);
      }
    };
    script.onerror = () => {
      (window as any).require = prevRequire;
      jsCache.delete(url);
      reject(new Error(`Failed to load JS: ${url}`));
    };
    document.body.appendChild(script);
  });
  jsCache.set(url, promise);
  return promise;
}
