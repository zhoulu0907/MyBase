import type { EntityAPI, EventsAPI, Entity, Field } from '../types'

export function createEntityAPI(
  events: EventsAPI,
  entities: Entity[] = [],
  fields: Record<string, Field[]> = {},
  base?: Partial<EntityAPI>
): EntityAPI {
  const api: EntityAPI = {
    listFields: () => [],
    getEntities: () => entities,
    getFields: (id: string) => fields[id] || [],
    setFieldValue: (name: string, value: any) => {
      events.emit('set-field', { name, value })
    },
    setFieldsValue: (values: Record<string, any>) => {
      console.log('[plugin-editor] setFieldsValue', values);
      events.emit('set-fields', { values })
    }
  }
  return { ...api, ...(base || {}) }
}
