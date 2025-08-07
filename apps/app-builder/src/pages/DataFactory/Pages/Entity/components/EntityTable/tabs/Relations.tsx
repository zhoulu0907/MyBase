import React, { useState, useEffect } from 'react';
import { Table, Tag, Button, Space, Message } from '@arco-design/web-react';
import type { TableColumnProps } from '@arco-design/web-react';
import type { EntityNode } from '../../../../../utils/interface';
import { getEntityRelations } from '@onebase/app/src/services/entity';
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

  useEffect(() => {
    // // 从localStorage加载关联关系数据
    // const { edges } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ edges: [] }));
    // const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));

    // // 过滤出与当前实体相关的关联关系
    // const entityRelations = edges.filter((edge: EdgeData) =>
    //   edge.source.cell === entity.id || edge.target.cell === entity.id
    // );

    // const relationData = entityRelations.map((edge: EdgeData, index: number) => {
    //   const sourceEntity = nodes.find((node: EntityNode) => node.id === edge.source.cell);
    //   const targetEntity = nodes.find((node: EntityNode) => node.id === edge.target.cell);

    //   return {
    //     id: `relation-${index}`,
    //     sourceEntity: sourceEntity?.title || edge.source.cell,
    //     sourceField: edge.source.port,
    //     targetEntity: targetEntity?.title || edge.target.cell,
    //     targetField: edge.target.port,
    //     relationType: '一对一', // 这里可以根据实际数据调整
    //   };
    // });

    // setRelations(relationData);

    getRelation();
  }, [entity.id]);

  const columns: TableColumnProps[] = [
    {
      title: '源实体',
      dataIndex: 'sourceEntity',
      key: 'sourceEntity'
    },
    {
      title: '源字段',
      dataIndex: 'sourceField',
      key: 'sourceField'
    },
    {
      title: '关联类型',
      dataIndex: 'relationType',
      key: 'relationType',
      render: (type: string) => <Tag color="purple">{type}</Tag>
    },
    {
      title: '目标实体',
      dataIndex: 'targetEntity',
      key: 'targetEntity'
    },
    {
      title: '目标字段',
      dataIndex: 'targetField',
      key: 'targetField'
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
        entityId: entity.id,
        pageNo: 1,
        pageSize: 10,
        appId: 1
      };
      const response = await getEntityRelations(params);
      console.log('getEntityRelations', response);
      if (response.data) {
        setRelations(response.data);
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
        <Button type="primary" size="small">
          添加关联
        </Button>
      </div>
      <Table columns={columns} data={relations} rowKey="id" pagination={false} className={styles.table} />
    </div>
  );
};

export default Relations;
