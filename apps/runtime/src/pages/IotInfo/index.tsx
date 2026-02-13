import { useEffect, useState, useRef, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, Descriptions, Typography, Button, Input, Space, Message } from '@arco-design/web-react';
import { IconSearch, IconRefresh } from '@arco-design/web-react/icon';
import { createClient, getRuntimeBackendURL } from '@onebase/common';
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

interface DeviceRuntimeParamResVO {
  speed?: number;
  current?: number;
  outputPower?: number;
  ratedPower?: number;
  outputVoltage?: number;
  inputVoltage?: number;
  outputCurrent?: number;
  faultCode?: string;
  runTime?: number;
  pressure?: number;
  deviceId?: string;
  deviceName?: string;
  updateTime?: string;
}

const httpClient = createClient('/tiangong', getRuntimeBackendURL());

const formatDateTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const formatNumber = (value: number | string | undefined): string => {
  if (value === undefined || value === null) return '--';
  const num = Number(value);
  if (isNaN(num)) return '--';
  return num.toFixed(2);
};

export default function IotInfo() {
  const [searchParams] = useSearchParams();
  const [params, setParams] = useState<ParamData[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [deviceParams, setDeviceParams] = useState<DeviceParam[]>([]);
  const pageSize = 10;
  const lastHeightRef = useRef<number>(0);
  const debounceTimerRef = useRef<NodeJS.Timeout | null>(null);
  const pollingTimerRef = useRef<NodeJS.Timeout | null>(null);

  const transformRuntimeData = (data: DeviceRuntimeParamResVO): DeviceParam[] => {
    const updateTime = formatDateTime();

    const currentStatus = data.current !== undefined && Number(data.current) > 80 ? 'warning' : 'normal';
    const faultStatus = data.faultCode ? 'error' : 'normal';

    return [
      { id: 'speed', name: '转速', value: formatNumber(data.speed), unit: 'rpm', status: 'normal', updateTime },
      { id: 'current', name: '电流', value: formatNumber(data.current), unit: 'A', status: currentStatus, updateTime },
      { id: 'outputPower', name: '输出功率', value: formatNumber(data.outputPower), unit: 'kW', status: 'normal', updateTime },
      { id: 'ratedPower', name: '电机额定功率', value: formatNumber(data.ratedPower), unit: 'kW', status: 'normal', updateTime },
      { id: 'outputVoltage', name: '输出电压', value: formatNumber(data.outputVoltage), unit: 'V', status: 'normal', updateTime },
      { id: 'inputVoltage', name: '输入电压', value: formatNumber(data.inputVoltage), unit: 'V', status: 'normal', updateTime },
      { id: 'outputCurrent', name: '输出电流', value: formatNumber(data.outputCurrent), unit: 'A', status: 'normal', updateTime },
      { id: 'faultCode', name: '故障码', value: data.faultCode || '--', unit: '', status: faultStatus, updateTime },
      { id: 'runTime', name: '运行时间', value: formatNumber(data.runTime), unit: 'h', status: 'normal', updateTime },
      { id: 'pressure', name: '压力', value: formatNumber(data.pressure), unit: 'MPa', status: 'normal', updateTime }
    ];
  };

  const fetchDeviceRuntimeParams = useCallback(async () => {
    try {
      const res = await httpClient.get<DeviceRuntimeParamResVO[]>('/device/device-runtime-params');
      if (res) {
        const allParams: DeviceParam[] = [];
        res.forEach((item) => {
          allParams.push(...transformRuntimeData(item));
        });
        setDeviceParams(allParams);
      }
    } catch (error) {
      console.error('获取设备运行参数失败:', error);
      Message.error('获取设备运行参数失败');
    }
  }, []);

  const clearPollingTimer = useCallback(() => {
    if (pollingTimerRef.current) {
      clearInterval(pollingTimerRef.current);
      pollingTimerRef.current = null;
    }
  }, []);

  useEffect(() => {
    fetchDeviceRuntimeParams();

    pollingTimerRef.current = setInterval(() => {
      fetchDeviceRuntimeParams();
    }, 3000);

    const handleBeforeUnload = () => {
      clearPollingTimer();
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      clearPollingTimer();
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [fetchDeviceRuntimeParams, clearPollingTimer]);

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
            <Button icon={<IconRefresh />} onClick={fetchDeviceRuntimeParams}>
              刷新
            </Button>
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
          <div className={styles.pageInfo}> 当前第 {currentPage} 页 </div>
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
