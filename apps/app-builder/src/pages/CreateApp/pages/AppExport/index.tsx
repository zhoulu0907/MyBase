import { Table, Button, Alert, Select, Tag, Modal } from '@arco-design/web-react';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import { IconInfoCircleFill } from '@arco-design/web-react/icon';
import { ExportStatus, type AppExportRecord } from '@onebase/app';
import dayjs from 'dayjs';
import { useAppStore } from '@/store';
import React, { useEffect, useState } from 'react';
import AppExportModal from '@/components/AppExportModal';
import AppImportModal from '@/components/AppImportModal';
import styles from './index.module.less';

const AppExportPage: React.FC = () => {
  const { curAppInfo } = useAppStore();
  // 应用导出/下载弹窗
  const [exportVisible, setExportVisible] = useState(false);

  const [status, setStatus] = useState('');
  const statusOptions = [{ label: '全部状态', value: '' }];
  const [tableData, setTableData] = useState<AppExportRecord[]>([]);
  const [pagination, setPagination] = useState({
    sizeCanChange: true,
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    pageSizeChangeResetCurrent: true
  });
  const columns: ColumnProps<AppExportRecord>[] = [
    {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator'
    },
    {
      title: '操作时间',
      dataIndex: 'operateTime',
      key: 'operateTime',
      render: (value: string) => {
        return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (value: ExportStatus) => {
        if (value === ExportStatus.EXPORTING) {
          return <Tag className={styles.exportingTag}>导出中</Tag>;
        }
        if (value === ExportStatus.SUCCESS) {
          return <Tag className={styles.successTag}>导出成功</Tag>;
        }
        if (value === ExportStatus.ERROR) {
          return <Tag className={styles.errorTag}>导出失败</Tag>;
        }
        return '--';
      }
    },
    {
      title: '操作',
      key: 'actions',
      align: 'center',
      width: 200,
      render: (_, record: AppExportRecord) => (
        <>
          {record.status === ExportStatus.SUCCESS && (
            <Button size="mini" type="text" onClick={() => handleDownload(record)}>
              下载
            </Button>
          )}
          {record.status === ExportStatus.ERROR && (
            <Button size="mini" type="text" onClick={() => handleRetry(record)}>
              重试
            </Button>
          )}
          <Button size="mini" type="text" status="danger" onClick={() => handleDelete(record)}>
            删除
          </Button>
        </>
      )
    }
  ];

  // 下载
  const handleDownload = (record: AppExportRecord) => {
    console.log(record);
    // todo
  };

  // 重试
  const handleRetry = (record: AppExportRecord) => {
    console.log(record);
    // todo
  };

  // 删除
  const handleDelete = (record: AppExportRecord) => {
    Modal.confirm({
      title: `确定要删除吗？`,
      content: `删除后，数据将被永久删除，操作不可逆，请谨慎操作。`,
      onOk: async () => {
        // todo 接口删除
      }
    });
  };

  // 状态改变重新获取表格数据
  useEffect(() => {
    getExportList();
  }, [status, pagination]);

  // todo 接口获取表格数据
  const getExportList = () => {
    setTableData([{ id: '11', operator: '张三', operateTime: '2026-01-23 11:13:00', status: 'success' }]);
  };

  return (
    <div className={styles.appExportPage}>
      <Alert icon={<IconInfoCircleFill />} content="导出文件保留180天，逾期将自动删除且无法恢复，请及时下载。" />
      <div className={styles.headerActions}>
        <Button type="primary" onClick={() => setExportVisible(true)}>
          应用导出
        </Button>
        <div>
          <Select
            placeholder="全部状态"
            bordered={false}
            style={{ width: 120 }}
            value={status}
            onChange={(value) => setStatus(value)}
            options={statusOptions}
          ></Select>
        </div>
      </div>
      <Table
        columns={columns}
        data={tableData}
        rowKey="id"
        pagination={pagination}
        onChange={(paginate) => {
          const { current, pageSize } = paginate;
          setPagination((prev) => ({ ...prev, current: current || prev.current, pageSize: pageSize || prev.pageSize }));
        }}
      />
      <AppExportModal visible={exportVisible} onClose={() => setExportVisible(false)} appInfo={curAppInfo} />
    </div>
  );
};

export default AppExportPage;
