/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useCallback, useContext, useMemo, useState } from 'react';

import { IconClose, IconMore, IconSmallTriangleDown, IconSmallTriangleLeft } from '@douyinfe/semi-icons';
import { Button, Dropdown, IconButton } from '@douyinfe/semi-ui';
import { useClientContext } from '@flowgram.ai/fixed-layout-editor';

import { NodeRenderContext, SidebarContext } from '../../context';
import { useIsSidebar } from '../../hooks';
import { FlowCommandId } from '../../shortcuts/constants';
import { type FlowNodeRegistry } from '../../typings';
import { Header, Operators } from './styles';
import { TitleInput } from './title-input';
import { getIcon } from './utils';

function DropdownContent(props: { updateTitleEdit: (editing: boolean) => void }) {
  const { updateTitleEdit } = props;
  const { node, deleteNode } = useContext(NodeRenderContext);
  const clientContext = useClientContext();
  const registry = node.getNodeRegistry<FlowNodeRegistry>();
  const handleCopy = useCallback(
    (e: React.MouseEvent) => {
      clientContext.playground.commandService.executeCommand(FlowCommandId.COPY, node);
      e.stopPropagation(); // Disable clicking prevents the sidebar from opening
    },
    [clientContext, node]
  );

  const handleDelete = useCallback(
    (e: React.MouseEvent) => {
      deleteNode();
      e.stopPropagation(); // Disable clicking prevents the sidebar from opening
    },
    [clientContext, node]
  );

  const handleEditTitle = useCallback(() => {
    updateTitleEdit(true);
  }, [updateTitleEdit]);

  const deleteDisabled = useMemo(() => {
    if (registry.canDelete) {
      return !registry.canDelete(clientContext, node);
    }
    return registry.meta!.deleteDisable;
  }, [registry, node]);

  return (
    <Dropdown.Menu>
      <Dropdown.Item onClick={handleEditTitle}>Edit Title</Dropdown.Item>
      <Dropdown.Item onClick={handleCopy} disabled={registry.meta!.copyDisable === true}>
        Copy
      </Dropdown.Item>
      <Dropdown.Item onClick={handleDelete} disabled={deleteDisabled}>
        Delete
      </Dropdown.Item>
    </Dropdown.Menu>
  );
}

export function FormHeader() {
  const { node, expanded, startDrag, toggleExpand, readonly } = useContext(NodeRenderContext);
  const [titleEdit, updateTitleEdit] = useState<boolean>(false);

  const { setNodeId } = useContext(SidebarContext);
  const isSidebar = useIsSidebar();
  const handleExpand = (e: React.MouseEvent) => {
    toggleExpand();
    e.stopPropagation(); // Disable clicking prevents the sidebar from opening
  };
  const handleClose = () => {
    setNodeId(undefined);
  };

  return (
    <Header
      onMouseDown={(e) => {
        // trigger drag node
        startDrag(e);
        // e.stopPropagation();
      }}
    >
      {getIcon(node)}
      <TitleInput readonly={readonly} titleEdit={titleEdit} updateTitleEdit={updateTitleEdit} />
      {node.renderData.expandable && !isSidebar && (
        <Button
          type="primary"
          icon={expanded ? <IconSmallTriangleDown /> : <IconSmallTriangleLeft />}
          size="small"
          theme="borderless"
          onClick={handleExpand}
        />
      )}
      {readonly ? undefined : (
        <Operators>
          <Dropdown
            trigger="hover"
            position="bottomRight"
            render={<DropdownContent updateTitleEdit={updateTitleEdit} />}
          >
            <IconButton
              color="secondary"
              size="small"
              theme="borderless"
              icon={<IconMore />}
              onClick={(e) => e.stopPropagation()}
            />
          </Dropdown>
        </Operators>
      )}
      {isSidebar && (
        <Button type="primary" icon={<IconClose />} size="small" theme="borderless" onClick={handleClose} />
      )}
    </Header>
  );
}
