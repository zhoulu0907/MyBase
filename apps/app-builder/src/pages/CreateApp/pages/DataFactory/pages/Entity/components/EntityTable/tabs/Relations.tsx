import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import type { TableColumnProps } from '@arco-design/web-react';
import { Button, Dropdown, Menu, Message, Space, Table, Tag } from '@arco-design/web-react';
import { getEntityRelations, deleteRelation } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import EditRelationDrawer from '../../Drawers/EditRelationDrawer';
import CreateRelationModal from '../../Modals/CreateRelationModal';
import CreateMasterDetailModal from '../../Modals/CreateMasterDetailModal';
import DeleteConfirmModal from '../../Modals/DeleteConfirmModal';
import styles from './tabs.module.less';

interface RelationsProps {
  entity: EntityListItem;
  activeTab: string;
  reloadList: () => void;
}

interface RelationData {
  id: string;
  sourceEntityName: string;
  sourceEntityId: string;
  sourceFieldDisplayName: string;
  sourceFieldId: string;
  targetEntityName: string;
  targetEntityId: string;
  targetFieldDisplayName: string;
  targetFieldId: string;
  relationshipType: string;
}

const Relations: React.FC<RelationsProps> = ({ entity, activeTab, reloadList }) => {
  const { curAppId } = useAppStore();
  const [relations, setRelations] = useState<RelationData[]>([]);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [createMasterDetailModalVisible, setCreateMasterDetailModalVisible] = useState(false);
  const [editRelationDrawerVisible, setEditRelationDrawerVisible] = useState(false);
  const [selectedRelation, setSelectedRelation] = useState<RelationData | null>(null);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  const [page, setPage] = useState({ pageNo: 1, pageSize: 10 });
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [deleteConfirmModalVisible, setDeleteConfirmModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const handleCreate = (type: 'master_child' | 'relation') => {
    if (type === 'master_child') {
      setCreateMasterDetailModalVisible(true);
    } else {
      setCreateRelationModalVisible(true);
    }
  };

  const handleSuccessCallback = (type?: 'new_master_child' | 'relation') => {
    getRelation();
    // 新建主子关系子表时，需重新加载资产列表
    if (type === 'new_master_child') {
      reloadList();
    }
  };

  const handleEditRelation = (record: RelationData) => {
    setSelectedRelation(record);
    setEditRelationDrawerVisible(true);
  };

  const handleDeleteConfirm = async () => {
    setDeleteLoading(true);
    const res = await deleteRelation(selectedRelation?.id || '');
    console.log('deleteRelation', res);
    if (res) {
      Message.success('删除成功');
      handleSuccessCallback();
    }
    setDeleteLoading(false);
    setDeleteConfirmModalVisible(false);
  };

  const handleDelete = (record: RelationData) => {
    setSelectedRelation(record);
    setDeleteConfirmModalVisible(true);
  };

  useEffect(() => {
    if (activeTab === 'relations') {
      getRelation();
    }
  }, [entity, activeTab]);

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      render: (text: string, record: any, index: number) => index + 1 + (page.pageNo - 1) * page.pageSize
    },
    {
      title: '源资产',
      dataIndex: 'sourceEntityName',
      key: 'sourceEntityName'
    },
    {
      title: '源字段',
      dataIndex: 'sourceFieldDisplayName',
      key: 'sourceFieldDisplayName'
    },
    {
      title: '关联类型',
      dataIndex: 'relationshipType',
      key: 'relationshipType',
      render: (type: string) => <Tag color="purple">{type}</Tag>
    },
    {
      title: '目标资产',
      dataIndex: 'targetEntityName',
      key: 'targetEntityName'
    },
    {
      title: '目标字段',
      dataIndex: 'targetFieldDisplayName',
      key: 'targetFieldDisplayName'
    },
    {
      title: '操作',
      key: 'operation',
      render: (_, record: RelationData) => (
        <Space>
          <Button type="text" size="mini" onClick={() => handleEditRelation(record)}>
            编辑
          </Button>
          {/* 本期不支持删除 */}
          <Button type="text" size="mini" status="danger" onClick={() => handleDelete(record)} disabled>
            删除
          </Button>
        </Space>
      )
    }
  ];

  const getRelation = async () => {
    try {
      setLoading(true);
      const params = {
        entityId: entity.id,
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        applicationId: curAppId
      };
      const response = await getEntityRelations(params);
      console.log('getEntityRelations', response);
      if (response?.list) {
        setRelations(response.list);
        setTotal(response.total || 0);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.relations}>
      <div className={styles.header}>
        <Dropdown
          droplist={
            <Menu>
              <Menu.Item key="1" onClick={() => handleCreate('master_child')}>
                添加主子关系
              </Menu.Item>
              <Menu.Item key="2" onClick={() => handleCreate('relation')}>
                添加关联关系
              </Menu.Item>
            </Menu>
          }
          trigger="hover"
        >
          <Button type="primary" size="small">
            添加关联
          </Button>
        </Dropdown>
      </div>
      <Table
        columns={columns}
        data={relations}
        rowKey="id"
        pagination={{
          pageSize: page.pageSize,
          current: page.pageNo,
          total: total,
          onChange: (pageNo, pageSize) => {
            setPage({ pageNo, pageSize });
          }
        }}
        loading={loading}
        className={styles.table}
      />

      <CreateRelationModal
        visible={createRelationModalVisible}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
        entityId={entity.id}
      />

      <CreateMasterDetailModal
        visible={createMasterDetailModalVisible}
        setVisible={setCreateMasterDetailModalVisible}
        entityId={entity.id}
        successCallback={handleSuccessCallback}
      />

      <EditRelationDrawer
        visible={editRelationDrawerVisible}
        setVisible={setEditRelationDrawerVisible}
        relationData={selectedRelation}
        onSuccess={handleSuccessCallback}
      />

      <DeleteConfirmModal
        visible={deleteConfirmModalVisible}
        onVisibleChange={setDeleteConfirmModalVisible}
        onConfirm={handleDeleteConfirm}
        confirmLoading={deleteLoading}
        title="确认删除"
        content="确定要删除这个关联关系吗？删除后无法恢复。"
        okText="确认删除"
        cancelText="取消"
      />
    </div>
  );
};

export default Relations;
