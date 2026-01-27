import { useState, useEffect } from 'react';
import { Table, Spin, Empty, Button, Input, Select, DatePicker, Space } from '@arco-design/web-react';
import { useConnectorWizardStore } from '../store';
import styles from '../index.module.less';

interface Step5RequestLogsProps {
  onSubmit: () => void;
}

const RequestLogsStep: React.FC<Step5RequestLogsProps> = ({ onSubmit }) => {
  const { prevStep } = useConnectorWizardStore();
  const [loading, setLoading] = useState(false);
  const [logs, setLogs] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      // TODO: 调用后端 API
      // const res = await getRequestLogs({
      //   connectorType: connectorType.nodeCode,
      //   appId: getHashQueryParam('appId'),
      //   ...filters,
      // });
      // setLogs(res.list);
      // setTotal(res.total);

      // 临时：空数据
      setLogs([]);
      setTotal(0);
    } catch (error) {
      console.error('获取请求日志失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '请求时间', dataIndex: 'requestTime' },
    { title: '请求URL', dataIndex: 'url' },
    { title: '请求方法', dataIndex: 'method' },
    { title: '状态', dataIndex: 'status' },
    { title: '响应时间 (ms)', dataIndex: 'responseTime' },
  ];

  return (
    <div className={styles.stepContainer}>
      <h3>请求日志</h3>

      {/* 筛选器 */}
      <Space className={styles.filters}>
        <Input placeholder="按请求ID搜索" style={{ width: 200 }} />
        <Select placeholder="按状态筛选" style={{ width: 150 }} />
        <DatePicker.RangePicker placeholder={['开始时间', '结束时间']} />
      </Space>

      <Spin loading={loading} size={40}>
        {logs.length > 0 ? (
          <Table data={logs} columns={columns} pagination={{ total }} />
        ) : (
          <Empty description="暂无请求日志" />
        )}
      </Spin>

      <div className={styles.stepFooter}>
        <Button onClick={prevStep}>上一步</Button>
        <Button type="primary" onClick={onSubmit}>
          完成
        </Button>
      </div>
    </div>
  );
};

export default RequestLogsStep;
