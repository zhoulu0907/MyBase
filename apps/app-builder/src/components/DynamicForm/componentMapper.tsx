import {
  Button,
  Cascader,
  Checkbox,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Radio,
  Rate,
  Select,
  Slider,
  Space,
  Switch,
  TimePicker,
  TreeSelect,
  Upload
} from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, mapProps, RecursionField, useField, useFieldSchema } from '@formily/react';
import React from 'react';
import { AuthSettingsCard, KeyValueList } from './AuthComponents';
import { TokenAuthPanel } from './TokenAuthPanel';

// 通用的 mapProps 辅助函数
type MapPropsOptions = {
  defaultValue?: any;
  valueKey?: string; // 默认 'value'，可以是 'checked', 'fileList' 等
  transformValue?: (value: any) => any; // 转换 value 的函数
};

const createFormilyComponent = <T extends React.ComponentType<any>>(Component: T, options: MapPropsOptions = {}) => {
  const { defaultValue = undefined, valueKey = 'value', transformValue } = options;

  return connect(
    Component,
    mapProps((props: any) => {
      const { onChange, ...restProps } = props;
      const value = transformValue ? transformValue(props.value) : (props.value ?? defaultValue);

      return {
        ...restProps,
        [valueKey]: value,
        onChange: (newValue: any) => {
          onChange?.(newValue);
        }
      };
    })
  );
};

// 使用 connect 包装 Arco Design 组件，使其兼容 Formily
// Formily 会自动传递 value 和 onChange，我们只需要确保它们正确映射到 Arco 组件
const FormilyInput = createFormilyComponent(Input, { defaultValue: '' });
const FormilyInputPassword = createFormilyComponent(Input.Password, { defaultValue: '' });
const FormilyInputNumber = createFormilyComponent(InputNumber);
const FormilyTextArea = createFormilyComponent(Input.TextArea, { defaultValue: '' });

// Select 组件需要特殊处理 enum 选项
const SelectInner: React.FC<any> = (props: any) => {
  const schema = useFieldSchema();
  const { value, onChange, options, ...restProps } = props;

  // 从 schema 中获取 enum，并转换为 options 格式
  let selectOptions = options;
  if (!selectOptions && schema?.enum) {
    const enumValues = schema.enum;
    // 判断 enum 是简单数组还是对象数组
    if (Array.isArray(enumValues) && enumValues.length > 0) {
      if (typeof enumValues[0] === 'object' && enumValues[0] !== null && 'value' in enumValues[0]) {
        // 对象数组格式：[{label: 'xxx', value: 'xxx'}]
        selectOptions = enumValues.map((item: any) => ({
          label: item.label || item.name || String(item.value || ''),
          value: item.value
        }));
      } else {
        // 简单数组格式：['a', 'b', 'c']
        selectOptions = enumValues.map((item: any) => ({
          label: String(item),
          value: item
        }));
      }
    }
  }

  return <Select value={value} onChange={onChange} options={selectOptions} {...restProps} />;
};

const FormilySelect = connect(SelectInner, mapProps());
const FormilyDatePicker = createFormilyComponent(DatePicker);
const FormilyTimePicker = createFormilyComponent(TimePicker);
const FormilySwitch = createFormilyComponent(Switch, {
  valueKey: 'checked',
  defaultValue: false
});

// Radio.Group 组件需要特殊处理 enum 选项
const RadioInner: React.FC<any> = (props: any) => {
  const schema = useFieldSchema();
  const { value, onChange, options, ...restProps } = props;

  // 从 schema 中获取 enum，并转换为 Radio 选项
  let radioOptions = options;
  if (!radioOptions && schema?.enum) {
    const enumValues = schema.enum;
    // 判断 enum 是简单数组还是对象数组
    if (Array.isArray(enumValues) && enumValues.length > 0) {
      if (typeof enumValues[0] === 'object' && enumValues[0] !== null && 'value' in enumValues[0]) {
        // 对象数组格式：[{label: 'xxx', value: 'xxx'}]
        radioOptions = enumValues.map((item: any) => ({
          label: item.label || item.name || String(item.value || ''),
          value: item.value
        }));
      } else {
        // 简单数组格式：['a', 'b', 'c']
        radioOptions = enumValues.map((item: any) => ({
          label: String(item),
          value: item
        }));
      }
    }
  }

  // 使用 Radio.Group 作为变量，避免 JSX 中直接使用 <Radio.Group>
  const RadioGroup = Radio.Group;

  // 如果有选项，使用子组件方式渲染
  if (radioOptions && radioOptions.length > 0) {
    return (
      <RadioGroup value={value} onChange={onChange} {...restProps}>
        {radioOptions.map((option: any, index: number) => (
          <Radio key={index} value={option.value}>
            {option.label}
          </Radio>
        ))}
      </RadioGroup>
    );
  }

  return <RadioGroup value={value} onChange={onChange} {...restProps} />;
};

