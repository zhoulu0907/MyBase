import {
  jsonToJsonSchema,
  schemaToFormData
} from '@/pages/CreateApp/pages/IntegratedManagement/pages/connector/action/create/util';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button } from '@arco-design/web-react';
import { getNodeForm, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { type ConditionField, DATA_SOURCE_TYPE, getEntityFields, getEntityFieldsWithChildren } from '@onebase/app';
import { NodeType } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { updateLoopOutputs } from '../../nodes/control/loop/output';
import { updateDataAddOutputs } from '../../nodes/data/data-add/output';
import { updateDataCalcOutputs } from '../../nodes/data/data-calc/output';
import { updateDataDeleteOutputs } from '../../nodes/data/data-delete/output';
import { updateDataQueryMultipleOutputs } from '../../nodes/data/data-query-multiple/output';
import { updateDataQueryOutputs } from '../../nodes/data/data-query/output';
import { updateDataUpdateOutputs } from '../../nodes/data/data-update/output';
import { updateModalOutputs } from '../../nodes/interaction/modal/output';
import { updateJavascriptOutputs } from '../../nodes/other/javascript/output';
import {
  clearDataOriginNodeId,
  getDataNodeSource,
  getEntityFieldList,
  searchNodeById,
  validateNodeForm
} from '../../nodes/utils';
import styles from './index.module.less';

export function FormFooter({ nodeInfo }: { nodeInfo: any }) {
  const { nodeId, nodeData, nodes, setNodeId, setNodeData } = triggerEditorSignal;
  // 流程数据文档 (固定布局), 存储流程的所有节点数据
  const ctx = useClientContext();

  const cancel = () => {
    // 取消 关闭弹窗
    setNodeId(undefined);
  };

  const saveNode = async () => {
    if (nodeId.value && nodeInfo?.props?.form) {
      // 通过指定 id 获取节点
      const node = ctx.document.getNode(nodeId.value);
      if (node) {
        // 节点表单
        const form = getNodeForm(node);
        validateNodeForm(form, nodeInfo.props.form, false);
      }
      // 获取表单数据
      const originalNodeData = nodeData.value[nodeId.value];
      const formInfo = nodeInfo.props.form.getFieldsValue();
      console.log('original nodeData: ', originalNodeData);
      console.log('formInfo', formInfo);

      let param = { ...nodeData.value[nodeId.value], ...formInfo };
      const curNode = searchNodeById(nodeId.value, nodes.value);

      if (originalNodeData) {
        //   针对有变更的节点，需要清空下游节点的依赖
        switch (curNode.type) {
          case NodeType.DATA_QUERY_MULTIPLE:
          case NodeType.DATA_QUERY: {
            if (
              originalNodeData.dataType != formInfo.dataType ||
              originalNodeData.mainEntityId != formInfo.mainEntityId ||
              originalNodeData.subEntityId != formInfo.subEntityId ||
              originalNodeData.dataNodeId != formInfo.dataNodeId
            ) {
              clearDataOriginNodeId(nodeId.value);
            }

            break;
          }

          case NodeType.DATA_ADD: {
            if (
              originalNodeData.addType != formInfo.addType ||
              originalNodeData.mainEntityId != formInfo.mainEntityId ||
              originalNodeData.subEntityId != formInfo.subEntityId ||
              originalNodeData.dataNodeId != formInfo.dataNodeId
            ) {
              clearDataOriginNodeId(nodeId.value);
            }
            break;
          }

          case NodeType.DATA_DELETE: {
            if (
              originalNodeData.dataType != formInfo.dataType ||
              originalNodeData.mainEntityId != formInfo.mainEntityId ||
              originalNodeData.subEntityId != formInfo.subEntityId
            ) {
              clearDataOriginNodeId(nodeId.value);
            }
            break;
          }

          case NodeType.DATA_UPDATE: {
            if (
              originalNodeData.updateType != formInfo.updateType ||
              originalNodeData.mainEntityId != formInfo.mainEntityId ||
              originalNodeData.subEntityId != formInfo.subEntityId ||
              originalNodeData.dataNodeId != formInfo.dataNodeId
            ) {
              clearDataOriginNodeId(nodeId.value);
            }
            break;
          }

          case NodeType.DATA_CALC: {
            if (originalNodeData.calType != formInfo.calType) {
              clearDataOriginNodeId(nodeId.value);
            }
            break;
          }

          case NodeType.JavaScript: {
            const noChange = formInfo.inputParameterFields.every((item: any) => {
              return originalNodeData.inputParameterFields?.find(
                (ele: any) => ele.name === item.name && ele.type === item.type
              );
            });

            if (
              !noChange ||
              (originalNodeData.inputParameterFields &&
                formInfo.inputParameterFields.length !== originalNodeData.inputParameterFields.length)
            ) {
              clearDataOriginNodeId(nodeId.value);
            }
            break;
          }

          // TODO(chenyongqiang): 补充其他节点类型

          default:
            break;
        }
      }

      // 更新outputs
      switch (curNode.type) {
        case NodeType.LOOP:
          const originDataSource = getDataNodeSource(formInfo.dataNodeId);
          const handleSetConditionFields = (conditionFields: ConditionField[]) => {
            updateLoopOutputs(curNode.id, conditionFields);
          };
          getEntityFieldList(originDataSource, handleSetConditionFields, () => {});
          break;
        case NodeType.DATA_ADD:
          const mainDataSource =
            formInfo.addType === DATA_SOURCE_TYPE.FORM ? formInfo.mainEntityId : formInfo.subEntityId;
          if (mainDataSource) {
            const res = await getEntityFields({ entityId: mainDataSource });
            const newConditionFields: ConditionField[] = (res || []).map((item: any) => {
              return {
                label: item.displayName,
                value: item.id,
                fieldType: item.fieldType
              };
            });
            updateDataAddOutputs(curNode.id, newConditionFields);
          }
          break;
        case NodeType.DATA_CALC:
          const dataCalcFields: ConditionField[] = formInfo.calRules
            .filter((item: any) => item && item.field && item.value && item.operatorType)
            .map((item: any) => {
              return {
                label: item.field,
                value: item.field,
                fieldType: item.operatorType
              };
            });
          updateDataCalcOutputs(curNode.id, dataCalcFields);
          break;
        case NodeType.DATA_DELETE:
          updateDataDeleteOutputs(curNode.id);
          break;
        case NodeType.DATA_QUERY:
          const dataQueryRes = await getEntityFieldsWithChildren(formInfo.mainEntityId);
          const dataQueryConditionFields = (): ConditionField[] => {
            if (formInfo.dataType === DATA_SOURCE_TYPE.FORM) {
              return (dataQueryRes?.parentFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            if (formInfo.dataType === DATA_SOURCE_TYPE.SUBFORM) {
              const subEntity = dataQueryRes?.childEntities?.find(
                (item: any) => item.childEntityId === formInfo.subEntityId
              );
              return (subEntity?.childFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            if (formInfo.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
              return (dataQueryRes?.parentFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            return [];
          };
          const dataQueryFields = dataQueryConditionFields();
          updateDataQueryOutputs(curNode.id, dataQueryFields);
          break;
        case NodeType.DATA_QUERY_MULTIPLE:
          const dataQueryMultipleRes = await getEntityFieldsWithChildren(formInfo.mainEntityId);
          const dataQueryMultipleConditionFields = (): ConditionField[] => {
            if (formInfo.dataType === DATA_SOURCE_TYPE.FORM) {
              return (dataQueryMultipleRes?.parentFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            if (formInfo.dataType === DATA_SOURCE_TYPE.SUBFORM) {
              const subEntity = dataQueryMultipleRes?.childEntities?.find(
                (item: any) => item.childEntityId === formInfo.subEntityId
              );
              return (subEntity?.childFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            if (formInfo.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
              return (dataQueryMultipleRes?.parentFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            return [];
          };
          const dataQueryMultipleFields = dataQueryMultipleConditionFields();
          updateDataQueryMultipleOutputs(curNode.id, dataQueryMultipleFields);
          break;
        case NodeType.DATA_UPDATE:
          const dataUpdateRes = await getEntityFieldsWithChildren(formInfo.mainEntityId);
          const dataUpdateConditionFields = (): ConditionField[] => {
            if (formInfo.dataType === DATA_SOURCE_TYPE.FORM) {
              return (dataUpdateRes?.parentFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            if (formInfo.dataType === DATA_SOURCE_TYPE.SUBFORM) {
              const subEntity = dataUpdateRes?.childEntities?.find(
                (item: any) => item.childEntityId === formInfo.subEntityId
              );
              return (subEntity?.childFields || []).map((item: any) => {
                return {
                  value: item.fieldId,
                  label: item.displayName,
                  fieldType: item.fieldType
                };
              });
            }
            return [];
          };
          const dataUpdateFields = dataUpdateConditionFields();
          updateDataUpdateOutputs(curNode.id, dataUpdateFields);
          break;
        case NodeType.MODAL:
          const modalFields: ConditionField[] = (formInfo.fields || [])
            .filter((item: any) => item && item.fieldName && item.fieldType)
            .map((item: any) => {
              return {
                label: item.fieldName,
                value: item.fieldName,
                fieldType: item.fieldType
              };
            });
          updateModalOutputs(curNode.id, modalFields);
          break;
        case NodeType.JavaScript:
          const outputParameter = JSON.parse(formInfo.outputParameter || '{}');
          const schema = jsonToJsonSchema(formInfo.outputParameter || '{}');
          const newFormData = schemaToFormData(schema, outputParameter);
          const jsFields: ConditionField[] = newFormData.map((item: any) => {
            // ? 类型处理
            return {
              label: item.name,
              value: item.name,
              fieldType: item.type === 'number' ? ENTITY_FIELD_TYPE.NUMBER.VALUE : ENTITY_FIELD_TYPE.TEXT.VALUE
            };
          });
          updateJavascriptOutputs(curNode.id, jsFields);
          break;
        default:
          break;
      }

      if (curNode && curNode.type === NodeType.MODAL) {
        const fields = nodeInfo.props.form.getFieldValue('fields');
        param = { ...param, fields };
      }
      if (curNode && curNode.type === NodeType.NAVIGATE) {
        const paramFields = nodeInfo.props.form.getFieldValue('paramFields');
        param = { ...param, paramFields };
      }
      // 过滤掉数据为空的数组  一维数组
      const keys = Object.keys(param);
      for (let key of keys) {
        // 数组
        if (Array.isArray(param[key])) {
          // 过滤掉数据全为空的数据
          param[key] = param[key].filter((ele) => {
            if (Object.prototype.toString.call(ele).slice(8, -1) === 'Object') {
              const itemKeys = Object.keys(ele);
              for (let itemKey of itemKeys) {
                if (ele[itemKey]) {
                  return true;
                }
              }
              return false;
            }
            return true;
          });
        }
      }
      setNodeData(nodeId.value, param);
    }

    setNodeId(undefined);
  };

  return (
    <div className={styles.formFooter}>
      <Button type="outline" style={{ marginRight: '20px' }} onClick={cancel}>
        取消
      </Button>
      <Button type="primary" onClick={saveNode}>
        确定
      </Button>
    </div>
  );
}
