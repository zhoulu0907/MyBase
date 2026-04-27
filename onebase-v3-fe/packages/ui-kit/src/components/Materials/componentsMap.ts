import { COMPONENT_REGISTRY } from './registry'

export const COMPONENT_MAP: Record<string, string> = Object.entries(COMPONENT_REGISTRY).reduce(
  (acc, [cpType, descriptor]) => {
    if (!descriptor) return acc
    if (descriptor.template.category === 'form' && descriptor.entityMap) {
      for (const fieldType of descriptor.entityMap) {
        acc[fieldType] = cpType
      }
    }
    return acc
  },
  {} as Record<string, string>
)

export const COMPONENT_FIELD_MAP: Record<string, string[]> = Object.entries(COMPONENT_REGISTRY).reduce(
  (acc, [cpType, descriptor]) => {
    if (!descriptor) return acc
    if (descriptor.template.category === 'form') {
      acc[cpType] = descriptor.fieldMap ?? []
    }
    return acc
  },
  {} as Record<string, string[]>
)
