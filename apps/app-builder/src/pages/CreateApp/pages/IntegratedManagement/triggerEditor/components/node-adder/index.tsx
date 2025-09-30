import { useCallback, useMemo, useState } from 'react';

import { IconCopyAdd, IconPlusCircle } from '@douyinfe/semi-icons';
import { Popover } from '@douyinfe/semi-ui';
import { useClientContext, type FlowNodeEntity } from '@flowgram.ai/fixed-layout-editor';

import { Message } from '@arco-design/web-react';
import { readData } from '../../shortcuts/utils';
import { NodeList } from '../node-list';
import { PasteIcon, Wrap } from './styles';
import { generateNodeId } from './utils';

const generateNewIdForChildren = (n: FlowNodeEntity): FlowNodeEntity => {
  if (n.blocks) {
    return {
      ...n,
      id: generateNodeId(n),
      blocks: n.blocks.map((b) => generateNewIdForChildren(b))
    } as FlowNodeEntity;
  } else {
    return {
      ...n,
      id: generateNodeId(n)
    } as FlowNodeEntity;
  }
};

export default function Adder(props: { from: FlowNodeEntity; to?: FlowNodeEntity; hoverActivated: boolean }) {
  const { from } = props;
  const isVertical = from.isVertical;
  const [visible, setVisible] = useState(false);
  const { playground, operation, clipboard } = useClientContext();

  const [pasteIconVisible, setPasteIconVisible] = useState(false);

  const activated = useMemo(
    () => props.hoverActivated && !playground.config.readonly,
    [props.hoverActivated, playground.config.readonly]
  );

  const add = (addProps: any) => {
    const blocks = addProps.blocks ? addProps.blocks : undefined;
    const block = operation.addFromNode(from, {
      ...addProps,
      blocks
    });
    setTimeout(() => {
      playground.scrollToView({
        bounds: block.bounds,
        scrollToCenter: true
      });
    }, 10);
    setVisible(false);
  };

  const handlePaste = useCallback(async (e: any) => {
    try {
      e.stopPropagation();
      const nodes = await readData(clipboard);

      if (!nodes) {
        Message.error('The clipboard content has been updated, please copy the node again.');
        return;
      }

      nodes.reverse().forEach((n: FlowNodeEntity) => {
        const newNodeData = generateNewIdForChildren(n);
        operation.addFromNode(from, newNodeData);
      });

      Message.success('Paste successfully!');
    } catch (error) {
      console.error(error);
      Message.error('Paste failed, please check if you have permission to read the clipboard');
    }
  }, []);

  if (playground.config.readonly) return null;

  return (
    <Popover
      visible={visible}
      onVisibleChange={setVisible}
      content={<NodeList onSelect={add} from={from} />}
      position="right"
      trigger="click"
      popupAlign={{ offset: [30, 0] }}
      overlayStyle={{
        padding: 0
      }}
    >
      <Wrap
        style={
          props.hoverActivated
            ? {
                width: 15,
                height: 15
              }
            : {}
        }
        onMouseDown={(e) => e.stopPropagation()}
      >
        {props.hoverActivated ? (
          <IconPlusCircle
            onClick={() => {
              setVisible(true);
            }}
            onMouseEnter={() => {
              const data = clipboard.readText();
              setPasteIconVisible(!!data);
            }}
            style={{
              backgroundColor: '#fff',
              color: '#3370ff',
              borderRadius: 15
            }}
          />
        ) : (
          ''
        )}
        {activated && pasteIconVisible && (
          <Popover position="top" showArrow content="粘贴">
            <PasteIcon
              onClick={handlePaste}
              style={
                isVertical
                  ? {
                      right: -25,
                      top: 0
                    }
                  : {
                      right: 0,
                      top: -20
                    }
              }
            >
              <IconCopyAdd
                style={{
                  backgroundColor: 'var(--semi-color-bg-0)',
                  borderRadius: 15
                }}
              />
            </PasteIcon>
          </Popover>
        )}
      </Wrap>
    </Popover>
  );
}
