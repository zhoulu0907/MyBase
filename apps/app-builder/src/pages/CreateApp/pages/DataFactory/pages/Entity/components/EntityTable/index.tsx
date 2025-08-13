import React, { useState, useEffect } from 'react';
import { Layout, Message } from '@arco-design/web-react';
import { getEntityList, deleteEntity } from '@onebase/app';
import { useResourceStore } from '@/store_resource';
import type { EntityListItem, EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import EntityList from './EntityList';
import EntityDetail from './EntityDetail';
import styles from './index.module.less';
import CreateEntityPage from '../Modals/CreateEntityModal';
import DeleteConfirmModal from '../Modals/DeleteConfirmModal';
const { Sider, Content } = Layout;

const EntityTable: React.FC = () => {
  const { curDataSourceId } = useResourceStore();
  const [selectedEntity, setSelectedEntity] = useState<EntityListItem | null>(null);
  const [entities, setEntities] = useState<EntityListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [createEntityModalVisible, setCreateEntityModalVisible] = useState(false);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  // 加载实体列表
  const loadEntities = async () => {
    try {
      setLoading(true);
      const response = await getEntityList(curDataSourceId);
      if (response) {
        setEntities(response);
        // 如果有实体数据，默认选择第一个
        if (response.length > 0 && !selectedEntity) {
          setSelectedEntity(response[0]);
        }
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
      Message.error('加载实体列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (entity: EntityListItem) => {
    setSelectedEntity(entity);
    setDeleteModalVisible(true);
  };

  const handleOpenAddModal = () => {
    setCreateEntityModalVisible(true);
  };

  const successCallback = () => {
    loadEntities();
  };

  const confirmDelete = async () => {
    setDeleteLoading(true);
    const res = await deleteEntity(selectedEntity?.id || '');
    console.log('deleteEntity', res);
    if (res) {
      Message.success('删除成功');
      loadEntities();
    }
    setDeleteLoading(false);
    setDeleteModalVisible(false);
  };

  // 加载实体详情
  // const loadEntityDetail = async (entityId: string) => {
  //   try {
  //     const response = await getEntity(entityId);
  //     console.log('getEntity', response)
  //     if (response.data) {
  //       setSelectedEntity(response.data);
  //     }
  //   } catch (error) {
  //     console.error('加载实体详情失败:', error);
  //     Message.error('加载实体详情失败');
  //   }
  // };

  useEffect(() => {
    if (curDataSourceId) {
      loadEntities();
    }
  }, [curDataSourceId]);

  const handleEntitySelect = async (entity: EntityListItem) => {
    setSelectedEntity(entity);
    // 可以在这里调用loadEntityDetail来获取最新的实体详情
    // await loadEntityDetail(entity.id);
  };

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
          />
        </Sider>
        <Content className={styles.content}>
          {selectedEntity ? (
            <EntityDetail entity={selectedEntity as unknown as EntityNode} />
          ) : (
            <div className={styles.emptyState}>请选择一个实体查看详情</div>
          )}
        </Content>
      </Layout>

      <CreateEntityPage
        visible={createEntityModalVisible}
        setVisible={setCreateEntityModalVisible}
        successCallback={successCallback}
      />
      <DeleteConfirmModal
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={confirmDelete}
        confirmLoading={deleteLoading}
        title="确认删除"
        content="确定要删除这个业务实体吗？删除后无法恢复。"
        okText="确认删除"
        cancelText="取消"
      />
    </>
  );
};

export default EntityTable;
