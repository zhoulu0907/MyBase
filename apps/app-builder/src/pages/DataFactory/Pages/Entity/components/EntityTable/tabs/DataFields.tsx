import React, { useState, useEffect } from 'react';
import { Table, Tag, Button, Space, Message, Modal } from '@arco-design/web-react';
import type { TableColumnProps } from '@arco-design/web-react';
import type { EntityNode } from '../../../../../utils/interface';
import { getEntityFields, deleteField } from '@onebase/app/src/services/entity';
import styles from './tabs.module.less';

interface DataFieldsProps {
  entity: EntityNode;
}

const DataFields: React.FC<DataFieldsProps> = ({ entity }) => {
  const [fields, setFields] = useState(entity.fields || []);
  const [loading, setLoading] = useState(false);

  // 加载字段列表
  const loadFields = async () => {
    try {
      setLoading(true);
      const response = await getEntityFields({ entityId: entity.id });
      console.log('getEntityFields', response);
      if (response) {
        setFields(response);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 删除字段
  const handleDeleteField = async (fieldId: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个字段吗？删除后无法恢复。',
      onOk: async () => {
        try {
          await deleteField(fieldId);
          Message.success('删除字段成功');
          loadFields(); // 重新加载字段列表
        } catch (error) {
          console.error('删除字段失败:', error);
          Message.error('删除字段失败');
        }
      }
    });
  };

  useEffect(() => {
    loadFields();
  }, [entity.id]);

  const columns: TableColumnProps[] = [
    {
      title: '字段编码',
      dataIndex: 'displayName',
      key: 'displayName'
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      key: 'fieldName'
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '数据类型',
      dataIndex: 'fieldType',
      key: 'fieldType'
    },
    {
      title: '字段类型',
      dataIndex: 'isSystemField',
      key: 'isSystemField',
      render: (isSystemField: boolean) => (
        <Tag color={isSystemField ? 'red' : 'green'}>{isSystemField ? '系统字段' : '自定义字段'}</Tag>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue'
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      key: 'isUnique'
    },
    {
      title: '必填',
      dataIndex: 'isRequired',
      key: 'isRequired'
    },
    {
      title: '允许空值',
      dataIndex: 'allowNull',
      key: 'allowNull'
    },
    {
      title: '长度范围',
      dataIndex: 'dataLength',
      key: 'dataLength'
    },
    {
      title: '正则校验',
      dataIndex: 'validationRulesId',
      key: 'validationRulesId'
    },
    {
      title: '操作',
      key: 'operation',
      render: (_, record) => (
        <Space>
          <Button type="text" size="mini">
            编辑
          </Button>
          <Button type="text" size="mini" status="danger" onClick={() => handleDeleteField(record.id)}>
            删除
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div className={styles.dataFields}>
      <div className={styles.header}>
        <h3>数据字段</h3>
        <Button type="primary" size="small">
          添加字段
        </Button>
      </div>
      <Table
        columns={columns}
        data={fields}
        rowKey="id"
        pagination={false}
        className={styles.table}
        loading={loading}
      />
    </div>
  );
};

export default DataFields;
