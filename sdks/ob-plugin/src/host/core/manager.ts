import EventEmitter from 'eventemitter3';
import { createHostSDK } from '../../sdk';
import { loadCss, loadJs } from './resource';
import { validatePlugin } from './validator';
import type { Context, HostSDK, LoadedPlugin, PluginMeta, PluginRegistry, HostEvents } from '../../sdk/types';

/** 插件管理器：负责插件注册、资源加载、初始化/销毁、方法调用与事件分发 */
export class PluginManager {
  private emitter: EventEmitter<HostEvents>;
  private plugins = new Map<string, LoadedPlugin>();
  private registry = new Map<string, PluginRegistry>();
  private sdk: HostSDK;

  constructor(context?: Context, emitter?: EventEmitter<HostEvents>) {
    this.emitter = emitter ?? new EventEmitter<HostEvents>();
    const ctx: Context = context ?? { terminal: 'PC' };
    this.sdk = createHostSDK(ctx);
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
      if (meta.resources?.css) await loadCss(meta.resources.css);
      const plugin = await loadJs(meta.resources?.js ?? '');
      validatePlugin(plugin, meta);
      const loaded: LoadedPlugin = { ...plugin, meta } as LoadedPlugin;
      this.plugins.set(key, loaded);
      item.status = 'loaded';
      if (loaded.initialize) await loaded.initialize(this.sdk);
      this.emitter.emit('plugin:loaded', { id: key });
      return loaded;
    } catch (error) {
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
    if (reg) this.registry.set(pluginId, { ...reg, status: 'registered' });
    this.emitter.emit('plugin:unloaded', { id: key });
  }

  /** 跨插件方法调用 */
  async callMethod(pluginId: string, methodKey: string, ...args: any[]): Promise<any> {
    const key = this.resolveKey(pluginId);
    const plugin = this.plugins.get(key);
    if (!plugin) throw new Error(`Plugin ${pluginId} not loaded`);
    const fn = plugin.methods?.[methodKey];
    if (!fn || typeof fn !== 'function') throw new Error(`Plugin ${pluginId} has no method ${methodKey}`);
    return await fn(...args);
  }

  /** 获取所有已加载插件 */
  getLoadedPlugins(): Record<string, LoadedPlugin> {
    return Object.fromEntries(this.plugins);
  }
}
