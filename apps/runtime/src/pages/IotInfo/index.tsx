import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, Descriptions, Typography } from '@arco-design/web-react';

const { Title } = Typography;

interface ParamData {
  key: string;
  value: string;
}

export default function IotInfo() {
  const [searchParams] = useSearchParams();
  const [params, setParams] = useState<ParamData[]>([]);

  useEffect(() => {
    const paramArray: ParamData[] = [];
    searchParams.forEach((value, key) => {
      paramArray.push({ key, value });
    });
    setParams(paramArray);
  }, [searchParams]);

  return (
    <div style={{ padding: '40px', maxWidth: '1200px', margin: '0 auto' }}>
      <Title heading={3} style={{ marginBottom: '24px' }}>
        IoT 信息页面
      </Title>

      <Card>
        <Descriptions
          title="URL 参数列表"
          data={params.map((param) => ({
            label: param.key,
            value: param.value || '(空值)'
          }))}
          column={1}
          bordered
        />
        {params.length === 0 && (
          <div style={{ textAlign: 'center', padding: '40px', color: '#86909c' }}>
            暂无参数，请通过 URL ?key=value 形式传递参数
          </div>
        )}
      </Card>

      <Card style={{ marginTop: '24px' }}>
        <Title heading={5} style={{ marginBottom: '16px' }}>
          完整 URL
        </Title>
        <div style={{ 
          background: '#f2f3f5', 
          padding: '12px', 
          borderRadius: '4px', 
          fontFamily: 'monospace',
          wordBreak: 'break-all'
        }}>
          {window.location.href}
        </div>
      </Card>

      <Card style={{ marginTop: '24px' }}>
        <Title heading={5} style={{ marginBottom: '16px' }}>
          参数统计
        </Title>
        <div>
          <p>参数数量: <strong>{params.length}</strong></p>
        </div>
      </Card>
    </div>
  );
}
