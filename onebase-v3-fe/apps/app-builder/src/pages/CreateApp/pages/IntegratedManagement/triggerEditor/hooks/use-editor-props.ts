import { useMemo } from 'react';

import {
  ConstantKeys,
  FixedLayoutProps,
  FlowLayoutDefault,
  FlowRendererKey,
  ShortcutsRegistry
} from '@flowgram.ai/fixed-layout-editor';
import { defaultFixedSemiMaterials } from '@flowgram.ai/fixed-semi-materials';
import { createMinimapPlugin } from '@flowgram.ai/minimap-plugin';
import { debounce } from 'lodash-es';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { DragNode } from '../components';
import { BaseNode } from '../components/base-node';
import { CollapseNode } from '../components/collapse-node';
import BranchAdder from '../components/branch-adder';
import NodeAdder from '../components/node-adder';
import { CustomService } from '../services';
import { shortcutGetter } from '../shortcuts';
import { type FlowNodeRegistry } from '../typings';

export function useEditorProps(
  //   initialData: FlowDocumentJSON,
  nodeRegistries: FlowNodeRegistry[]
): FixedLayoutProps {
  return useMemo<FixedLayoutProps>(
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
       * Initial data
       * 初始化数据
       */
      //   initialData,
      /**
       * Node registries
       * 节点注册
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
            /**
             * Default expanded
             * 默认展开所有节点
             */
            defaultExpanded: true
          }
        };
      },
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
       * Set default layout
       */
      defaultLayout: FlowLayoutDefault.VERTICAL_FIXED_LAYOUT, // or FlowLayoutDefault.HORIZONTAL_FIXED_LAYOUT
      /**
       * Style config
       */
      constants: {
        [ConstantKeys.NODE_SPACING]: 50,
        [ConstantKeys.BRANCH_SPACING]: 40,
        // [ConstantKeys.INLINE_SPACING_BOTTOM]: 24,
        // [ConstantKeys.INLINE_BLOCKS_INLINE_SPACING_BOTTOM]: 13,
        // [ConstantKeys.ROUNDED_LINE_X_RADIUS]: 8,
        // [ConstantKeys.ROUNDED_LINE_Y_RADIUS]: 10,
        // [ConstantKeys.INLINE_BLOCKS_INLINE_SPACING_TOP]: 23,
        // [ConstantKeys.INLINE_BLOCKS_PADDING_BOTTOM]: 30,
        // [ConstantKeys.COLLAPSED_SPACING]: 10,
        [ConstantKeys.BASE_COLOR]: '#B8BCC1',
        [ConstantKeys.BASE_ACTIVATED_COLOR]: 'rgb(var(--primary-3))'
      },
      /**
       * SelectBox config
       */
      //   selectBox: {
      //     SelectorBoxPopover,
      //   },

      // Config shortcuts
      shortcuts: (registry: ShortcutsRegistry, ctx) => {
        registry.addHandlers(...shortcutGetter.map((getter) => getter(ctx)));
      },
      /**
       * Drag/Drop config
       */
      dragdrop: {
        /**
         * Callback when drag drop
         */
        onDrop: (ctx, dropData) => {},
        canDrop: (ctx, dropData) => true
      },
      /**
       * Redo/Undo enable
       */
      history: {
        enable: true,
        enableChangeNode: true, // Listen Node engine data change
        onApply: debounce((ctx, opt) => {
          if (ctx.document.disposed) return;
          // Listen change to trigger auto save
          console.log('auto save: ', ctx.document.toJSON());
          triggerEditorSignal.setNodes(ctx.document.toJSON().nodes);
        }, 100)
      },
      /**
       * Node engine enable, you can configure formMeta in the FlowNodeRegistry
       */
      nodeEngine: {
        enable: true
      },
      /**
       * Variable engine enable
       */
      variableEngine: {
        enable: true
      },
      /**
       * Materials, components can be customized based on the key
       * @see https://github.com/bytedance/flowgram.ai/blob/main/packages/materials/fixed-semi-materials/src/components/index.tsx
       * 可以通过 key 自定义 UI 组件
       */
      materials: {
        components: {
          ...defaultFixedSemiMaterials,
          [FlowRendererKey.ADDER]: NodeAdder, // Node Add Button
          [FlowRendererKey.BRANCH_ADDER]: BranchAdder, // Branch Add Button
          [FlowRendererKey.DRAG_NODE]: DragNode, // Component in node dragging
          [FlowRendererKey.COLLAPSE]: CollapseNode // 条件、分支、循环  收起/展示
        },
        renderDefaultNode: BaseNode, // node render
        renderTexts: {
          'loop-end-text': '循环结束',
          'loop-traverse-text': '循环',
          'try-start-text': '尝试开始',
          'try-end-text': '尝试结束',
          'catch-text': '捕获错误'
        }
      },
      /**
       * Bind custom service
       */
      onBind: ({ bind }) => {
        console.log('onBind');
        bind(CustomService).toSelf().inSingletonScope();
      },
      scroll: {
        /**
         * 限制滚动，防止节点都看不到
         * Limit scrolling so that none of the nodes can see it
         */
        enableScrollLimit: true
      },
      /**
       * Playground init
       */
      onInit: (ctx) => {
        /**
         * Data can also be dynamically loaded via fromJSON
         * 也可以通过 fromJSON 动态加载数据
         */
        // ctx.document.fromJSON(initialData)
        console.log('---- Playground Init ----');
      },
      /**
       * Playground render
       */
      onAllLayersRendered: (ctx) => {
        setTimeout(() => {
          // fitView all nodes
          ctx.tools.fitView();
        }, 10);
      },
      /**
       * Playground dispose
       */
      onDispose: () => {
        console.log('---- Playground Dispose ----');
      },
      plugins: () => [
        /**
         * Minimap plugin
         * 缩略图插件
         */
        createMinimapPlugin({
          disableLayer: true,
          enableDisplayAllNodes: true,
          canvasStyle: {
            canvasWidth: 182,
            canvasHeight: 102,
            canvasPadding: 50,
            canvasBackground: 'rgba(245, 245, 245, 1)',
            canvasBorderRadius: 10,
            viewportBackground: 'rgba(235, 235, 235, 1)',
            viewportBorderRadius: 4,
            viewportBorderColor: 'rgba(201, 201, 201, 1)',
            viewportBorderWidth: 1,
            viewportBorderDashLength: 2,
            nodeColor: 'rgba(255, 255, 255, 1)',
            nodeBorderRadius: 2,
            nodeBorderWidth: 0.145,
            nodeBorderColor: 'rgba(6, 7, 9, 0.10)',
            overlayColor: 'rgba(255, 255, 255, 0)'
          },
          inactiveDebounceTime: 1
        })
        // /**
        //  * Group plugin
        //  * 分组插件
        //  */
        // createGroupPlugin({
        //   components: {
        //     GroupBoxHeader,
        //     GroupNode,
        //   },
        // }),
        // /**
        //  * Clipboard plugin
        //  * 剪切板插件
        //  */
        // createClipboardPlugin(),

        // /**
        //  * Variable panel plugin
        //  * 变量面板插件
        //  */
        // createVariablePanelPlugin({}),
      ]
    }),
    []
  );
}
