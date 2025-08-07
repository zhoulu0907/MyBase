import React, { useEffect, useState } from 'react';
import { Table, Tag, Button, Message } from '@arco-design/web-react';
import type { TableColumnProps } from '@arco-design/web-react';
import type { EntityNode } from '../../../../../utils/interface';
import { getEntityMethods } from '@onebase/app/src/services/entity';
import styles from './tabs.module.less';

interface DataMethodsProps {
  entity: EntityNode;
}

const DataMethods: React.FC<DataMethodsProps> = ({ entity }) => {
  const [methods, setMethods] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadMethods = async () => {
    try {
      setLoading(true);
      const response = await getEntityMethods({ entityId: entity.id });
      console.log('getEntityMethods', response);
      if (response) {
        setMethods(response || []);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    } finally {
      setLoading(false);
    }
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index'
    },
    {
      title: '方法名称',
      dataIndex: 'methodName',
      key: 'methodName'
    },
    {
      title: '方法编码',
      dataIndex: 'methodCode',
      key: 'methodCode'
    },
    {
      title: '方法类型',
      dataIndex: 'methodType',
      key: 'methodType',
      render: (methodType: string) => <Tag color="cyan">{methodType}</Tag>
    },
    {
      title: 'URL',
      dataIndex: 'url',
      key: 'url'
    }
    // {
    //   title: '方法描述',
    //   dataIndex: 'description',
    //   key: 'description',
    // },
    // {
    //   title: '状态',
    //   dataIndex: 'status',
    //   key: 'status',
    //   render: (status: string) => (
    //     <Tag color={status === 'active' ? 'green' : 'red'}>
    //       {status === 'active' ? '启用' : '禁用'}
    //     </Tag>
    //   ),
    // },
  ];

  useEffect(() => {
    loadMethods();
  }, []);

  return (
    <div className={styles.dataMethods}>
      <div className={styles.header}>
        <h3>数据方法</h3>
        <Button type="primary" size="small">
          添加方法
        </Button>
      </div>
      <Table
        columns={columns}
        data={methods}
        rowKey="id"
        pagination={false}
        className={styles.table}
        loading={loading}
      />
    </div>
  );
};

export default DataMethods;
