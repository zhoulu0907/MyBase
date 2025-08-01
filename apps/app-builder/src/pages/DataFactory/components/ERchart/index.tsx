import React, { useState, useEffect, useRef } from 'react';
import { Graph } from '@antv/x6';
import { register } from '@antv/x6-react-shape';
import DetailDrawer from '../Drawer/DetailDrawer';
import { type EntityNode, type EntityERProps } from '../../utils/interface';
import styles from './ERchart.module.less';
import EntityNodeComponent from './ERnode';

const LINE_HEIGHT = 34;
const NODE_WIDTH = 280;
const NODE_HEIGHT = 200;

const ERchart: React.FC<EntityERProps> = ({
  mode = 'edit',
  data,
  onNodeEdit,
  onNodeAdd,
  onNodeDelete
}) => {
  const [selectedNode, setSelectedNode] = useState<EntityNode | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const graphRef = useRef<Graph | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // 注册自定义节点
  useEffect(() => {

  }, []);

        // 注册自定义节点
  useEffect(() => {
    Graph.registerPortLayout(
      'erPortPosition',
      (portsPositionArgs) => {
        return portsPositionArgs.map((_, index) => {
          return {
            position: {
              x: 0,
              y: (index + 1) * LINE_HEIGHT,
            },
            angle: 0,
          }
        })
      },
      true,
    )

    // 注册 React 形状节点
    register({
      shape: 'er-entity-node',
      width: 280,
      height: 200,
      component: EntityNodeComponent,
      effect: ['data'],
    });

    return () => {
      // Cleanup registration if needed
      try {
        Graph.unregisterNode('er-entity-node');
      } catch (e) {
        // Ignore if node was not registered
        console.log(e)
      }
    };
  }, [mode]); // 添加 mode 到依赖项

  // 初始化图形
  useEffect(() => {
    if (!containerRef.current) {
      console.warn('Container ref is not available');
      return;
    }

    try {
      graphRef.current = new Graph({
        container: containerRef.current,
        // width: 800, // 默认父容器宽高
        // height: 600,
        background: {
          color: 'rgba(173, 219, 179, 0.5)',
        },
        grid: {
          visible: true,
          type: 'dot',
          size: 10,
          args: {
            color: '#E2E2E2',
            thickness: 1,
          },
        },
        connecting: {
          anchor: 'right',
          connector: 'rounded', // 或 'smooth', 'jumpover', 或自定义
          connectionPoint: 'anchor',
          allowBlank: false,
          highlight: true,
          snap: true,
          createEdge() {
            return graphRef.current!.createEdge({
              shape: 'edge',
              attrs: {
                line: {
                  stroke: '#5F95FF',
                  strokeWidth: 2,
                  targetMarker: {
                    name: 'block',
                    width: 12,
                    height: 8,
                  },
                },
              },
              labels: [
                {
                  position: 0.5,
                  attrs: {
                    label: {
                      text: '关系',
                      fill: '#5F95FF',
                      fontSize: 12,
                      textAnchor: 'middle',
                      textVerticalAnchor: 'middle',
                    },
                  },
                },
              ],
            });
          },
        },
        interacting: {
          nodeMovable: mode === 'edit',
          edgeMovable: mode === 'edit',
          edgeLabelMovable: mode === 'edit',
        },
        panning: true, // 支持拖拽平移
        mousewheel: true, // 支持鼠标滚轮缩放
        // zooming: true, // 支持缩放
        // zoomingOptions: {
        //   min: 0.5,
        //   max: 2,
        //   step: 0.1,
        // },
      });
    } catch (error) {
      console.error('Failed to create graph:', error);
      return;
    }

    // 添加节点
    data.nodes.forEach(nodeData => {
      const node = graphRef.current!.createNode({
        id: nodeData.id,
        x: nodeData.x,
        y: nodeData.y,
        width:  NODE_WIDTH,
        height:  NODE_HEIGHT,
        shape: 'er-entity-node',
        data: {
          data: nodeData,
          // mode,
          onNodeEdit,
          onNodeAdd,
          onNodeDelete,
        },
        attrs: {
          body: {
            fill: '#fff',
            stroke: '#d9d9d9',
            strokeWidth: 1,
            rx: 4,
            ry: 4,
          },
        },
        // --- 动态添加 ports ---
        // ports: {
        //   groups: {
        //     // 保留您已定义的 groups (top, bottom, left, right, list)
        //     // ... (保留 top, bottom, left, right groups 定义) ...
        //     list: {
        //       markup: [
        //         {
        //           tagName: 'rect',
        //           selector: 'portBody',
        //         },
        //         {
        //           tagName: 'text',
        //           selector: 'portNameLabel',
        //         },
        //         {
        //           tagName: 'text',
        //           selector: 'portTypeLabel',
        //         },
        //       ],
        //       attrs: {
        //         portBody: {
        //           width: NODE_WIDTH,
        //           height: LINE_HEIGHT,
        //           strokeWidth: 1,
        //           stroke: '#5F95FF',
        //           fill: '#EFF4FF', // 可以根据需要调整颜色
        //           magnet: true, // 确保 port 是可以连接的
        //         },
        //         portNameLabel: {
        //           ref: 'portBody',
        //           refX: 6,
        //           refY: 6,
        //           fontSize: 10,
        //         },
        //         portTypeLabel: {
        //           ref: 'portBody',
        //           refX: 95, // 根据需要调整
        //           refY: 6,
        //           fontSize: 10,
        //         },
        //       },
        //       position: 'erPortPosition', // 使用您自定义的布局
        //     },
        //   },
        //   // --- 动态生成 port items ---
        //   items: nodeData.fields.map((field, index) => ({
        //     id: field.id, // 使用字段的唯一 ID 作为 port ID
        //     group: 'list', // 指定属于 'list' 组
        //     // args: { index }, // 传递字段索引，用于布局
        //     // 可以通过 args 传递额外信息给布局函数或样式
        //     // args: { index },
        //     // 可以自定义 attrs 来覆盖 group 的默认 attrs
        //     // attrs: {
        //     //   portBody: {
        //     //     fill: field.isSystem ? '#f0f0f0' : '#e6f7ff', // 示例：区分系统和自定义字段
        //     //   }
        //     // }
        //   })),
        // },
      });
      graphRef.current?.addNode(node);
    });

    // 添加边
    data.edges.forEach(edgeData => {
      // 确保 source 和 target 是对象，并包含 port
      const source = typeof edgeData.source === 'string' ? { cell: edgeData.source } : edgeData.source;
      const target = typeof edgeData.target === 'string' ? { cell: edgeData.target } : edgeData.target;
    
      const edge = graphRef.current!.createEdge({
        source,
        target,
        attrs: {
          line: {
            stroke: '#5F95FF',
            strokeWidth: 2,
            targetMarker: {
              name: 'block',
              width: 12,
              height: 8,
            },
          },
        },
        labels: edgeData.label ? [
          {
            position: 0.5,
            attrs: {
              label: {
                text: edgeData.label,
                fill: '#5F95FF',
                fontSize: 12,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle',
              },
            },
          },
        ] : [],
        // 可以添加其他边的属性，如 router, connector 等来优化连线路径
        // router: 'manhattan', // 例如使用直角路由
        // connector: { name: 'rounded', args: { radius: 8 }}, // 例如使用圆角连接
      });
      graphRef.current?.addEdge(edge);
    });

    // 事件监听
    graphRef.current.on('node:click', ({ node }) => {
      const nodeData = node.getData() as EntityNode;
      if (nodeData) {
        setSelectedNode(nodeData);
        // setDrawerVisible(true);
      }
    });

    graphRef.current.on('node:mouseenter', ({ node }) => {
      if (mode === 'edit') {
        node.setAttrs({
          body: {
            stroke: '#5F95FF',
            strokeWidth: 2,
          },
        });
      }
    });

    graphRef.current.on('node:mouseleave', ({ node }) => {
      node.setAttrs({
        body: {
          stroke: '#d9d9d9',
          strokeWidth: 1,
        },
      });
    });

    return () => {
      if (graphRef.current) {
        graphRef.current.dispose();
        graphRef.current = null;
      }
    };
  }, [data, mode]);

  return (
    <div className={styles['entity-er-container']}>
     
      <div ref={containerRef} className={styles['graph-container']} />

      {/* 节点详情抽屉 */}
      <DetailDrawer selectedNode={selectedNode as EntityNode} visible={drawerVisible} setVisible={setDrawerVisible} />
    </div>
  );
};

export default ERchart;
