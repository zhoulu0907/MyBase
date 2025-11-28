import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Button, Dropdown, Menu } from '@arco-design/web-react';
import { IconCaretDown, IconCaretLeft, IconClose, IconMore } from '@arco-design/web-react/icon';
import { useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { NodeType } from '@onebase/common';
import { useCallback, useContext, useMemo, useState } from 'react';
import { NodeRenderContext } from '../../context';
import { useIsSidebar } from '../../hooks';
import { NodeTypeName } from '../../nodes/const';
import { clearDataOriginNodeId } from '../../nodes/utils';
import { FlowCommandId } from '../../shortcuts/constants';
import { type FlowNodeRegistry } from '../../typings';
import styles from './index.module.less';
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
      console.log('delete node: ', node.id);

      // 删除相关应用的节点配置
      clearDataOriginNodeId(node.id);

      triggerEditorSignal.setNodeId(undefined);
      triggerEditorSignal.deleteNodeData(node.id);

      triggerNodeOutputSignal.removeTriggerNodeOutput(node.id);

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
      {/* <Menu.Item
        key="editTitle"
        onClick={(e: Event) => {
          e.stopPropagation();

          handleEditTitle();
        }}
      >
        修改节点名称
      </Menu.Item> */}
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
  const [titleEdit, setTitleEdit] = useState<boolean>(false);

  const { setNodeId } = triggerEditorSignal;
  const isSidebar = useIsSidebar();
  const handleExpand = (e: Event) => {
    toggleExpand();
    e.stopPropagation(); // Disable clicking prevents the sidebar from opening
  };
  const handleClose = () => {
    setNodeId(undefined);
  };

  const getNodeTypeNameTag = () => {
    if (!node.flowNodeType || isSidebar) {
      return null;
    }
    const nodeTypeName = NodeTypeName[node.flowNodeType as keyof typeof NodeTypeName];
    if (node.flowNodeType === NodeType.START_FORM) {
      return <div className={styles.orangeTag}>{nodeTypeName}</div>;
    }
    return <div className={styles.tag}>{nodeTypeName}</div>;
  };

  return (
    <div className={styles.nodeHeader}>
      <div
        className={styles.content}
        onMouseDown={(e) => {
          // trigger drag node
          startDrag(e);
          e.stopPropagation();
        }}
      >
        {getIcon(node)}
        <TitleInput isSidebar={isSidebar} readonly={readonly} titleEdit={titleEdit} updateTitleEdit={setTitleEdit} />
        {getNodeTypeNameTag()}
        {node.renderData.expandable && !isSidebar && (
          <Button
            type="secondary"
            icon={expanded ? <IconCaretDown /> : <IconCaretLeft />}
            size="small"
            onClick={handleExpand}
          />
        )}
        {readonly ? undefined : (
          <div className={styles.operation}>
            <Dropdown trigger="hover" position="br" droplist={<DropdownContent updateTitleEdit={setTitleEdit} />}>
              <Button size="mini" type="secondary" icon={<IconMore />} onClick={(e: Event) => e.stopPropagation()} />
            </Dropdown>
          </div>
        )}
        {/* 如果是在sidebar中，则显示关闭按钮 */}
        {isSidebar && <Button type="text" icon={<IconClose />} size="small" onClick={handleClose} />}
      </div>
      {/* 如果不是在sidebar中，则显示节点id */}
      {!isSidebar && (
        <div className={styles.footer}>
          <span>ID:</span>
          <span style={{ paddingLeft: '12px' }}>{node.id}</span>
        </div>
      )}
    </div>
  );
}
