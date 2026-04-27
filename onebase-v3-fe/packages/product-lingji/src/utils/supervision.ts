/**
 * 监督插件管理模块
 * 用于管理监督插件的加载、初始化、埋点更新和销毁
 */

import { UserPermissionManager } from '@onebase/common';

// 监督插件配置接口
interface SupervisionPluginConfig {
  ENABLE: boolean;
  PLATFORM: string;
  SUPERVISION_URL: string;
  SSO_URL: string;
  // 模块编码（固定的编码）
  MODULE_CODE?: string;
  // 菜单编码映射（路由对应的菜单编码）
  MENU_CODES?: Record<string, string>;
  // 默认菜单编码
  DEFAULT_MENU_CODE?: string;
}

// 埋点信息接口
interface PageInfo {
  moduleCode?: string;
  menuCode?: string;
  error?: string;
}

// 监督插件实例接口
interface SupervisionPluginInstance {
  init: (config: {
    platform: string;
    mobile: string;
    superVisionUrl: string;
    ssoUrl: string;
  }) => void;
  updatePageInfo: (info: PageInfo) => void;
  destroy: () => void;
}

// 全局监督插件类
declare global {
  interface Window {
    SupervisionPlugin: {
      new (): SupervisionPluginInstance;
      showPlugin: () => void;
      hidePlugin: () => void;
    };
    supervisionPluginInstance?: SupervisionPluginInstance;
  }
}

let isLoaded = false;
let isInitialized = false;
let isInitializing = false;  // 防止并发初始化

/**
 * 获取监督插件配置
 */
function getPluginConfig(): SupervisionPluginConfig | null {
  const config = (window as any).supervision_config;
  console.log('[SupervisionPlugin] 读取配置:', config);
  if (!config) {
    console.warn('[SupervisionPlugin] 配置未找到，请检查 supervision.config.js 配置文件');
    return null;
  }
  return config;
}

/**
 * 动态加载监督插件 JS 文件
 */
async function loadPluginScript(): Promise<boolean> {
  if (isLoaded) {
    return true;
  }

  const config = getPluginConfig();
  if (!config || !config.ENABLE) {
    console.log('[SupervisionPlugin] 插件未启用');
    return false;
  }

  return new Promise((resolve) => {
    const script = document.createElement('script');
    const time = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    script.src = `./supervision/plugin/SupervisionPlugin.umd.js?t=${time}`;
    script.defer = true;

    script.onload = () => {
      isLoaded = true;
      console.log('[SupervisionPlugin] JS 文件加载成功');
      resolve(true);
    };

    script.onerror = () => {
      console.error('[SupervisionPlugin] JS 文件加载失败');
      resolve(false);
    };

    document.head.appendChild(script);
  });
}

/**
 * 初始化监督插件
 * 需要在用户登录成功后调用
 */
export async function initSupervisionPlugin(): Promise<boolean> {
  const config = getPluginConfig();
  if (!config || !config.ENABLE) {
    console.log('[SupervisionPlugin] 插件未启用，跳过初始化');
    return false;
  }

  // 防止并发初始化
  if (isInitialized || isInitializing) {
    console.log('[SupervisionPlugin] 插件已初始化或正在初始化');
    return true;
  }

  isInitializing = true;

  // 加载 JS 文件
  const loaded = await loadPluginScript();
  if (!loaded) {
    isInitializing = false;
    return false;
  }

  // 检查 SupervisionPlugin 类是否存在
  if (!window.SupervisionPlugin) {
    console.error('[SupervisionPlugin] SupervisionPlugin 类未找到');
    isInitializing = false;
    return false;
  }

  // 获取用户手机号
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();
  const mobile = userPermissionInfo?.user?.mobile || '';

  if (!mobile) {
    console.warn('[SupervisionPlugin] 用户手机号未找到，插件可能无法正常工作');
  }

  try {
    // 创建插件实例
    const instance = new window.SupervisionPlugin();
    window.supervisionPluginInstance = instance;

    // 初始化插件
    instance.init({
      platform: config.PLATFORM,
      mobile: mobile,
      superVisionUrl: config.SUPERVISION_URL,
      ssoUrl: config.SSO_URL
    });

    isInitialized = true;
    console.log('[SupervisionPlugin] 插件初始化成功');
    return true;
  } catch (e) {
    console.error('[SupervisionPlugin] 插件初始化失败:', e);
    return false;
  } finally {
    isInitializing = false;
  }
}

