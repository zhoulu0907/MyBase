import { useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { useCallback, useContext, useMemo, useState } from 'react';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button, Dropdown, Menu } from '@arco-design/web-react';
import { IconCaretDown, IconCaretLeft, IconClose, IconMore } from '@arco-design/web-react/icon';
import { NodeRenderContext } from '../../context';
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
      triggerEditorSignal.deleteNodeData(node.id);
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
    <Menu>
      <Menu.Item
        key="editTitle"
        onClick={(e: Event) => {
          e.stopPropagation();

          handleEditTitle();
        }}
      >
        编辑节点
      </Menu.Item>
      <Menu.Item key="copy" onClick={handleCopy} disabled={registry.meta!.copyDisable === true}>
        复制
      </Menu.Item>
      <Menu.Item key="delete" onClick={handleDelete} disabled={deleteDisabled}>
        删除
      </Menu.Item>
    </Menu>
  );
}

export function FormHeader() {
  const { node, expanded, startDrag, toggleExpand, readonly } = useContext(NodeRenderContext);
  const [titleEdit, updateTitleEdit] = useState<boolean>(false);

  const { setNodeId } = triggerEditorSignal;
  const isSidebar = useIsSidebar();
  const handleExpand = (e: Event) => {
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
        e.stopPropagation();
      }}
    >
      {getIcon(node)}
      <TitleInput readonly={readonly} titleEdit={titleEdit} updateTitleEdit={updateTitleEdit} />
      {node.renderData.expandable && !isSidebar && (
        <Button
          type="secondary"
          icon={expanded ? <IconCaretDown /> : <IconCaretLeft />}
          size="small"
          onClick={handleExpand}
        />
      )}
      {readonly ? undefined : (
        <Operators>
          <Dropdown trigger="hover" position="br" droplist={<DropdownContent updateTitleEdit={updateTitleEdit} />}>
            <Button size="mini" type="secondary" icon={<IconMore />} onClick={(e: Event) => e.stopPropagation()} />
          </Dropdown>
        </Operators>
      )}
      {/* 如果是在sidebar中，则显示关闭按钮 */}
      {isSidebar && <Button type="text" icon={<IconClose />} size="small" onClick={handleClose} />}
    </Header>
  );
}
