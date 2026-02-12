import { useEffect, useState, useRef, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, Descriptions, Typography, Table, Button, Input, Space } from '@arco-design/web-react';
import { IconSearch, IconRefresh } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const { Title } = Typography;

interface ParamData {
  key: string;
  value: string;
}

interface DeviceParam {
  id: string;
  name: string;
  value: string;
  unit: string;
  status: 'normal' | 'warning' | 'error';
  updateTime: string;
}

const deviceParams: DeviceParam[] = [
  { id: '1', name: '转速', value: '3', unit: 'rpm', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '2', name: '轴振动', value: '3.18', unit: 'mm/s', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '3', name: '轴承温度', value: '20.39', unit: '°C', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '4', name: '电机温度', value: '0.09', unit: '°C', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '5', name: '绕组温度', value: '38777', unit: '°C', status: 'error', updateTime: '2026-02-10 16:11:10' },
  { id: '6', name: '轴向位移', value: '--', unit: 'mm', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '7', name: '径向振动', value: '--', unit: 'mm', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '8', name: '电压', value: '--', unit: 'V', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '9', name: '电流', value: '100', unit: 'A', status: 'warning', updateTime: '2026-02-10 16:11:10' },
  { id: '10', name: '功率', value: '50', unit: 'kW', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '11', name: '频率', value: '50', unit: 'Hz', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  { id: '12', name: '功率因数', value: '0.95', unit: '', status: 'normal', updateTime: '2026-02-10 16:11:10' }
];

export default function IotInfo() {
  const [searchParams] = useSearchParams();
  const [params, setParams] = useState<ParamData[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;
  const lastHeightRef = useRef<number>(0);
  const debounceTimerRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    const paramArray: ParamData[] = [];
    searchParams.forEach((value, key) => {
      paramArray.push({ key, value });
    });
    setParams(paramArray);
    // 控制台输出参数
    console.log('URL参数:', Object.fromEntries(searchParams));
  }, [searchParams]);

  const sendHeight = useCallback(() => {
    const htmlHeight = document.documentElement.offsetHeight;
    const htmlScrollHeight = document.documentElement.scrollHeight;

    const height = Math.max(htmlHeight, htmlScrollHeight);

    console.log('当前高度:', {
      htmlHeight,
      htmlScrollHeight,
      finalHeight: height,
      lastHeight: lastHeightRef.current
    });

    if (height !== lastHeightRef.current && height > 0) {
      lastHeightRef.current = height;
      window.parent.postMessage({ type: 'iframeHeight', height }, '*');
      console.log('发送高度消息:', height);
    }
  }, []);

  useEffect(() => {
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }

    debounceTimerRef.current = setTimeout(() => {
      sendHeight();
    }, 300);

    const handleMessage = (event: MessageEvent) => {
      if (event.data && event.data.type === 'recalculateHeight') {
        requestAnimationFrame(() => {
          requestAnimationFrame(() => {
            sendHeight();
          });
        });
      }
    };

    window.addEventListener('message', handleMessage);

    return () => {
      if (debounceTimerRef.current) {
        clearTimeout(debounceTimerRef.current);
      }
      window.removeEventListener('message', handleMessage);
    };
  }, [searchParams, searchValue, currentPage, sendHeight]);

  // 过滤数据
  const filteredParams = deviceParams.filter((param) => param.name.toLowerCase().includes(searchValue.toLowerCase()));

  // 分页数据
  const paginatedParams = filteredParams.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className={styles.iotInfoPage}>
      {/* 设备概览 */}
      <Card className={styles.deviceOverview}>
        <Title heading={4} style={{ margin: 0, padding: '16px 0' }}>
          设备概览
        </Title>
        <Descriptions
          data={[
            { label: '设备名称', value: '离心式压缩机' },
            { label: '设备型号', value: 'C1200-15-4.5/0.8' },
            { label: '设备编号', value: '8888202502050001' },
            { label: '设备状态', value: '运行中' },
            { label: '安装位置', value: '一号厂房' },
            { label: '额定功率', value: '1000kW' },
            { label: '额定转速', value: '1500rpm' },
            { label: '额定电压', value: '10kV' },
            { label: '额定电流', value: '65A' },
            { label: '上次维护', value: '2026-02-01' }
          ]}
          column={2}
        />
      </Card>

      {/* 设备运行参数 */}
      <Card className={styles.deviceParams}>
        <div className={styles.header}>
          <Title heading={4} style={{ margin: 0, padding: '16px 0' }}>
            设备运行参数
          </Title>
          <Space>
            <Button type="primary">开机</Button>
            <Button type="primary" status="danger">
              关机
            </Button>
          </Space>
        </div>

        {/* 搜索栏 */}
        <div className={styles.searchBar}>
          <Input
            prefix={<IconSearch />}
            placeholder="搜索参数"
            value={searchValue}
            onChange={(value) => setSearchValue(value)}
            style={{ width: 300 }}
          />
          <Space>
            <Button type="primary" icon={<IconSearch />}>
              搜索
            </Button>
            <Button onClick={() => setSearchValue('')}>重置</Button>
          </Space>
        </div>

        {/* 参数卡片列表 */}
        <div className={styles.paramsGrid}>
          {paginatedParams.map((param) => (
            <Card key={param.id} bordered={false} hoverable>
              <div className={styles.paramCard}>
                <div className={styles.paramName}>{param.name}</div>
                <div className={styles.paramValue}>
                  <span
                    className={`${styles.value} ${param.status === 'error' ? styles.error : param.status === 'warning' ? styles.warning : ''}`}
                  >
                    {param.value}
                  </span>
                  <span className={styles.unit}>{param.unit}</span>
                </div>
                <div className={styles.updateTime}>{param.updateTime}</div>
              </div>
            </Card>
          ))}
        </div>

        {/* 分页 */}
        <div className={styles.pagination}>
          <div className={styles.pageInfo}>
            {' '}
            共 {filteredParams.length} 条，当前第 {currentPage} 页{' '}
          </div>
          <Space>
            <Button disabled={currentPage === 1} onClick={() => setCurrentPage(currentPage - 1)}>
              上一页
            </Button>
            <Button
              disabled={currentPage >= Math.ceil(filteredParams.length / pageSize)}
              onClick={() => setCurrentPage(currentPage + 1)}
            >
              下一页
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  );
}
