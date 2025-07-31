import React, { useState, useEffect, useRef, Component } from 'react';
import { Button } from '@arco-design/web-react';
import { Graph } from '@antv/x6';
// import { ReactShape } from '@antv/x6-react-shape';
import { register } from '@antv/x6-react-shape';
import DetailDrawer from '../Drawer/DetailDrawer';
import EditDrawer from '../Drawer/EditDrawer';
import { type EntityNode } from '../../utils/interface';
import styles from './ERchart.module.less';
import EntityNodeComponent from './ERnode';
// import TestNodeComponent from './TestNodeComponent'; // 临时导入测试组件

const LINE_HEIGHT = 24
const NODE_WIDTH = 150

interface EntityERProps {
  mode: 'view' | 'edit'; // 查看模式或编辑模式
  data: {
    nodes: EntityNode[];
    edges: Array<{
      source: string;
      target: string;
      label?: string;
    }>;
  };
  onNodeEdit?: (nodeId: string, data: EntityNode) => void;
  onNodeAdd?: () => void;
}

const ERchart: React.FC<EntityERProps> = ({
  mode = 'edit',
  data,
  onNodeEdit,
  onNodeAdd
}) => {
  const [selectedNode, setSelectedNode] = useState<EntityNode | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [editDrawerVisible, seteditDrawerVisible] = useState(false);
  const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
  const graphRef = useRef<Graph | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // 处理节点编辑
  const handleNodeEdit = (nodeId: string) => {
    const nodeData = data.nodes.find(n => n.id === nodeId);
    if (nodeData) {
      setEditingNode(nodeData);
      seteditDrawerVisible(true);
    }
  };

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
      // component: TestNodeComponent,

        // render:(node: { getData: () => EntityNode }) => {
        //   const data = node.getData();
        //   console.log('Rendering node with data:', data); // 调试信息
        //   console.log('Mode:', mode); // 调试信息
        //   console.log('Collapsed sections:', collapsedSections); // 调试信息
        //   return (
        //     <EntityNodeComponent
        //       nodeData={data}
        //       mode={mode}
        //       onEdit={handleNodeEdit}
        //       onToggleSection={toggleSection}
        //       collapsedSections={collapsedSections}
        //     />
        //   );
        // },
      // },
      attrs: {
        body: {
          fill: 'transparent',
          stroke: 'transparent',
        },
      },
      ports: {
        groups: {
          top: {
            position: 'top',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
              },
            },
          },
          bottom: {
            position: 'bottom',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
              },
            },
          },
          left: {
            position: 'left',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
              },
            },
          },
          right: {
            position: 'right',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
              },
            },
          },
        },
      },
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
  }, [mode, handleNodeEdit]); // 添加 mode 到依赖项

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
          color: 'rgb(229, 235, 230, 0.5)',
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
          anchor: 'center',
          connector: 'rounded',
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
      console.log('Adding node:', nodeData); // 调试信息
      
      // 确保节点数据有必要的属性
      const nodeConfig = {
        id: nodeData.id,
        x: nodeData.x || 100,
        y: nodeData.y || 100,
        width: 280,
        height: 200,
        shape: 'er-entity-node',
        data: {
          ...nodeData,
          mode,
          onNodeEdit,
          onNodeAdd,
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
        ports: {
          groups: {
            list: {
              markup: [
                {
                  tagName: 'rect',
                  selector: 'portBody',
                },
                {
                  tagName: 'text',
                  selector: 'portNameLabel',
                },
                {
                  tagName: 'text',
                  selector: 'portTypeLabel',
                },
              ],
              attrs: {
                portBody: {
                  width: NODE_WIDTH,
                  height: LINE_HEIGHT,
                  strokeWidth: 1,
                  stroke: '#5F95FF',
                  fill: '#EFF4FF',
                  magnet: true,
                },
                portNameLabel: {
                  ref: 'portBody',
                  refX: 6,
                  refY: 6,
                  fontSize: 10,
                },
                portTypeLabel: {
                  ref: 'portBody',
                  refX: 95,
                  refY: 6,
                  fontSize: 10,
                },
              },
              position: 'erPortPosition',
            },
          },
        },
      };
      
      const node = graphRef.current!.addNode(nodeConfig);
      console.log('Node created:', node); // 调试信息
    });

    // 添加边
    data.edges.forEach(edgeData => {
      const edge = graphRef.current!.createEdge({
        source: edgeData.source,
        target: edgeData.target,
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
      {/* <div className={styles['toolbar']}>
        <Button onClick={() => graphRef.current?.zoomToFit()}>适应画布</Button>
        <Button onClick={() => graphRef.current?.centerContent()}>居中</Button>
        {mode === 'edit' && (
          <>
            <Button onClick={onNodeAdd}>添加节点</Button>
            <Button onClick={() => {
              // TODO: 实现连线模式功能
              console.log('连线模式功能待实现');
            }}>连线模式</Button>
          </>
        )}
      </div> */}
      
      <div ref={containerRef} className={styles['graph-container']} />

      {/* 节点详情抽屉 */}
      <DetailDrawer selectedNode={selectedNode as EntityNode} visible={drawerVisible} setVisible={setDrawerVisible} />

      {/* 编辑节点抽屉 */}
      <EditDrawer
        editingNode={editingNode as EntityNode}
        visible={editDrawerVisible}
        setVisible={seteditDrawerVisible}
        setEditingNode={(node: EntityNode | null) => setEditingNode(node)}
        onNodeEdit={onNodeEdit as (nodeId: string, data: EntityNode) => void}
      />
    </div>
  );
};

export default ERchart;
