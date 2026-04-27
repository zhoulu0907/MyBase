import ResizableTable from '@/components/ResizableTable';
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
  Steps,
  Switch,
  Table,
  Tabs,
  TimePicker,
  TreeSelect,
  Upload
} from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { json } from '@codemirror/lang-json';
import { Decoration, EditorView, MatchDecorator, ViewPlugin } from '@codemirror/view';
import { connect, mapProps, observer, RecursionField, useField, useFieldSchema, useForm } from '@formily/react';
import ReactCodeMirror from '@uiw/react-codemirror';
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
    mapProps((props: any, field: any) => {
      const { onChange, dataSource, ...restProps } = props;
      const value = transformValue ? transformValue(props.value) : (props.value ?? defaultValue);

      const finalProps = {
        ...restProps,
        [valueKey]: value,
        onChange: (newValue: any) => {
          onChange?.(newValue);
        }
      };

      // 优先使用 props 中的 dataSource，其次使用 field.dataSource (enum)
      const options = dataSource || field?.dataSource;
      if (options && !finalProps.options) {
        finalProps.options = options;
      }

      return finalProps;
    })
  );
};

// 使用 connect 包装 Arco Design 组件，使其兼容 Formily
// Formily 会自动传递 value 和 onChange，我们只需要确保它们正确映射到 Arco 组件
const FormilyInput = createFormilyComponent(Input, { defaultValue: '' });
const FormilyPassword = createFormilyComponent(Input.Password, { defaultValue: '' });
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
const FormilyRadio = createFormilyComponent(Radio.Group);
// 兼容 x-component: "Radio.Group" 的写法
(FormilyRadio as any).Group = FormilyRadio;

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

const FormilyCheckbox = createFormilyComponent(Checkbox.Group, { defaultValue: [] });
const FormilyUpload = createFormilyComponent(Upload, {
  valueKey: 'fileList',
  defaultValue: []
});
const FormilySlider = createFormilyComponent(Slider, { defaultValue: 0 });
const FormilyRate = createFormilyComponent(Rate, { defaultValue: 0 });
const FormilyCascader = createFormilyComponent(Cascader);
const FormilyTreeSelect = createFormilyComponent(TreeSelect);

const StepNav: React.FC<any> = observer((props) => {
  const form = useForm();
  const stepsProp = props.steps as { key: string; title: string }[] | undefined;
  const steps = stepsProp || [
    { key: 'basic', title: '基础信息' },
    { key: 'input', title: '入参配置' },
    { key: 'output', title: '出参配置' },
    { key: 'test', title: '接口调试' }
  ];

  const currentKey = (form.values as any)?.currentStep || steps[0].key;
  const currentIndex = Math.max(
    0,
    steps.findIndex((item) => item.key === currentKey)
  );

  React.useEffect(() => {
    if (!(form.values as any)?.currentStep) {
      form.setValuesIn('currentStep', steps[0].key);
    }
  }, [form, steps]);

  return (
    <Steps
      current={currentIndex + 1}
      onChange={(index) => {
        const item = steps[index - 1];
        if (item) {
          form.setValuesIn('currentStep', item.key);
        }
      }}
    >
      {steps.map((item) => (
        <Steps.Step key={item.key} title={item.title} />
      ))}
    </Steps>
  );
});

