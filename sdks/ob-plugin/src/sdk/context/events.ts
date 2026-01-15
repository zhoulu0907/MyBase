import EventEmitter from 'eventemitter3'
import type { EventsAPI } from '../types'

export function createEvents(existing?: EventsAPI): EventsAPI {
  if (existing) return existing
  const emitter = new EventEmitter()
  return {
    on: (event: string, handler: (payload: any) => void) => emitter.on(event, handler as any),
    off: (event: string, handler: (payload: any) => void) => emitter.off(event, handler as any),
    emit: (event: string, payload?: any) => emitter.emit(event, payload)
  }
}
