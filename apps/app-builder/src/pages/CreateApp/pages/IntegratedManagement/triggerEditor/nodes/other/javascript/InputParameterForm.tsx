import { FormulaEditor } from '@/components/FormulaEditor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import type { FormInstance } from '@arco-design/web-react';
import { Button, Form, Grid, Input, InputNumber, Select, Switch, TreeSelect } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { IconLaunch } from '@arco-design/web-react/icon';
import { FieldType, type ConditionField } from '@onebase/app';
import { NodeType } from '@onebase/common';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { getPrecedingNodes } from '../../../nodes/utils';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';

const Row = Grid.Row;
const Col = Grid.Col;

// 值类型选项
const valueTypeOptions = [
  { label: '静态值', value: FieldType.VALUE },
  { label: '变量', value: FieldType.VARIABLES },
  { label: '公式', value: FieldType.FORMULA }
];

// 输入参数字段项组件
interface InputParameterFieldItemProps {
  field: any;
  form: FormInstance;
  onOpenFormulaEditor: (fieldKey: string) => void;
  renderStaticValueComponent: (fieldName: string, fieldType: string) => React.ReactNode;
  getVariableOptions: () => TreeSelectDataType[];
  showTriggerElement: (params: any, options: TreeSelectDataType[]) => string;
}

export const InputParameterFieldItem: React.FC<InputParameterFieldItemProps> = ({
  field,
  form,
  onOpenFormulaEditor,
  renderStaticValueComponent,
  getVariableOptions,
  showTriggerElement
}) => {
  const fieldName = field.field;
  const fieldType = Form.useWatch(`${fieldName}.type`, form) || ENTITY_FIELD_TYPE.TEXT.VALUE;
  const operatorType = Form.useWatch(`${fieldName}.operatorType`, form);
  const variableOptions = getVariableOptions();

  // 字段类型下拉
  const typeOptions = Object.keys(ENTITY_FIELD_TYPE).map((ele) => {
    const item = ENTITY_FIELD_TYPE[ele as keyof typeof ENTITY_FIELD_TYPE];
    return {
      label: item.LABEL,
      value: item.VALUE
    };
  });

  return (
    <Row gutter={8} align="center" style={{ marginBottom: 12 }}>
      {/* 第一列：字段名称（不可编辑） */}
      <Col span={6}>
        <Form.Item field={`${fieldName}.name`} style={{ marginBottom: 0 }}>
          <Input readOnly placeholder="字段名称" style={{ width: '190px' }} />
        </Form.Item>
      </Col>

      {/* 第二列：字段类型（不可编辑） */}
      <Col span={6}>
        <Form.Item field={`${fieldName}.type`} style={{ marginBottom: 0 }}>
          <Select disabled options={typeOptions} style={{ width: '190px' }}></Select>
        </Form.Item>
      </Col>

      <Col span={5}>
        <Form.Item field={`${fieldName}.operatorType`} style={{ marginBottom: 0 }} initialValue={FieldType.VALUE}>
          <Select
            placeholder="选择类型"
            options={valueTypeOptions}
            onChange={() => {
              form.setFieldValue(`${fieldName}.value`, undefined);
            }}
            style={{ width: '158px' }}
          />
        </Form.Item>
      </Col>

      {/* 第四列：根据第三列动态展示 */}
      <Col span={7}>
        {!operatorType && (
          <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
            <Input placeholder="请先选择类型" disabled style={{ width: '210px' }} />
          </Form.Item>
        )}
        {operatorType === FieldType.VALUE && renderStaticValueComponent(`${fieldName}.value`, fieldType)}
        {operatorType === FieldType.VARIABLES && (
          <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
            <TreeSelect
              placeholder="请选择变量"
              treeData={variableOptions}
              triggerElement={(params) => {
                const displayValue = showTriggerElement(params, variableOptions);
                return (
                  <Input readOnly value={displayValue || ''} placeholder="请选择变量" style={{ width: '210px' }} />
                );
              }}
            />
          </Form.Item>
        )}
        {operatorType === FieldType.FORMULA && (
          <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
            <Button
              onClick={() => onOpenFormulaEditor(`${fieldName}.value`)}
              long
              style={{ width: '210px' }}
              type={form.getFieldValue(`${fieldName}.value`) ? 'secondary' : 'outline'}
            >
              {form.getFieldValue(`${fieldName}.value`) ? '已设置公式' : 'ƒx 编辑公式'}
              {form.getFieldValue(`${fieldName}.value`) && <IconLaunch style={{ marginLeft: 4 }} />}
            </Button>
          </Form.Item>
        )}
      </Col>
    </Row>
  );
};

