import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Drawer } from '@arco-design/web-react';
import { PlaygroundEntityContext, useClientContext, useRefresh } from '@flowgram.ai/fixed-layout-editor';
import { useSignals } from '@preact/signals-react/runtime';
import { startTransition, useEffect, useMemo } from 'react';
import { IsSidebarContext } from '../../context';
import type { FlowNodeMeta } from '../../typings';
import { SidebarNodeRenderer } from './sidebar-node-renderer';

export interface SidebarRendererProps {
  refWrapper: React.RefObject<HTMLDivElement>;
}

export const SidebarRenderer = (props: SidebarRendererProps) => {
  const { refWrapper } = props;
  useSignals();

  const { nodeId, setNodeId } = triggerEditorSignal;
  const { selection, playground, document } = useClientContext();
  const refresh = useRefresh();

  const handleClose = () => {
    // Sidebar delayed closing
    startTransition(() => {
      setNodeId(undefined);
    });
  };

  const node = nodeId.value ? document.getNode(nodeId.value) : undefined;

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
    console.log('Node disposed: ', node?.id);
    if (node?.id) {
      const toDispose = node.onDispose(() => {
        setNodeId(undefined);
      });
      return () => toDispose.dispose();
    }
  }, [node]);

  const visible = useMemo(() => {
    if (!node) {
      return false;
    }
    const { sidebarDisable = false } = node.getNodeMeta<FlowNodeMeta>();
    return !sidebarDisable;
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
      unmountOnExit={true}
      width={800}
      headerStyle={{
        display: 'none'
      }}
      bodyStyle={{
        padding: 0
      }}
      getPopupContainer={() => refWrapper && refWrapper?.current!}
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