const ParamsTableInner: React.FC<any> = observer(() => {
  const fieldRaw = useField();
  const field = fieldRaw as any;
  const data: any[] = Array.isArray(field.value) ? field.value : [];

  const setRow = (index: number, patch: Record<string, any>) => {
    const next = [...data];
    next[index] = { ...(next[index] || {}), ...patch };
    field.setValue(next);
  };

  const removeRow = (index: number) => {
    const next = [...data];
    next.splice(index, 1);
    field.setValue(next);
  };

  const columns = [
    {
      title: "参数名",
      dataIndex: "key",
      width: 150,
      render: (value: any, _record: any, index: number) => (
        <Input
          value={value}
          onChange={(v) => setRow(index, { key: v })}
          placeholder="Key"
        />
      ),
    },
    {
      title: "值类型",
      dataIndex: "paramType",
      width: 100,
      render: (value: any, _record: any, index: number) => (
        <Select
          value={value || "fixed"}
          onChange={(v) => setRow(index, { paramType: v })}
        >
          <Select.Option value="fixed">固定值</Select.Option>
          <Select.Option value="variable">变量</Select.Option>
        </Select>
      )
    },
    {
      title: "数据类型",
      dataIndex: "dataType",
      width: 100,
      render: (value: any, record: any, index: number) => (
        <Select
          value={value || "string"}
          onChange={(v) => setRow(index, { dataType: v })}
        >
          <Select.Option value="string">String</Select.Option>
          <Select.Option value="number">Number</Select.Option>
          <Select.Option value="boolean">Boolean</Select.Option>
        </Select>
      ),
    },
    {
      title: "参数值",
      dataIndex: "value",
      render: (value: any, record: any, index: number) => {
        if (record.paramType === "variable") {
          return (
            <Input
              value={value}
              onChange={(v) => setRow(index, { value: v })}
              placeholder="请输入变量名"
              prefix="${"
              suffix="}"
            />
          );
        }

        // Fixed Value Rendering based on DataType
        const dataType = record.dataType || "string";
        
        if (dataType === "boolean") {
          return (
            <Select
              value={value === undefined ? undefined : String(value)}
              onChange={(v) => setRow(index, { value: v === "true" })}
              placeholder="请选择"
            >
              <Select.Option value="true">True</Select.Option>
              <Select.Option value="false">False</Select.Option>
            </Select>
          );
        }

        if (dataType === "number") {
          return (
            <InputNumber
              value={value}
              onChange={(v) => setRow(index, { value: v })}
              placeholder="请输入数值"
              style={{ width: '100%' }}
            />
          );
        }

        return (
          <Input
            value={value}
            onChange={(v) => setRow(index, { value: v })}
            placeholder="请输入值"
          />
        );
      },
    },
    {
      title: "操作",
      dataIndex: "operation",
      width: 60,
      render: (_value: any, _record: any, index: number) => (
        <Button type="text" status="danger" icon={<IconDelete />} onClick={() => removeRow(index)} />
      )
    }
  ];

  return (
    <div>
      <ResizableTable
        size="small"
        pagination={false}
        columns={columns}
        data={data}
        rowKey={(record) => record._id || record.key}
      />
      <Button
        type="dashed"
        icon={<IconPlus />}
        onClick={() => {
          const next = [
            ...data,
            {
              _id: Date.now() + Math.random().toString(36).slice(2),
              key: "",
              label: "",
              type: "string",
              description: "",
              paramType: "fixed",
              dataType: "string",
            },
          ];
          field.setValue(next);
        }}
        style={{ width: '100%', marginTop: 8 }}
      >
        添加
      </Button>
    </div>
  );
});

