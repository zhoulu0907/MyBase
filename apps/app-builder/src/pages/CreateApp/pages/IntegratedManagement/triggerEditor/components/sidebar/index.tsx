import { Drawer } from '@arco-design/web-react';
import {
  PlaygroundEntityContext,
  useClientContext,
  useRefresh,
  type FlowNodeMeta
} from '@flowgram.ai/free-layout-editor';
import { startTransition, useCallback, useContext, useEffect, useMemo } from 'react';
import { IsSidebarContext, SidebarContext } from '../../context';
import { SidebarNodeRenderer } from './sidebar-node-renderer';

export const SidebarRenderer = () => {
  const { nodeId, setNodeId } = useContext(SidebarContext);
  const { selection, playground, document } = useClientContext();
  const refresh = useRefresh();
  const handleClose = useCallback(() => {
    // Sidebar delayed closing
    startTransition(() => {
      setNodeId(undefined);
    });
  }, []);
  const node = nodeId ? document.getNode(nodeId) : undefined;
  /**
   * Listen readonly
   */
  useEffect(() => {
    const disposable = playground.config.onReadonlyOrDisabledChange(() => {
      handleClose();
      refresh();
    });
    return () => disposable.dispose();
  }, [playground]);
  /**
   * Listen selection
   */
  useEffect(() => {
    const toDispose = selection.onSelectionChanged(() => {
      /**
       * 如果没有选中任何节点，则自动关闭侧边栏
       * If no node is selected, the sidebar is automatically closed
       */
      if (selection.selection.length === 0) {
        handleClose();
      } else if (selection.selection.length === 1 && selection.selection[0] !== node) {
        handleClose();
      }
    });
    return () => toDispose.dispose();
  }, [selection, handleClose, node]);
  /**
   * Close when node disposed
   */
  useEffect(() => {
    if (node) {
      const toDispose = node.onDispose(() => {
        setNodeId(undefined);
      });
      return () => toDispose.dispose();
    }
    return () => {};
  }, [node]);

  const visible = useMemo(() => {
    if (!node) {
      return false;
    }
    const { sidebarDisabled = false } = node.getNodeMeta<FlowNodeMeta>();
    return !sidebarDisabled;
  }, [node]);

  if (playground.config.readonly) {
    return null;
  }
  /**
   * Add "key" to rerender the sidebar when the node changes
   */
  const content =
    node && visible ? (
      <PlaygroundEntityContext.Provider key={node.id} value={node}>
        <SidebarNodeRenderer node={node} />
      </PlaygroundEntityContext.Provider>
    ) : null;

  return (
    <Drawer
      mask={false}
      visible={visible}
      onCancel={handleClose}
      closable={false}
      width={400}
      headerStyle={{
        display: 'none'
      }}
      footer={null}
      style={{
        background: 'none',
        boxShadow: 'none'
      }}
    >
      <IsSidebarContext.Provider value={true}>{content}</IsSidebarContext.Provider>
    </Drawer>
  );
};