// 渲染静态值输入组件
const renderStaticValueComponent = (fieldName: string, fieldType: string) => {
  switch (fieldType) {
    case 'number':
      return (
        <Form.Item field={fieldName} style={{ marginBottom: 0 }}>
          <InputNumber placeholder="请输入静态值" style={{ width: '210px' }} />
        </Form.Item>
      );
    case 'boolean':
      return (
        <Form.Item field={fieldName} triggerPropName="checked" style={{ marginBottom: 0 }}>
          <Switch checkedText="true" uncheckedText="false" style={{ width: '210px' }} />
        </Form.Item>
      );
    // case 'object':
    // case 'array':
    //   return (
    //     <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>
    //       {fieldType === 'object' ? '对象类型' : '数组类型'}
    //     </div>
    //   );
    // case 'null':
    //   return <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>null</div>;
    default:
      return (
        <Form.Item field={fieldName} style={{ marginBottom: 0 }}>
          <Input placeholder="请输入静态值" style={{ width: '210px' }} />
        </Form.Item>
      );
  }
};

export interface InputParameterFormProps {
  inputParameter: any[];
  form: FormInstance;
  nodeId: string;
}

/**
 * 输入参数表单组件
 * 用于渲染和编辑 inputParameter 的图形化表单
 */
export const InputParameterForm: React.FC<InputParameterFormProps> = ({ inputParameter, form, nodeId }) => {
  const [formulaVisible, setFormulaVisible] = useState(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  // 提取公共的字段处理逻辑
  const processConditionFields = (
    nodeId: string,
    conditionFields: ConditionField[],
    children: TreeSelectDataType[]
  ): void => {
    if (!conditionFields) return;

    conditionFields.forEach((field: ConditionField) => {
      // 对于 inputParameter 中的字段，不进行类型过滤，允许所有类型的变量
      children.push({
        key: `${nodeId}.${field.value}`,
        title: field.label
      });
    });
  };

  // 使用 useMemo 缓存节点类型集合，避免重复创建
  const nodesWithConditionFields = useMemo(
    () =>
      new Set([
        NodeType.START_FORM,
        NodeType.START_ENTITY,
        NodeType.START_DATE_FIELD,
        NodeType.DATA_ADD,
        NodeType.DATA_QUERY,
        NodeType.DATA_QUERY_MULTIPLE,
        NodeType.DATA_UPDATE,
        NodeType.DATA_CALC,
        NodeType.MODAL
      ]),
    []
  );

  // 使用 useCallback 缓存函数，避免不必要的重新创建
  const getVariableOptions = useCallback((): TreeSelectDataType[] => {
    if (!nodeId) {
      return [];
    }

    const nodeTypes = [
      NodeType.DATA_QUERY,
      NodeType.DATA_QUERY_MULTIPLE,
      NodeType.DATA_UPDATE,
      NodeType.DATA_ADD,
      NodeType.DATA_CALC,
      NodeType.START_FORM,
      NodeType.START_ENTITY,
      NodeType.START_TIME,
      NodeType.START_DATE_FIELD,
      NodeType.START_API,
      NodeType.START_BPM,
      NodeType.LOOP,
      NodeType.MODAL
    ];

    const nodes = getPrecedingNodes(nodeId, triggerEditorSignal.nodes.value, nodeTypes);
    const options: TreeSelectDataType[] = [];

    nodes.forEach((node: any) => {
      const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(node.id);

      // 只处理有 conditionFields 的节点类型
      if (!node.type || !nodesWithConditionFields.has(node.type as NodeType)) {
        return;
      }

      const treeNode: TreeSelectDataType = {
        key: node.id,
        title: node.data?.title,
        disabled: true,
        children: []
      };

      // 统一处理 conditionFields
      if (nodeOutput.conditionFields && treeNode.children) {
        processConditionFields(node.id, nodeOutput.conditionFields, treeNode.children);
      }

      // 只有当有子字段时才添加到选项中
      if (treeNode.children && treeNode.children.length > 0) {
        options.push(treeNode);
      }
    });

    return options;
  }, [nodeId, nodesWithConditionFields]);

  const showTriggerElement = useCallback((params: any, options: TreeSelectDataType[]): string => {
    if (params.value) {
      const parentId = params.value.split('.')[0];
      const parentNode = options.find((item) => item.key == parentId);

      const childrenName = parentNode?.children?.find((item) => item.key == params.value)?.title;
      return `${parentNode?.title} - ${childrenName}`;
    }

    return '';
  }, []);

  // 合并已保存的数据和新生成的表单数据
  const mergeFormData = (savedData: any[], newFormData: any[]): any[] => {
    if (!savedData || savedData.length === 0) {
      return newFormData;
    }

    // 创建一个以字段名为 key 的映射，方便查找已保存的数据
    const savedDataMap = new Map<string, any>();
    savedData.forEach((item) => {
      if (item.name) {
        savedDataMap.set(item.name, item);
      }
    });

    // 合并数据：保留已保存的 operatorType 和 value
    return newFormData.map((newItem) => {
      const savedItem = savedDataMap.get(newItem.name);
      if (savedItem) {
        return {
          ...newItem,
          operatorType: savedItem.operatorType,
          value: savedItem.value
        };
      }
      return newItem;
    });
  };

  // 初始化表单数据：当 inputParameter 变化时，合并已保存的数据
  useEffect(() => {
    if (inputParameter?.length) {
      try {
        // 获取已保存的表单数据（可能来自 initialValues）
        const savedFormData = form.getFieldValue('inputParameterFields') || [];

        // 合并已保存的数据和新生成的数据
        const mergedFormData = mergeFormData(savedFormData, inputParameter);

        if (mergedFormData.length > 0) {
          // 只有当合并后的数据与当前数据不同时才更新，避免不必要的更新
          const currentFormData = form.getFieldValue('inputParameterFields') || [];
          if (JSON.stringify(mergedFormData) !== JSON.stringify(currentFormData)) {
            form.setFieldValue('inputParameterFields', mergedFormData);
          }
        }
      } catch (e) {
        console.error('解析 inputParameter 失败:', e);
      }
    }
  }, [inputParameter, form]);

  const handleFormulaConfirm = (formulaData: any, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    form.setFieldValue(formulaFieldKey, {
      formulaData: formulaData,
      formula: formattedFormula,
      parameters: params
    });
    setFormulaData('');
    setFormulaFieldKey('');
  };

  const openFormulaEditor = (fieldKey: string) => {
    setFormulaVisible(true);
    const currentValue = form.getFieldValue(fieldKey);
    setFormulaData(currentValue?.formulaData || '');
    setFormulaFieldKey(fieldKey);
  };

  return (
    <div style={{ padding: '16px 0', width: '100%' }}>
      <Form form={form}>
        <Form.List field="inputParameterFields">
          {(fields: any[]) => {
            return (
              <>
                {fields.map((field) => (
                  <InputParameterFieldItem
                    key={field.key}
                    field={field}
                    form={form}
                    onOpenFormulaEditor={openFormulaEditor}
                    renderStaticValueComponent={renderStaticValueComponent}
                    getVariableOptions={getVariableOptions}
                    showTriggerElement={showTriggerElement}
                  />
                ))}
              </>
            );
          }}
        </Form.List>
      </Form>

      <FormulaEditor
        visible={formulaVisible}
        onCancel={() => {
          setFormulaVisible(false);
          setFormulaData('');
          setFormulaFieldKey('');
        }}
        onConfirm={handleFormulaConfirm}
        initialFormula={formulaData}
      />
    </div>
  );
};
