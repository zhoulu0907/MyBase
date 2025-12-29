import EventEmitter from 'eventemitter3'

export type PluginEmitterEvents = {
  'set-field': { name: string; value: any }
  'set-fields': { values: Record<string, any> }
  'set-subrow-field': { tableName: string; rowIndex: number; fieldName: string; value: any }
  'set-subrow-fields': { tableName: string; rowIndex: number; values: Record<string, any> }
}

export const pluginEmitter = new EventEmitter<PluginEmitterEvents>()
