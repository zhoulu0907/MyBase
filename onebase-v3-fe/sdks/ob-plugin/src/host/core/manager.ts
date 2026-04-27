import EventEmitter from 'eventemitter3';
import { createHostSDK } from '../../sdk';
import { loadCss, loadJs } from './resource';
import { validatePlugin } from './validator';
import type { Context, HostSDK, LoadedPlugin, PluginMeta, PluginRegistry, HostEvents, Entity, Field, UIAPI, PluginMethodMeta } from '../../sdk/types';

/** 插件管理器：负责插件注册、资源加载、初始化/销毁、方法调用与事件分发 */
export class PluginManager {
  private emitter: EventEmitter<HostEvents>;
  private plugins = new Map<string, LoadedPlugin>();
  private registry = new Map<string, PluginRegistry>();
  private sdk: HostSDK;

  constructor(
    context?: Omit<Context, 'entity'> & { entity?: Partial<Context['entity']> },
    emitter?: EventEmitter<HostEvents>,
    overrides?: {
      ui?: UIAPI;
      entities?: Entity[];
      fields?: Record<string, Field[]>;
    }
  ) {
    this.emitter = emitter ?? new EventEmitter<HostEvents>();
    const ctx = context ?? { terminal: 'PC' };
    this.sdk = createHostSDK(ctx, overrides);
  }

  /** 事件总线 */
  get bus(): EventEmitter<HostEvents> {
    return this.emitter;
  }

  /** 计算注册主键：优先使用 routePrefix，其次 name */
  private getKey(meta: PluginMeta): string {
    return meta.routePrefix || meta.name;
  }

  /** 根据传入标识解析真实 key（兼容 name 或 routePrefix） */
  private resolveKey(id: string): string {
    if (this.registry.has(id)) return id;
    for (const [key, reg] of this.registry.entries()) {
      if (reg.meta.name === id || reg.meta.routePrefix === id) return key;
    }
    return id;
  }

  /** 仅登记元信息，不加载资源（主键为 routePrefix） */
  registerPlugin(meta: PluginMeta): void {
    const key = this.getKey(meta);
    if (this.registry.has(key)) return;
    this.registry.set(key, { meta, status: 'registered' });
    this.emitter.emit('plugin:registered', { meta });
  }

  /** 加载并初始化插件 */
  async loadPlugin(pluginId: string): Promise<LoadedPlugin> {
    // console.error(`ob-plugin-template 插件加载中 ${pluginId}`)
    const key = this.resolveKey(pluginId);
    const item = this.registry.get(key);
    if (!item) throw new Error(`Plugin ${pluginId} not registered`);
    if (item.status === 'loading') throw new Error(`Plugin ${pluginId} is loading`);
    if (item.status === 'loaded') {
      const exist = this.plugins.get(key);
      if (exist) return exist;
    }
    item.status = 'loading';
    const { meta } = item;
    try {
      if (meta.resources?.css) {
        try {
          await loadCss(meta.resources.css);
        } catch (cssError) {
          this.sdk.ui.reportError(cssError, { scope: 'plugin:css' } as any);
          // CSS 加载失败不影响主流程；继续加载 JS
        }
      }
      const plugin = await loadJs(meta.resources?.js ?? '');
      validatePlugin(plugin, meta);
      const loaded: LoadedPlugin = { ...plugin, meta } as LoadedPlugin;
      this.plugins.set(key, loaded);
      item.status = 'loaded';
      if (loaded.initialize) await loaded.initialize(this.sdk);
      this.emitter.emit('plugin:loaded', { id: key });
      return loaded;
    } catch (error) {
      // 统一上报 JS 加载错误，便于监控与定位
      console.error(`ob-plugin-template 插件加载失败 ${pluginId}`, error);
      this.sdk.ui.reportError(error, { scope: 'plugin:js' } as any);
      item.status = 'error';
      this.emitter.emit('plugin:error', { id: key, error });
      throw error;
    }
  }

  /** 卸载插件并执行销毁逻辑 */
  async unloadPlugin(pluginId: string): Promise<void> {
    const key = this.resolveKey(pluginId);
    const plugin = this.plugins.get(key);
    if (!plugin) return;
    if (plugin.destroy) await plugin.destroy();
    this.plugins.delete(key);
    const reg = this.registry.get(key);
    // 使用真实 key 写回注册表，避免 name/routePrefix 混用导致状态项错位
    if (reg) this.registry.set(key, { ...reg, status: 'registered' });
    this.emitter.emit('plugin:unloaded', { id: key });
  }

  /** 跨插件方法调用 */
  // 方法发现：优先返回结构化元信息，便于宿主生成文档或可视化
  listMethods(pluginId: string): (string | PluginMethodMeta)[] {
    const key = this.resolveKey(pluginId);
    const plugin = this.plugins.get(key);
    if (!plugin) return [];
    if (plugin.methodsMeta) return Object.values(plugin.methodsMeta);
    return Object.keys(plugin.methods || {});
  }

  async callMethod(pluginId: string, methodKey: string, ...args: any[]): Promise<any> {
    const key = this.resolveKey(pluginId);
    const plugin = this.plugins.get(key);
    if (!plugin) throw new Error(`Plugin ${pluginId} not loaded`);
    const fn = plugin.methods?.[methodKey];
    if (!fn || typeof fn !== 'function') throw new Error(`Plugin ${pluginId} has no method ${methodKey}`);
    const tail = args[args.length - 1];
    // 可选调用参数：支持超时与 SDK 注入；不破坏原有签名
    const hasOptions = tail && typeof tail === 'object' && ('timeout' in tail || 'injectSDK' in tail);
    const options = hasOptions ? (args.pop() as any) : {};
    this.emitter.emit('plugin:method:start', { id: key, method: methodKey });
    const exec = async () => (options && options.injectSDK ? await (fn as any)(this.sdk, ...args) : await fn(...args));
    try {
      if (options && typeof options.timeout === 'number' && options.timeout > 0) {
        const timeoutPromise = new Promise((_, reject) => setTimeout(() => reject(new Error(`Method ${methodKey} timeout`)), options.timeout));
        const result = await Promise.race([exec(), timeoutPromise]);
        this.emitter.emit('plugin:method:end', { id: key, method: methodKey });
        return result;
      } else {
        const result = await exec();
        this.emitter.emit('plugin:method:end', { id: key, method: methodKey });
        return result;
      }
    } catch (error) {
      this.emitter.emit('plugin:method:error', { id: key, method: methodKey, error });
      throw error;
    }
  }

  /** 获取所有已加载插件 */
  getLoadedPlugins(): Record<string, LoadedPlugin> {
    return Object.fromEntries(this.plugins);
  }
}
