import { Card, Pagination, Space, Table, Typography } from '@arco-design/web-react';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import React, { useState } from 'react';
import EditVersionModal from '../modals/EditVersionModal';
import SaveVersionModal from '../modals/SaveVersionModal';

import dayjs from 'dayjs';
import type { VersionRecord } from '../..';
import styles from './index.module.less';

interface VersionManagementProps {
  applicationId: string;
  list: VersionRecord[];
  total: number;
  pageSize: number;
  setPageSize: (pageSize: number) => void;
  currentPage: number;
  setCurrentPage: (currentPage: number) => void;
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

const VersionManagement: React.FC<VersionManagementProps> = ({
  list,
  total,
  pageSize,
  setPageSize,
  currentPage,
  setCurrentPage
}) => {
  // 弹窗状态
  const [publishModalVisible, setPublishModalVisible] = useState(false);
  const [saveModalVisible, setSaveModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editModalData, setEditModalData] = useState<EditModalData | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

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

  const columns: ColumnProps<VersionRecord>[] = [
    {
      title: '名称',
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
      dataIndex: 'versionDescription',
      key: 'versionDescription',
      align: 'center',
      width: 200,
      ellipsis: true
    },
    {
      title: '版本类型',
      dataIndex: 'versionTypeLabel',
      key: 'versionTypeLabel',
      align: 'center',
      width: 120
      // render: (value: number) => {
      //   return value === OperationType.PUBLISH ? '发布版本' : '保存版本';
      // }
    },
    {
      title: '操作人',
      dataIndex: 'updaterName',
      key: 'updaterName',
      align: 'center',
      width: 120
    },
    {
      title: '操作时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      align: 'center',
      width: 180,
      render: (value: string) => {
        return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      }
    }
    // {
    //   title: '操作',
    //   key: 'actions',
    //   align: 'center',
    //   width: 200,
    //   render: (_: unknown, record: VersionRecord) => (
    //     <Space>
    //       <Button type="text" size="small" onClick={() => handleEnableVersion(record)}>
    //         启用此版本
    //       </Button>
    //       <Button type="text" size="small" onClick={() => handleAccessVersion(record)}>
    //         访问
    //       </Button>
    //       <Button type="text" size="small" onClick={() => handleEditVersion(record)}>
    //         编辑
    //       </Button>
    //       {/* <Button type="text" size="small" onClick={() => handleCompareVersion(record)}>
    //         版本比对
    //       </Button> */}
    //     </Space>
    //   )
    // }
  ];

  return (
    <Card className={styles.versionManagement}>
      <div className={styles.header}>
        <Typography.Title heading={5} className={styles.title}>
          版本管理
        </Typography.Title>
        <Space>
          {/* <Button icon={<IconArrowLeft />} className={styles.actionButton}>
            版本回退
          </Button>
          <Button icon={<IconSave />} onClick={handleSaveVersion} className={styles.actionButton}>
            保存版本
          </Button>
          <Button type="primary" icon={<IconArrowUp />} onClick={handlePublishVersion}>
            发布版本
          </Button> */}
        </Space>
      </div>

      <Table
        scroll={{
          y: 300
        }}
        columns={columns}
        data={list}
        rowKey="id"
        pagination={false}
        className={styles.versionTable}
      />

      <div className={styles.pagination}>
        <Pagination
          total={total}
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