const ConnectorParamsInner: React.FC<any> = observer(() => {
  const form = useForm();
  const fieldRaw = useField();
  const field = fieldRaw as any;

  // Use form.values to ensure reactivity tracking by observer
  const values = form.values;
  const inputGroup = values.inputGroup || {};
  
  const headers = (inputGroup.headers || []) as any[];
  const queryParams = (inputGroup.queryParams || []) as any[];
  const requestPath = inputGroup.path as string;
  const bodyRaw = inputGroup.body as any;

  type Row = {
    id: string;
    name: string;
    label?: string;
    location: string;
    type: string;
    isExplicitType?: boolean;
  };

  const rows: Row[] = [];
  const varRowIds = new Set<string>();

  const addVariableRow = (source: string, variableName: string, usage?: string, type: string = "string", isExplicitType: boolean = false) => {
    if (!variableName) return;
    const id = `var-${source}-${variableName}`;
    if (varRowIds.has(id)) return;
    varRowIds.add(id);
    rows.push({
      id,
      name: variableName,
      label: usage,
      location: `${source}`,
      type,
      isExplicitType,
    });
  };

  // ... (Calculation Logic) ...
  // 1. Parse URL Path & Query variables
  if (requestPath) {
    const [pathPart, queryPart] = requestPath.split('?');

    // 1.1 Path Variables
    if (pathPart) {
      const regexp = /\$\{([^}]+)\}/g;
      let match: RegExpExecArray | null;
      while ((match = regexp.exec(pathPart))) {
        const varName = match[1];
        addVariableRow("URL路径", varName, "Path参数");
      }
    }

    // 1.2 Query Variables
    if (queryPart) {
      const searchParams = new URLSearchParams(queryPart);
      searchParams.forEach((value, key) => {
        const regexp = /\$\{([^}]+)\}/g;
        let match: RegExpExecArray | null;
        while ((match = regexp.exec(value))) {
          const varName = match[1];
          addVariableRow("URL查询参数", varName, key);
        }
      });
    }
  }

  // 2. Parse Params Table (Headers, Query, Path)
  const processParamsTable = (items: any[], location: string) => {
    items.forEach((item) => {
      if (!item || !item.key) return;

      // Case 1: Explicit Variable Type
      if (item.paramType === 'variable' && item.value) {
        addVariableRow(location, item.value, item.key, item.dataType || "string", true);
      }
      // Case 2: Fixed Type but contains ${var} syntax (backward compatibility & flexibility)
      else if (item.value && typeof item.value === 'string') {
        const regexp = /\$\{([^}]+)\}/g;
        let match: RegExpExecArray | null;
        while ((match = regexp.exec(item.value))) {
          const varName = match[1];
          // If it's a fixed string with embedded variable, the variable itself is effectively a string part
          addVariableRow(location, varName, item.key, "string", false); 
        }
      }
    });
  };

  processParamsTable(headers, "HTTP请求头");
  processParamsTable(queryParams, "URL查询参数");

  const addBodyParams = (prefix: string, value: any) => {
    if (value === null || value === undefined) {
      return;
    }
    if (typeof value === 'object' && !Array.isArray(value)) {
      Object.keys(value).forEach((key) => {
        const nextPath = prefix ? `${prefix}.${key}` : key;
        addBodyParams(nextPath, value[key]);
      });
      return;
    }
    if (Array.isArray(value)) {
      value.forEach((item, index) => {
        const nextPath = `${prefix}[${index}]`;
        addBodyParams(nextPath, item);
      });
      return;
    }
    if (typeof value === 'string') {
      const regexp = /\$\{([^}]+)\}/g;
      let match: RegExpExecArray | null;
      while ((match = regexp.exec(value))) {
        const varName = match[1];
        addVariableRow('HTTP请求体', varName, prefix);
      }
    }
  };

  if (typeof bodyRaw === 'string' && bodyRaw.trim()) {
    try {
      const parsed = JSON.parse(bodyRaw);
      addBodyParams('', parsed);
    } catch {
      // ignore invalid json
    }
  }
  // ... (End of Calculation Logic) ...

  // Sync logic: Merge calculated rows with stored state to preserve 'type' selection
  const currentParams = (field.value || []) as Row[];
  const mergedParams: Row[] = rows.map((calculatedRow) => {
    const existing = currentParams.find((p) => p.name === calculatedRow.name && p.location === calculatedRow.location);
    if (existing) {
      // If the variable comes from an explicit configuration (ParamsTable), strictly follow its type.
      // Otherwise, respect the user's manual override in this table (existing.type), falling back to calculated type.
      const resolvedType = calculatedRow.isExplicitType 
        ? calculatedRow.type 
        : (existing.type || calculatedRow.type);

      return { 
        ...calculatedRow, 
        // Preserve label if it exists in store, otherwise use calculated one
        label: existing.label || calculatedRow.label,
        type: resolvedType
      };
    }
    return calculatedRow;
  });

  // Effect to update field value when calculation changes
  React.useEffect(() => {
    const isDifferent = JSON.stringify(mergedParams) !== JSON.stringify(currentParams);
    if (isDifferent) {
      field.setValue(mergedParams);
    }
  }, [JSON.stringify(rows.map(r => r.id))]); // Dependency on structure changes, not deep values to avoid loop

  const updateType = (index: number, newType: string) => {
    const next = [...mergedParams];
    const target = next[index];
    next[index] = { ...target, type: newType };
    field.setValue(next);

    // Sync back to ParamsTable sources (headers, queryParams)
    // Only sync if the variable is used as an explicit variable parameter
    const varName = target.name;
    
    const syncToSource = (path: string) => {
       const list = form.getValuesIn(path);
       if (Array.isArray(list)) {
         let modified = false;
         const newList = list.map((item: any) => {
           // Match logic: paramType is 'variable' AND value is the variable name
           if (item?.paramType === 'variable' && item?.value === varName) {
             if (item.dataType !== newType) {
               modified = true;
               return { ...item, dataType: newType };
             }
           }
           return item;
         });
         
         if (modified) {
           form.setValuesIn(path, newList);
         }
       }
    };
    
    syncToSource("inputGroup.headers");
    syncToSource("inputGroup.queryParams");
  };

  const columns = [
    {
      title: '参数名',
      dataIndex: 'name'
    },
    {
      title: '来源',
      dataIndex: 'location'
    },
    {
      title: "类型",
      dataIndex: "type",
      width: 120,
      render: (value: any, _record: any, index: number) => (
        <Select
          value={value}
          onChange={(v) => updateType(index, v)}
          size="small"
        >
          <Select.Option value="string">String</Select.Option>
          <Select.Option value="number">Number</Select.Option>
          <Select.Option value="boolean">Boolean</Select.Option>
        </Select>
      ),
    },
    {
      title: "说明",
      dataIndex: "label",
    },
  ];

  return (
    <ResizableTable
      size="small"
      pagination={false}
      columns={columns}
      data={mergedParams}
      rowKey={(record) => record.id}
    />
  );
});

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

