import { List } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XListConfig } from './schema';

const XList = memo((props: XListConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true } = props;

  return (
    <List
      style={{
        width: '100%',
        margin: 0,
        padding: 6,
        borderRadius: 8,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}
      size="small"
      header="List title"
      dataSource={[
        'Beijing Bytedance Technology Co., Ltd.',
        'Bytedance Technology Co., Ltd.',
        'Beijing Toutiao Technology Co., Ltd.',
        'Beijing Volcengine Technology Co., Ltd.',
        'China Beijing Bytedance Technology Co., Ltd.'
      ]}
      render={(item, index) => <List.Item key={index}>{item}</List.Item>}
    />
  );
});

export default XList;
