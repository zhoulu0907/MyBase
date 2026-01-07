import React, { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import PreviewContainer from './components/preview';
import { getApplicationMenuPermission, getEntityListWithFields, menuSignal } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { menuDictSignal, setMainMetaData, useAppEntityStore } from '@onebase/ui-kit';
import { menuPermissionSignal } from '@onebase/common';

const Runtime: React.FC = () => {
  useSignals();

  const { mainEntity, subEntities, setMainEntity, setSubEntities } = useAppEntityStore();

  const { curMenu, setCurMenu } = menuSignal;
  const { batchSetAppDict } = menuDictSignal;
  const { setMenuPermission } = menuPermissionSignal;

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
    if (curMenu.value?.entityUuid) {
      getMainMetaData(curMenu.value?.entityUuid || localStorage.getItem('curMenu-entityUuid') || '');
      getMenuPermission(curMenu.value.id);
    }
  }, [curMenu.value]);

  const getMainMetaData = async (entityUuid: string) => {
    if (!entityUuid) {
      return;
    }
    const entityListWithFields = await getEntityListWithFields({ entityUuids: [entityUuid] });

    const [entityWithChildren] = entityListWithFields;
    if (entityWithChildren) {
      setMainMetaData(entityWithChildren, setMainEntity, setSubEntities, batchSetAppDict);
    }
  };

  const getMenuPermission = async (menuId: string) => {
    const permission = await getApplicationMenuPermission(menuId);
    console.log('permission: ', permission);
    setMenuPermission(permission);
  };

  const renderContent =
    curMenuId.indexOf('TASK-') >= 0 ? null : (
      <PreviewContainer
        menuId={curMenuId || ''}
        menuName={curMenu.value?.title}
        runtime={true}
        mainEntity={mainEntity}
        subEntities={subEntities}
        pageSetType={curMenu.value?.pagesetType}
      />
    );
  return <div>{renderContent}</div>;
};

export default Runtime;
