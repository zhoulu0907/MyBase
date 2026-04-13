/**
 * 平台选择器
 * 根据 config.js 中的 PLATFORM 字段选择对应的平台包
 */

import * as defaultProduct from './default/index';

// 平台包的导出类型
export interface PlatformExports {
  config: {
    platform: string;
    platformName: string;
    features: {
      supervisionPlugin: boolean;
      ssoLogin: boolean;
      oauthLogin: boolean;
    };
    routes: {
      callback: string;
    };
  };
  usePlatformInit?: () => void;
  useSupervisionPlugin?: () => {
    isInitialized: boolean;
    updatePageInfo: (moduleCode?: string, menuCode?: string) => void;
    show: () => void;
    hide: () => void;
  };
  LingjiCallback?: React.ComponentType<any>;
  TiangongOAuthCallback?: React.ComponentType<any>;
  LingjiAppCard?: React.ComponentType<any>;
  TiangongAppCard?: React.ComponentType<any>;
  LingjiLayout?: React.ComponentType<any>;
  TiangongLayout?: React.ComponentType<any>;
  LingjiSider?: React.ComponentType<any>;
  TiangongSider?: React.ComponentType<any>;
  VerticalMenuItem?: React.ComponentType<any>;
  PlatformRoutes?: React.ComponentType<any>;
  themeLoader?: () => Promise<void>;
  themeName?: string;
}

// 获取平台 ID
function getPlatformId(): string {
  if (typeof window === 'undefined') return 'default';

  // 优先使用 PLATFORM 配置
  let platform = (window as any).global_config?.PLATFORM;

  // 如果 PLATFORM 不存在，使用 THEME 的值作为 PLATFORM
  if (!platform) {
    const theme = (window as any).global_config?.THEME;
    if (theme === 'lingji' || theme === 'tiangong') {
      platform = theme;
      // 将 PLATFORM 写入 global_config，避免后续重复判断
      if ((window as any).global_config) {
        (window as any).global_config.PLATFORM = theme;
      }
    }
  }

  if (platform) return platform;

  // 兼容：通过域名判断
  if (window.location.hostname.includes('artifex-cmcc')) return 'tiangong';

  return 'default';
}

// 动态加载平台包
async function loadPlatformPackage(platformId: string): Promise<PlatformExports> {
  try {
    switch (platformId) {
      case 'lingji':
        const lingjiModule = await import('@onebase/product-lingji');
        console.log('[Platform] 已加载平台: lingji');
        return lingjiModule as PlatformExports;
      case 'tiangong':
        const tiangongModule = await import('@onebase/product-tiangong');
        console.log('[Platform] 已加载平台: tiangong');
        return tiangongModule as PlatformExports;
      default:
        console.log('[Platform] 使用默认平台');
        return defaultProduct;
    }
  } catch (e) {
    console.warn('[Platform] 平台包加载失败，使用默认实现:', e);
    return defaultProduct;
  }
}

// 缓存加载的平台包
let cachedPlatform: PlatformExports | null = null;
let cachedPlatformId: string | null = null;

/**
 * 获取平台导出
 */
export async function getPlatformExports(): Promise<PlatformExports> {
  const platformId = getPlatformId();

  // 如果平台ID发生变化，清除缓存重新加载
  if (cachedPlatform && cachedPlatformId !== platformId) {
    console.log('[Platform] 平台ID变化，重新加载:', cachedPlatformId, '->', platformId);
    cachedPlatform = null;
  }

  if (cachedPlatform) {
    return cachedPlatform;
  }

  cachedPlatformId = platformId;
  cachedPlatform = await loadPlatformPackage(platformId);
  return cachedPlatform;
}

/**
 * 同步获取平台 ID
 */
export function getPlatform(): string {
  return getPlatformId();
}

/**
 * 判断是否为灵畿平台
 */
export function isLingjiPlatform(): boolean {
  return getPlatformId() === 'lingji';
}

/**
 * 判断是否为天工平台
 */
export function isTiangongPlatform(): boolean {
  return getPlatformId() === 'tiangong';
}

/**
 * 判断是否为天工域名（artifex-cmcc）
 * 兼容旧的 isArtifexDomain 函数
 */
export function isArtifexDomain(): boolean {
  return getPlatformId() === 'tiangong';
}

// 导出默认实现的便捷方法
export { defaultProduct };