import { convertEntityListItemToConfigField } from '@/pages/CreateApp/pages/DataFactory/utils/dataConverter';
import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { FIELD_TYPE, FIELD_TYPE_LABEL } from '@onebase/ui-kit';
import { useAppStore } from '@/store/store_app';
import { useFieldStore } from '@/store/store_field';
import type { TableColumnProps } from '@arco-design/web-react';
import { Button, Message, Modal, Space, Table, Tag } from '@arco-design/web-react';
import { deleteField, getEntityFieldsPage } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import EditFieldDrawer from '../../Drawers/EditFieldDrawer';
import ConfigFieldModal from '../../Modals/ConfigFieldModal';
import styles from './tabs.module.less';

interface DataFieldsProps {
  entity: EntityListItem;
  activeTab: string;
}

const DataFields: React.FC<DataFieldsProps> = ({ entity, activeTab }) => {
  const { curAppId } = useAppStore();
  const [fields, setFields] = useState([]);
  const [loading, setLoading] = useState(false);
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [selectedFieldId, setSelectedFieldId] = useState<string>('');
  const [configFieldModalVisible, setConfigFieldModalVisible] = useState(false);
  const [page, setPage] = useState({ pageNo: 1, pageSize: 10 });
  const [total, setTotal] = useState(0);
  const { fieldTypes } = useFieldStore();

  // 加载字段列表
  const loadFields = async () => {
    try {
      setLoading(true);
      const params = {
        entityId: entity.id,
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        applicationId: curAppId
      };
      const response = await getEntityFieldsPage(params);
      console.log('getEntityFields', response);
      if (response?.list) {
        setFields(response.list || []);
        setTotal(response.total || 0);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddField = () => {
    setSelectedFieldId('');
    setConfigFieldModalVisible(true);
  };

  // 处理编辑字段
  const handleEditField = (fieldId: string) => {
    setSelectedFieldId(fieldId);
    setEditDrawerVisible(true);
  };

  // 编辑成功回调
  const handleEditSuccess = () => {
    loadFields(); // 重新加载字段列表
  };

  // 删除字段
  const handleDeleteField = async (fieldId: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个字段吗？删除后无法恢复。',
      onOk: async () => {
        try {
          const res = await deleteField(fieldId);
          if (res) {
            Message.success('删除字段成功');
            loadFields(); // 重新加载字段列表
          }
        } catch (error) {
          console.error('删除字段失败:', error);
        }
      }
    });
  };

  useEffect(() => {
    if (activeTab === 'fields') {
      loadFields();
    }
  }, [entity, activeTab, page]);

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 80,
      render: (text: string, record: any, index: number) => index + 1 + (page.pageNo - 1) * page.pageSize
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      key: 'fieldName',
      width: 250
    },
    {
      title: '展示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 250
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '数据类型',
      dataIndex: 'fieldType',
      key: 'fieldType',
      width: 200,
      render: (fieldType: string) => (
        <Tag color="cyan">{fieldTypes.find((item) => item.fieldType === fieldType)?.displayName}</Tag>
      )
    },
    // {
    //   title: '字段类型',
    //   dataIndex: 'isSystemField',
    //   key: 'isSystemField',
    //   render: (isSystemField: number) => (
    //     <Tag color={isSystemField === FIELD_TYPE.SYSTEM ? 'red' : 'green'}>
    //       {FIELD_TYPE_LABEL[isSystemField as keyof typeof FIELD_TYPE_LABEL]}
    //     </Tag>
    //   )
    // },
    // {
    //   title: '默认值',
    //   dataIndex: 'defaultValue',
    //   key: 'defaultValue'
    // },
    // {
    //   title: '唯一',
    //   dataIndex: 'isUnique',
    //   key: 'isUnique',
    //   width: 70,
    //   render: (isUnique: boolean) => (isUnique ? '是' : '否')
    // },
    // {
    //   title: '必填',
    //   dataIndex: 'isRequired',
    //   key: 'isRequired',
    //   width: 70,
    //   render: (isRequired: boolean) => (isRequired ? '是' : '否')
    // },
    // {
    //   title: '长度范围',
    //   dataIndex: 'dataLength',
    //   key: 'dataLength',
    //   width: 90
    // },
    // {
    //   title: '正则校验',
    //   dataIndex: 'validationRulesId',
    //   key: 'validationRulesId',
    //   width: 90
    // },
    {
      title: '操作',
      key: 'operation',
      width: 80,
      render: (_, record) => (
        <Space>
          {record.isSystemField === FIELD_TYPE.CUSTOM && (
            <Button type="text" size="mini" onClick={() => handleEditField(record.id)}>
              编辑
            </Button>
          )}
          {/* 本期不支持删除 */}
          {record.isSystemField === FIELD_TYPE.CUSTOM && (
            <Button type="text" size="mini" status="danger" onClick={() => handleDeleteField(record.id)} disabled>
              删除
            </Button>
          )}
        </Space>
      )
    }
  ];

  return (
    <div className={styles.dataFields}>
      <div className={styles.header}>
        {/* <h3>数据字段</h3> */}
        <Button type="primary" size="small" onClick={() => handleAddField()}>
          字段配置
        </Button>
      </div>
      <Table
        columns={columns}
        data={fields}
        rowKey="id"
        pagination={{
          pageSize: page.pageSize,
          current: page.pageNo,
          total: total,
          onChange: (pageNo, pageSize) => {
            setPage({ pageNo, pageSize });
          }
        }}
        className={styles.table}
        loading={loading}
      />
      <EditFieldDrawer
        visible={editDrawerVisible}
        setVisible={setEditDrawerVisible}
        fieldId={selectedFieldId}
        onSuccess={handleEditSuccess}
      />
      <ConfigFieldModal
        visible={configFieldModalVisible}
        setVisible={setConfigFieldModalVisible}
        entity={convertEntityListItemToConfigField(entity)}
        successCallback={loadFields}
      />
    </div>
  );
};

export default DataFields;
