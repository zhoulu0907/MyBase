import type { EdgeData, Entity, EntityERProps, EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { newFieldSignal } from '@/store/singals/new_field';
import { useGraphEntitytore } from '@onebase/ui-kit';
import { Button, Message } from '@arco-design/web-react';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import EditEntityDrawer from '../components/Drawers/EditEntityDrawer';
import EditFieldDrawer from '../components/Drawers/EditFieldDrawer';
import EditRelationDrawer from '../components/Drawers/EditRelationDrawer';
import { MODAL_TYPE, useModalManager } from '../hooks/useModalManager';
// import FieldDetailDrawer from '../components/Drawers/FieldDetailDrawer';
import ERchart, { type ERchartRef } from '../components/ERchart';
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
import type { DatasourceRecord } from './EntityPageContainer';
import { RELATIONSHIP_TYPE_LABEL_MAP, type RelationshipType } from '@/pages/CreateApp/pages/DataFactory/utils/relation';
import styles from '../index.module.less';

export const EntityERContainer: React.FC<{
  refreshEntityList: boolean;
  setRefreshEntityList: (refresh: boolean) => void;
  onlyUpdateNode: boolean;
  setOnlyUpdateNode: (onlyUpdateNode: boolean) => void;
  dsData: DatasourceRecord;
  handleMenuClick: (key: string) => void;
}> = ({ refreshEntityList, setRefreshEntityList, onlyUpdateNode, setOnlyUpdateNode, dsData, handleMenuClick }) => {
  const { curAppId } = useAppStore();
  const { curDataSourceId } = useResourceStore();
  const setCurEntityId = useGraphEntitytore((state) => state.setCurEntityId);

  // 使用统一的弹窗/抽屉管理器
  const { openModal, closeModal, isModalOpen, getModalData, setModalDataValue } = useModalManager();

  const [data, setData] = useState<EntityERProps['data']>({ nodes: [], edges: [] });
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  const chartRef = useRef<ERchartRef | null>(null);
  const prevDataSourceIdRef = useRef<string>('');

  const loadEntityList = useCallback(async (dataSourceId: string) => {
    if (!dataSourceId) {
      return;
    }

    try {
      const res = await getEntityGraph(dataSourceId);

      if (res?.entities || res?.relationships) {
        setData({
          nodes:
            res?.entities.map((item: unknown) => {
              const entityItem = item as Record<string, unknown>;
              const pos = JSON.parse((entityItem?.displayConfig as string) || '{}');
              return {
                ...entityItem,
                positionX: pos?.x,
                positionY: pos?.y
              };
            }) || [],
          edges:
            res?.relationships.map((item: unknown) => {
              const relationItem = item as Record<string, unknown>;
              return {
                ...relationItem,
                label: RELATIONSHIP_TYPE_LABEL_MAP[(relationItem?.relationshipType as RelationshipType) || '']
              };
            }) || []
        });
      } else {
        setData({ nodes: [], edges: [] });
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
      setData({ nodes: [], edges: [] });
    }
  }, []);

  // 打开节点编辑抽屉
  const handleOpenNodeEditDrawer = (editData: Partial<EntityNode>) => {
    if (editData?.entityId) {
      setCurEntityId(editData.entityId);
    }
    openModal(MODAL_TYPE.EDIT_ENTITY, { editingNode: editData as unknown as EntityNode });
  };

  // 打开批量添加字段弹窗
  const handleOpenFieldConfigModal = (node: EntityNode) => {
    if (node?.entityId) {
      setCurEntityId(node.entityId);
    }
    openModal(MODAL_TYPE.CONFIG_FIELD, { nodedata: node as unknown as EntityNode });
  };

  // 打开节点添加关联关系弹窗
  const handleOpenRelationModal = (id: string) => {
    setCurEntityId(id);
    setUpdateRelationOptions(true);
    setOnlyUpdateNode(false);
    openModal(MODAL_TYPE.CREATE_RELATION, { selectedEntityId: id });
  };

  // 打开节点添加主子关系弹窗
  const handleOpenMasterModal = (id: string) => {
    setCurEntityId(id);
    setOnlyUpdateNode(false);
    openModal(MODAL_TYPE.CREATE_MASTER_DETAIL, { selectedEntityId: id });
  };

  // 字段点击
  const handleFieldClick = (fieldId: string, entityId?: string) => {
    // 点击字段移除新增标记
    if (entityId && newFieldSignal.isNewField(entityId, fieldId)) {
      setTimeout(() => {
        newFieldSignal.removeNewField(entityId, fieldId);
      }, 500);
    }
    openModal(MODAL_TYPE.EDIT_FIELD, { selectedFieldId: fieldId });
  };

  // 打开节点删除弹窗
  const handleOpenNodeDeleteModal = (id: string) => {
    openModal(MODAL_TYPE.DELETE_CONFIRM, { nodeId: id });
  };

  // 更新节点位置
  const handleUpdateEntityPosition = async (data: EntityNode, x: number, y: number) => {
    const params = {
      id: data.entityId,
      displayName: data.entityName,
      tableName: data.tableName,
      displayConfig: JSON.stringify({ x, y }),
      code: data.code,
      datasourceId: curDataSourceId,
      applicationId: curAppId
    };
    const res = await updateEntity(params as unknown as UpdateEntityReqVO);
    if (res) {
      console.log('实体位置成功');
    }
  };

  // 更新节点状态
  const handleStatusChange = async (data: Partial<EntityNode>) => {
    const params = {
      id: data.entityId,
      status: data.status,
      tableName: data.tableName,
      displayName: data.entityName,
      datasourceId: curDataSourceId,
      applicationId: curAppId
    };
    const res = await updateEntity(params as unknown as UpdateEntityReqVO);
    if (res) {
      console.log('实体状态更新成功');
      handleSuccessCallback();
    }
  };

  // 编辑实体信息
  const editEntityInfo = async (data: Partial<Entity>) => {
    const params = {
      id: data.id,
      displayName: data.displayName,
      tableName: data.tableName,
      description: data.description,
      datasourceId: curDataSourceId,
      applicationId: curAppId
    };
    const res = await updateEntity(params as unknown as UpdateEntityReqVO);
    if (res) {
      Message.success('保存成功');
      console.log('实体信息更新成功');
      handleSuccessCallback();
    }
  };

  // 删除节点
  const confirmDelete = async () => {
    setDeleteLoading(true);
    const nodeId = getModalData('nodeId') as string;
    const res = await deleteEntity(nodeId);
    if (res) {
      Message.success('删除节点成功');
      loadEntityList(curDataSourceId);
    }
    closeModal();
    setDeleteLoading(false);
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  // 成功回调
  const handleSuccessCallback = async () => {
    setOnlyUpdateNode(true);
    setRefreshEntityList(true);
  };

  // 创建实体成功回调
  const createEntityCallback = () => {
    setRefreshEntityList(true);
    setOnlyUpdateNode(false);
  };

  // 打开编辑关联关系抽屉
  const handleOpenEdgeEditDrawer = (data: EdgeData) => {
    openModal(MODAL_TYPE.EDIT_RELATION, { relationData: data });
  };

  // 跳转到字典页面
  const gotoDictPage = () => {
    closeModal();
    handleMenuClick('data-dict');
  };

  // 获取图表位置
  // const getGraphPositon = () => {
  //   return chartRef.current?.getGraphPositon();
  // };

  useEffect(() => {
    if (refreshEntityList && curDataSourceId) {
      loadEntityList(curDataSourceId);
      setRefreshEntityList(false);
    }
  }, [refreshEntityList]);

  // 数据源ID变化
  useEffect(() => {
    if (!dsData?.id) {
      setData({ nodes: [], edges: [] });
      return;
    }

    if (prevDataSourceIdRef.current && prevDataSourceIdRef.current !== dsData?.id) {
      console.log('数据源切换，清理旧实体数据');
      setData({ nodes: [], edges: [] });
      newFieldSignal.clearAllNewFields();
    }

    prevDataSourceIdRef.current = dsData?.id;

    loadEntityList(dsData?.id);
  }, [dsData]);

  return (
    <div style={{ height: '100%' }} className={styles['entity-page-container']}>
      <ERchart
        mode="edit"
        data={data as unknown as EntityERProps['data']}
        onNodeEdit={handleOpenNodeEditDrawer}
        onNodeAddField={handleOpenFieldConfigModal}
        onNodeAddRelation={handleOpenRelationModal}
        onNodeAddMasterDetail={handleOpenMasterModal}
        onNodeDelete={handleOpenNodeDeleteModal}
        onFieldClick={handleFieldClick}
        onlyUpdateNode={onlyUpdateNode}
        updateEntityPosition={handleUpdateEntityPosition}
        onEdgeEdit={handleOpenEdgeEditDrawer}
        onStatusChange={handleStatusChange}
        ref={chartRef}
      />
      <Button
        type="primary"
        className={styles.entityPageCreateButton}
        onClick={() => {
          openModal(MODAL_TYPE.CREATE_ENTITY);
        }}
      >
        <IconPlus />
        创建数据资产
      </Button>

      {/* 交互弹窗、抽屉、模态框 */}
      <EditEntityDrawer
        visible={isModalOpen(MODAL_TYPE.EDIT_ENTITY)}
        setVisible={(visible) => !visible && closeModal()}
        editingNode={getModalData('editingNode') as EntityNode}
        setEditingNode={(node: EntityNode | null) => setModalDataValue('editingNode', node)}
        onNodeEdit={editEntityInfo}
        successCallback={handleSuccessCallback}
      />
      <CreateEntityModal
        visible={isModalOpen(MODAL_TYPE.CREATE_ENTITY)}
        setVisible={(visible) => !visible && closeModal()}
        successCallback={createEntityCallback}
        // getGraphPositon={getGraphPositon}
      />
      <ConfigFieldModal
        visible={isModalOpen(MODAL_TYPE.CONFIG_FIELD)}
        setVisible={(visible) => !visible && closeModal()}
        entity={getModalData('nodedata') as EntityNode}
        successCallback={handleSuccessCallback}
        gotoDictPage={gotoDictPage}
        entities={data.nodes}
      />
      <CreateRelationModal
        visible={isModalOpen(MODAL_TYPE.CREATE_RELATION)}
        entityId={getModalData('selectedEntityId') as string}
        setVisible={(visible) => !visible && closeModal()}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />
      <CreateMasterDetailModal
        visible={isModalOpen(MODAL_TYPE.CREATE_MASTER_DETAIL)}
        setVisible={(visible) => !visible && closeModal()}
        entityId={getModalData('selectedEntityId') as string}
        successCallback={handleSuccessCallback}
      />
      <EditRelationDrawer
        visible={isModalOpen(MODAL_TYPE.EDIT_RELATION)}
        setVisible={(visible) => !visible && closeModal()}
        relationData={getModalData('relationData') as EdgeData & { relationshipType: string }}
        onSuccess={handleSuccessCallback}
      />
      <EditFieldDrawer
        visible={isModalOpen(MODAL_TYPE.EDIT_FIELD)}
        setVisible={(visible) => !visible && closeModal()}
        fieldId={getModalData('selectedFieldId') as string}
        onSuccess={handleSuccessCallback}
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
    </div>
  );
};
