import { useMemo } from 'react';

import { FreeLayoutProps } from '@flowgram.ai/free-layout-editor';
import { createFreeSnapPlugin } from '@flowgram.ai/free-snap-plugin';
import { createMinimapPlugin } from '@flowgram.ai/minimap-plugin';

import { etlEditorSignal } from '@onebase/common';
import { BaseNode } from '../components/base-node';
import type { FlowNodeRegistry } from '../typings';

export const useEditorProps = (nodeRegistries: FlowNodeRegistry[]) =>
  useMemo<FreeLayoutProps>(
    () => ({
      /**
       * Whether to enable the background
       */
      background: true,
      playground: {
        /**
         * Prevent Mac browser gestures from turning pages
         * 阻止 mac 浏览器手势翻页
         */
        preventGlobalGesture: true
      },
      /**
       * Whether it is read-only or not, the node cannot be dragged in read-only mode
       */
      readonly: false,
      /**
       * Initial data
       * 初始化数据
       */
      //   initialData: {
      //     nodes: [],
      //     edges: []
      //   },
      /**
       * Node registries
       * 节点注册
       */
      nodeRegistries,
      /**
       * 节点数据转换, 由 ctx.document.fromJSON 调用
       * Node data transformation, called by ctx.document.fromJSON
       * @param node
       * @param json
       */
      fromNodeJSON(node, json) {
        return json;
      },
      /**
       * 节点数据转换, 由 ctx.document.toJSON 调用
       * Node data transformation, called by ctx.document.toJSON
       * @param node
       * @param json
       */
      toNodeJSON(node, json) {
        return json;
      },
      /**
       * Get the default node registry, which will be merged with the 'nodeRegistries'
       * 提供默认的节点注册，这个会和 nodeRegistries 做合并
       */
      getNodeDefaultRegistry(type) {
        return {
          type,
          meta: {
            defaultExpanded: true
          }
        };
      },
      lineColor: {
        hidden: 'var(--g-workflow-line-color-hidden,transparent)',
        default: 'var(--g-workflow-line-color-default,#4d53e8)',
        drawing: 'var(--g-workflow-line-color-drawing, #009E9E)',
        hovered: 'var(--g-workflow-line-color-hover,#009E9E)',
        selected: 'var(--g-workflow-line-color-selected,#009E9E)',
        error: 'var(--g-workflow-line-color-error,red)',
        flowing: 'var(--g-workflow-line-color-flowing,#4d53e8)'
      },
      materials: {
        /**
         * Render Node
         */
        renderDefaultNode: BaseNode
        // renderDefaultNode: (props: WorkflowNodeProps) => {
        //   const { form } = useNodeRender();
        //   return (
        //     <WorkflowNodeRenderer className="demo-free-node" node={props.node}>
        //       {form?.render()}
        //     </WorkflowNodeRenderer>
        //   );
        // }
      },
      /**
       * Content change
       */
      onContentChange(ctx, event) {
        // console.log('Auto Save: ', event, ctx.document.toJSON());
        etlEditorSignal.setGraphData(ctx.document.toJSON());
      },
      // /**
      //  * Node engine enable, you can configure formMeta in the FlowNodeRegistry
      //  */
      nodeEngine: {
        enable: true
      },
      /**
       * Redo/Undo enable
       */
      history: {
        enable: true,
        enableChangeNode: true // Listen Node engine data change
      },
      /**
       * Playground init
       */
      onInit: (ctx) => {},
      /**
       * Playground render
       */
      onAllLayersRendered(ctx) {
        //  Fitview
        ctx.document.fitView(false);
      },
      /**
       * Playground dispose
       */
      onDispose() {
        console.log('---- Playground Dispose ----');
      },
      plugins: () => [
        /**
         * Minimap plugin
         * 缩略图插件
         */
        createMinimapPlugin({
          disableLayer: true,
          canvasStyle: {
            canvasWidth: 182,
            canvasHeight: 102,
            canvasPadding: 50,
            canvasBackground: 'rgba(242, 243, 245, 1)',
            canvasBorderRadius: 10,
            viewportBackground: 'rgba(255, 255, 255, 1)',
            viewportBorderRadius: 4,
            viewportBorderColor: 'rgba(6, 7, 9, 0.10)',
            viewportBorderWidth: 1,
            viewportBorderDashLength: undefined,
            nodeColor: 'rgba(0, 0, 0, 0.10)',
            nodeBorderRadius: 2,
            nodeBorderWidth: 0.145,
            nodeBorderColor: 'rgba(6, 7, 9, 0.10)',
            overlayColor: 'rgba(255, 255, 255, 0.55)'
          },
          inactiveDebounceTime: 1
        }),
        /**
         * Snap plugin
         * 自动对齐及辅助线插件
         */
        createFreeSnapPlugin({
          edgeColor: '#00B2B2',
          alignColor: '#00B2B2',
          edgeLineWidth: 1,
          alignLineWidth: 1,
          alignCrossWidth: 8
        })
      ]
    }),
    []
  );
