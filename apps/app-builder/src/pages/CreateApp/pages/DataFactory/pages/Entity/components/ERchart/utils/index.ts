import { SectionCollapseHandler } from './sectionCollapseHandler';
import { GridNodePositioner } from './nodePositioner';
import { performAutoLayout } from './autoLayout';
import { Node } from '@antv/x6';

// 切换节点阴影（选中）效果，供画布事件调用
export const toggleNodeShadow = (node: Node, selected: boolean) => {
  const prev = node.getData?.() as Record<string, unknown> | undefined;
  node.setData({
    ...prev,
    selected
  });
};

export { SectionCollapseHandler, GridNodePositioner, performAutoLayout };
