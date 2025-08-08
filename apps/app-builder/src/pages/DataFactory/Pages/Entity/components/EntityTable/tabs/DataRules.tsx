import React, { useEffect, useState } from 'react';
import { Table, Tag, Button, Space, Message } from '@arco-design/web-react';
import type { TableColumnProps } from '@arco-design/web-react';
import type { EntityNode } from '../../../../../utils/interface';
import { getEntityRules } from '@onebase/app';
import styles from './tabs.module.less';

interface DataRulesProps {
  entity: EntityNode;
}

const DataRules: React.FC<DataRulesProps> = ({ entity }) => {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadRules = async () => {
    try {
      setLoading(true);
      const response = await getEntityRules({ entityId: entity.entityId });
      console.log('getEntityRules', response);
      if (response.list) {
        setRules(response.list || []);
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
      title: '校验类型',
      dataIndex: 'validationType',
      key: 'validationType',
      render: (validationType: string) => <Tag color="orange">{validationType}</Tag>
    },
    {
      title: '规则名称',
      dataIndex: 'validationName',
      key: 'validationName'
    },
    {
      title: '校验数据项',
      dataIndex: 'fieldName',
      key: 'fieldName'
    },
    {
      title: '条件设置',
      dataIndex: 'validationCondition',
      key: 'validationCondition'
    },
    {
      title: '验证失败提示语',
      dataIndex: 'errorMessage',
      key: 'errorMessage'
    },
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

  useEffect(() => {
    loadRules();
  }, []);

  return (
    <div className={styles.dataRules}>
      <div className={styles.header}>
        <h3>数据规则</h3>
        <Button type="primary" size="small">
          添加规则
        </Button>
      </div>
      <Table columns={columns} data={rules} rowKey="id" pagination={false} className={styles.table} loading={loading} />
    </div>
  );
};

export default DataRules;
