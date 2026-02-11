import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, Descriptions, Typography, Table, Button, Input, Space } from '@arco-design/web-react';
import { IconSearch, IconRefresh } from '@arco-design/web-react/icon';

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

export default function IotInfo() {
  const [searchParams] = useSearchParams();
  const [params, setParams] = useState<ParamData[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 8;

  // 假数据
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
    { id: '12', name: '功率因数', value: '0.95', unit: '', status: 'normal', updateTime: '2026-02-10 16:11:10' },
  ];

  useEffect(() => {
    const paramArray: ParamData[] = [];
    searchParams.forEach((value, key) => {
      paramArray.push({ key, value });
    });
    setParams(paramArray);
    // 控制台输出参数
    console.log('URL参数:', Object.fromEntries(searchParams));
  }, [searchParams]);

  // 过滤数据
  const filteredParams = deviceParams.filter(param => 
    param.name.toLowerCase().includes(searchValue.toLowerCase())
  );

  // 分页数据
  const paginatedParams = filteredParams.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  return (
    <div style={{ padding: '20px', background: '#f5f5f5', minHeight: '100vh', boxSizing: 'border-box' }}>
      {/* 设备概览 */}
      <Card style={{ marginBottom: '20px' }}>
        <Title heading={4} style={{ margin: 0 }}>
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
            { label: '上次维护', value: '2026-02-01' },
          ]}
          column={2}
          bordered
        />
      </Card>

      {/* 设备运行参数 */}
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <Title heading={4} style={{ margin: 0 }}>
            设备运行参数
          </Title>
          <Space>
            <Button type="primary" style={{ color: '#ff0000', borderColor: '#ff0000' }}>
              开机
            </Button>
            <Button type="primary" style={{ color: '#0000ff', borderColor: '#0000ff' }}>
              关机
            </Button>
          </Space>
        </div>

        {/* 搜索栏 */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
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
             <Button onClick={() => setSearchValue('')}>
               重置
             </Button>
           </Space>
         </div>

        {/* 参数卡片列表 */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '16px', marginBottom: '20px' }}>
          {paginatedParams.map((param) => (
            <Card key={param.id} bordered={false} hoverable>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div style={{ fontSize: '14px', color: '#86909c' }}>{param.name}</div>
                <div style={{ display: 'flex', alignItems: 'baseline', gap: '8px' }}>
                  <span style={{ fontSize: '24px', fontWeight: 'bold', color: param.status === 'error' ? '#ff4d4f' : param.status === 'warning' ? '#faad14' : '#165dff' }}>
                    {param.value}
                  </span>
                  <span style={{ fontSize: '14px', color: '#86909c' }}>{param.unit}</span>
                </div>
                <div style={{ fontSize: '12px', color: '#86909c' }}>{param.updateTime}</div>
              </div>
            </Card>
          ))}
        </div>

        {/* 分页 */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: '14px', color: '#86909c' }}>
            共 {filteredParams.length} 条，当前第 {currentPage} 页
          </div>
          <Space>
            <Button disabled={currentPage === 1} onClick={() => setCurrentPage(currentPage - 1)}>
              上一页
            </Button>
            <Button disabled={currentPage >= Math.ceil(filteredParams.length / pageSize)} onClick={() => setCurrentPage(currentPage + 1)}>
              下一页
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  );
}
