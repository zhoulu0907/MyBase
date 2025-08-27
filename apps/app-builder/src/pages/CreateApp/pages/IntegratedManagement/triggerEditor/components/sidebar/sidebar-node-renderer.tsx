import { FlowNodeEntity, useNodeRender } from '@flowgram.ai/fixed-layout-editor';
import { NodeRenderContext } from '../../context';
import styles from './index.module.less';

export function SidebarNodeRenderer(props: { node: FlowNodeEntity }) {
  const { node } = props;
  const nodeRender = useNodeRender(node);

  return (
    <NodeRenderContext.Provider value={nodeRender}>
      <div className={styles.sidebar}>
        <div className={styles.body}>{nodeRender.form?.render()}</div>
      </div>
    </NodeRenderContext.Provider>
  );
}
