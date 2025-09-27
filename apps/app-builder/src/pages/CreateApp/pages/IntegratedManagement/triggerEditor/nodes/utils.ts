import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { v4 as uuidv4 } from 'uuid';
import { NodeType } from './const';

export const generateNodeId = (nodeType: NodeType) => {
  const uuid = uuidv4().replaceAll('-', '');
  return `${nodeType}_${uuid}`;
};

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

    // TODO(mickey): 对条件进行检查删除条件
    if (nodeData[key].filterCondition) {
      let newFilterCondition = [];

      for (let filterCondition of nodeData[key].filterCondition) {
        // TODO(mickey): remove debug log
        console.log('XXX: ', filterCondition);

        filterCondition.conditions = filterCondition.conditions
          .filter((c: any) => !c.fieldId.startsWith(nodeId))
          .filter((c: any) => c.value && !c.value.startsWith(nodeId));
        if (filterCondition.conditions.length > 0) {
          newFilterCondition.push(filterCondition);
        }
      }

      triggerEditorSignal.setNodeData(key, {
        ...nodeData[key],
        filterCondition: newFilterCondition
      });
    }

    // TODO(mickey): 对字段进行检查
  }
};

// 判断bolcks 是否包含当前节点

const enum JudgeStatus {
  NO_FOUND = 0,
  // 在blocks的第一层中找到了目标节点
  FOUND = 1,
  // 在blocks的更深层次中找到了目标节点
  INCLUDE = 2
}

const judge = (targetNodeId: string, blocks: FlowNodeJSON[], depth: number): JudgeStatus => {
  let status: JudgeStatus = JudgeStatus.NO_FOUND;
  for (let item of blocks) {
    if (item.blocks?.length) {
      status = judge(targetNodeId, item.blocks, depth + 1);
    }

    if (item.id === targetNodeId) {
      status = JudgeStatus.FOUND;
      if (depth > 0) {
        status = JudgeStatus.INCLUDE;
      }

      break;
    }
  }

  return status;
};

const getBlockNode = (curNodeId: string, blocks: FlowNodeJSON[], nodeTypes: NodeType[]): FlowNodeJSON[] => {
  let blockNode: FlowNodeJSON[] = [];

  for (let ele of blocks) {
    if (ele.id === curNodeId) {
      break;
    }

    if (ele.blocks?.length) {
      const hasCurNode = judge(curNodeId, ele.blocks, 0);
      if (hasCurNode == JudgeStatus.FOUND || hasCurNode == JudgeStatus.INCLUDE) {
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

/** 获取当前节点的数据
 * @param curNodeId 当前节点ID
 * @param allNodes 所有节点
 * @param nodeTypes 过滤节点类型
 * @returns 节点数据对象，如果不存在则返回[]
 */
export function getPrecedingNodes(
  targetNodeId: string,
  allNodes: FlowNodeJSON[],
  nodeTypes: NodeType[]
): FlowNodeJSON[] {
  let nodes: FlowNodeJSON[] = [];

  for (let ele of allNodes) {
    if (ele.id === targetNodeId) {
      return nodes;
    }

    // 带blocks的节点
    if (ele.blocks?.length) {
      // 判断是否包含目标节点
      const hasCurNode = judge(targetNodeId, ele.blocks, 0);

      if (hasCurNode == JudgeStatus.FOUND) {
        const curIndex = ele.blocks.findIndex((block: any) => block.id === targetNodeId);
        let blocks: any[] = [];
        if (curIndex - 1 > 0) {
          blocks = ele.blocks.slice(0, curIndex - 1);
        } else if (curIndex - 1 === 0) {
          blocks = [ele.blocks[0]];
        }

        // 平铺 blocks
        nodes.push({ ...ele, blocks: [] }, ...blocks);

        return nodes;
      } else if (hasCurNode == JudgeStatus.INCLUDE) {
        // 在当前节点的blocks中
        const blocks = getBlockNode(targetNodeId, ele.blocks, nodeTypes);
        nodes.push(...blocks);
      } else {
        // 如果不包含 继续向下递归搜索
        const blocks = getPrecedingNodes(targetNodeId, ele.blocks, nodeTypes);
        nodes.push(...blocks);
      }
    }

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
        if (nodeData.addType === DATA_SOURCE_TYPE.FORM) {
          return nodeData.mainEntityId;
        }
        if (nodeData.addType === DATA_SOURCE_TYPE.SUBFORM) {
          return nodeData.subEntityId;
        }
        break;
      case NodeType.DATA_UPDATE:
        if (nodeData.updateType === DATA_SOURCE_TYPE.FORM) {
          return nodeData.mainEntityId;
        }
        if (nodeData.updateType === DATA_SOURCE_TYPE.SUBFORM) {
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
  const fieldIds: string[] = [];
  const newConditionFields: ConfitionField[] = [];
  res.forEach((item: any) => {
    fieldIds.push(item.id);
    newConditionFields.push({
      label: item.displayName,
      value: item.id,
      fieldType: item.fieldType
    });
  });

  setConditionFields(newConditionFields);
  if (fieldIds?.length) {
    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    setValidationTypes(newValidationTypes);
  }
};
