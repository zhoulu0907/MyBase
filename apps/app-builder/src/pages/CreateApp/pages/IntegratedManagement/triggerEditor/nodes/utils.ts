import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import { NodeType } from './const';

// 清除数据节点依赖关系
export const clearDataOriginNodeId = (nodeId: string) => {
  const nodeData = triggerEditorSignal.nodeData.value;
  const keys = Object.keys(triggerEditorSignal.nodeData.value);
  for (let key of keys) {
    if (nodeData[key].dataNodeId === nodeId) {
      triggerEditorSignal.setNodeData(nodeData[key].id, {
        ...nodeData[key],
        dataNodeId: undefined,
        sortBy: [] // 清除已选择排序字段
      });
    }
  }
};

// 判断bolcks 是否包含当前节点
const judge = (curNodeId: string, blocks: FlowNodeJSON[]): boolean => {
  let status: boolean = false;
  for (let item of blocks) {
    if (item.blocks?.length) {
      status = judge(curNodeId, item.blocks);
    }
    if (item.id === curNodeId) {
      status = true;
      break;
    }
  }
  return status;
};

// 只有存在当前节点的支线才可以使用
const getBlockNode = (curNodeId: string, blocks: FlowNodeJSON[]): FlowNodeJSON[] => {
  let blockNode: FlowNodeJSON[] = [];
  for (let ele of blocks) {
    if (ele.id === curNodeId) {
      break;
    }
    // ? 可能 根据格式需要修改内容
    if (ele.blocks?.length) {
      const hasCurNode = judge(curNodeId, ele.blocks);
      if (hasCurNode) {
        const nodeData = triggerEditorSignal.nodeData.value[ele.id];
        if (
          ele.type === NodeType.DATA_QUERY_MULTIPLE
          //   nodeData.dataSource &&
          //   nodeData.dataType !== DATA_SOURCE_TYPE.DATA_NODE
        ) {
          blockNode.push(ele);
        }
        const newBlocks = getBlockNode(curNodeId, ele.blocks);
        blockNode.push.apply(blockNode, newBlocks);
      }
    }
  }

  return blockNode;
};

export function getBeforeCurQueryNodes(
  curNodeId: string,
  allNodes: FlowNodeJSON[],
  nodeTypes: NodeType[]
): FlowNodeJSON[] {
  // 获取当前节点前并且是数据查询节点的数据
  // 条件节点  blocks
  let nodes: FlowNodeJSON[] = [];
  for (let ele of allNodes) {
    if (ele.id === curNodeId) {
      break;
    }
    if (ele.blocks?.length) {
      // todo 处理数据 然后递归
      // 判断是否包含当前节点
      const hasCurNode = judge(curNodeId, ele.blocks);
      if (hasCurNode) {
        const blocks = getBlockNode(curNodeId, ele.blocks);
        nodes.push.apply(nodes, blocks);
      } else {
        const blocks = getBeforeCurQueryNodes(curNodeId, ele.blocks, nodeTypes);
        nodes.push.apply(nodes, blocks);
      }
    }
    // const nodeData = triggerEditorSignal.nodeData.value[ele.id];
    if (nodeTypes.includes(ele.type as NodeType)) {
      nodes.push(ele);
    }
  }
  return nodes;
}

/**
 * 校验 payloadForm 表单并根据校验结果设置 form 的 invalid 字段
 * @param form 外层表单对象
 * @param payloadForm 需要校验的表单对象
 * @param validateOnly 是否只做校验（可选，默认为 true）
 */
export async function validateNodeForm(form: any, payloadForm: any, validateOnly: boolean = false) {
  try {
    form.setValueIn('invalid', false);
    await payloadForm.validate({ validateOnly });
  } catch (error: any) {
    // console.warn('validateNodeForm error: ', error.errors);
    // 捕获校验错误并设置 invalid
    form.setValueIn('invalid', true);
  }
}
