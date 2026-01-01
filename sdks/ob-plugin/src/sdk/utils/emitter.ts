import EventEmitter from 'eventemitter3'

export type PluginEmitterEvents = {
  'set-field': { name: string; value: any }
  'set-fields': { values: Record<string, any> }
}

export const pluginEmitter = new EventEmitter<PluginEmitterEvents>()
