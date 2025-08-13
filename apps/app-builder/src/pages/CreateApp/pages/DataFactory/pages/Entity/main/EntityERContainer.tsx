import React, { useEffect, useState } from 'react';
// import { Button, Space } from '@arco-design/web-react';
import type {
  EdgeData,
  EntityData,
  EntityERProps,
  EntityNode
} from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Message } from '@arco-design/web-react';
import EditDrawer from '../components/Drawers/EditEntityDrawer';
import FieldDetailDrawer from '../components/Drawers/FieldDetailDrawer';
import ERchart from '../components/ERchart';
import CreateEntityModal from '../components/Modals/CreateEntityModal';
// import CreateFieldModal from '../components/Modals/CreateFieldModal';
import { resouceId } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { IconPlus } from '@arco-design/web-react/icon';
import { getEntityGraph, deleteEntity, updateEntity } from '@onebase/app';
import {
  ConfigFieldModal,
  CreateRelationModal,
  CreateMasterDetailModal,
  DeleteConfirmModal
} from '../components/Modals';
import styles from '../index.module.less';
export const EntityERContainer: React.FC<{
  refreshEntityList: boolean;
  setRefreshEntityList: (refresh: boolean) => void;
  onlyUpdateNode: boolean;
  setOnlyUpdateNode: (onlyUpdateNode: boolean) => void;
}> = ({ refreshEntityList, setRefreshEntityList, onlyUpdateNode, setOnlyUpdateNode }) => {
  // const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [data, setData] = useState<EntityERProps['data']>(
    JSON.parse(
      localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
    ) as unknown as EntityData
  );
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
  const [createEntityModalVisible, setCreateEntityModalVisible] = useState(false);
  // const [createFieldModalVisible, setCreateFieldModalVisible] = useState(false);
  const [configFieldModalVisible, setConfigFieldModalVisible] = useState(false);
  const [nodeId, setNodeId] = useState('');
  const [nodedata, setNodedata] = useState<EntityNode | null>(null);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [createMasterDetailModalVisible, setCreateMasterDetailModalVisible] = useState(false);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  const [fieldDetailDrawerVisible, setFieldDetailDrawerVisible] = useState(false);
  const [selectedFieldId, setSelectedFieldId] = useState<string>('');

  const loadEntityList = async () => {
    const res = await getEntityGraph(resouceId);
    console.log('loadEntityList', res);
    if (res?.entities || res?.relationships) {
      setData({
        nodes:
          res?.entities.map((item) => {
            const pos = JSON.parse(item?.displayConfig || '{}');
            return {
              ...item,
              positionX: pos?.x || 0,
              positionY: pos?.y || 0
            };
          }) || [],
        edges: res?.relationships.map((item) => {
          return {
            source: { cell: item.sourceEntityId, port: item.sourceFieldId },
            target: { cell: item.targetEntityId, port: item.targetFieldId },
            label: item.relationshipName
          };
        })
      });
    }
  };

  const handleNodeEdit = (editData: Partial<EntityNode>) => {
    console.log('节点编辑:', editData);
    setEditDrawerVisible(true);
    setEditingNode(editData as unknown as EntityNode);
  };

  const handleNodeAddField = (node: EntityNode) => {
    setConfigFieldModalVisible(true);
    setNodedata(node as unknown as EntityNode);
  };

  const handleNodeAddRelation = (id: string) => {
    console.log('添加关联:', id);
    setCreateRelationModalVisible(true);
    setUpdateRelationOptions(true);
    setOnlyUpdateNode(false);
  };

  const handleNodeAddMasterDetail = (id: string) => {
    console.log('添加主子关系:', id);
    setCreateMasterDetailModalVisible(true);
    setOnlyUpdateNode(false);
  };

  const handleFieldClick = (fieldId: string) => {
    console.log('字段点击:', fieldId);
    setSelectedFieldId(fieldId);
    setFieldDetailDrawerVisible(true);
  };

  const handleNodeDelete = (id: string) => {
    console.log('删除节点:', id);
    setDeleteModalVisible(true);
    setNodeId(id);
  };

  const handleUpdateEntityPosition = async (data: EntityNode, x: number, y: number) => {
    console.log('更新节点位置:', data, x, y);

    const params = {
      id: data.entityId,
      displayName: data.entityName,
      displayConfig: JSON.stringify({ x, y }),
      code: data.code,
      datasourceId: resouceId,
      appId: '1'
    };
    const res = await updateEntity(params);
    console.log('updateEntity', res);
    if (res) {
      console.log('实体位置成功');
    }
  };

  const confirmDelete = async () => {
    setDeleteLoading(true);
    const res = await deleteEntity(nodeId);
    console.log('deleteEntity', res);
    if (res) {
      Message.success('删除成功');
      loadEntityList();
    }
    setDeleteModalVisible(false);
    setDeleteLoading(false);
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  const handleSuccessCallback = async () => {
    await loadEntityList();
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  const createEntityCallback = () => {
    setRefreshEntityList(true);
    setOnlyUpdateNode(false);
  };

  useEffect(() => {
    if (refreshEntityList) {
      loadEntityList();
      setRefreshEntityList(false);
    }
  }, [refreshEntityList]);

  useEffect(() => {
    loadEntityList();
  }, []);

  // useEffect(() => {
  //   const storedData = localStorage.getItem('entityFormValues');
  //   if (storedData) {
  //     const parsedData = JSON.parse(storedData);
  //     setData(parsedData as EntityERProps['data']);
  //   }
  // }, []);

  return (
    <div style={{ height: '100%' }} className={styles['entity-page-container']}>
      <ERchart
        mode="edit"
        data={data as unknown as EntityERProps['data']}
        onNodeEdit={handleNodeEdit}
        onNodeAddField={handleNodeAddField}
        onNodeAddRelation={handleNodeAddRelation}
        onNodeAddMasterDetail={handleNodeAddMasterDetail}
        onNodeDelete={handleNodeDelete}
        onFieldClick={handleFieldClick}
        onlyUpdateNode={onlyUpdateNode}
        updateEntityPosition={handleUpdateEntityPosition}
      />
      <Button
        type="primary"
        className={styles['entity-page-create-button']}
        onClick={() => {
          setCreateEntityModalVisible(true);
        }}
      >
        <IconPlus />
        创建业务实体
      </Button>

      {/* 交互弹窗、抽屉、模态框 */}
      <EditDrawer
        visible={editDrawerVisible}
        setVisible={setEditDrawerVisible}
        editingNode={editingNode as EntityNode}
        setEditingNode={(node: EntityNode | null) => setEditingNode(node)}
        onNodeEdit={handleNodeEdit}
        successCallback={handleSuccessCallback}
      />
      <CreateEntityModal
        visible={createEntityModalVisible}
        setVisible={setCreateEntityModalVisible}
        successCallback={createEntityCallback}
        entityListLength={data.nodes.length}
      />
      <ConfigFieldModal
        visible={configFieldModalVisible}
        setVisible={setConfigFieldModalVisible}
        entity={nodedata as EntityNode}
        successCallback={handleSuccessCallback}
      />
      <CreateRelationModal
        visible={createRelationModalVisible}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />
      <CreateMasterDetailModal
        visible={createMasterDetailModalVisible}
        setVisible={setCreateMasterDetailModalVisible}
        entity={nodedata as EntityNode}
        successCallback={handleSuccessCallback}
      />
      <FieldDetailDrawer
        visible={fieldDetailDrawerVisible}
        setVisible={setFieldDetailDrawerVisible}
        fieldId={selectedFieldId}
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
    </div>
  );
};
