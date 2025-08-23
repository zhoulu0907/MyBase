import { type FC } from 'react';

import { type NodePanelRenderProps } from '@flowgram.ai/free-node-panel-plugin';

import { Popover } from '@arco-design/web-react';
import './index.less';
import { NodeList } from './node-list';

export const NodePanel: FC<NodePanelRenderProps> = (props) => {
  const { onSelect, position, onClose, containerNode, panelProps = {} } = props;

  return (
    // Mickey.zhou 为了替换semi的popover组件，但是效果还行，先这样吧 :）
    <Popover
      trigger="hover"
      onVisibleChange={(v) => (v ? null : onClose())}
      content={<NodeList onSelect={onSelect} containerNode={containerNode} />}
      position="right"
    >
      <div
        style={{
          position: 'absolute',
          top: position.y - 10,
          left: position.x - 10,
          width: 20,
          height: 20,
          backgroundColor: 'transparent'
        }}
      ></div>
    </Popover>
  );
};
