import React, { useCallback, useRef, useState } from 'react';

import { useClientContext, WorkflowPortEntity, WorkflowPortRender } from '@flowgram.ai/free-layout-editor';

import iconDelete from '@assets/images/delete.svg';
import { useNodeRenderContext } from '../../hooks';
import { type FlowNodeMeta } from '../../typings';
import './node-wrapper.less';
import { NodeWrapperStyle } from './styles';
import { scrollToView } from './utils';
import { clearDownStreamNodeConfig } from '../../configs/utils';
import { useSignals } from '@preact/signals-react/runtime';
import { etlEditorSignal } from '@onebase/common';

export interface NodeWrapperProps {
  isScrollToView?: boolean;
  children: React.ReactNode;
}

/**
 * Used for drag-and-drop/click events and ports rendering of nodes
 * 用于节点的拖拽/点击事件和点位渲染
 */
export const NodeWrapper: React.FC<NodeWrapperProps> = (props) => {
  const { children, isScrollToView = false } = props;
  const nodeRender = useNodeRenderContext();
  const { node, selected, startDrag, ports, selectNode, nodeRef, onFocus, onBlur, deleteNode } = nodeRender;

  useSignals();
  
  const { nodeData, graphData } = etlEditorSignal;

  const [isDragging, setIsDragging] = useState(false);
  const [isHover, setIsHover] = useState(false);

  const form = nodeRender.form;
  const ctx = useClientContext();
  const meta = node.getNodeMeta<FlowNodeMeta>();
  const childRef = useRef<HTMLDivElement>(null);
  const portsRender = ports.map((p: WorkflowPortEntity) => <WorkflowPortRender key={p.id} entity={p} />);
  const onMouseOver = useCallback(() => {
    setIsHover(true);
  }, [node]);
  const onMouseOut = useCallback(
    (e: React.MouseEvent) => {
      const toElement = e.relatedTarget as Node | null;
      // 判断鼠标是不是进入了子元素
      if (childRef.current && childRef.current.contains(toElement)) {
        return; // 不隐藏
      }
      setIsHover(false);
    },
    [node]
  );
  const handleDeleteNode = useCallback(() => {
    // 删除节点
    console.log('删除节点', node);
    clearDownStreamNodeConfig(node.id, graphData.value, nodeData.value);
    deleteNode();
  }, [node]);

  const copyNode = useCallback(() => {
    console.log('复制节点');
  }, [node]);

  return (
    <>
      {(isHover || selected) && (
        <div className="showMore" ref={childRef} onMouseOut={onMouseOut}>
          {/* <div className="iconBox" onClick={copyNode}>
            <img src={iconCopy} />
          </div> */}
          <div className="iconBox" onClick={handleDeleteNode}>
            <img src={iconDelete} />
          </div>
        </div>
      )}
      <NodeWrapperStyle
        className={selected ? 'selected' : ''}
        ref={nodeRef}
        draggable
        onMouseOver={onMouseOver}
        onMouseOut={onMouseOut}
        onDragStart={(e) => {
          startDrag(e);
          setIsDragging(true);
        }}
        onTouchStart={(e) => {
          startDrag(e as unknown as React.MouseEvent);
          setIsDragging(true);
        }}
        onClick={(e) => {
          selectNode(e);
          if (!isDragging) {
            // 可选：将 isScrollToView 设为 true，可以让节点选中后滚动到画布中间
            // Optional: Set isScrollToView to true to scroll the node to the center of the canvas after it is selected.
            if (isScrollToView) {
              scrollToView(ctx, nodeRender.node);
            }
          }
        }}
        onMouseUp={() => setIsDragging(false)}
        onFocus={onFocus}
        onBlur={onBlur}
        data-node-selected={String(selected)}
        style={{
          ...meta.wrapperStyle,
          outline: form?.values?.errMsg?.length > 0 ? '1px solid red' : 'none'
        }}
      >
        {children}
      </NodeWrapperStyle>
      {portsRender}
    </>
  );
};
