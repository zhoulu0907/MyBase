import EventEmitter from 'eventemitter3';
import { type PluginMeta, type HostEvents, type PluginStatus } from '../sdk/types';

/**
 * 插件注册中心：管理注册、加载、卸载、失效
 */
export class HostRegistry {
  private emitter: EventEmitter<HostEvents>;
  private plugins = new Map<string, PluginMeta>();
  private status = new Map<string, PluginStatus>();

  constructor(emitter?: EventEmitter<HostEvents>) {
    this.emitter = emitter ?? new EventEmitter<HostEvents>();
  }

  /** 获取事件总线 */
  get bus(): EventEmitter<HostEvents> {
    return this.emitter;
  }

  /** 注册插件（仅记录元数据，不加载） */
  register(meta: PluginMeta): void {
    this.plugins.set(meta.name, meta);
    this.status.set(meta.name, 'registered');
    this.emitter.emit('plugin:registered', { meta });
  }

  /** 加载插件（宿主可在此处执行资源加载与挂载） */
  load(name: string): void {
    if (!this.plugins.has(name)) return;
    this.status.set(name, 'loaded');
    this.emitter.emit('plugin:loaded', { id: name });
  }

  /** 卸载插件（宿主可在此处执行卸载与清理） */
  unload(name: string): void {
    if (!this.plugins.has(name)) return;
    this.status.set(name, 'unloaded');
    this.emitter.emit('plugin:unloaded', { id: name });
  }

  /** 使插件失效（移除所有注册信息） */
  invalidate(name: string): void {
    if (!this.plugins.has(name)) return;
    this.plugins.delete(name);
    this.status.set(name, 'invalidated');
    this.emitter.emit('plugin:invalidated', { id: name });
  }

  /** 获取插件定义 */
  get(name: string): PluginMeta | undefined {
    return this.plugins.get(name);
  }

  /** 获取插件状态 */
  getStatus(name: string): PluginStatus | undefined {
    return this.status.get(name);
  }

  /** 列出所有插件 */
  list(): PluginMeta[] {
    return [...this.plugins.values()];
  }
}
