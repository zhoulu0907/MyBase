import { listApplicationMenu, type ApplicationMenu, runtimeListApplicationBPMMenu } from '@onebase/app';


/**
 * 菜单缓存管理器
 * 单例模式，确保菜单数据只加载一次
 */
class MenuCacheManager {
  private menuCache: Map<string, ApplicationMenu[]> = new Map();
  private loadingPromises: Map<string, Promise<ApplicationMenu[]>> = new Map();
  private bpmMenuCache: Map<string, ApplicationMenu[]> = new Map();
  private bpmLoadingPromises: Map<string, Promise<ApplicationMenu[]>> = new Map();

  /**
   * 获取应用菜单数据（带缓存）
   * @param appId 应用ID
   * @returns 菜单列表
   */
  async getMenuList(appId: string): Promise<ApplicationMenu[]> {
    // 如果已有缓存，直接返回
    if (this.menuCache.has(appId)) {
      return this.menuCache.get(appId)!;
    }

    // 如果正在加载中，返回同一个 Promise
    if (this.loadingPromises.has(appId)) {
      return this.loadingPromises.get(appId)!;
    }

    // 开始加载菜单
    const loadPromise = listApplicationMenu({ applicationId: appId }).then((res: ApplicationMenu[]) => {
      const menuList = res || [];
      this.menuCache.set(appId, menuList);
      this.loadingPromises.delete(appId);
      return menuList;
    });

    this.loadingPromises.set(appId, loadPromise);
    return loadPromise;
  }

  /**
   * 获取应用 BPM 菜单数据（带缓存）
   * @param appId 应用ID
   * @returns BPM 菜单列表
   */
  async getBPMMenuList(appId: string): Promise<ApplicationMenu[]> {
    // 如果已有缓存，直接返回
    if (this.bpmMenuCache.has(appId)) {
      return this.bpmMenuCache.get(appId)!;
    }

    // 如果正在加载中，返回同一个 Promise
    if (this.bpmLoadingPromises.has(appId)) {
      return this.bpmLoadingPromises.get(appId)!;
    }

    // 加载 BPM 菜单
    const loadPromise = runtimeListApplicationBPMMenu({ applicationId: appId }).then((res: ApplicationMenu[]) => {
      const menuList = res || [];
      this.bpmMenuCache.set(appId, menuList);
      this.bpmLoadingPromises.delete(appId);
      return menuList;
    });

    this.bpmLoadingPromises.set(appId, loadPromise);
    return loadPromise;
  }

  /**
   * 清除指定应用的菜单缓存
   * @param appId 应用ID
   */
  clearCache(appId?: string): void {
    if (appId) {
      this.menuCache.delete(appId);
      this.loadingPromises.delete(appId);
      this.bpmMenuCache.delete(appId);
      this.bpmLoadingPromises.delete(appId);
    } else {
      this.menuCache.clear();
      this.loadingPromises.clear();
      this.bpmMenuCache.clear();
      this.bpmLoadingPromises.clear();
    }
  }
}

const menuCacheManager = new MenuCacheManager();

export { menuCacheManager };