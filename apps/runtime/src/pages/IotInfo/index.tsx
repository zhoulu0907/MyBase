import { useEffect, useState, useRef, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, Descriptions, Typography, Button, Input, Space, Message } from '@arco-design/web-react';
import { IconSearch, IconRefresh } from '@arco-design/web-react/icon';
import { createClient } from '@onebase/common';
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





// 第三方接口请求封装 - 使用 fetch API
const createThirdPartyClient = (baseURL: string) => {
  return {
    async post<T>(url: string, data: any, config?: any) {
      try {
        const headers = {
          'Content-Type': 'application/json',
          ...(config?.headers || {})
        };
        
        const response = await fetch(`${baseURL}${url}`, {
          method: 'POST',
          headers,
          body: JSON.stringify(data)
        });
        
        const responseData = await response.json();
        return responseData as T;
      } catch (error) {
        console.error('第三方接口请求失败:', error);
        throw error;
      }
    }
  };
};

const thirdPartyClient = createThirdPartyClient('http://dfecoc.ft.internal.virtueit.net');

interface DatapointPageRequest {
  deviceId: number;
  current: number;
  size: number;
  type: number;
}

interface DatapointRecord {
  id: string;
  pointId: string;
  labelId: string | null;
  labelName: string;
  code: string;
  type: string;
  name: string;
  identifier: string;
  functionType: string;
  valueType: string;
  valueDesc: string;
  value: string | number | null;
  eventTime: string | null;
  rw: number;
  unit: string | null;
  isBeAdopted: boolean;
  createdBy: string;
  createdTime: string;
  updatedBy: string;
  updatedTime: string;
  valueRange: string;
  inputValue: string | null;
  deviceId: string | null;
  mainId: string | null;
  iexpressionParamId: string | null;
  rexpressionParamId: string | null;
}

interface DatapointPageResponse {
  code: string;
  msg: string;
  data: {
    records: DatapointRecord[];
    total: number;
    size: number;
    current: number;
    orders: any[];
    optimizeCountSql: boolean;
    hitCount: boolean;
    countId: any;
    maxLimit: any;
    searchCount: boolean;
    pages: number;
  };
  success: boolean;
}

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



export default function IotInfo() {
  const [searchParams] = useSearchParams();
  const [params, setParams] = useState<ParamData[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [deviceParams, setDeviceParams] = useState<DeviceParam[]>([]);
  const [total, setTotal] = useState(0);
  const [pages, setPages] = useState(0);
  const pageSize = 10;
  const lastHeightRef = useRef<number>(0);
  const debounceTimerRef = useRef<NodeJS.Timeout | null>(null);
  const pollingTimerRef = useRef<NodeJS.Timeout | null>(null);



  const fetchDeviceRuntimeParams = useCallback(async () => {
    try {
      const requestData: DatapointPageRequest = {
        deviceId: 36, // 对应设备device表的字段为organize_id
        current: currentPage,   // 分页参数当前页
        size: pageSize,     // 分页参数页大小
        type: 1       // 固定值
      };
      
      const res = await thirdPartyClient.post<DatapointPageResponse>('/v1/proxybe/api/iot/v1.0.0/devicemodel/datapoint/page', requestData, {
        headers: {
          'tenant_id': '2026495195650420737', // 对应设备device表的字段为main_id
          'customer_id': '1',                // 固定值
          'project': 'indusiot',             // 固定值
          'product': 'base'                  // 固定值
        }
      });
      
      // 灵活处理响应数据，确保即使格式与预期不完全一致也能正确处理
      if (res && (res.success === true || res.code === '100000I')) {
        const data = res.data || {};
        const records = data.records || [];
        
        // 更新分页信息
        setTotal(data.total || 0);
        setPages(data.pages || 0);
        
        const updateTime = formatDateTime();
        const transformedParams: DeviceParam[] = records.map((record: any) => {
          // 解析valueDesc获取单位等信息
          let unit = record.unit || '';
          try {
            const valueDesc = JSON.parse(record.valueDesc);
            if (valueDesc.unit) {
              unit = valueDesc.unit;
            }
          } catch (e) {
            // 解析失败时使用默认值
          }
          
          // 确定状态
          let status: 'normal' | 'warning' | 'error' = 'normal';
          // 这里可以根据实际业务逻辑设置状态判断
          
          return {
            id: record.pointId || record.id,
            name: record.name || '',
            value: record.value !== null ? record.value.toString() : '--',
            unit,
            status,
            updateTime
          };
        });
        setDeviceParams(transformedParams);
      } else {
        // 处理响应失败的情况
        console.error('接口响应失败:', res);
        Message.error('获取设备运行参数失败');
      }
    } catch (error) {
      console.error('获取设备运行参数失败:', error);
      // 不显示错误消息，避免将成功的响应误判为错误
    }
  }, [currentPage, pageSize]);

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

  // 由于接口已经返回了分页后的数据，这里不需要再进行本地分页
  // 直接使用 deviceParams 即可
  const paginatedParams = deviceParams;

  return (
    <div className={styles.iotInfoPage}>
      {/* 设备概览 */}
      <Card className={styles.deviceOverview} bordered={false}>
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
      <Card className={styles.deviceParams} bordered={false}>
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
              <div className={styles.paramCard} >
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
          ))}
        </div>

        {/* 分页 */}
        <div className={styles.pagination}>
          <div className={styles.pageInfo}> 当前第 {currentPage} 页，共 {pages} 页，总 {total} 条 </div>
          <Space>
            <Button disabled={currentPage === 1} onClick={() => setCurrentPage(currentPage - 1)}>
              上一页
            </Button>
            <Button
              disabled={currentPage >= pages}
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
