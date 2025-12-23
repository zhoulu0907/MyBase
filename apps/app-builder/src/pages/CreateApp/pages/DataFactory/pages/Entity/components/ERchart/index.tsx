import { Graph, Node as X6Node } from '@antv/x6';
import { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { register } from '@antv/x6-react-shape';
import { Button, InputNumber } from '@arco-design/web-react';
import DetailDrawer from '../Drawers/DetailDrawer';
import { type EntityNode, type EntityERProps } from '../../../../utils/interface';
import { FIELD_TYPE, useGraphEntitytore } from '@onebase/ui-kit';
import EntityNodeComponent from './ERnode';
import nodeStyles from './ERnode.module.less';
import styles from './index.module.less';
import {
  GridNodePositioner,
  performAutoLayout,
  SectionCollapseHandler,
  toggleNodeShadow,
  toggleEdgeSelected,
  LINE_HEAD_HEIGHT,
  LINE_HEIGHT,
  LINE_TITLE_HEIGHT,
  NODE_WIDTH,
  NODE_HEIGHT
} from './utils';

export interface ERchartRef {
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
      updateEntityPosition
      // onlyUpdateNode
    },
    ref
  ) => {
    const { newNodes } = useGraphEntitytore();
    const [selectedNode] = useState<EntityNode | null>(null);
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [zoom, setZoom] = useState(100);

    const graphRef = useRef<Graph | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const isUnmounting = useRef(false);
    const isGraphInitialized = useRef(false);
    const collapseHandlerRef = useRef<SectionCollapseHandler | null>(null);

    const getGraphPositon = () => {
      const contentArea = graphRef.current?.getContentArea();
      return contentArea
        ? { x: contentArea.x + contentArea.width / 2, y: contentArea.y + contentArea.height / 2 }
        : undefined;
    };

    useImperativeHandle(ref, () => ({
      getGraphPositon
    }));

    const handleSectionCollapse = useCallback(
      (nodeId: string, section: 'system' | 'custom', isCollapsed: boolean) => {
        if (!collapseHandlerRef.current) return;

        const nodeData = data.nodes.find((n) => n.entityId === nodeId);
        if (!nodeData) return;

        collapseHandlerRef.current.handleSectionCollapse(nodeId, section, isCollapsed, nodeData);
      },
      [data]
    );

    const portsItems = (nodeData: EntityNode, systemCollapsed: boolean = true) => {
      const items: object[] = [];

      const systemFields = nodeData?.fields.filter((f) => f.isSystemField === FIELD_TYPE.SYSTEM);
      const customFields = nodeData?.fields.filter((f) => f.isSystemField === FIELD_TYPE.CUSTOM);

      // 系统字段标题行的聚合 port
      if (systemFields.length > 0) {
        const systemTitleY = LINE_HEAD_HEIGHT + LINE_TITLE_HEIGHT / 2; // 标题行垂直居中
        items.push({
          id: `${nodeData.entityId}_system_fields_source`, // 聚合 source port
          group: 'right',
          args: { x: NODE_WIDTH, y: systemTitleY }
        });
        items.push({
          id: `${nodeData.entityId}_system_fields_target`,
          group: 'left',
          args: { x: 0, y: systemTitleY }
        });
      }

      // 自定义字段标题行的聚合 port
      if (customFields.length > 0) {
        // 如果系统字段折叠，自定义字段标题位置需要向上偏移
        const systemTitleOffset = systemFields.length > 0 ? (systemCollapsed ? 0 : LINE_TITLE_HEIGHT) : 0;
        const customTitleY = LINE_HEAD_HEIGHT + systemTitleOffset + LINE_TITLE_HEIGHT / 2;
        items.push({
          id: `${nodeData.entityId}_custom_fields_source`,
          group: 'right',
          args: { x: NODE_WIDTH, y: customTitleY }
        });
        items.push({
          id: `${nodeData.entityId}_custom_fields_target`,
          group: 'left',
          args: { x: 0, y: customTitleY }
        });
      }

      nodeData?.fields?.forEach((field, index) => {
        const extraTitleHeight = field.isSystemField === FIELD_TYPE.SYSTEM ? LINE_TITLE_HEIGHT : LINE_TITLE_HEIGHT * 2;
        const accumulatedHeight = index >= 1 ? index * LINE_HEIGHT : 0;
        const finalY = LINE_HEAD_HEIGHT + extraTitleHeight + accumulatedHeight + LINE_HEIGHT / 2; // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半

        items.push({
          id: `${field.fieldId || field.fieldName}_source`,
          group: 'right',
          args: { x: NODE_WIDTH, y: finalY }
        });
        items.push({
          id: `${field.fieldId || field.fieldName}_target`,
          group: 'left',
          args: { x: 0, y: finalY }
        });
      });
      return items;
    };

    const createAndAddNode = useCallback(
      (positioner: GridNodePositioner, nodeDatas: EntityNode[]) => {
        nodeDatas.forEach((nodeData) => {
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
              onStatusChange,
              onUpdatePorts: (nodeId: string, section: 'system' | 'custom', isCollapsed: boolean) => {
                handleSectionCollapse(nodeId, section, isCollapsed);
              }
            },
            zIndex: newNodes.includes(nodeData.entityId) ? newNodes.length : 0,
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
              // 连接桩定义 - 使用默认的系统字段折叠状态（true）
              items: portsItems(nodeData, true)
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
        onStatusChange,
        handleSectionCollapse,
        newNodes
      ]
    );

    // 根据字段ID和节点数据，获取应该连接的连接桩
    const getPortForField = (
      fieldId: string,
      nodeId: string,
      nodeData: EntityNode,
      isSource: boolean,
      systemCollapsed: boolean = true,
      customCollapsed: boolean = false
    ): string => {
      const field = nodeData.fields.find((f) => f.fieldId === fieldId || f.fieldName === fieldId);
      if (!field) {
        return `${fieldId}_${isSource ? 'source' : 'target'}`;
      }

      const isSystemField = field.isSystemField === FIELD_TYPE.SYSTEM;
      const isCollapsed = isSystemField ? systemCollapsed : customCollapsed;

      // 折叠状态，使用聚合连接桩
      if (isCollapsed) {
        const section = isSystemField ? 'system' : 'custom';
        return `${nodeId}_${section}_fields_${isSource ? 'source' : 'target'}`;
      }

      // 展开状态，使用字段连接桩
      return `${fieldId}_${isSource ? 'source' : 'target'}`;
    };

    const createAndAddEdge = (
      graph: Graph,
      edgeData: {
        sourceEntityId: string;
        sourceFieldId: string;
        targetEntityId: string;
        targetFieldId: string;
        label?: string;
      },
      nodesData: EntityNode[],
      systemCollapsed: boolean = true,
      customCollapsed: boolean = false
    ) => {
      const sourceNode = nodesData.find((n) => n.entityId === edgeData.sourceEntityId);
      const targetNode = nodesData.find((n) => n.entityId === edgeData.targetEntityId);

      // 获取连接桩
      const sourcePort = sourceNode
        ? getPortForField(
            edgeData.sourceFieldId,
            edgeData.sourceEntityId,
            sourceNode,
            true,
            systemCollapsed,
            customCollapsed
          )
        : `${edgeData.sourceFieldId}_source`;
      const targetPort = targetNode
        ? getPortForField(
            edgeData.targetFieldId,
            edgeData.targetEntityId,
            targetNode,
            false,
            systemCollapsed,
            customCollapsed
          )
        : `${edgeData.targetFieldId}_target`;

      // 保存原始连接桩（用于后续展开时恢复）
      const originalSource = { cell: edgeData.sourceEntityId, port: `${edgeData.sourceFieldId}_source` };
      const originalTarget = { cell: edgeData.targetEntityId, port: `${edgeData.targetFieldId}_target` };

      const edge = graph.createEdge({
        source: { cell: edgeData.sourceEntityId, port: sourcePort },
        target: { cell: edgeData.targetEntityId, port: targetPort },
        attrs: {
          line: {
            stroke: 'rgb(var(--gray-5))',
            strokeWidth: 1,
            targetMarker: { name: 'block', width: 12, height: 8 }
          }
        },
        connector: { name: 'smooth' },
        data: {
          ...edgeData,
          originalSource,
          originalTarget
        },
        labels: edgeData.label
          ? [
              {
                position: 0.5,
                interactive: false,
                attrs: {
                  label: {
                    text: edgeData.label,
                    fill: 'rgb(var(--primary-6))',
                    fontSize: 12
                  }
                }
              }
            ]
          : []
      });
      graph.addEdge(edge, { options: { silent: true } });
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
                      stroke: 'rgba(var(--primary-6), 1)',
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
              edgeMovable: mode === 'edit'
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

        // 初始化折叠处理器
        collapseHandlerRef.current = new SectionCollapseHandler(graphRef.current);

        // 事件监听
        graphRef.current.on('node:mouseenter', ({ node }) => {
          if (mode === 'edit') {
            node.setAttrs({
              body: {
                stroke: 'rgba(var(--primary-6), 1)',
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

        // 拖拽开始标记
        let isDragging = false;
        let dragStartPosition = { x: 0, y: 0 };

        graphRef.current.on('node:move', ({ x, y }) => {
          // 记录拖拽开始位置
          dragStartPosition = { x, y };
          isDragging = false; // 重置拖拽状态
        });

        graphRef.current.on('node:moving', ({ x, y }) => {
          // 移动距离超过阈值则判断为在拖拽
          const deltaX = Math.abs(x - dragStartPosition.x);
          const deltaY = Math.abs(y - dragStartPosition.y);
          const threshold = 5;

          if (deltaX > threshold || deltaY > threshold) {
            isDragging = true;
          }
        });

        graphRef.current.on('node:moved', ({ e, x, y, node }) => {
          // 阻止折叠图标点击触发
          const target = e.target as HTMLElement;
          if (
            target.closest('#collapse-icon') ||
            target.closest('#status-change-icon') ||
            target.closest('#node-footer') ||
            target.closest(`.${nodeStyles['field-section-content']}`)
          ) {
            e.stopPropagation();
            return;
          }

          // 只在拖拽时才更新位置
          if (isDragging) {
            e.preventDefault();
            e.stopPropagation();
            updateEntityPosition?.(node.getData().data, x, y);
          }

          // 延迟重置拖拽状态，给点击事件处理留出时间
          setTimeout(() => {
            isDragging = false;
          }, 50);
        });

        // 记录当前选中边 id
        let currentSelectedEdgeId: string | null = null;

        graphRef.current.on('edge:click', ({ edge }) => {
          // 取消已选中边
          if (currentSelectedEdgeId && currentSelectedEdgeId !== edge.id) {
            const prev = graphRef.current?.getCellById(currentSelectedEdgeId);
            if (prev && prev.isEdge()) {
              toggleEdgeSelected(prev, false);
            }
          }
          // 选中当前边
          toggleEdgeSelected(edge, true);
          currentSelectedEdgeId = edge.id as string;

          onEdgeEdit?.(edge.data);
        });

        // 记录当前选中节点 id
        let currentSelectedId: string | null = null;

        graphRef.current.on('node:click', ({ e, node }) => {
          // 阻止折叠图标点击触发
          const target = e.target as HTMLElement;
          if (
            target.closest('#collapse-icon') ||
            target.closest('#status-change-icon') ||
            target.closest('#node-footer') ||
            target.closest(`.${nodeStyles.fieldSectionContent}`)
          ) {
            e.stopPropagation();
            return;
          }

          // 如果刚刚拖拽过，不触发点击事件
          if (isDragging) {
            e.preventDefault();
            e.stopPropagation();
            return;
          }

          // 切换选中态阴影
          const clickedId = node.id as string;

          // 取消之前选中
          if (currentSelectedId && currentSelectedId !== clickedId) {
            const prev = graphRef.current?.getCellById(currentSelectedId);
            if (prev && prev.isNode()) {
              toggleNodeShadow(prev, false);
            }
          }

          // 选中当前
          toggleNodeShadow(node, true);
          currentSelectedId = clickedId;

          // 添加短暂延迟，确保不是拖拽操作，再触发编辑
          setTimeout(() => {
            if (!isDragging) {
              e.preventDefault();
              e.stopPropagation();
              onNodeEdit?.(node.getData().data);
            }
          }, 10);
        });

        // 点击画布空白处，取消选中
        graphRef.current.on('blank:click', () => {
          if (!currentSelectedId) return;
          const prev = graphRef.current?.getCellById(currentSelectedId);
          if (prev && prev.isNode()) {
            toggleNodeShadow(prev, false);
          }
          currentSelectedId = null;

          // 清除选中边
          const selectedEdge = currentSelectedEdgeId && graphRef.current?.getCellById(currentSelectedEdgeId);
          if (selectedEdge && selectedEdge.isEdge()) {
            toggleEdgeSelected(selectedEdge, false);
          }
          currentSelectedEdgeId = null;
        });

        graphRef.current.on('scale', ({ sx }) => {
          setZoom(Number((sx * 100).toFixed(0)));
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

      // --- 1、添加节点 ---
      if (data.nodes && data.nodes.length > 0) {
        createAndAddNode(positioner, data.nodes);
      }

      // --- 2、更新连接桩位置（自定义字段的位置依赖系统字段的折叠状态）
      if (data.nodes && data.nodes.length > 0 && collapseHandlerRef.current) {
        collapseHandlerRef.current.updatePortsPosition(data.nodes, true);
      }

      // --- 3、添加边（根据新的连接桩位置）---
      if (data.edges && data.edges.length > 0) {
        data.edges.forEach((edge) => createAndAddEdge(graphRef.current!, edge, data.nodes, true, false));
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

    // 自动布局
    const handleAutoLayout = () => {
      if (!graphRef.current || !data?.nodes || data.nodes.length === 0) {
        console.warn('Graph not ready or no nodes to layout');
        return;
      }

      try {
        const newPositions = performAutoLayout(data.nodes, data.edges || [], {
          nodeWidth: NODE_WIDTH,
          horizontalSpacing: 50,
          verticalSpacing: 100,
          startX: 100,
          startY: 100
        });

        // 更新节点位置
        newPositions.forEach(({ id, x, y }) => {
          const cell = graphRef.current?.getCellById(id);
          if (cell && cell.isNode()) {
            (cell as X6Node).position(x, y);

            // 同步更新数据
            const nodeData = data.nodes.find((n) => n.entityId === id);
            if (nodeData && updateEntityPosition) {
              updateEntityPosition(nodeData, x, y);
            }
          }
        });

        // 居中显示
        setTimeout(() => {
          graphRef.current?.centerContent();
        }, 100);

        console.log('Auto layout completed');
      } catch (error) {
        console.error('Auto layout failed:', error);
      }
    };

    return (
      <div className={styles.entityERContainer}>
        <div ref={containerRef} className={styles.graphContainer} />

        {/* 工具栏 */}
        <div className={styles.toolbar}>
          <InputNumber
            mode="button"
            size="mini"
            suffix="%"
            max={150}
            min={60}
            step={5}
            defaultValue={100}
            className={styles.zoomInput}
            value={zoom}
            onChange={(value) => changeZoom(value)}
          />
          <Button type="outline" size="mini" onClick={() => changeZoom(100)}>
            重置
          </Button>
          <Button type="primary" size="mini" onClick={handleAutoLayout}>
            自动布局
          </Button>
        </div>

        {/* 节点详情抽屉 */}
        <DetailDrawer selectedNode={selectedNode as EntityNode} visible={drawerVisible} setVisible={setDrawerVisible} />
      </div>
    );
  }
);

export default ERchart;
