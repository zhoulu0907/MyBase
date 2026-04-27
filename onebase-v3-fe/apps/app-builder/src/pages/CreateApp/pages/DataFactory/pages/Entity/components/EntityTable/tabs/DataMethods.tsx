import ResizableTable from '@/components/ResizableTable';
import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import type { TableColumnProps } from '@arco-design/web-react';
import { Link, Tag } from '@arco-design/web-react';
import { getEntityMethods } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './tabs.module.less';
import CheckMethodModal from '../../Modals/CheckMethodModal';

interface DataMethodsProps {
  entity: EntityListItem;
  activeTab: string;
}

const DataMethods: React.FC<DataMethodsProps> = ({ entity, activeTab }) => {
  const [methods, setMethods] = useState([]);
  const [loading, setLoading] = useState(false);
  const [checkMethodModalVisible, setCheckMethodModalVisible] = useState(false);
  const [checkMethodMethodCode, setcheckMethodMethodCode] = useState('');
  const loadMethods = async () => {
    try {
      setLoading(true);
      const params = {
        entityId: entity.id
      };
      const response = await getEntityMethods(params);
      console.log('getEntityMethods', response);
      if (response) {
        setMethods(response || []);
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleMethodClick = (methodName: string, methodCode: string) => {
    setcheckMethodMethodCode(methodCode);
    setCheckMethodModalVisible(true);
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      render: (text: string, record: { id: string }, index: number) => index + 1
    },
    {
      title: '方法名称',
      dataIndex: 'methodName',
      key: 'methodName',
      render: (methodName: string, record: { methodCode: string }) => (
        <Link onClick={() => handleMethodClick(methodName, record.methodCode)} className={styles.methodName}>
          {methodName}
        </Link>
      )
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
    if (activeTab === 'methods') {
      loadMethods();
    }
  }, [entity, activeTab]);

  return (
    <div className={styles.dataMethods}>
      {/* 后续迭代补充 */}
      {/* <div className={styles.header}>
        <Button type="primary" size="small">
          添加方法
        </Button>
      </div> */}
      <ResizableTable
        columns={columns}
        data={methods}
        rowKey="methodCode"
        pagination={false}
        className={styles.table}
        loading={loading}
      />
      <CheckMethodModal
        visible={checkMethodModalVisible}
        setVisible={setCheckMethodModalVisible}
        entity={entity}
        methodCode={checkMethodMethodCode}
      />
    </div>
  );
};

export default DataMethods;
