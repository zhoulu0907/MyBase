import { Graph } from '@antv/x6';
import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
// import { ReactShape } from '@antv/x6-react-shape';
import { register } from '@antv/x6-react-shape';
import { Button, InputNumber } from '@arco-design/web-react';
import DetailDrawer from '../Drawers/DetailDrawer';
// import EditDrawer from '../Drawer/EditDrawer';
import { type EntityNode, type EntityERProps } from '../../../../utils/interface';
import { FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import EntityNodeComponent from './ERnode';
import styles from './index.module.less';

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
      updateEntityPosition,
      onlyUpdateNode
    },
    ref
  ) => {
    const [selectedNode, setSelectedNode] = useState<EntityNode | null>(null);
    const [drawerVisible, setDrawerVisible] = useState(false);
    // const [editDrawerVisible, seteditDrawerVisible] = useState(false);
    // const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
    const graphRef = useRef<Graph | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const isGraphInitialized = useRef(false); // 标记是否已初始化
    const [zoom, setZoom] = useState(100);

    const getGraphPositon = () => {
      const node = graphRef.current?.getContentArea()?.center;
      return node;
    };

    useImperativeHandle(ref, () => ({
      getGraphPositon
    }));

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
        // Cleanup registration if needed
        try {
          Graph.unregisterNode('er-entity-node');
        } catch (e) {
          // Ignore if node was not registered
          console.log(e);
        }
      };
    }, []); // 添加 mode 到依赖项

    // 初始化图形
    useEffect(() => {
      console.log('Initializing graph...');

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

        graphRef.current.on('node:moved', ({ e, x, y, node, view }) => {
          console.log('node:moved', e, x, y, node, view);
          updateEntityPosition?.(node.getData().data, x, y);
        });

        graphRef.current.on('edge:click', ({ e, x, y, edge, view }) => {
          console.log('edge:click', e, x, y, edge, view);
          onEdgeEdit?.(edge.data);
        });

        // 监听画布平移
        // graphRef.current.on('translate', ({ tx, ty }) => {
        //   console.log('translate', tx, ty);
        //   const center = graphRef.current?.getContentArea()?.center;
        //   console.log('center', center);
        // });

        isGraphInitialized.current = true; // 标记已初始化
      };

      initGraph();

      return () => {
        if (graphRef?.current) {
          graphRef.current?.dispose();
          graphRef.current = null;
          isGraphInitialized.current = false;
        }
      };
    }, []);

    useEffect(() => {
      console.log('data changed.');

      // 只在首次渲染时居中，后续更新保持用户操作的位置
      if (isGraphInitialized.current && data?.nodes?.length) {
        console.log('First data load, centering content.');
        graphRef?.current?.zoomToFit({ maxScale: 1 });
        // graphRef?.current?.centerContent();
      }

      graphRef?.current?.clearCells();

      // 添加节点
      if (data?.nodes) {
        data?.nodes?.forEach((nodeData) => {
          const portsItems = (nodeData: EntityNode) => {
            const items: object[] = [];
            nodeData?.fields?.forEach((field, index) => {
              const extraTitleHeight = field.isSystemField === FIELD_TYPE.SYSTEM ? LINE_TITLE_HEIGHT : LINE_TITLE_HEIGHT * 2;
              const accumulatedHeight = index >= 1 ? index * LINE_HEIGHT : 0;

              const leftItem = {
                id: field.fieldId + '_target', // 使用字段的唯一 ID 作为 port ID
                group: 'left', // 指定属于 'left' 组
                args: {
                  x: 0,
                  y: LINE_HEAD_HEIGHT + extraTitleHeight + accumulatedHeight + LINE_HEIGHT / 2 // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半
                }
              };
              const rightItem = {
                id: field.fieldId + '_source', // 使用字段的唯一 ID 作为 port ID
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
          const node = graphRef.current!.createNode({
            id: nodeData.entityId,
            x: nodeData.positionX,
            y: nodeData.positionY,
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
              onFieldClick
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
                  // position: 'erPortPosition', // 使用您自定义的布局
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
              // items: nodeData.fields.map((field, index) => {
              //   const currentFieldHeight = LINE_HEIGHT;
              //   const extraTitleHeight = field.isSystem ? LINE_TITLE_HEIGHT : LINE_TITLE_HEIGHT * 2;
              //   const accumulatedHeight = index >= 1 ? index * LINE_HEIGHT: 0;

              //   console.log('accumulatedHeight', index, field.id, accumulatedHeight, LINE_HEAD_HEIGHT + accumulatedHeight + extraTitleHeight + currentFieldHeight / 2)
              //   return {
              //     id: field.id, // 使用字段的唯一 ID 作为 port ID
              //     group: 'left', // 指定属于 'list' 组
              //     args: {
              //       x: 0,
              //       y: LINE_HEAD_HEIGHT + extraTitleHeight + accumulatedHeight , // y = 头部高度 + 累积高度 + 额外标题行高度
              //      }, // 传递字段索引，用于布局
              //     // 可以自定义 attrs 来覆盖 group 的默认 attrs
              //     attrs: {
              //       // circle: {
              //         // fill: field.isSystem ? '#f0f0f0' : '#e6f7ff', // 示例：区分系统和自定义字段
              //         // height: currentFieldHeight,
              //         // width: NODE_WIDTH,
              //       // }
              //     }
              //   };
              // }),
            }
          });
          graphRef.current?.addNode(node);
        });
      }

      // 添加边
      if (data?.edges) {
        data?.edges?.forEach((edgeData) => {
          // 确保 source 和 target 是对象，并包含 port
          const source = edgeData.source;
          const target = edgeData.target;

          const edge = graphRef.current!.createEdge({
            source: { cell: source?.cell, port: source?.port + '_source' },
            target: { cell: target?.cell, port: target?.port + '_target' },
            attrs: {
              line: {
                // 连线样式
                stroke: '#39B85F',
                strokeWidth: 2,
                targetMarker: {
                  name: 'block',
                  width: 12,
                  height: 8
                }
              }
            },
            connector: { name: 'smooth' }, // 曲线
            // vertices: [ // 路径
            //   { x: 200, y: 200 },
            //   { x: 380, y: 120 },
            // ],
            data: edgeData,
            labels:
              edgeData.label === '主子关系'
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
            // 可以添加其他边的属性，如 router, connector 等来优化连线路径
            // router: 'manhattan', // 例如使用直角路由
            // connector: { name: 'rounded', args: { radius: 8 }}, // 例如使用圆角连接
          });
          graphRef.current?.addEdge(edge);
        });
      }

      if (!isGraphInitialized.current && data?.nodes?.length === 1 && !onlyUpdateNode) {
        console.log('onlyUpdateNode.');
        // graphRef?.current?.centerPoint(data?.nodes?.[0]?.x || 0, data?.nodes?.[0]?.y - 200 || 0);
        graphRef?.current?.centerPoint(data?.nodes?.[0]?.positionX || 0, data?.nodes?.[0]?.positionY - 200 || 0);
      }

      if (isGraphInitialized.current && data?.nodes?.length) {
        console.log('isFirstRender.');
        // graphRef?.current?.zoomToFit({ maxScale: 1 });
        // graphRef?.current?.centerContent();
        graphRef?.current?.centerPoint(data?.nodes?.[0]?.positionX || 0, data?.nodes?.[0]?.positionY - 200 || 0);
        isGraphInitialized.current = false;
      }
    }, [data]);

    // 重置缩放
    const resetZoom = () => {
      setZoom(100);
      graphRef?.current?.zoomTo(1);
    };

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
            defaultValue={100}
            className={styles['zoom-input']}
            value={zoom}
            onChange={(value) => changeZoom(value)}
          />
          <Button type="outline" size="mini" onClick={() => resetZoom()}>
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
