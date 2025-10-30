import { useCallback, useMemo, useState } from 'react';
import { IconCopyAdd, IconPlusCircle } from '@douyinfe/semi-icons';
import { Popover } from '@douyinfe/semi-ui';
import { useClientContext, type FlowNodeEntity } from '@flowgram.ai/fixed-layout-editor';
import { Message } from '@arco-design/web-react';
import { readData } from '../../shortcuts/utils';
import { NodeList } from '../node-list';
import { PasteIcon, Wrap } from './styles';
import { generateNodeId } from './utils';
import { getIsLoop, getHasLoop, getNodeTitle } from '../../nodes/utils';

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
    const title = getNodeTitle(addProps.data?.title);
    addProps.data = { ...addProps.data, title };
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
        Message.error('剪贴板内容已更新，请重新复制节点。');
        return;
      }
      // 判断复制的nodes 里面是否包含循环节点
      const hasLoop = getHasLoop(nodes);
      //  判断当前位置是否在循环节点内
      const isLoop = getIsLoop(from);
      if (hasLoop && isLoop) {
        Message.error('循环节点不能嵌套循环节点。');
        return;
      }
      nodes.reverse().forEach((n: FlowNodeEntity) => {
        const newNodeData = generateNewIdForChildren(n);
        operation.addFromNode(from, newNodeData);
      });

      Message.success('复制成功！');
    } catch (error) {
      console.error(error);
      Message.error('粘贴失败，请检查您是否有权限读取剪贴板');
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
              color: 'rgb(var(--primary-6))',
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
