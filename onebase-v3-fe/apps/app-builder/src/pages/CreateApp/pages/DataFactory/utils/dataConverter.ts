import type { EntityListItem, EntityNode } from './interface';

/**
 * 将 EntityListItem 转换为 ConfigFieldModal 需要的 EntityNode 格式
 * @param entityListItem EntityListItem 对象，包含 id、displayName、code 字段
 * @returns 转换后的对象，包含 entityId、entityName、code 字段
 */
export const convertEntityListItemToConfigField = (entityListItem: EntityListItem) => ({
  entityId: entityListItem.id,
  entityName: entityListItem.displayName,
  code: entityListItem.code
});

/**
 * 将 EntityListItem 转换为完整的 EntityNode 格式
 * @param entityListItem EntityListItem 对象
 * @returns 转换后的 EntityNode 对象
 */
export const convertEntityListItemToEntityNode = (entityListItem: EntityListItem): Partial<EntityNode> => ({
  entityId: entityListItem.id,
  entityName: entityListItem.displayName,
  code: entityListItem.code,
  description: '',
  fields: [],
  positionX: 0,
  positionY: 0
});
