export type Terminal = 'PC' | 'MOBILE'

export interface Entity {
  entityUuid: string
  entityName: string
  tableName: string
  [key: string]: any
}

export interface Field {
  fieldName: string
  displayName: string
  fieldType: string
  [key: string]: any
}

export interface EntityAPI {
  listFields: () => string[]
  getEntities: () => Entity[]
  getFields: (entityId: string) => Field[]
  getFieldOptions?: (dataField: string[]) => Promise<any[]>
  setFieldValue?: (name: string, value: any) => void
  setFieldsValue?: (values: Record<string, any>) => void
  setSubRowFieldValue?: (tableName: string, rowIndex: number, fieldName: string, value: any) => void
  setSubRowFieldsValue?: (tableName: string, rowIndex: number, values: Record<string, any>) => void
}

export interface EventsAPI {
  on: (event: string, handler: (payload: any) => void) => void
  off: (event: string, handler: (payload: any) => void) => void
  emit: (event: string, payload?: any) => void
}

export interface RequestAPI {
  get: (url: string, config?: any) => Promise<any>
  post: (url: string, data?: any, config?: any) => Promise<any>
  put: (url: string, data?: any, config?: any) => Promise<any>
  delete: (url: string, config?: any) => Promise<any>
  request: (config: any) => Promise<any>
}

export interface Context {
  terminal: Terminal
  entity: EntityAPI
  events?: EventsAPI
  request?: { request: (config: any) => Promise<any> }
}

export interface ErrorReportOptions { scope?: string }
export interface UIAPI { reportError(error: unknown, options?: ErrorReportOptions): void }
export interface HostSDK {
  context: Context
  ui: UIAPI
  // Request methods exposed on root
  get: RequestAPI['get']
  post: RequestAPI['post']
  put: RequestAPI['put']
  delete: RequestAPI['delete']
  request: RequestAPI['request']
}
