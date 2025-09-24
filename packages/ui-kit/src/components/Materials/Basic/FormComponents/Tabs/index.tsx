import { memo } from 'react';
import { Form, Tabs } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XTabsConfig } from './schema';
// import '../index.css';

const TabPane = Tabs.TabPane;

const XTabs = memo((props: XTabsConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, layout, labelColSpan = 0, runtime = true, type, tabPosition } = props;

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <Tabs defaultActiveTab='1' type={type} tabPosition={tabPosition} style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}>
          <TabPane key='1' title='标签页1'>
            <div>Content of Tab Panel 1</div>
          </TabPane>
          <TabPane key='2' title='标签页2'>
            <div>Content of Tab Panel 2</div>
          </TabPane>
          <TabPane key='3' title='标签页3'>
            <div>Content of Tab Panel 3</div>
          </TabPane>
        </Tabs>
      </Form.Item>
    </div>
  );
});

export default XTabs;
