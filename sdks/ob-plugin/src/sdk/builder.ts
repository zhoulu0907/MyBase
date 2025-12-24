import { LoadedPlugin, PluginMeta, PluginPage, PluginComponent, PluginMethod } from './types';
import { definePlugin } from './define';

/**
 * 插件构建器
 * 提供链式调用的方式来注册插件的各个部分
 */
export class PluginBuilder {
  private plugin: Partial<LoadedPlugin> = {
    pages: {},
    components: {},
    configRenderers: {},
    methods: {}
  };

  constructor(meta: PluginMeta) {
    this.plugin.meta = meta;
  }

  /** 注册页面 */
  registerPage(key: string, page: PluginPage): this {
    if (!this.plugin.pages) this.plugin.pages = {};
    this.plugin.pages[key] = page;
    return this;
  }

  /** 批量注册页面 */
  registerPages(pages: Record<string, PluginPage>): this {
    if (!this.plugin.pages) this.plugin.pages = {};
    Object.assign(this.plugin.pages, pages);
    return this;
  }

  /** 注册组件 */
  registerComponent(key: string, component: PluginComponent): this {
    if (!this.plugin.components) this.plugin.components = {};
    this.plugin.components[key] = component;
    return this;
  }

  /** 批量注册组件 */
  registerComponents(components: Record<string, PluginComponent>): this {
    if (!this.plugin.components) this.plugin.components = {};
    Object.assign(this.plugin.components, components);
    return this;
  }

  /** 注册配置渲染器 */
  registerConfigRenderer(key: string, renderer: PluginComponent): this {
    if (!this.plugin.configRenderers) this.plugin.configRenderers = {};
    this.plugin.configRenderers[key] = renderer;
    return this;
  }

  /** 批量注册配置渲染器 */
  registerConfigRenderers(renderers: Record<string, PluginComponent>): this {
    if (!this.plugin.configRenderers) this.plugin.configRenderers = {};
    Object.assign(this.plugin.configRenderers, renderers);
    return this;
  }

  /** 注册方法 */
  registerMethod(key: string, method: PluginMethod): this {
    if (!this.plugin.methods) this.plugin.methods = {};
    this.plugin.methods[key] = method;
    return this;
  }

  /** 批量注册方法 */
  registerMethods(methods: Record<string, PluginMethod>): this {
    if (!this.plugin.methods) this.plugin.methods = {};
    Object.assign(this.plugin.methods, methods);
    return this;
  }

  /** 设置初始化函数 */
  onInitialize(fn: (sdk: any) => Promise<void>): this {
    this.plugin.initialize = fn;
    return this;
  }

  /** 设置销毁函数 */
  onDestroy(fn: () => Promise<void>): this {
    this.plugin.destroy = fn;
    return this;
  }

  /** 构建最终的插件对象（自动处理前缀） */
  build(): LoadedPlugin {
    return definePlugin(this.plugin as LoadedPlugin);
  }
}

/**
 * 创建插件构建器实例
 */
export function createPlugin(meta: PluginMeta): PluginBuilder {
  return new PluginBuilder(meta);
}
