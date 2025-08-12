import React, { useState } from 'react';
import { Card, Table, Button, Space, Typography, Pagination } from '@arco-design/web-react';
import { IconArrowLeft, IconSave, IconArrowUp } from '@arco-design/web-react/icon';
import styles from './VersionManagement.module.less';
import PublishVersionModal from './modals/PublishVersionModal';
import SaveVersionModal from './modals/SaveVersionModal';
import EditVersionModal from './modals/EditVersionModal';

interface VersionRecord {
  id: string;
  versionName: string;
  versionNumber: string;
  description: string;
  environment: string;
  operationType: string;
  operator: string;
  operationTime: string;
}

interface EditModalData {
  versionName: string;
  versionNumber: string;
  description: string;
}

interface formData {
  versionName: string;
  versionNumber: string;
  description: string;
  environment: string;
}

const VersionManagement: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 弹窗状态
  const [publishModalVisible, setPublishModalVisible] = useState(false);
  const [saveModalVisible, setSaveModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editModalData, setEditModalData] = useState<EditModalData | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

  // 模拟数据
  const mockData: VersionRecord[] = [
    {
      id: '1',
      versionName: 'OB3.0_V2.0.7',
      versionNumber: 'V2.0.7',
      description: '这是一段版本描述',
      environment: '正式环境',
      operationType: '发布版本',
      operator: '巫炘',
      operationTime: '2025-07-24 10:08:49'
    },
    {
      id: '2',
      versionName: 'OB3.0_V2.0.6',
      versionNumber: 'V2.0.6',
      description: '这是一段版本描述',
      environment: '',
      operationType: '保存版本',
      operator: '某管理员',
      operationTime: '2025-07-23 20:56:39'
    },
    {
      id: '3',
      versionName: '回退至OB3.0_V2版本',
      versionNumber: 'V2.0.4',
      description: '这是一段版本描述',
      environment: '',
      operationType: '保存版本',
      operator: '某管理员',
      operationTime: '2025-07-22 15:30:12'
    }
  ];

  // 弹窗处理函数
  const handlePublishVersion = () => {
    setPublishModalVisible(true);
  };

  const handleSaveVersion = () => {
    setSaveModalVisible(true);
  };

  const handleEnableVersion = (record: VersionRecord) => {
    console.log('启用版本:', record);
  };

  // const handleCompareVersion = (record: VersionRecord) => {
  //   console.log('版本比对:', record);
  // };

  const handleAccessVersion = (record: VersionRecord) => {
    console.log('访问版本:', record);
  };

  const handleEditVersion = (record: VersionRecord) => {
    setEditModalData({
      versionName: record.versionName,
      versionNumber: record.versionNumber,
      description: record.description
    });
    setEditModalVisible(true);
  };

  const handlePublishModalOk = async (values: formData) => {
    setModalLoading(true);
    try {
      console.log('发布版本:', values);
      // TODO: 调用发布版本API
      setPublishModalVisible(false);
    } catch (error) {
      console.error('发布版本失败:', error);
    } finally {
      setModalLoading(false);
    }
  };

  const handleSaveModalOk = async (values: Partial<formData>) => {
    setModalLoading(true);
    try {
      console.log('保存版本:', values);
      // TODO: 调用保存版本API
      setSaveModalVisible(false);
    } catch (error) {
      console.error('保存版本失败:', error);
    } finally {
      setModalLoading(false);
    }
  };

  const handleEditModalOk = async (values: Partial<formData>) => {
    setModalLoading(true);
    try {
      console.log('编辑版本:', values);
      // TODO: 调用编辑版本API
      setEditModalVisible(false);
    } catch (error) {
      console.error('编辑版本失败:', error);
    } finally {
      setModalLoading(false);
    }
  };

  const columns = [
    {
      title: '版本名称',
      dataIndex: 'versionName',
      key: 'versionName',
      align: 'center',
      width: 200
    },
    {
      title: '版本号',
      dataIndex: 'versionNumber',
      key: 'versionNumber',
      align: 'center',
      width: 120
    },
    {
      title: '版本描述',
      dataIndex: 'description',
      key: 'description',
      align: 'center',
      width: 200,
      ellipsis: true
    },
    {
      title: '发布环境',
      dataIndex: 'environment',
      key: 'environment',
      align: 'center',
      width: 120,
      render: (value: string) => value || '-'
    },
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      align: 'center',
      width: 120
    },
    {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator',
      align: 'center',
      width: 120
    },
    {
      title: '操作时间',
      dataIndex: 'operationTime',
      key: 'operationTime',
      align: 'center',
      width: 180
    },
    {
      title: '操作',
      key: 'actions',
      align: 'center',
      width: 200,
      render: (_: unknown, record: VersionRecord) => (
        <Space>
          <Button type="text" size="small" onClick={() => handleEnableVersion(record)}>
            启用此版本
          </Button>
          <Button type="text" size="small" onClick={() => handleAccessVersion(record)}>
            访问
          </Button>
          <Button type="text" size="small" onClick={() => handleEditVersion(record)}>
            编辑
          </Button>
          {/* <Button type="text" size="small" onClick={() => handleCompareVersion(record)}>
            版本比对
          </Button> */}
        </Space>
      )
    }
  ];

  return (
    <Card className={styles.versionManagement}>
      <div className={styles.header}>
        <Typography.Title heading={5} className={styles.title}>
          版本管理
        </Typography.Title>
        <Space>
          <Button type="outline" icon={<IconArrowLeft />} className={styles.actionButton}>
            版本回退
          </Button>
          <Button type="outline" icon={<IconSave />} onClick={handleSaveVersion} className={styles.actionButton}>
            保存版本
          </Button>
          <Button type="primary" icon={<IconArrowUp />} onClick={handlePublishVersion} className={styles.publishButton}>
            发布版本
          </Button>
        </Space>
      </div>

      <Table columns={columns} data={mockData} rowKey="id" pagination={false} className={styles.versionTable} />

      <div className={styles.pagination}>
        <Pagination
          total={20}
          current={currentPage}
          pageSize={pageSize}
          onChange={setCurrentPage}
          onPageSizeChange={setPageSize}
          showTotal
          sizeCanChange
          sizeOptions={[10, 20, 50]}
        />
      </div>

      {/* 弹窗组件 */}
      <PublishVersionModal
        visible={publishModalVisible}
        onCancel={() => setPublishModalVisible(false)}
        onOk={handlePublishModalOk}
        loading={modalLoading}
      />

      <SaveVersionModal
        visible={saveModalVisible}
        onCancel={() => setSaveModalVisible(false)}
        onOk={handleSaveModalOk}
        loading={modalLoading}
      />

      <EditVersionModal
        visible={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        onOk={handleEditModalOk}
        loading={modalLoading}
        initialData={editModalData || undefined}
      />
    </Card>
  );
};

export default VersionManagement;