export const FormilyStepNav = connect(StepNav, mapProps());

export const FormilyParamsTable = connect(ParamsTableInner, mapProps());

export const FormilyConnectorParamsTable = connect(ConnectorParamsInner, mapProps());

const SectionTitleInner: React.FC<any> = (props) => {
  const field = useField();
  const title = props.title || field.title;

  return (
    <div
      style={{
        fontSize: 16,
        fontWeight: 500,
        margin: '24px 0 16px',
        color: 'var(--color-text-1)',
        borderLeft: '4px solid rgb(var(--primary-6))',
        paddingLeft: 8,
        lineHeight: '22px'
      }}
    >
      {title}
    </div>
  );
};

export const FormilySectionTitle = connect(SectionTitleInner, mapProps());

const StepActionsInner: React.FC<any> = observer((props) => {
  const form = useForm();
  const stepsProp = props.steps as { key: string; title: string }[] | undefined;
  const steps = stepsProp || [
    { key: 'basic', title: '基础信息' },
    { key: 'input', title: '入参配置' },
    { key: 'output', title: '出参配置' },
    { key: 'test', title: '接口调试' }
  ];

  const currentKey = (form.values as any)?.currentStep || steps[0].key;
  const currentIndex = Math.max(
    0,
    steps.findIndex((item) => item.key === currentKey)
  );

  return (
    <div style={{ marginTop: 24, display: 'flex', justifyContent: 'space-between' }}>
      <Button
        disabled={currentIndex === 0}
        onClick={() => {
          if (currentIndex > 0) {
            form.setValuesIn('currentStep', steps[currentIndex - 1].key);
          }
        }}
      >
        上一步
      </Button>
      <Button
        type="primary"
        disabled={currentIndex === steps.length - 1}
        onClick={() => {
          if (currentIndex < steps.length - 1) {
            // TODO: Validate current step fields before proceeding
            form.setValuesIn('currentStep', steps[currentIndex + 1].key);
          }
        }}
      >
        下一步
      </Button>
    </div>
  );
});

