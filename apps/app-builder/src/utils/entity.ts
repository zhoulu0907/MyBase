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

  if (entityWithChildren) {
    // 主表数据
    setMainEntity({
      entityId: entityWithChildren.entityId,
      entityUuid: entityWithChildren.entityUuid,
      tableName: entityWithChildren.tableName,
      entityName: entityWithChildren.entityName,
      entityType: ENTITY_TYPE.MAIN,
      fields: entityWithChildren.fields
    });

    // 子表数据
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

    // TODO(mickey): 批量获取字典内容，移除组件中每次获取system/dict-data/simple-list-by-type?dictTypeId的接口
    // // 收集主表字段中的 dictTypeId
    // const mainDictTypeIds = entityWithChildren.fields
    //   .filter((field: AppEntityField) => field.dictTypeId)
    //   .map((field: AppEntityField) => field.dictTypeId!);

    // // 收集子表字段中的 dictTypeId
    // const childDictTypeIds: string[] = [];
    // if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
    //   entityWithChildren.childEntities.forEach((childEntity: ChildEntity) => {
    //     if (childEntity.childFields) {
    //       const childFieldDictTypeIds = childEntity.childFields
    //         .filter((field: AppEntityField) => field.dictTypeId)
    //         .map((field: AppEntityField) => field.dictTypeId!);
    //       childDictTypeIds.push(...childFieldDictTypeIds);
    //     }
    //   });
    // }

    // // 合并并去重
    // const dictTypeIds = Array.from(new Set([...mainDictTypeIds, ...childDictTypeIds]));
    // console.log('dictTypeIds: ', dictTypeIds);
  }
};
