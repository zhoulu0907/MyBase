import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  FLOW_ENTITY_TYPE,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
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
const getBlockNode = (curNodeId: string, blocks: FlowNodeJSON[], nodeTypes: NodeType[]): FlowNodeJSON[] => {
  let blockNode: FlowNodeJSON[] = [];
  for (let ele of blocks) {
    if (ele.id === curNodeId) {
      break;
    }
    // ? 可能 根据格式需要修改内容
    if (ele.blocks?.length) {
      const hasCurNode = judge(curNodeId, ele.blocks);
      if (hasCurNode) {
        if (nodeTypes.includes(ele.type as NodeType)) {
          blockNode.push(ele);
        }
        const newBlocks = getBlockNode(curNodeId, ele.blocks, nodeTypes);
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
        const blocks = getBlockNode(curNodeId, ele.blocks, nodeTypes);
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

export const getDataNodeSource = (nodeId: string): string => {
  const nodeData = triggerEditorSignal.nodeData.value[nodeId];
  const node = triggerEditorSignal.nodes.value.find((item: any) => item.id === nodeId);

  if (nodeData && node) {
    console.log('node: ', node);
    console.log('nodeData: ', nodeData);

    switch (node.type) {
      case NodeType.START_ENTITY:
        return nodeData.entityId;
      case NodeType.DATA_ADD:
        if (nodeData.addType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
          return nodeData.mainEntityId;
        }
        if (nodeData.addType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
          return nodeData.subEntityId;
        }
        break;
      case NodeType.DATA_UPDATE:
        if (nodeData.updateType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
          return nodeData.mainEntityId;
        }
        if (nodeData.updateType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
          return nodeData.subEntityId;
        }
        break;

      case NodeType.DATA_QUERY:
        if (nodeData.dataType === DATA_SOURCE_TYPE.FORM) {
          return nodeData.mainEntityId;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.SUBFORM) {
          return nodeData.subEntityId;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
          return getDataNodeSource(nodeData.dataNodeId);
        }
        break;

      case NodeType.DATA_QUERY_MULTIPLE:
        if (nodeData.dataType === DATA_SOURCE_TYPE.FORM) {
          return nodeData.mainEntityId;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.SUBFORM) {
          return nodeData.subEntityId;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
          return getDataNodeSource(nodeData.dataNodeId);
        }
        break;

      default:
        return '';
    }
  }

  return '';
};

export const getEntityFieldList = async (
  dataSource: string,
  setConditionFields: (fields: ConfitionField[]) => void,
  setValidationTypes: (types: EntityFieldValidationTypes[]) => void
) => {
  if (!dataSource) {
    return;
  }
  const res = await getEntityFields({ entityId: dataSource });
  const filedIds: string[] = [];
  const newConditionFields: ConfitionField[] = [];
  res.forEach((item: any) => {
    filedIds.push(item.id);
    newConditionFields.push({
      label: item.displayName,
      value: item.id,
      fieldType: item.fieldType
    });
  });
  console.log('newConditionFields: ', newConditionFields);
  setConditionFields(newConditionFields);
  if (filedIds?.length) {
    const newValidationTypes = await getFieldCheckTypeApi(filedIds);
    console.log('newValidationTypes: ', newValidationTypes);
    setValidationTypes(newValidationTypes);
  }
};
