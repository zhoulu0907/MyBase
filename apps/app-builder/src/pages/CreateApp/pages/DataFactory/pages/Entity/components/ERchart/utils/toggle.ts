import { Node, Edge } from '@antv/x6';
import { EDGE_NORMAL_STYLE, EDGE_SELECTED_STYLE } from './constants';

// 切换节点阴影（选中）效果，供画布事件调用
export const toggleNodeShadow = (node: Node, selected: boolean) => {
  const prev = node.getData?.() as Record<string, unknown> | undefined;
  node.setData({
    ...prev,
    selected
  });
};

// 切换边的选中样式（加粗与颜色加深）
export const toggleEdgeSelected = (edge: Edge, selected: boolean) => {
  const prev = edge.getData?.() as Record<string, unknown> | undefined;
  edge.setData({
    ...prev,
    selected
  });

  if (selected) {
    edge.setAttrs({
      line: EDGE_SELECTED_STYLE
    });
  } else {
    edge.setAttrs({
      line: EDGE_NORMAL_STYLE
    });
  }
};
