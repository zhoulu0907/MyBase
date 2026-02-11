import type { Context, HostSDK, UIAPI, Entity, Field, RequestAPI } from '../types'
import { createUI } from './ui'
import { createEvents } from './events'
import { createEntityAPI } from './entity'
import { createRequest, enrichRequest } from './request'

export function createHostSDK(
  context: Omit<Context, 'entity'> & { entity?: Partial<Context['entity']> },
  overrides?: {
    ui?: UIAPI
    entities?: Entity[]
    fields?: Record<string, Field[]>
  }
): HostSDK {
  const events = createEvents(context.events)
  // 如果 context.request 存在，则使用它（即使它只有 request 方法，我们也会通过 enrichRequest 补全）
  // 否则使用默认的 createRequest 创建
  const baseRequest = context.request || createRequest()
  // 补全所有请求方法
  const requestAPI = enrichRequest(baseRequest.request)

  const entity = createEntityAPI(events, overrides?.entities || [], overrides?.fields || {}, context.entity)
  const finalContext: Context = { ...context, entity, events, request: requestAPI }
  return { 
    context: finalContext, 
    ui: createUI(overrides?.ui), 
    ...requestAPI
  }
}
