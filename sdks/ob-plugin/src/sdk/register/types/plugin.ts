export interface PluginMeta {
  name: string
  version: string
  displayName: string
  description?: string
  routePrefix: string
  resources?: {
    js: string
    css?: string
  }
}

export interface PluginRegistry {
  meta: PluginMeta
  status: 'registered' | 'loading' | 'loaded' | 'error'
}

export interface HostEvents {
  'plugin:registered': (payload: { meta: PluginMeta }) => void
  'plugin:loaded': (payload: { id: string }) => void
  'plugin:unloaded': (payload: { id: string }) => void
  'plugin:invalidated': (payload: { id: string }) => void
  'plugin:error': (payload: { id: string; error: unknown }) => void
  'plugin:method:start': (payload: { id: string; method: string }) => void
  'plugin:method:end': (payload: { id: string; method: string }) => void
  'plugin:method:error': (payload: { id: string; method: string; error: unknown }) => void
}

export type PluginStatus = 'registered' | 'loading' | 'loaded' | 'error' | 'unloaded' | 'invalidated'
