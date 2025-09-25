import { memo } from 'react';
import { Form, Collapse, Divider } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XCollapseConfig } from './schema';
import '../index.css';

const CollapseItem = Collapse.Item;

const XCollapse = memo((props: XCollapseConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, layout, labelColSpan = 0, runtime = true } = props;

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
        <Collapse
          bordered={false}
          defaultActiveKey={['1']}
          style={{ maxWidth: 1180 }}
        >
          <CollapseItem header='Beijing Toutiao Technology Co., Ltd.' name='1'>
            Beijing Toutiao Technology Co., Ltd.
            <Divider style={{ margin: '8px 0' }}
            />
            Beijing Toutiao Technology Co., Ltd.
            <Divider style={{ margin: '8px 0' }}
            />
            Beijing Toutiao Technology Co., Ltd.
          </CollapseItem>

          <CollapseItem header='Introduce' name='2'>
            ByteDance's core product, Toutiao ('Headlines'), is a content platform in China and around
            the world. Toutiao started out as a news recommendation engine and gradually evolved into a
            platform delivering content in various formats, such as texts, images, question-and-answer
            posts, microblogs, and videos.
          </CollapseItem>

          <CollapseItem header='The Underlying AI Technology' name='3'>
            In 2016, ByteDance's AI Lab and Peking University co-developed Xiaomingbot (张小明), an
            artificial intelligence bot that writes news articles. The bot published 450 articles during
            the 15-day 2016 Summer Olympics in Rio de Janeiro. In general, Xiaomingbot published stories
            approximately two seconds after the event ended.
          </CollapseItem>
        </Collapse>
      </Form.Item>
    </div>
  );
});

export default XCollapse;
