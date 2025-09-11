import { Graph } from '@antv/x6';
import { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { register } from '@antv/x6-react-shape';
import { Button, InputNumber } from '@arco-design/web-react';
import DetailDrawer from '../Drawers/DetailDrawer';
import { type EntityNode, type EntityERProps } from '../../../../utils/interface';
import { FIELD_TYPE } from '@onebase/ui-kit';
import EntityNodeComponent from './ERnode';
import styles from './index.module.less';
import { GridNodePositioner } from './utils/nodePositioner';

const LINE_HEAD_HEIGHT = 48;
const LINE_HEIGHT = 34.8;
const LINE_TITLE_HEIGHT = 44;
const NODE_WIDTH = 280;
const NODE_HEIGHT = 200;

interface ERchartRef {
  getGraphPositon: () => void;
}

const ERchart = forwardRef<ERchartRef, EntityERProps>(
  (
    {
      mode = 'edit',
      data,
      onNodeEdit,
      onNodeAdd,
      onNodeDelete,
      onNodeAddField,
      onNodeAddRelation,
      onNodeAddMasterDetail,
      onFieldClick,
      onEdgeEdit,
      onStatusChange,
      updateEntityPosition,
      onlyUpdateNode
    },
    ref
  ) => {
    const [selectedNode, setSelectedNode] = useState<EntityNode | null>(null);
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [zoom, setZoom] = useState(90);

    const graphRef = useRef<Graph | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const isUnmounting = useRef(false);
    const isGraphInitialized = useRef(false);

    const getGraphPositon = () => {
      const contentArea = graphRef.current?.getContentArea();
      return contentArea
        ? { x: contentArea.x + contentArea.width / 2, y: contentArea.y + contentArea.height / 2 }
        : undefined;
    };

    useImperativeHandle(ref, () => ({
      getGraphPositon
    }));

    const createAndAddNode = useCallback(
      (positioner: any, nodeDatas: EntityNode[]) => {
        nodeDatas.forEach((nodeData) => {
          const portsItems = (nodeData: EntityNode) => {
            const items: object[] = [];
            nodeData?.fields?.forEach((field, index) => {
              const extraTitleHeight =
                field.isSystemField === FIELD_TYPE.SYSTEM ? LINE_TITLE_HEIGHT : LINE_TITLE_HEIGHT * 2;
              const accumulatedHeight = index >= 1 ? index * LINE_HEIGHT : 0;

              const leftItem = {
                id: (field?.fieldId || field.fieldName) + '_target', // 使用字段的唯一 ID 作为 port ID
                group: 'left', // 指定属于 'left' 组
                args: {
                  x: 0,
                  y: LINE_HEAD_HEIGHT + extraTitleHeight + accumulatedHeight + LINE_HEIGHT / 2 // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半
                }
              };
              const rightItem = {
                id: (field?.fieldId || field.fieldName) + '_source', // 使用字段的唯一 ID 作为 port ID
                group: 'right', // 指定属于 'right' 组
                args: {
                  x: NODE_WIDTH,
                  y: LINE_HEAD_HEIGHT + extraTitleHeight + accumulatedHeight + LINE_HEIGHT / 2 // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半
                }, // 传递字段索引，用于布局
                // 可以自定义 attrs 来覆盖 group 的默认 attrs
                attrs: {
                  // circle: {
                  // fill: field.isSystem ? '#f0f0f0' : '#e6f7ff', // 示例：区分系统和自定义字段
                  // height: currentFieldHeight,
                  // width: NODE_WIDTH,
                  // }
                }
              };
              items.push(leftItem);
              items.push(rightItem);
            });
            return items;
          };

          positioner.addNode({
            id: nodeData.entityId,
            x: nodeData?.positionX,
            y: nodeData?.positionY,
            width: NODE_WIDTH,
            height: NODE_HEIGHT,
            shape: 'er-entity-node',
            data: {
              data: nodeData,
              // mode,
              onNodeEdit,
              onNodeAdd,
              onNodeDelete,
              onNodeAddField,
              onNodeAddRelation,
              onNodeAddMasterDetail,
              onFieldClick,
              onStatusChange
            },
            attrs: {
              body: {
                fill: '#fff',
                stroke: '#d9d9d9',
                strokeWidth: 1,
                rx: 4,
                ry: 4
              }
            },
            // --- 动态添加 ports ---
            ports: {
              // 连接桩分组
              groups: {
                left: {
                  attrs: {
                    circle: {
                      // width: NODE_WIDTH,
                      // height: LINE_HEIGHT,
                      // strokeWidth: 1,
                      // stroke: '#5F95FF',
                      // fill: '#EFF4FF', // 可以根据需要调整颜色
                      // magnet: true, // 确保 port 是可以连接的
                      r: 1,
                      magnet: true,
                      stroke: 'transparent',
                      fill: 'transparent',
                      strokeWidth: 0
                    }
                  },
                  position: {
                    name: 'absolute'
                    // args: { x: 0, y: 0 },
                  }
                },
                right: {
                  attrs: {
                    circle: {
                      r: 1,
                      magnet: true,
                      stroke: 'transparent',
                      fill: 'transparent',
                      strokeWidth: 0
                    }
                  },
                  position: {
                    name: 'absolute'
                  }
                }
              },
              // 连接桩定义
              items: portsItems(nodeData)
            }
          });
        });
      },
      [
        onNodeEdit,
        onNodeAdd,
        onNodeDelete,
        onNodeAddField,
        onNodeAddRelation,
        onNodeAddMasterDetail,
        onFieldClick,
        onStatusChange
      ]
    );

    const createAndAddEdge = (graph: Graph, edgeData: any) => {
      const edge = graph.createEdge({
        source: { cell: edgeData.sourceEntityId, port: `${edgeData.sourceFieldId}_source` },
        target: { cell: edgeData.targetEntityId, port: `${edgeData.targetFieldId}_target` },
        attrs: {
          line: {
            stroke: '#39B85F',
            strokeWidth: 2,
            targetMarker: { name: 'block', width: 12, height: 8 }
          }
        },
        connector: { name: 'smooth' },
        data: edgeData,
        labels: edgeData.label
          ? [
              {
                position: 0.5,
                attrs: {
                  label: {
                    text: edgeData.label,
                    fill: '#39B85F',
                    fontSize: 12,
                    textAnchor: 'middle',
                    textVerticalAnchor: 'middle'
                  }
                }
              }
            ]
          : []
      });
      graph.addEdge(edge);
      return edge;
    };

    useEffect(() => {
      // 注册 React 形状节点
      register({
        shape: 'er-entity-node',
        width: 280,
        height: 200,
        component: EntityNodeComponent,
        effect: ['data']
      });

      return () => {
        isUnmounting.current = true; // 标记正在卸载
        // Cleanup registration if needed
        try {
          Graph.unregisterNode('er-entity-node');
        } catch (e) {
          // Ignore if node was not registered
          console.log(e);
        }
      };
    }, []);

    // 初始化图形
    useEffect(() => {
      console.log('Initializing graph...');

      isUnmounting.current = false;

      const initGraph = () => {
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
              // color: 'rgba(173, 219, 179, 0.5)',
            },
            grid: {
              visible: true,
              type: 'mesh',
              size: 20,
              args: {
                color: '#eee',
                thickness: 1
              }
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
                      stroke: '#39B85F',
                      strokeWidth: 2,
                      targetMarker: {
                        name: 'block',
                        width: 12,
                        height: 8
                      }
                    }
                  }
                });
              }
            },
            interacting: {
              nodeMovable: mode === 'edit',
              edgeMovable: mode === 'edit',
              edgeLabelMovable: mode === 'edit'
            },
            panning: true, // 支持拖拽平移
            // 支持鼠标滚轮缩放
            mousewheel: {
              enabled: true,
              modifiers: ['ctrl'] // 修饰键：按 Ctrl 键+鼠标滚轮实现缩放
            },
            // 缩放级别
            scaling: {
              min: 0.6,
              max: 1.5
            }
          });
        } catch (error) {
          console.error('Failed to create graph:', error);
          return;
        }

        // 事件监听
        // graphRef.current.on('node:click', ({ node }) => {
        //   const nodeData = node.getData() as EntityERProps;
        //   if (nodeData) {
        //     setSelectedNode(nodeData.data as unknown as EntityNode);
        //     console.log('node:click', nodeData);
        //     onNodeEdit?.(nodeData.data as unknown as EntityNode);
        //   }
        // });

        graphRef.current.on('node:mouseenter', ({ node }) => {
          if (mode === 'edit') {
            node.setAttrs({
              body: {
                stroke: '#39B85F',
                strokeWidth: 2
              }
            });
          }
        });

        graphRef.current.on('node:mouseleave', ({ node }) => {
          node.setAttrs({
            body: {
              stroke: '#d9d9d9',
              strokeWidth: 1
            }
          });
        });

        graphRef.current.on('node:moved', ({ e, x, y, node }) => {
          e.preventDefault();
          e.stopPropagation();
          updateEntityPosition?.(node.getData().data, x, y);
        });

        graphRef.current.on('edge:click', ({ e, x, y, edge, view }) => {
          console.log('edge:click', e, x, y, edge, view);
          onEdgeEdit?.(edge.data);
        });

        graphRef.current.on('node:click', ({ e, node }) => {
          e.preventDefault();
          e.stopPropagation();
          onNodeEdit?.(node.getData().data);
        });

        // 监听画布平移
        // graphRef.current.on('translate', ({ tx, ty }) => {
        //   console.log('translate', tx, ty);
        //   const center = graphRef.current?.getContentArea()?.center;
        //   console.log('center', center);
        // });
      };

      initGraph();

      graphRef?.current?.zoomTo(zoom / 100);

      return () => {
        if (graphRef?.current && !isUnmounting.current) {
          graphRef.current?.dispose();
          graphRef.current = null;
        }
      };
    }, []);

    useEffect(() => {
      console.log('data changed.');

      if (!graphRef.current || !data) {
        console.log('Graph not ready or no data, skipping update.');
        return;
      }

      // 初始化定位器
      const positioner = new GridNodePositioner(graphRef.current, {
        startX: 0,
        startY: 0,
        columns: 5,
        nodeWidth: NODE_WIDTH,
        nodeHeight: 430,
        horizontalSpacing: 50,
        verticalSpacing: 50
      });

      graphRef?.current?.clearCells();

      // --- 添加节点 ---
      if (data.nodes && data.nodes.length > 0) {
        createAndAddNode(positioner, data.nodes);
      }

      // --- 添加边 ---
      if (data.edges && data.edges.length > 0) {
        data.edges.forEach((edge) => createAndAddEdge(graphRef.current!, edge));
      }

      // 是否首次加载且有节点
      const isFirstLoadWithData = !isGraphInitialized.current && data?.nodes && data.nodes.length > 0;

      // 首次加载画布内容居中显示
      if (isFirstLoadWithData) {
        console.log('First data load, fitting and centering content.');
        requestAnimationFrame(() => {
          // 检查内容区域是否有效
          const contentArea = graphRef.current?.getContentArea();

          if (contentArea && (contentArea.width > 0 || contentArea.height > 0)) {
            graphRef?.current?.centerContent();
            isGraphInitialized.current = true;
          } else {
            console.warn('Content area is empty, cannot center');
          }
        });
      }
    }, [data]);

    // 改变缩放
    const changeZoom = (value: number) => {
      setZoom(value);
      graphRef?.current?.zoomTo(value / 100);
    };

    return (
      <div className={styles['entity-er-container']}>
        <div ref={containerRef} className={styles['graph-container']} />

        {/* 工具栏 */}
        <div className={styles['toolbar']}>
          <InputNumber
            mode="button"
            size="mini"
            suffix="%"
            max={150}
            min={60}
            step={5}
            defaultValue={90}
            className={styles['zoom-input']}
            value={zoom}
            onChange={(value) => changeZoom(value)}
          />
          <Button type="outline" size="mini" onClick={() => changeZoom(90)}>
            重置
          </Button>
        </div>

        {/* 节点详情抽屉 */}
        <DetailDrawer selectedNode={selectedNode as EntityNode} visible={drawerVisible} setVisible={setDrawerVisible} />
      </div>
    );
  }
);

export default ERchart;
