import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import type { FlowNodeEntity, FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConditionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { NodeType } from '@onebase/common';
import { v4 as uuidv4 } from 'uuid';

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
        if (filterCondition.conditions && filterCondition.conditions.length > 0) {
          const newConditions = filterCondition.conditions
            .filter((c: any) => c !== null && c !== undefined)
            .filter((c: any) => c.fieldId && !c.fieldId.startsWith(nodeId))
            .filter((c: any) => c.value && !`${c.value}`.startsWith(nodeId));
          if (newConditions.length > 0) {
            newFilterCondition.push({ conditions: newConditions });
          }
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
    if (item.id === targetNodeId) {
      status = JudgeStatus.FOUND;

      if (depth > 0) {
        status = JudgeStatus.INCLUDE;
      }

      return status;
    }

    if (item.blocks?.length) {
      status = judge(targetNodeId, item.blocks, depth + 1);
    }
  }

  return status;
};

const getBlockNode = (targetNodeId: string, blocks: FlowNodeJSON[], nodeTypes: NodeType[]): FlowNodeJSON[] => {
  let blockNode: FlowNodeJSON[] = [];

  for (let ele of blocks) {
    if (ele.id === targetNodeId) {
      const curIndex = blocks.findIndex((block: any) => block.id === targetNodeId);
      let newBlocks: any[] = [];
      if (curIndex > 0) {
        newBlocks = blocks.slice(0, curIndex);
      }

      blockNode.push(...newBlocks);

      break;
    }

    if (ele.blocks?.length) {
      const hasCurNode = judge(targetNodeId, ele.blocks, 0);

      if (hasCurNode == JudgeStatus.FOUND || hasCurNode == JudgeStatus.INCLUDE) {
        if (nodeTypes.includes(ele.type as NodeType)) {
          blockNode.push(ele);
        }

        const newBlocks = getBlockNode(targetNodeId, ele.blocks, nodeTypes);

        blockNode.push(...newBlocks);
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

      //   目标节点就在当前block下
      if (hasCurNode == JudgeStatus.FOUND) {
        // 找到在当前block中当前节点之前的节点
        const curIndex = ele.blocks.findIndex((block: any) => block.id === targetNodeId);

        let newBlocks: any[] = [];
        if (curIndex > 0) {
          newBlocks = ele.blocks.slice(0, curIndex);
        }

        // 平铺 blocks
        nodes.push({ ...ele, blocks: [] }, ...newBlocks);

        return nodes;
      } else if (hasCurNode == JudgeStatus.INCLUDE) {
        // 在当前节点的下游blocks中
        const blocks = getBlockNode(targetNodeId, ele.blocks, nodeTypes);
        nodes.push(...blocks);
      }
    } else {
      // 不包含blocks的节点
      if (nodeTypes.includes(ele.type as NodeType)) {
        nodes.push(ele);
      }
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
  const nodeId = payloadForm.getFieldValue('id');
  try {
    await payloadForm.validate({ validateOnly });
    // form.setValueIn('invalid', false);
    triggerEditorSignal.setInvalidNode(nodeId, false);
  } catch (error: any) {
    console.warn('validateNodeForm error: ', error.errors);

    // 捕获校验错误并设置 invalid
    // form.setValueIn('invalid', true);
    triggerEditorSignal.setInvalidNode(nodeId, true);
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
        if (nodeData.addType === DATA_SOURCE_TYPE.MAIN_TABLE) {
          return nodeData.mainTableName;
        }
        if (nodeData.addType === DATA_SOURCE_TYPE.SUB_TABLE) {
          return nodeData.subTableName;
        }
        break;
      case NodeType.DATA_UPDATE:
        if (nodeData.updateType === DATA_SOURCE_TYPE.MAIN_TABLE) {
          return nodeData.mainTableName;
        }
        if (nodeData.updateType === DATA_SOURCE_TYPE.SUB_TABLE) {
          return nodeData.subTableName;
        }
        break;

      case NodeType.DATA_QUERY:
        if (nodeData.dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
          return nodeData.mainTableName;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
          return nodeData.subTableName;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
          return getDataNodeSource(nodeData.dataNodeId);
        }
        break;

      case NodeType.DATA_QUERY_MULTIPLE:
        if (nodeData.dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
          return nodeData.mainTableName;
        }
        if (nodeData.dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
          return nodeData.subTableName;
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
  setConditionFields: (fields: ConditionField[]) => void,
  setValidationTypes: (types: EntityFieldValidationTypes[]) => void
) => {
  if (!dataSource) {
    return;
  }
  const fieldIds: string[] = [];
  const newConditionFields: ConditionField[] = [];

  const res = await getEntityFields({ entityId: dataSource });
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

export const getEntityFieldListV2 = async (
  dataSource: string,
  entityName: string,
  setConditionFields: (entityID: string, entityName: string, fields: ConditionField[]) => void,
  setValidationTypes: (types: EntityFieldValidationTypes[]) => void
) => {
  if (!dataSource) {
    return;
  }
  const fieldIds: string[] = [];
  const newConditionFields: ConditionField[] = [];

  const res = await getEntityFields({ entityId: dataSource });
  res.forEach((item: any) => {
    fieldIds.push(item.id);
    newConditionFields.push({
      label: item.displayName,
      value: item.id,
      fieldType: item.fieldType
    });
  });

  setConditionFields(dataSource, entityName, newConditionFields);
  if (fieldIds?.length) {
    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    setValidationTypes(newValidationTypes);
  }
};

// 判断是否在循环节点内
export const getIsLoop = (element: FlowNodeEntity): boolean => {
  if (element.flowNodeType === NodeType.LOOP) {
    return true;
  } else if (element.flowNodeType === 'root') {
    return false;
  } else if (element.parent) {
    return getIsLoop(element.parent);
  }
  return false;
};

// 判断节点是否包含循环节点
export const getHasLoop = (nodes: any[]): boolean => {
  let hasLoop = false;
  for (let ele of nodes) {
    if (ele.type === NodeType.LOOP) {
      hasLoop = true;
      return hasLoop;
    }
    if (ele.blocks?.length) {
      hasLoop = getHasLoop(ele.blocks);
    }
  }
  return hasLoop;
};

export const searchNodeById = (nodeId: string, nodes: any[]) => {
  let node: any = undefined;
  for (let item of nodes) {
    if (item.id === nodeId) {
      node = item;
      break;
    }
    if (item.blocks?.length) {
      node = searchNodeById(nodeId, item.blocks);
      if (node !== undefined) {
        break;
      }
    }
  }
  return node;
};

// 新增节点时 判断是否已有节点名称
export const hasNodeTitle = (title: string, nodes: any[]): boolean => {
  let hasTitle = false;
  for (let ele of nodes) {
    if (ele.data?.title === title) {
      hasTitle = true;
      return hasTitle;
    }
    if (ele.blocks?.length) {
      hasTitle = hasNodeTitle(title, ele.blocks);
    }
  }
  return hasTitle;
};

// 新增节点时 返回未被命名的节点名称
export const getNodeTitle = (title: string): string => {
  if (!title) {
    return '';
  }
  const nodes = triggerEditorSignal.nodes.value;
  const hasTitle = hasNodeTitle(title, nodes);
  if (hasTitle) {
    let i = 0;
    do {
      i++;
    } while (hasNodeTitle(`${title}_${i}`, nodes));
    return `${title}_${i}`;
  }
  return title;
};
