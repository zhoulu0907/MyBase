import { FormulaEditor } from '@/components/FormulaEditor';
import {
  jsonToJsonSchema,
  schemaToFormData
} from '@/pages/CreateApp/pages/IntegratedManagement/pages/connector/action/create/util';
import type { FormInstance } from '@arco-design/web-react';
import { Button, Form, Grid, Input, InputNumber, Select, Switch, TreeSelect } from '@arco-design/web-react';
import { IconLaunch } from '@arco-design/web-react/icon';
import { FieldType } from '@onebase/app';
import React, { useEffect, useState } from 'react';

const Row = Grid.Row;
const Col = Grid.Col;

// 字段类型标签映射
const FIELD_TYPE_LABELS: Record<string, string> = {
  string: '字符串',
  number: '数字',
  boolean: '布尔值',
  object: '对象',
  array: '数组',
  null: '空值'
};

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
}

export const InputParameterFieldItem: React.FC<InputParameterFieldItemProps> = ({
  field,
  form,
  onOpenFormulaEditor,
  renderStaticValueComponent
}) => {
  const fieldName = field.field;
  const fieldType = Form.useWatch(`${fieldName}.type`, form) || 'string';
  const operatorType = Form.useWatch(`${fieldName}.operatorType`, form);

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
          <Input readOnly value={FIELD_TYPE_LABELS[fieldType] || fieldType} style={{ width: '190px' }} />
        </Form.Item>
      </Col>

      <Col span={5}>
        <Form.Item field={`${fieldName}.operatorType`} style={{ marginBottom: 0 }}>
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
              treeData={[]}
              triggerElement={(params) => {
                return (
                  <Input readOnly value={params.value || ''} placeholder="请选择变量" style={{ width: '210px' }} />
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
  inputParameter: string;
  form: FormInstance;
}

/**
 * 输入参数表单组件
 * 用于渲染和编辑 inputParameter 的图形化表单
 */
export const InputParameterForm: React.FC<InputParameterFormProps> = ({ inputParameter, form }) => {
  const [formulaVisible, setFormulaVisible] = useState(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

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
    if (inputParameter && inputParameter !== '{}') {
      try {
        const inputParameterObj = JSON.parse(inputParameter);
        const schema = jsonToJsonSchema(inputParameter);
        const newFormData = schemaToFormData(schema, inputParameterObj);

        // 获取已保存的表单数据（可能来自 initialValues）
        const savedFormData = form.getFieldValue('inputParameterFields') || [];

        // 合并已保存的数据和新生成的数据
        const mergedFormData = mergeFormData(savedFormData, newFormData);

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

  // 验证 inputParameter 格式
  if (inputParameter && inputParameter !== '{}') {
    try {
      JSON.parse(inputParameter);
    } catch (e) {
      return <div>参数格式错误</div>;
    }
  }

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
