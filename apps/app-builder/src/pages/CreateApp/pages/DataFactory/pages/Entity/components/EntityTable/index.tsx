import type { EntityListItem, EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Layout, Message } from '@arco-design/web-react';
import { deleteEntity, getEntityList, updateEntity, type UpdateEntityReqVO } from '@onebase/app';
import React, { useCallback, useEffect, useState } from 'react';
import { MODAL_TYPE, useModalManager } from '../../hooks/useModalManager';
import EditEntityDrawer from '../Drawers/EditEntityDrawer';
import CreateEntityPage from '../Modals/CreateEntityModal';
import DeleteConfirmModal from '../Modals/DeleteConfirmModal';
import EntityDetail from './EntityDetail';
import EntityList from './EntityList';
import styles from './index.module.less';
const { Sider, Content } = Layout;

const EntityTable: React.FC = () => {
  const { curDataSourceId } = useResourceStore();
  const { curAppId } = useAppStore();

  // 使用统一的弹窗/抽屉管理器
  const { openModal, closeModal, isModalOpen, getModalData, setModalDataValue } = useModalManager();

  const [selectedEntity, setSelectedEntity] = useState<EntityListItem | null>(null);
  const [entities, setEntities] = useState<EntityListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  // 加载实体列表
  const loadEntities = useCallback(async () => {
    try {
      setLoading(true);
      const response = await getEntityList(curDataSourceId);
      if (response) {
        setEntities(response);
        // 如果没有选中的实体，默认选择第一个
        if (response.length > 0 && !selectedEntity) {
          setSelectedEntity(response[0]);
        }
        // 如果有选中的实体，需要重新设置以触发右侧标签页数据刷新
        if (response.length > 0 && selectedEntity) {
          const updatedEntity = response.find((entity: EntityListItem) => entity.id === selectedEntity.id);
          if (updatedEntity) {
            setSelectedEntity(updatedEntity);
          } else {
            setSelectedEntity(response[0]);
          }
        }
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [curDataSourceId, curAppId]);

  const handleDelete = (entity: EntityListItem) => {
    setSelectedEntity(entity);
    openModal(MODAL_TYPE.DELETE_CONFIRM, { selectedEntity: entity });
  };

  const handleOpenAddModal = () => {
    openModal(MODAL_TYPE.CREATE_ENTITY);
  };

  const successCallback = () => {
    loadEntities();
  };

  const confirmDelete = async () => {
    setDeleteLoading(true);
    const selectedEntity = getModalData('selectedEntity') as EntityListItem;
    const res = await deleteEntity(selectedEntity?.id || '');
    console.log('deleteEntity', res);
    if (res) {
      Message.success('删除成功');
      loadEntities();
    }
    setDeleteLoading(false);
    closeModal();
  };

  const handleEntitySelect = async (entity: EntityListItem) => {
    setSelectedEntity(entity);
    // 可以在这里调用loadEntityDetail来获取最新的实体详情
    // await loadEntityDetail(entity.id);
  };

  const handleClickEdit = async (entity: EntityListItem) => {
    openModal(MODAL_TYPE.EDIT_ENTITY, { editingNode: entity as unknown as EntityNode });
  };

  const onNodeEdit = async (data: Partial<EntityNode>) => {
    const params = {
      id: data.id,
      displayName: data.displayName,
      tableName: data.tableName,
      description: data.description,
      datasourceId: curDataSourceId,
      appId: curAppId
    };

    const res = await updateEntity(params as unknown as UpdateEntityReqVO);
    if (res) {
      Message.success('保存成功');
      console.log('实体信息更新成功');
    }
  };

  useEffect(() => {
    if (curDataSourceId && curAppId) {
      loadEntities();
    }
  }, [curDataSourceId, curAppId]);

  return (
    <>
      <Layout className={styles.entityTableContainer}>
        <Sider width={200} className={styles.sider}>
          <EntityList
            entities={entities}
            selectedEntity={selectedEntity}
            onEntitySelect={handleEntitySelect}
            loading={loading}
            handleDelete={handleDelete}
            handleOpenAddModal={handleOpenAddModal}
            handleClickEdit={handleClickEdit}
          />
        </Sider>
        <Content className={styles.content}>
          {selectedEntity ? (
            <EntityDetail entity={selectedEntity as unknown as EntityNode} reloadList={successCallback} />
          ) : (
            <div className={styles.emptyState}>请选择一个实体查看详情</div>
          )}
        </Content>
      </Layout>

      <CreateEntityPage
        visible={isModalOpen(MODAL_TYPE.CREATE_ENTITY)}
        setVisible={(visible) => !visible && closeModal()}
        successCallback={successCallback}
      />
      <DeleteConfirmModal
        visible={isModalOpen(MODAL_TYPE.DELETE_CONFIRM)}
        onVisibleChange={(visible) => !visible && closeModal()}
        onConfirm={confirmDelete}
        confirmLoading={deleteLoading}
        title="确认删除"
        content="确定要删除这个数据资产吗？删除后无法恢复。"
        okText="确认删除"
        cancelText="取消"
      />
      <EditEntityDrawer
        visible={isModalOpen(MODAL_TYPE.EDIT_ENTITY)}
        setVisible={(visible) => !visible && closeModal()}
        editingNode={getModalData('editingNode') as unknown as EntityNode}
        setEditingNode={(node: EntityNode | null) => setModalDataValue('editingNode', node)}
        onNodeEdit={onNodeEdit}
        successCallback={successCallback}
        onlyShowEntity={true}
      />
    </>
  );
};

export default EntityTable;
