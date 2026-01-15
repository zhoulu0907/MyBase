import { LoadedPlugin, PluginMeta, PluginPage, PluginComponent, PluginMethod, PluginMethodMeta } from '../types'
import { definePlugin } from './define'

export class PluginBuilder {
  private plugin: Partial<LoadedPlugin> = {
    pages: {},
    components: {},
    configRenderers: {},
    methods: {}
  }

  constructor(meta: PluginMeta) {
    this.plugin.meta = meta
  }

  registerPage(key: string, page: PluginPage): this {
    if (!this.plugin.pages) this.plugin.pages = {}
    this.plugin.pages[key] = page
    return this
  }

  registerPages(pages: Record<string, PluginPage>): this {
    if (!this.plugin.pages) this.plugin.pages = {}
    Object.assign(this.plugin.pages, pages)
    return this
  }

  registerComponent(key: string, component: PluginComponent): this {
    if (!this.plugin.components) this.plugin.components = {}
    this.plugin.components[key] = component
    return this
  }

  registerComponents(components: Record<string, PluginComponent>): this {
    if (!this.plugin.components) this.plugin.components = {}
    Object.assign(this.plugin.components, components)
    return this
  }

  registerConfigRenderer(key: string, renderer: PluginComponent): this {
    if (!this.plugin.configRenderers) this.plugin.configRenderers = {}
    this.plugin.configRenderers[key] = renderer
    return this
  }

  registerConfigRenderers(renderers: Record<string, PluginComponent>): this {
    if (!this.plugin.configRenderers) this.plugin.configRenderers = {}
    Object.assign(this.plugin.configRenderers, renderers)
    return this
  }

  registerMethod(key: string, method: PluginMethod): this {
    if (!this.plugin.methods) this.plugin.methods = {}
    this.plugin.methods[key] = method
    return this
  }

  registerMethods(methods: Record<string, PluginMethod>): this {
    if (!this.plugin.methods) this.plugin.methods = {}
    Object.assign(this.plugin.methods, methods)
    return this
  }

  registerMethodMeta(key: string, meta: PluginMethodMeta): this {
    if (!this.plugin.methodsMeta) this.plugin.methodsMeta = {} as any
    ;(this.plugin.methodsMeta as Record<string, PluginMethodMeta>)[key] = meta
    return this
  }

  registerMethodsMeta(metas: Record<string, PluginMethodMeta>): this {
    if (!this.plugin.methodsMeta) this.plugin.methodsMeta = {} as any
    Object.assign(this.plugin.methodsMeta as Record<string, PluginMethodMeta>, metas)
    return this
  }

  onInitialize(fn: (sdk: any) => Promise<void>): this {
    this.plugin.initialize = fn
    return this
  }

  onDestroy(fn: () => Promise<void>): this {
    this.plugin.destroy = fn
    return this
  }

  build(): LoadedPlugin {
    return definePlugin(this.plugin as LoadedPlugin)
  }
}

export function createPlugin(meta: PluginMeta): PluginBuilder {
  return new PluginBuilder(meta)
}
