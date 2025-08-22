import { type FreeLayoutProps } from '@flowgram.ai/free-layout-editor';
import { createFreeSnapPlugin } from '@flowgram.ai/free-snap-plugin';
import { createMinimapPlugin } from '@flowgram.ai/minimap-plugin';
import { useMemo } from 'react';

import { BaseNode } from '../components/base-node'; // 节点渲染
import defaultFormMeta from '../node/defaultFormMeta';
import type { FlowDocumentJSON, FlowNodeRegistry } from '../typings';

export function useEditorProps(initialData: FlowDocumentJSON, nodeRegistries: FlowNodeRegistry[]): FreeLayoutProps {
  return useMemo<FreeLayoutProps>(
    () => ({
      /**
       * Whether to enable the background
       */
      background: true,
      /**
       * 画布相关配置
       * Canvas-related configurations
       */
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
       * 初始化数据
       */
      initialData,
      /**
       * 画布节点定义
       */
      nodeRegistries,
      /**
       * Get the default node registry, which will be merged with the 'nodeRegistries'
       * 提供默认的节点注册，这个会和 nodeRegistries 做合并
       */
      getNodeDefaultRegistry(type) {
        return {
          type,
          meta: {
            defaultExpanded: true
          },
          // TODO(mickey): defaultFormMeta
          formMeta: {
            render: defaultFormMeta
          }
        };
      },
      /**
       * 物料
       */
      materials: {
        components: {},
        /**
         * Render Node
         */
        renderDefaultNode: BaseNode,
        renderNodes: {}
      },
      /**
       * 节点引擎, 用于渲染节点表单
       */
      nodeEngine: {
        enable: true
      },
      /**
       * 画布历史记录, 用于控制 redo/undo
       */
      history: {
        enable: true,
        enableChangeNode: true // 用于监听节点表单数据变化
      },
      /**
       * 画布初始化回调
       */
      onInit: (ctx) => {
        // 如果要动态加载数据，可以通过如下方法异步执行
        // ctx.docuemnt.fromJSON(initialData)
      },
      /**
       * 画布第一次渲染完整回调
       */
      onAllLayersRendered: (ctx) => {
        ctx.document.fitView(false);
      },
      /**
       * 画布销毁回调
       */
      onDispose: () => {},
      plugins: () => [
        /**
         * 缩略图插件
         */
        createMinimapPlugin({}),
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
}