export const FormilyStepActions = connect(StepActionsInner, mapProps());

const FormilyTabsInner: React.FC<any> = observer((props) => {
  const schema = useFieldSchema();
  const tabs: { name: string; schema: any }[] = [];
  schema.mapProperties((schema, name) => {
    tabs.push({ name: String(name), schema });
  });

  return (
    <Tabs defaultActiveTab={tabs[0]?.name} type="card-gutter">
      {tabs.map(({ name, schema }) => (
        <Tabs.TabPane key={name} title={schema.title}>
          <RecursionField name={name} schema={schema} />
        </Tabs.TabPane>
      ))}
    </Tabs>
  );
});

export const FormilyTabs = connect(FormilyTabsInner);

const HorizontalLayoutInner: React.FC<any> = observer((props) => {
  const schema = useFieldSchema();
  const properties: any[] = [];
  schema.mapProperties((s, name) => {
    properties.push({ name, schema: s });
  });

  return (
    <div style={{ display: "flex", gap: 8, alignItems: "flex-start" }}>
      {properties.map(({ name, schema }) => (
        <RecursionField key={name} name={name} schema={schema} />
      ))}
    </div>
  );
});

export const FormilyHorizontalLayout = connect(HorizontalLayoutInner);

// 变量高亮插件
const variableMatcher = new MatchDecorator({
  regexp: /\$\{([^}]+)\}/g,
  decoration: Decoration.mark({
    attributes: {
      style: 'color: #1677ff; background: #e6f4ff; border-radius: 2px; padding: 0 2px; border: 1px solid #91caff;'
    }
  })
});

const variablePlugin = ViewPlugin.fromClass(
  class {
    placeholders: any;
    constructor(view: EditorView) {
      this.placeholders = variableMatcher.createDeco(view);
    }
    update(update: any) {
      this.placeholders = variableMatcher.updateDeco(update, this.placeholders);
    }
  },
  {
    decorations: (instance) => instance.placeholders
  }
);

const JsonEditorInner: React.FC<any> = (props) => {
  const { value, onChange, ...rest } = props;
  return (
    <ReactCodeMirror
      value={typeof value === 'string' ? value : value ? JSON.stringify(value, null, 2) : ''}
      height="300px"
      extensions={[json(), variablePlugin]}
      onChange={(val) => {
        onChange(val);
      }}
      theme="light"
      style={{ border: '1px solid var(--color-border-2)', borderRadius: 2 }}
      {...rest}
    />
  );
};

export const FormilyJsonEditor = connect(
  JsonEditorInner,
  mapProps((props) => {
    return {
      ...props
    };
  })
);

const TextAreaEditorInner: React.FC<any> = (props) => {
  const { value, onChange, ...rest } = props;
  return (
    <ReactCodeMirror
      value={typeof value === 'string' ? value : value ? String(value) : ''}
      height="300px"
      onChange={(val) => {
        onChange(val);
      }}
      theme="light"
      style={{ border: '1px solid var(--color-border-2)', borderRadius: 2 }}
      {...rest}
    />
  );
};

export const FormilyTextAreaEditor = connect(
  TextAreaEditorInner,
  mapProps((props) => {
    return {
      ...props
    };
  })
);

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
  Password: FormilyPassword,
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
  StepNav: FormilyStepNav,
  ParamsTable: FormilyParamsTable,
  ConnectorParamsTable: FormilyConnectorParamsTable,
  SectionTitle: FormilySectionTitle,
  StepActions: FormilyStepActions,
  Tabs: FormilyTabs,
  HorizontalLayout: FormilyHorizontalLayout,
  JsonEditor: FormilyJsonEditor,
  TextAreaEditor: FormilyTextAreaEditor,
  AuthSettingsCard: AuthSettingsCard,
  KeyValueList: KeyValueList,
  TokenAuthPanel: TokenAuthPanel
};