/**
 * 更新页面埋点信息
 * @param moduleCode 模块编码
 * @param menuCode 菜单编码
 * @param error 错误信息（可选）
 */
export function updatePageInfo(moduleCode?: string, menuCode?: string, error?: string): void {
  if (!isInitialized || !window.supervisionPluginInstance) {
    return;
  }

  const info: PageInfo = {};
  if (moduleCode) info.moduleCode = moduleCode;
  if (menuCode) info.menuCode = menuCode;
  if (error) info.error = error;

  try {
    window.supervisionPluginInstance.updatePageInfo(info);
    console.log('[SupervisionPlugin] 更新埋点信息:', info);
  } catch (e) {
    console.error('[SupervisionPlugin] 更新埋点信息失败:', e);
  }
}

/**
 * 更新错误信息
 * @param error 错误信息
 */
export function updateErrorInfo(error: string): void {
  updatePageInfo(undefined, undefined, error);
}

/**
 * 显示监督插件
 */
export function showPlugin(): void {
  if (!isLoaded) {
    console.warn('[SupervisionPlugin] 插件未加载');
    return;
  }

  try {
    if (window.SupervisionPlugin && typeof window.SupervisionPlugin.showPlugin === 'function') {
      window.SupervisionPlugin.showPlugin();
      console.log('[SupervisionPlugin] 显示插件');
    }
  } catch (e) {
    console.error('[SupervisionPlugin] 显示插件失败:', e);
  }
}

/**
 * 隐藏监督插件
 */
export function hidePlugin(): void {
  if (!isLoaded) {
    return;
  }

  try {
    if (window.SupervisionPlugin && typeof window.SupervisionPlugin.hidePlugin === 'function') {
      window.SupervisionPlugin.hidePlugin();
      console.log('[SupervisionPlugin] 隐藏插件');
    }
  } catch (e) {
    console.error('[SupervisionPlugin] 隐藏插件失败:', e);
  }
}

/**
 * 销毁监督插件
 * 在用户退出登录时调用
 */
export function destroyPlugin(): void {
  if (!isInitialized || !window.supervisionPluginInstance) {
    return;
  }

  try {
    window.supervisionPluginInstance.destroy();
    window.supervisionPluginInstance = undefined;
    isInitialized = false;
    console.log('[SupervisionPlugin] 插件已销毁');
  } catch (e) {
    console.error('[SupervisionPlugin] 销毁插件失败:', e);
  }
}

/**
 * 从路由路径提取模块编码和菜单编码
 * @param pathname 路由路径
 * @returns 如果路由不在 MENU_CODES 配置中，返回 null 表示不需要更新
 */
export function extractRouteInfo(pathname: string): { moduleCode: string; menuCode: string } | null {
  const config = getPluginConfig();

  // 移除租户ID前缀
  // 路径格式: /onebase/:tenantId/setting/user 或 /onebase/:tenantId/home/create-app/data-factory
  const pathWithoutTenant = pathname.replace(/^\/onebase\/[^/]+/, '');

  // 提取路由层级
  const parts = pathWithoutTenant.split('/').filter(Boolean);

  // 模块编码：使用配置中的固定值
  const moduleCode = config?.MODULE_CODE || 'CMDEVOPS-ZEROCODE';

  // 菜单编码：从路由最后一级提取（一级菜单 key）
  // 如 /setting/user -> user, /home/create-app/data-factory -> data-factory
  const menuKey = parts.length > 0 ? parts[parts.length - 1] : 'home';

  // 从配置获取菜单编码
  const menuCode = config?.MENU_CODES?.[menuKey];

  // 如果不在 MENU_CODES 配置中，返回 null 表示不需要更新
  if (!menuCode) {
    return null;
  }

  return { moduleCode, menuCode };
}

/**
 * 检查插件是否已初始化
 */
export function isPluginInitialized(): boolean {
  return isInitialized;
}

/**
 * 检查插件是否已加载
 */
export function isPluginLoaded(): boolean {
  return isLoaded;
}