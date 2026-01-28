import { Table, Button, Alert, Select, Tag, Modal, Message } from '@arco-design/web-react';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import { IconInfoCircleFill } from '@arco-design/web-react/icon';
import {
  ExportStatus,
  pageExportAppVersion,
  deleteExportAppVersion,
  exportAppVersionFile,
  retryExportAppVersion,
  type AppExportRecord
} from '@onebase/app';
import { downloadFileByUrl } from '@onebase/ui-kit';
import dayjs from 'dayjs';
import { useAppStore } from '@/store';
import React, { useEffect, useState } from 'react';
import AppExportModal from '@/components/AppExportModal';
import styles from './index.module.less';

const AppExportPage: React.FC = () => {
  const { curAppInfo } = useAppStore();
  // 应用导出/下载弹窗
  const [exportVisible, setExportVisible] = useState(false);

  const [status, setStatus] = useState('');
  const statusOptions = [
    { label: '全部状态', value: '' },
    { label: '导出中', value: ExportStatus.EXPORTING },
    { label: '导出成功', value: ExportStatus.SUCCESS },
    { label: '导出失败', value: ExportStatus.ERROR }
  ];
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
      dataIndex: 'creatorName',
      key: 'creatorName'
    },
    {
      title: '操作时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (value: string) => {
        return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      }
    },
    {
      title: '状态',
      dataIndex: 'exportStatus',
      key: 'exportStatus',
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
        return '未知';
      }
    },
    {
      title: '操作',
      key: 'actions',
      align: 'center',
      width: 200,
      render: (_, record: any) => (
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
  const handleDownload = async (record: AppExportRecord) => {
    const fileUrl = await exportAppVersionFile({ exportId: record.id }, curAppInfo.appName);
    if (fileUrl) {
      const date = dayjs(new Date()).format('YYYYMMDD');
      downloadFileByUrl(fileUrl, `${curAppInfo.appName}_${curAppInfo.appCode}_${date}.zip`);
    }
  };

  // 重试
  const handleRetry = async (record: AppExportRecord) => {
    const res = await retryExportAppVersion({ exportId: record.id });
    if (res) {
      getExportList();
    }
  };

  // 删除
  const handleDelete = (record: AppExportRecord) => {
    Modal.confirm({
      title: `确定要删除吗？`,
      content: `删除后，数据将被永久删除，操作不可逆，请谨慎操作。`,
      onOk: async () => {
        await deleteExportAppVersion({ exportId: record.id });
        Message.success('删除成功');
      }
    });
  };

  // 状态改变重新获取表格数据
  useEffect(() => {
    getExportList();
  }, [status, pagination.current, pagination.pageSize]);

  // 接口获取表格数据
  const getExportList = async () => {
    const param = {
      exportStatus: status,
      pageNo: pagination.current,
      pageSize: pagination.pageSize
    };
    const res = await pageExportAppVersion(param);
    setTableData(res?.list || []);
    setPagination((prev) => ({ ...prev, total: res?.total || 0 }));
  };

  return (
    <div className={styles.appExportPage}>
      <Alert icon={<IconInfoCircleFill />} content="导出文件保留180天，逾期将自动删除且无法恢复，请及时下载。" />
      <div className={styles.headerActions}>
        {/* <Button type="primary" onClick={() => setExportVisible(true)}>
          应用导出
        </Button> */}
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
