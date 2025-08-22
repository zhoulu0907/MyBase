import { useNodeRender, type WorkflowNodeProps } from '@flowgram.ai/free-layout-editor';
import { NodeRenderContext } from '../../context';
import { NodeWrapper } from './node-wrapper';

export const BaseNode = (props: WorkflowNodeProps) => {
  /**
   * Provides methods related to node rendering
   * 提供节点渲染相关的方法
   */
  const nodeRender = useNodeRender();
  /**
   * It can only be used when nodeEngine is enabled
   * 只有在节点引擎开启时候才能使用表单
   */
  const form = nodeRender.form;

  /**
   * WorkflowNodeRenderer 会添加节点拖拽事件及 端口渲染，如果要深度定制，可以看该组件源代码:
   * https://github.com/bytedance/flowgram.ai/blob/main/packages/client/free-layout-editor/src/components/workflow-node-renderer.tsx
   */

  return (
    // <WorkflowNodeRenderer className={styles.baseNode} node={props.node}>
    //   {form?.render()}
    // </WorkflowNodeRenderer>

    <NodeRenderContext.Provider value={nodeRender}>
      <NodeWrapper>{form?.render()}</NodeWrapper>
      {/* <NodeStatusBar /> */}
    </NodeRenderContext.Provider>
  );
};
