import { Collapse, Divider } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XCollapseConfig } from './schema';

const CollapseItem = Collapse.Item;

const XCollapse = memo((props: XCollapseConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true } = props;

  return (
    <Collapse
      defaultActiveKey={['1', '2']}
      style={{
        maxWidth: '100%',
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
      }}
    >
      <CollapseItem header="Beijing Toutiao Technology Co., Ltd." name="1">
        Beijing Toutiao Technology Co., Ltd.
        <Divider style={{ margin: '8px 0' }} />
        Beijing Toutiao Technology Co., Ltd.
        <Divider style={{ margin: '8px 0' }} />
        Beijing Toutiao Technology Co., Ltd.
      </CollapseItem>

      <CollapseItem header="Introduce" name="2" disabled>
        ByteDance's core product, Toutiao ('Headlines'), is a content platform in China and around the world. Toutiao
        started out as a news recommendation engine and gradually evolved into a platform delivering content in various
        formats, such as texts, images, question-and-answer posts, microblogs, and videos.
      </CollapseItem>

      <CollapseItem header="The Underlying AI Technology" name="3">
        In 2016, ByteDance's AI Lab and Peking University co-developed Xiaomingbot (张小明), an artificial intelligence
        bot that writes news articles. The bot published 450 articles during the 15-day 2016 Summer Olympics in Rio de
        Janeiro. In general, Xiaomingbot published stories approximately two seconds after the event ended.
      </CollapseItem>
    </Collapse>
  );
});

export default XCollapse;
