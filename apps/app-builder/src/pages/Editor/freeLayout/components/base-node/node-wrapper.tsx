/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import React, { useState, useContext, useRef, useCallback, useEffect } from 'react';
import { WorkflowPortRender } from '@flowgram.ai/free-layout-editor';
import { Tooltip } from '@arco-design/web-react';
import { IconExclamationCircleFill } from '@arco-design/web-react/icon';
import { useClientContext, CommandService } from '@flowgram.ai/free-layout-editor';
import { type FlowNodeMeta } from '../../typings';
import { useNodeRenderContext, usePortClick } from '../../hooks';
import { SidebarContext } from '../../context';
import { scrollToView } from './utils';
import { NodeWrapperStyle } from './styles';
import iconCopy from '../../assets/copy.svg';
import iconDelete from '../../assets/delete.svg';
import { WorkflowNodeType } from '../../nodes/constants';
import { CopyShortcut } from '../../shortcuts/copy/index';
import { PasteShortcut } from '../../shortcuts/paste/index';
import './node-wrapper.less';

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
  const [isHover, setIsHover] = useState(false);
  const sidebar = useContext(SidebarContext);
  const form = nodeRender.form;
  const ctx = useClientContext();
  const onPortClick = usePortClick();
  const meta = node.getNodeMeta<FlowNodeMeta>();
  const childRef = useRef<HTMLDivElement>(null);
  const portsRender = ports.map((p) => (
    <WorkflowPortRender
      className={'nodePort' + (selected ? ' selectedPort' : '') + (readonly ? ' readonlyPort' : '')}
      key={p.id}
      entity={p}
      onClick={!readonly ? onPortClick : undefined}
    />
  ));
  const onMouseOver = useCallback(() => {
    if (readonly) return;
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
  const deleteNode = useCallback(() => {
    ctx.get<CommandService>(CommandService).executeCommand('DELETE', [node]);
  }, [node]);
  const copyNode = useCallback(
    (e: React.MouseEvent) => {
      const copyShortcut = new CopyShortcut(ctx);
      const pasteShortcut = new PasteShortcut(ctx);
      const data = copyShortcut.toClipboardData([node]);
      pasteShortcut.apply(data);
      e.stopPropagation();
    },
    [ctx, node]
  );
  const setErrmsg = useCallback(() => {
    let errorMsg = '';
    if (nodeRender.ports) {
      let flag = false;
      nodeRender.ports.forEach((port) => {
        if (port.availableLines.length) {
          flag = true;
        }
      });
      if (!flag) {
        errorMsg = '该节点为独立节点';
      }
    }
    if (!nodeRender.data.errorMsg || nodeRender.data.errorMsg == '该节点为独立节点') {
      nodeRender.updateData(Object.assign({}, nodeRender.data, { errorMsg }));
    }
  }, [node]);
  useEffect(() => {
    setErrmsg();
  });
  return (
    <>
      {(isHover || selected) &&
        !readonly &&
        nodeRender.type !== WorkflowNodeType.START &&
        nodeRender.type !== WorkflowNodeType.END &&
        nodeRender.type !== WorkflowNodeType.INITIATION && (
          <div className="nodeShowMore" ref={childRef} onMouseOut={onMouseOut}>
            <div className="iconBox" onClick={copyNode}>
              <img src={iconCopy} />
            </div>
            <div className="iconBox" onClick={deleteNode}>
              <img src={iconDelete} />
            </div>
          </div>
        )}
      {nodeRender.data.errorMsg && (
        <div className={'nodeErrorIcon'}>
          <Tooltip content={nodeRender.data.errorMsg}>
            <IconExclamationCircleFill />
          </Tooltip>
        </div>
      )}
      <NodeWrapperStyle
        className={`${selected && !readonly ? 'selected' : ''} 
        ${readonly && nodeRender.data.runStatus + 'Border'} 
        ${nodeRender.id.includes('branch') && 'branchNode'}
        ${nodeRender.data.errorMsg && 'errorMsgNode'}`}
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
          outline: form?.values?.errMsg?.length > 0 ? '1px solid red' : 'none'
        }}
      >
        {children}
      </NodeWrapperStyle>
      {portsRender}
    </>
  );
};
