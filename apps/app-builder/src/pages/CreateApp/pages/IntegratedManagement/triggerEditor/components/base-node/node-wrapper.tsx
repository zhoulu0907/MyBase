import React, { useContext, useState } from 'react';

import { useClientContext, WorkflowPortRender } from '@flowgram.ai/free-layout-editor';

import { SidebarContext } from '../../context';
import { useNodeRenderContext } from '../../hooks/use-node-render-context';
import { type FlowNodeMeta } from '../../typings';
import { NodeWrapperStyle } from './styles.tsx';
import { scrollToView } from './utils';

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
  const { node, selected, startDrag, ports, selectNode, nodeRef, onFocus, onBlur, readonly } = nodeRender;
  const [isDragging, setIsDragging] = useState(false);
  const sidebar = useContext(SidebarContext);
  const form = nodeRender.form;
  const ctx = useClientContext();
  const meta = node.getNodeMeta<FlowNodeMeta>();

  const portsRender = ports.map((p) => <WorkflowPortRender key={p.id} entity={p} />);

  return (
    <>
      <NodeWrapperStyle
        className={selected ? 'selected' : ''}
        ref={nodeRef}
        draggable
        onDragStart={(e: React.DragEvent<HTMLDivElement>) => {
          startDrag(e);
          setIsDragging(true);
        }}
        onTouchStart={(e: React.TouchEvent<HTMLDivElement>) => {
          startDrag(e as unknown as React.MouseEvent);
          setIsDragging(true);
        }}
        onClick={(e: React.MouseEvent<HTMLDivElement>) => {
          selectNode(e);
          if (!isDragging) {
            sidebar.setNodeId(nodeRender.node.id);
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
          outline: form?.state.invalid ? '1px solid red' : 'none'
        }}
      >
        {children}
      </NodeWrapperStyle>
      {portsRender}
    </>
  );
};
