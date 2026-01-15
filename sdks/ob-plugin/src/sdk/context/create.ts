import type { Context, HostSDK, UIAPI, Entity, Field, RequestAPI } from '../types'
import { createUI } from './ui'
import { createEvents } from './events'
import { createEntityAPI } from './entity'
import { createRequest } from './request'

export function createHostSDK(
  context: Omit<Context, 'entity'> & { entity?: Partial<Context['entity']> },
  overrides?: {
    ui?: UIAPI
    entities?: Entity[]
    fields?: Record<string, Field[]>
    request?: RequestAPI
  }
): HostSDK {
  const events = createEvents(context.events)
  const request = overrides?.request ?? createRequest()
  const entity = createEntityAPI(events, overrides?.entities || [], overrides?.fields || {}, context.entity)
  const finalContext: Context = { ...context, entity, events, request }
  return { context: finalContext, ui: createUI(overrides?.ui) }
}
