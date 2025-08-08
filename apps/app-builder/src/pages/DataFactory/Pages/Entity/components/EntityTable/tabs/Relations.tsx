import React, { useState, useEffect } from 'react';
import { Table, Tag, Button, Space, Message } from '@arco-design/web-react';
import type { TableColumnProps } from '@arco-design/web-react';
import type { EntityNode } from '../../../../../utils/interface';
import { getEntityRelations } from '@onebase/app';
import CreateRelationModal from '../../Modals/CreateRelationModal';
import styles from './tabs.module.less';

interface RelationsProps {
  entity: EntityNode;
}

interface RelationData {
  id: string;
  sourceEntity: string;
  sourceField: string;
  targetEntity: string;
  targetField: string;
  relationType: string;
}

const Relations: React.FC<RelationsProps> = ({ entity }) => {
  const [relations, setRelations] = useState<RelationData[]>([]);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);

  const handleCreate = () => {
    setCreateRelationModalVisible(true);
  }

  const handleSuccessCallback = () => {
    getRelation();
  }

  useEffect(() => {
    getRelation();
  }, []);

  const columns: TableColumnProps[] = [
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
      dataIndex: 'targetEntityName',
      key: 'targetEntityName'
    },
    {
      title: '操作',
      key: 'operation',
      render: () => (
        <Space>
          <Button type="text" size="mini">
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
      // setLoading(true);
      // TODO 传参后续补充完整
      const params = {
        entityId: entity.entityId,
        pageNo: 1,
        pageSize: 10,
        appId: 1
      };
      const response = await getEntityRelations(params);
      console.log('getEntityRelations', response);
      if (response?.list?.length > 0) {
        setRelations(response.list);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    } finally {
      // setLoading(false);
    }
  };

  return (
    <div className={styles.relations}>
      <div className={styles.header}>
        <h3>关联关系</h3>
        <Button type="primary" size="small" onClick={handleCreate}>
          添加关联
        </Button>
      </div>
      <Table 
        columns={columns} 
        data={relations} 
        rowKey="id" 
        pagination={false} 
        className={styles.table} 
      />

      <CreateRelationModal
        visible={createRelationModalVisible}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />  
    </div>
  );
};

export default Relations;
