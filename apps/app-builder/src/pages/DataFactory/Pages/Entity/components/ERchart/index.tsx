import React, { useState, useEffect, useRef } from "react";
import { Graph } from "@antv/x6";
// import { ReactShape } from '@antv/x6-react-shape';
import { register } from "@antv/x6-react-shape";
import DetailDrawer from "../Drawers/DetailDrawer";
// import EditDrawer from '../Drawer/EditDrawer';
import { type EntityNode, type EntityERProps } from "../../../../utils/interface";
import styles from "./index.module.less";
import EntityNodeComponent from "./ERnode";

const LINE_HEAD_HEIGHT = 48;
const LINE_HEIGHT = 34.8;
const LINE_TITLE_HEIGHT = 44;
const NODE_WIDTH = 280;
const NODE_HEIGHT = 200;

const ERchart: React.FC<EntityERProps> = ({
  mode = "edit",
  data,
  onNodeEdit,
  onNodeAdd,
  onNodeDelete,
  onNodeAddField,
  onNodeAddRelation,
  onlyUpdateNode,
}) => {
  const [selectedNode, setSelectedNode] = useState<EntityNode | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  // const [editDrawerVisible, seteditDrawerVisible] = useState(false);
  // const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
  const graphRef = useRef<Graph | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const isGraphInitialized = useRef(false); // 标记是否已初始化

  useEffect(() => {
    // 注册 React 形状节点
    register({
      shape: "er-entity-node",
      width: 280,
      height: 200,
      component: EntityNodeComponent,
      effect: ["data"],
    });

    return () => {
      // Cleanup registration if needed
      try {
        Graph.unregisterNode("er-entity-node");
      } catch (e) {
        // Ignore if node was not registered
        console.log(e);
      }
    };
  }, []); // 添加 mode 到依赖项

  // 初始化图形
  useEffect(() => {
    console.log("Initializing graph...");

    const initGraph = () => {
      if (!containerRef.current) {
        console.warn("Container ref is not available");
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
            type: "mesh",
            size: 20,
            args: {
              color: "#eee",
              thickness: 1,
            },
          },
          connecting: {
            anchor: "right",
            connector: "rounded", // 或 'smooth', 'jumpover', 或自定义
            connectionPoint: "anchor",
            allowBlank: false,
            highlight: true,
            snap: true,
            createEdge() {
              return graphRef.current!.createEdge({
                shape: "edge",
                attrs: {
                  line: {
                    stroke: "#39B85F",
                    strokeWidth: 2,
                    targetMarker: {
                      name: "block",
                      width: 12,
                      height: 8,
                    },
                  },
                },
              });
            },
          },
          interacting: {
            nodeMovable: mode === "edit",
            edgeMovable: mode === "edit",
            edgeLabelMovable: mode === "edit",
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
        console.error("Failed to create graph:", error);
        return;
      }

      // 事件监听
      graphRef.current.on("node:click", ({ node }) => {
        const nodeData = node.getData() as EntityNode;
        if (nodeData) {
          setSelectedNode(nodeData);
          // setDrawerVisible(true);
        }
      });

      graphRef.current.on("node:mouseenter", ({ node }) => {
        if (mode === "edit") {
          node.setAttrs({
            body: {
              stroke: "#39B85F",
              strokeWidth: 2,
            },
          });
        }
      });

      graphRef.current.on("node:mouseleave", ({ node }) => {
        node.setAttrs({
          body: {
            stroke: "#d9d9d9",
            strokeWidth: 1,
          },
        });
      });

      graphRef.current.on("node:moved", ({ e, x, y, node, view }) => {
        console.log("node:moved", e, x, y, node, view);
        const { nodes, edges } = JSON.parse(
          localStorage.getItem("entityFormValues") ||
            JSON.stringify({ nodes: [], edges: [] }),
        );
        const nodeData = nodes.find((n: EntityNode) => n.id === node.id);
        if (nodeData) {
          nodeData.x = x;
          nodeData.y = y;
        }
        localStorage.setItem(
          "entityFormValues",
          JSON.stringify({ nodes, edges }),
        );
      });

      isGraphInitialized.current = true; // 标记已初始化
    };

    initGraph();

    return () => {
      if (graphRef.current) {
        graphRef.current.dispose();
        graphRef.current = null;
        isGraphInitialized.current = false;
      }
    };
  }, []);

  useEffect(() => {
    console.log("data changed.");

    // 只在首次渲染时居中，后续更新保持用户操作的位置
    if (isGraphInitialized.current) {
      console.log("First data load, centering content.");
      graphRef?.current?.zoomToFit({ maxScale: 1 });
      // graphRef?.current?.centerContent();
    }

    graphRef?.current?.clearCells();

    // 添加节点
    if (data.nodes) {
      data.nodes.forEach((nodeData) => {
        const portsItems = (nodeData: EntityNode) => {
          const items: object[] = [];
          nodeData.fields.forEach((field, index) => {
            const extraTitleHeight = field.isSystem
              ? LINE_TITLE_HEIGHT
              : LINE_TITLE_HEIGHT * 2;
            const accumulatedHeight = index >= 1 ? index * LINE_HEIGHT : 0;

            const leftItem = {
              id: field.id + "_target", // 使用字段的唯一 ID 作为 port ID
              group: "left", // 指定属于 'left' 组
              args: {
                x: 0,
                y:
                  LINE_HEAD_HEIGHT +
                  extraTitleHeight +
                  accumulatedHeight +
                  LINE_HEIGHT / 2, // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半
              },
            };
            const rightItem = {
              id: field.id + "_source", // 使用字段的唯一 ID 作为 port ID
              group: "right", // 指定属于 'right' 组
              args: {
                x: NODE_WIDTH,
                y:
                  LINE_HEAD_HEIGHT +
                  extraTitleHeight +
                  accumulatedHeight +
                  LINE_HEIGHT / 2, // y = 头部高度 + 累积高度 + 额外标题行高度 + 当前字段高度的一半
              }, // 传递字段索引，用于布局
              // 可以自定义 attrs 来覆盖 group 的默认 attrs
              attrs: {
                // circle: {
                // fill: field.isSystem ? '#f0f0f0' : '#e6f7ff', // 示例：区分系统和自定义字段
                // height: currentFieldHeight,
                // width: NODE_WIDTH,
                // }
              },
            };
            items.push(leftItem);
            items.push(rightItem);
          });
          return items;
        };
        const node = graphRef.current!.createNode({
          id: nodeData.id,
          x: nodeData.x,
          y: nodeData.y,
          width: NODE_WIDTH,
          height: NODE_HEIGHT,
          shape: "er-entity-node",
          data: {
            data: nodeData,
            // mode,
            onNodeEdit,
            onNodeAdd,
            onNodeDelete,
            onNodeAddField,
            onNodeAddRelation,
          },
          attrs: {
            body: {
              fill: "#fff",
              stroke: "#d9d9d9",
              strokeWidth: 1,
              rx: 4,
              ry: 4,
            },
          },
          // portMarkup: [Markup.getForeignObjectMarkup()],
          // --- 动态添加 ports ---
          ports: {
            // 连接桩分组
            groups: {
              left: {
                // markup: [
                //   {
                //     tagName: 'rect',
                //     selector: 'portBody',
                //   }
                // ],
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
                    stroke: "transparent",
                    fill: "transparent",
                    strokeWidth: 0,
                  },
                },
                // position: 'erPortPosition', // 使用您自定义的布局
                position: {
                  name: "absolute",
                  // args: { x: 0, y: 0 },
                },
              },
              right: {
                attrs: {
                  circle: {
                    r: 1,
                    magnet: true,
                    stroke: "transparent",
                    fill: "transparent",
                    strokeWidth: 0,
                  },
                },
                position: {
                  name: "absolute",
                },
              },
            },
            // 连接桩定义
            items: portsItems(nodeData),
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
          },
        });
        graphRef.current?.addNode(node);
      });
    }

    // 添加边
    if (data.edges) {
      data.edges.forEach((edgeData) => {
        // 确保 source 和 target 是对象，并包含 port
        const source = edgeData.source;
        const target = edgeData.target;

        const edge = graphRef.current!.createEdge({
          source: { cell: source.cell, port: source.port + "_source" },
          target: { cell: target.cell, port: target.port + "_target" },
          attrs: {
            line: {
              // 连线样式
              stroke: "#39B85F",
              strokeWidth: 2,
              targetMarker: {
                name: "block",
                width: 12,
                height: 8,
              },
            },
          },
          connector: { name: "smooth" }, // 曲线
          // vertices: [ // 路径
          //   { x: 200, y: 200 },
          //   { x: 380, y: 120 },
          // ],
          labels: edgeData.label
            ? [
                {
                  position: 0.5,
                  attrs: {
                    label: {
                      text: edgeData.label,
                      fill: "#39B85F",
                      fontSize: 12,
                      textAnchor: "middle",
                      textVerticalAnchor: "middle",
                    },
                  },
                },
              ]
            : [],
          // 可以添加其他边的属性，如 router, connector 等来优化连线路径
          // router: 'manhattan', // 例如使用直角路由
          // connector: { name: 'rounded', args: { radius: 8 }}, // 例如使用圆角连接
        });
        graphRef.current?.addEdge(edge);
      });
    }

    if (
      !isGraphInitialized.current &&
      data?.nodes?.length === 1 &&
      !onlyUpdateNode
    ) {
      console.log("onlyUpdateNode.");
      // graphRef?.current?.centerPoint(data?.nodes?.[0]?.x || 0, data?.nodes?.[0]?.y - 200 || 0);
      graphRef?.current?.centerPoint(
        data?.nodes?.[0]?.x || 0,
        data?.nodes?.[0]?.y - 200 || 0,
      );
    }

    if (isGraphInitialized.current) {
      console.log("isFirstRender.");
      // graphRef?.current?.zoomToFit({ maxScale: 1 });
      // graphRef?.current?.centerContent();
      graphRef?.current?.centerPoint(
        data?.nodes?.[0]?.x || 0,
        data?.nodes?.[0]?.y - 200 || 0,
      );
      isGraphInitialized.current = false;
    }
  }, [data]);

  return (
    <div className={styles["entity-er-container"]}>
      <div ref={containerRef} className={styles["graph-container"]} />

      {/* 节点详情抽屉 */}
      <DetailDrawer
        selectedNode={selectedNode as EntityNode}
        visible={drawerVisible}
        setVisible={setDrawerVisible}
      />
    </div>
  );
};

export default ERchart;
