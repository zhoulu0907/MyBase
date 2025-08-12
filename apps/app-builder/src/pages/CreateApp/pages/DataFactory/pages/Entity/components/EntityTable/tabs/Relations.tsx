import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import type { TableColumnProps } from '@arco-design/web-react';
import { Button, Message, Space, Table, Tag } from '@arco-design/web-react';
import { getEntityRelations } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import CreateRelationModal from '../../Modals/CreateRelationModal';
import EditRelationDrawer from '../../Drawers/EditRelationDrawer';
import styles from './tabs.module.less';

interface RelationsProps {
  entity: EntityListItem;
  activeTab: string;
}

interface RelationData {
  id: string;
  sourceEntityName: string;
  sourceEntityId: string;
  sourceFieldName: string;
  sourceFieldId: string;
  targetEntityName: string;
  targetEntityId: string;
  targetFieldName: string;
  targetFieldId: string;
  relationshipType: string;
}

const Relations: React.FC<RelationsProps> = ({ entity, activeTab }) => {
  const [relations, setRelations] = useState<RelationData[]>([]);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [editRelationDrawerVisible, setEditRelationDrawerVisible] = useState(false);
  const [selectedRelation, setSelectedRelation] = useState<RelationData | null>(null);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  const [page, setPage] = useState({ pageNo: 1, pageSize: 10 });
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const handleCreate = () => {
    setCreateRelationModalVisible(true);
  };

  const handleSuccessCallback = () => {
    getRelation();
  };

  const handleEditRelation = (record: RelationData) => {
    setSelectedRelation(record);
    setEditRelationDrawerVisible(true);
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
      title: '源实体',
      dataIndex: 'sourceEntityName',
      key: 'sourceEntityName'
    },
    {
      title: '源字段',
      dataIndex: 'sourceFieldName',
      key: 'sourceFieldName'
    },
    {
      title: '关联类型',
      dataIndex: 'relationshipType',
      key: 'relationshipType',
      render: (type: string) => <Tag color="purple">{type}</Tag>
    },
    {
      title: '目标实体',
      dataIndex: 'targetEntityName',
      key: 'targetEntityName'
    },
    {
      title: '目标字段',
      dataIndex: 'targetFieldName',
      key: 'targetFieldName'
    },
    {
      title: '操作',
      key: 'operation',
      render: (_, record: RelationData) => (
        <Space>
          <Button type="text" size="mini" onClick={() => handleEditRelation(record)}>
            编辑
          </Button>
          <Button type="text" size="mini" status="danger">
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
        appId: '1'
      };
      const response = await getEntityRelations(params);
      console.log('getEntityRelations', response);
      if (response?.list?.length > 0) {
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
        <Button type="primary" size="small" onClick={handleCreate}>
          添加关联
        </Button>
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
      />

      <EditRelationDrawer
        visible={editRelationDrawerVisible}
        setVisible={setEditRelationDrawerVisible}
        relationData={selectedRelation}
        onSuccess={handleSuccessCallback}
      />
    </div>
  );
};

export default Relations;
