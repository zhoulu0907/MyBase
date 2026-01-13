import { LoadedPlugin, PluginComponent } from '../types'

export function definePlugin(plugin: LoadedPlugin): LoadedPlugin {
  const { name } = plugin.meta
  const prefix = (str: string) => `${name}_${str}`

  const localTypes = new Set<string>()
  if (plugin.components) {
    Object.values(plugin.components).forEach(c => {
      if (c.type) localTypes.add(c.type)
    })
  }
  if (plugin.configRenderers) {
    Object.values(plugin.configRenderers).forEach(c => {
      if (c.type) localTypes.add(c.type)
    })
  }

  const deepPrefixSchema = (schema: any) => {
    if (!schema || typeof schema !== 'object') return
    if (Array.isArray(schema)) {
      schema.forEach(item => deepPrefixSchema(item))
      return
    }
    if (schema.type && typeof schema.type === 'string' && localTypes.has(schema.type)) {
      schema.type = prefix(schema.type)
    }
    Object.keys(schema).forEach(k => {
      const v = (schema as any)[k]
      if (v && (typeof v === 'object' || Array.isArray(v))) deepPrefixSchema(v)
    })
  }

  if (plugin.components) {
    Object.values(plugin.components).forEach(c => {
      if (c.type) {
        if (c.schema) deepPrefixSchema(c.schema)
        c.type = prefix(c.type)
      }
    })
  }

  if (plugin.configRenderers) {
    Object.values(plugin.configRenderers).forEach(c => {
      if (c.type) {
        c.type = prefix(c.type)
      }
    })
  }

  return plugin
}
