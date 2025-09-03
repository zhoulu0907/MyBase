import type { EdgeData, EntityERProps, EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Button, Message } from '@arco-design/web-react';
import React, { useEffect, useRef, useState } from 'react';
import EditEntityDrawer from '../components/Drawers/EditEntityDrawer';
import EditFieldDrawer from '../components/Drawers/EditFieldDrawer';
import EditRelationDrawer from '../components/Drawers/EditRelationDrawer';
// import FieldDetailDrawer from '../components/Drawers/FieldDetailDrawer';
import ERchart from '../components/ERchart';
import CreateEntityModal from '../components/Modals/CreateEntityModal';
// import CreateFieldModal from '../components/Modals/CreateFieldModal';

import { IconPlus } from '@arco-design/web-react/icon';
import { deleteEntity, getEntityGraph, updateEntity, type UpdateEntityReqVO } from '@onebase/app';
import {
  ConfigFieldModal,
  CreateMasterDetailModal,
  CreateRelationModal,
  DeleteConfirmModal
} from '../components/Modals';
import styles from '../index.module.less';

const relationshipTypeMap: Record<string, string> = {
  ONE_TO_ONE: '1:1',
  ONE_TO_MANY: '1:N',
  MANY_TO_ONE: 'N:1',
  MANY_TO_MANY: 'M:N'
};

export const EntityERContainer: React.FC<{
  refreshEntityList: boolean;
  setRefreshEntityList: (refresh: boolean) => void;
  onlyUpdateNode: boolean;
  setOnlyUpdateNode: (onlyUpdateNode: boolean) => void;
}> = ({ refreshEntityList, setRefreshEntityList, onlyUpdateNode, setOnlyUpdateNode }) => {
  const { curAppId } = useAppStore();
  const { curDataSourceId } = useResourceStore();
  // const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [data, setData] = useState<EntityERProps['data']>({ nodes: [], edges: [] });
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
  const [editFieldDrawerVisible, setEditFieldDrawerVisible] = useState(false);
  const [selectedFieldId, setSelectedFieldId] = useState<string>('');
  const [selectedEntityId, setSelectedEntityId] = useState<string>('');
  const [editRelationDrawerVisible, setEditRelationDrawerVisible] = useState(false);
  const [relationData, setRelationData] = useState<EdgeData | null>(null);
  const chartRef = useRef<any>(null);
  const loadEntityList = async () => {
    const res = await getEntityGraph(curDataSourceId);
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
        edges:
          res?.relationships.map((item) => {
            return {
              ...item,
              label: relationshipTypeMap[item?.relationshipType || '']
            };
          }) || []
      });
    }
  };

  const handleNodeEdit = (editData: Partial<EntityNode>) => {
    console.log('节点编辑:', editData);
    setEditDrawerVisible(true);
    setEditFieldDrawerVisible(false);
    setEditingNode(editData as unknown as EntityNode);
  };

  const handleNodeAddField = (node: EntityNode) => {
    console.log('添加字段', node);
    setConfigFieldModalVisible(true);
    setEditDrawerVisible(false);
    setNodedata(node as unknown as EntityNode);
  };

  const handleNodeAddRelation = (id: string) => {
    console.log('添加关联:', id);
    setSelectedEntityId(id);
    setEditDrawerVisible(false);
    setCreateRelationModalVisible(true);
    setUpdateRelationOptions(true);
    setOnlyUpdateNode(false);
  };

  const handleNodeAddMasterDetail = (id: string) => {
    console.log('添加主子关系:', id);
    setSelectedEntityId(id);
    setEditDrawerVisible(false);
    setCreateMasterDetailModalVisible(true);
    setOnlyUpdateNode(false);
  };

  const handleFieldClick = (fieldId: string) => {
    console.log('字段点击:', fieldId);
    setSelectedFieldId(fieldId);
    setEditDrawerVisible(false);
    setEditFieldDrawerVisible(true);
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
      tableName: data.tableName,
      displayConfig: JSON.stringify({ x, y }),
      code: data.code,
      datasourceId: curDataSourceId,
      appId: curAppId
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
    setOnlyUpdateNode(true);
    setRefreshEntityList(true);
  };

  const createEntityCallback = () => {
    setRefreshEntityList(true);
    setOnlyUpdateNode(false);
  };

  const handleEdgeEdit = (data: EdgeData) => {
    console.log('handleEdgeEdit', data);
    setEditRelationDrawerVisible(true);
    setRelationData(data);
  };

  const getGraphPositon = () => {
    console.log('chartRef.current', chartRef.current);
    return chartRef.current?.getGraphPositon();
  };

  const handleStatusChange = async (data: Partial<EntityNode>) => {
    console.log('handleStatusChange', data);
    const params = {
      id: data.entityId,
      status: data.status,
      tableName: data.tableName,
      displayName: data.entityName,
      datasourceId: curDataSourceId,
      appId: curAppId
    };
    const res = await updateEntity(params as unknown as UpdateEntityReqVO);
    if (res) {
      console.log('实体状态更新成功');
      setOnlyUpdateNode(true);
      loadEntityList();
    }
  };

  useEffect(() => {
    console.log('refreshEntityList', refreshEntityList);
    if (refreshEntityList) {
      loadEntityList();
      setRefreshEntityList(false);
    }
  }, [refreshEntityList]);

  useEffect(() => {
    if (curDataSourceId) {
      loadEntityList();
    }
  }, [curDataSourceId]);

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
        onEdgeEdit={handleEdgeEdit}
        onStatusChange={handleStatusChange}
        ref={chartRef}
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
      <EditEntityDrawer
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
        lastEntity={data.nodes[data.nodes.length - 1]}
        getGraphPositon={getGraphPositon}
      />
      <ConfigFieldModal
        visible={configFieldModalVisible}
        setVisible={setConfigFieldModalVisible}
        entity={nodedata as EntityNode}
        successCallback={handleSuccessCallback}
      />
      <CreateRelationModal
        visible={createRelationModalVisible}
        entityId={selectedEntityId}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />
      <CreateMasterDetailModal
        visible={createMasterDetailModalVisible}
        setVisible={setCreateMasterDetailModalVisible}
        entityId={selectedEntityId}
        successCallback={handleSuccessCallback}
      />
      <EditRelationDrawer
        visible={editRelationDrawerVisible}
        setVisible={setEditRelationDrawerVisible}
        relationData={relationData}
        onSuccess={handleSuccessCallback}
      />
      <EditFieldDrawer
        visible={editFieldDrawerVisible}
        setVisible={setEditFieldDrawerVisible}
        fieldId={selectedFieldId}
        onSuccess={handleSuccessCallback}
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
