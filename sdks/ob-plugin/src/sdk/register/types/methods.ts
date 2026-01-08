export type PluginMethod = (...args: any[]) => any | Promise<any>

export interface PluginMethodParam { name: string; type?: string; optional?: boolean; description?: string }
export interface PluginMethodReturn { type?: string; description?: string }
export interface PluginMethodMeta {
  key: string
  description?: string
  params?: PluginMethodParam[]
  returns?: PluginMethodReturn
  scope?: string
  deprecated?: boolean
  version?: string
}
