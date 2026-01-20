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
    Upload,
} from "@arco-design/web-react";
import { IconDelete, IconPlus } from "@arco-design/web-react/icon";
import { connect, mapProps, RecursionField, useField, useFieldSchema } from "@formily/react";
import React from "react";

// 通用的 mapProps 辅助函数
type MapPropsOptions = {
  defaultValue?: any;
  valueKey?: string; // 默认 'value'，可以是 'checked', 'fileList' 等
  transformValue?: (value: any) => any; // 转换 value 的函数
};

const createFormilyComponent = <T extends React.ComponentType<any>>(
  Component: T,
  options: MapPropsOptions = {}
) => {
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
        },
      };
    })
  );
};

// 使用 connect 包装 Arco Design 组件，使其兼容 Formily
// Formily 会自动传递 value 和 onChange，我们只需要确保它们正确映射到 Arco 组件
const FormilyInput = createFormilyComponent(Input, { defaultValue: "" });
const FormilyInputNumber = createFormilyComponent(InputNumber);
const FormilyTextArea = createFormilyComponent(Input.TextArea, { defaultValue: "" });
const FormilySelect = createFormilyComponent(Select);
const FormilyDatePicker = createFormilyComponent(DatePicker);
const FormilyTimePicker = createFormilyComponent(TimePicker);
const FormilySwitch = createFormilyComponent(Switch, {
  valueKey: 'checked',
  defaultValue: false
});
const FormilyRadio = createFormilyComponent(Radio.Group);
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
  const items = ((field.value || []) as any[]);

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
          const fieldAddress = (field.address?.toString() || '') || String(index);
          return (
            <div key={`${fieldAddress}-${index}`} style={{ width: '100%', display: 'flex', alignItems: 'flex-start', gap: 8 }}>
              <div style={{ flex: 1}}>
                <RecursionField
                  schema={itemSchema}
                  name={index}
                />
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

export const FormilyArrayItems = connect(
  ArrayItemsInner,
  mapProps()
);

// FormItem 装饰器
export const FormilyFormItem = connect(
  Form.Item,
  mapProps((props: any) => {
    const { errors, title, required, children, ...rest } = props;
    return {
      label: title,
      validateStatus: errors?.length ? "error" : undefined,
      help: errors?.length ? errors[0].message : undefined,
      required,
      children,
      ...rest,
    };
  })
);

// 组件映射表
export const componentMap: Record<string, React.ComponentType<any>> = {
  Input: FormilyInput,
  InputNumber: FormilyInputNumber,
  TextArea: FormilyTextArea,
  Select: FormilySelect,
  DatePicker: FormilyDatePicker,
  TimePicker: FormilyTimePicker,
  Switch: FormilySwitch,
  Radio: FormilyRadio,
  Checkbox: FormilyCheckbox,
  Upload: FormilyUpload,
  Slider: FormilySlider,
  Rate: FormilyRate,
  Cascader: FormilyCascader,
  TreeSelect: FormilyTreeSelect,
  ArrayItems: FormilyArrayItems,
};
