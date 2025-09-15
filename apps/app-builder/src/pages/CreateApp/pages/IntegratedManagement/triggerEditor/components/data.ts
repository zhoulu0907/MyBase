import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';

export function getBeforeCurNodes(curNodeId: string, allNode: FlowNodeJSON[]): FlowNodeJSON[] {
  // 获取当前节点前并且是数据查询节点的数据
  // 条件节点  blocks
  let nodes: FlowNodeJSON[] = [];
  for (let ele of allNode) {
    if (ele.id === curNodeId) {
      break;
    }
    if (ele.blocks?.length) {
        // todo 处理数据 然后递归
        const blocks = ele.blocks;
        nodes.push({...ele,blocks})
    }
    if (ele.type === 'dataQuery' || ele.type === 'dataQueryMultiple') {
      nodes.push(ele);
    }
  }
  return nodes;
}
