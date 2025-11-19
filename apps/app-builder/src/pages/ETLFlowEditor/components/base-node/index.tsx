import { FlowNodeEntity, useNodeRender } from '@flowgram.ai/free-layout-editor';
import { ETLDrawerTab, etlEditorSignal, ETLNodeType } from '@onebase/common';
import { NodeRenderContext } from '../../context';
import styles from './index.module.less';
import { NodeWrapper } from './node-wrapper';

export const BaseNode = ({ node }: { node: FlowNodeEntity }) => {
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

  return (
    <NodeRenderContext.Provider value={nodeRender}>
      <NodeWrapper>
        <div
          className={`${styles.baseNodeStyle}`}
          onDoubleClick={() => {
            etlEditorSignal.setCurNode({
              id: node.id,
              title: form?.values?.title || '',
              flowNodeType: node.flowNodeType
            });

            if (node.flowNodeType === ETLNodeType.INPUT_NODE) {
              etlEditorSignal.resetCurDrawerTab();
            } else {
              etlEditorSignal.setCurDrawerTab(ETLDrawerTab.DATA_CONFIG);
            }
          }}
          style={{
            /**
             * Lets you precisely control the style of branch nodes
             * 用于精确控制分支节点的样式
             */
            ...node.getNodeRegistry().meta.style
          }}
        >
          {form?.render()}
        </div>
      </NodeWrapper>
    </NodeRenderContext.Provider>
  );
};
