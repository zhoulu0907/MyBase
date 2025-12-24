import { ENTITY_TYPE, getEntityListWithFields, getPageSetMetaData, type ChildEntity } from '@onebase/app';
import { useAppEntityStore } from '@onebase/ui-kit';

// 获取主表对应的主实体信息
export const setMainMetaData = async (pageSetId: string) => {
  console.log('载入页面集对应实体信息, 页面集ID: ', pageSetId);
  // 在普通函数中使用 getState() 而不是 Hook，避免 "Invalid hook call" 错误
  const { setMainEntity, /* setAppEntities, */ setSubEntities } = useAppEntityStore.getState();

  const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });

  const entityListWithFields = await getEntityListWithFields({ entityUuids: [mainMetaData] });
  const [entityWithChildren] = entityListWithFields;
  console.log('entityWithChildren: ', entityWithChildren);

  // 主表数据
  if (entityWithChildren) {
    setMainEntity({
      entityId: entityWithChildren.entityId,
      entityUuid: entityWithChildren.entityUuid,
      tableName: entityWithChildren.tableName,
      entityName: entityWithChildren.entityName,
      entityType: ENTITY_TYPE.MAIN,
      fields: entityWithChildren.fields
    });

    if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
      // 返回新Promise对象，当所有输入Promise成功时返回结果数组（顺序与输入一致）
      const allChildFields = await Promise.all(
        entityWithChildren.childEntities.map(async (entity: ChildEntity) => {
          return entity.childFields;
        })
      );
      const subEntities = entityWithChildren.childEntities.map((entity: ChildEntity, index: number) => ({
        entityId: entity.childEntityId,
        entityUuid: entity.childEntityUuid,
        tableName: entity.childTableName,
        entityName: entity.childEntityName,
        entityType: ENTITY_TYPE.SUB,
        fields: allChildFields[index]
      }));

      setSubEntities({
        entities: subEntities
      });
    } else {
      setSubEntities({ entities: [] });
    }
  }
};
