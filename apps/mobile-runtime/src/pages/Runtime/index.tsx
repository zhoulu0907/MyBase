import React, { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import PreviewContainer from './components/preview';
import { ENTITY_TYPE, getEntityFieldsWithChildren, menuSignal, type ChildEntity } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useAppEntityStore } from '@onebase/ui-kit';

const Runtime: React.FC = () => {
  useSignals();

  const { mainEntity, subEntities, setMainEntity, setSubEntities } = useAppEntityStore();

  const { curMenu, setCurMenu } = menuSignal;
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu') || '';
  useEffect(() => {
    setCurMenu({
      ...curMenu.value,
      id: curMenuId,
      menuCode: curMenuId,
      menuSort: 1,
      menuType: 1,
      menuName: curMenuId,
      menuIcon: '',
      isVisible: 1,
      pagesetType: curMenu.value?.pagesetType,
      children: []
    });
  }, [curMenuId]);

  useEffect(() => {
    getMainMetaData();
  }, [curMenu.value]);

  const getMainMetaData = async () => {
    const sessionEntityUuid = sessionStorage.getItem('ENTITY_UUID');
    if (!(curMenu.value?.entityUuid || sessionEntityUuid)) {
      return;
    }

    const entityWithChildren = await getEntityFieldsWithChildren(curMenu.value.entityUuid || sessionEntityUuid);
    if (entityWithChildren) {
      setMainEntity({
        entityId: entityWithChildren.entityId,
        entityUuid: entityWithChildren.entityUuid,
        tableName: entityWithChildren.tableName,
        entityName: entityWithChildren.entityName,
        entityType: ENTITY_TYPE.MAIN,
        fields: entityWithChildren.parentFields
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

  const renderContent =
    curMenuId.indexOf('TASK-') >= 0 ? null : (
      <PreviewContainer
        menuId={curMenuId || ''}
        runtime={true}
        mainEntity={mainEntity}
        subEntities={subEntities}
        pageSetType={curMenu.value?.pagesetType}
      />
    );
  return <div>{renderContent}</div>;
};

export default Runtime;