const FormilyRadio = connect(RadioInner, mapProps());
const FormilyCheckbox = createFormilyComponent(Checkbox.Group, { defaultValue: [] });
const FormilyUpload = createFormilyComponent(Upload, {
  valueKey: 'fileList',
  defaultValue: []
});
const FormilySlider = createFormilyComponent(Slider, { defaultValue: 0 });
const FormilyRate = createFormilyComponent(Rate, { defaultValue: 0 });
const FormilyCascader = createFormilyComponent(Cascader);
const FormilyTreeSelect = createFormilyComponent(TreeSelect);

// ArrayItems 组件 - 支持动态增减数组项
const ArrayItemsInner: React.FC<any> = () => {
  const fieldRaw = useField();
  const field = fieldRaw as any; // 类型断言，确保可以访问 value 和 setValue
  const schema = useFieldSchema();
  const items = (field.value || []) as any[];

  // 获取 items schema，如果是数组则取第一个，确保是单个 schema 对象
  let itemSchema: any = {};
  if (schema.items) {
    if (Array.isArray(schema.items)) {
      itemSchema = schema.items[0] || {};
    } else {
      itemSchema = schema.items;
    }
  }

  return (
    <div>
      <Space direction="vertical" style={{ width: '100%' }} size="small">
        {items.map((_: any, index: number) => {
          const fieldAddress = field.address?.toString() || '' || String(index);
          return (
            <div
              key={`${fieldAddress}-${index}`}
              style={{ width: '100%', display: 'flex', alignItems: 'flex-start', gap: 8 }}
            >
              <div style={{ flex: 1 }}>
                <RecursionField schema={itemSchema} name={index} />
              </div>
              <Button
                type="text"
                status="danger"
                icon={<IconDelete />}
                onClick={() => {
                  const currentValue = (field.value || []) as any[];
                  if (Array.isArray(currentValue)) {
                    const newValue = [...currentValue];
                    newValue.splice(index, 1);
                    if (field.setValue) {
                      field.setValue(newValue);
                    }
                  }
                }}
              />
            </div>
          );
        })}
        <Button
          type="dashed"
          icon={<IconPlus />}
          onClick={() => {
            const currentValue = (field.value || []) as any[];
            // 根据 schema 判断初始值类型
            const itemSchema = Array.isArray(schema.items) ? schema.items[0] : schema.items;
            let initialValue: any = '';
            if (itemSchema?.type === 'object' && itemSchema?.properties) {
              // 如果是对象类型，创建空对象
              initialValue = {};
            }
            if (field.setValue) {
              field.setValue([...currentValue, initialValue]);
            }
          }}
          style={{ width: '100%' }}
        >
          添加
        </Button>
      </Space>
    </div>
  );
};

export const FormilyArrayItems = connect(ArrayItemsInner, mapProps());

// FormItem 装饰器
const FormItemInner: React.FC<any> = (props: any) => {
  const schema = useFieldSchema();
  const field = useField();
  const { errors, required, children, ...rest } = props;

  // 从多个来源获取 title：props.title > schema.title > field.title > field.componentProps.title
  const finalTitle =
    props.title || schema?.title || field?.title || field?.componentProps?.title || field?.decoratorProps?.title;

  return (
    <Form.Item
      label={finalTitle || undefined}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      validateStatus={errors?.length ? 'error' : undefined}
      help={errors?.length ? errors[0].message : undefined}
      required={required}
      {...rest}
    >
      {children}
    </Form.Item>
  );
};

export const FormilyFormItem = connect(FormItemInner, mapProps());

// 组件映射表
export const componentMap: Record<string, React.ComponentType<any>> = {
  Input: FormilyInput,
  'Input.Password': FormilyInputPassword,
  InputNumber: FormilyInputNumber,
  TextArea: FormilyTextArea,
  Select: FormilySelect,
  DatePicker: FormilyDatePicker,
  TimePicker: FormilyTimePicker,
  Switch: FormilySwitch,
  Radio: FormilyRadio,
  'Radio.Group': FormilyRadio,
  Checkbox: FormilyCheckbox,
  Upload: FormilyUpload,
  Slider: FormilySlider,
  Rate: FormilyRate,
  Cascader: FormilyCascader,
  TreeSelect: FormilyTreeSelect,
  ArrayItems: FormilyArrayItems,
  AuthSettingsCard: AuthSettingsCard,
  KeyValueList: KeyValueList,
  TokenAuthPanel: TokenAuthPanel
};
