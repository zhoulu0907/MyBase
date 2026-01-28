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
  Steps,
  Table,
  Tabs,
} from "@arco-design/web-react";
import { IconDelete, IconPlus } from "@arco-design/web-react/icon";
import { connect, mapProps, RecursionField, useField, useFieldSchema, useForm, observer } from "@formily/react";
import React from "react";
import ReactCodeMirror from "@uiw/react-codemirror";
import { json } from "@codemirror/lang-json";
import { AuthSettingsCard, KeyValueList } from './AuthComponents';
import { TokenAuthPanel } from './TokenAuthPanel';
import { MatchDecorator, ViewPlugin, Decoration, EditorView } from "@codemirror/view";

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
const FormilyInput = createFormilyComponent(Input, { defaultValue: "" });
const FormilyPassword = createFormilyComponent(Input.Password, { defaultValue: "" });
const FormilyInputPassword = createFormilyComponent(Input.Password, { defaultValue: "" });
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
  const steps =
    stepsProp ||
    [
      { key: "basic", title: "基础信息" },
      { key: "input", title: "入参配置" },
      { key: "output", title: "出参配置" },
      { key: "test", title: "接口调试" },
    ];

  const currentKey = (form.values as any)?.currentStep || steps[0].key;
  const currentIndex = Math.max(
    0,
    steps.findIndex((item) => item.key === currentKey)
  );

  React.useEffect(() => {
    if (!(form.values as any)?.currentStep) {
      form.setValuesIn("currentStep", steps[0].key);
    }
  }, [form, steps]);

  return (
    <Steps
      current={currentIndex + 1}
      onChange={(index) => {
        const item = steps[index - 1];
        if (item) {
          form.setValuesIn("currentStep", item.key);
        }
      }}
    >
      {steps.map((item) => (
        <Steps.Step key={item.key} title={item.title} />
      ))}
    </Steps>
  );
});

const ParamsTableInner: React.FC<any> = () => {
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
      title: "字段 Key",
      dataIndex: "key",
      render: (value: any, _record: any, index: number) => (
        <Input
          value={value}
          onChange={(v) => setRow(index, { key: v })}
        />
      ),
    },
    {
      title: "字段名称",
      dataIndex: "label",
      render: (value: any, _record: any, index: number) => (
        <Input
          value={value}
          onChange={(v) => setRow(index, { label: v })}
        />
      ),
    },
    {
      title: "字段类型",
      dataIndex: "type",
      render: (value: any, _record: any, index: number) => (
        <Select
          value={value}
          onChange={(v) => setRow(index, { type: v })}
        >
          <Select.Option value="string">string</Select.Option>
          <Select.Option value="number">number</Select.Option>
          <Select.Option value="boolean">boolean</Select.Option>
        </Select>
      ),
    },
    {
      title: "字段描述",
      dataIndex: "description",
      render: (value: any, _record: any, index: number) => (
        <Input
          value={value}
          onChange={(v) => setRow(index, { description: v })}
        />
      ),
    },
    {
      title: "操作",
      dataIndex: "operation",
      render: (_value: any, _record: any, index: number) => (
        <Button
          type="text"
          status="danger"
          icon={<IconDelete />}
          onClick={() => removeRow(index)}
        />
      ),
    },
  ];

  return (
    <div>
      <Table
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
            },
          ];
          field.setValue(next);
        }}
        style={{ width: "100%", marginTop: 8 }}
      >
        添加
      </Button>
    </div>
  );
};

const ConnectorParamsInner: React.FC<any> = observer(() => {
  const form = useForm();

  const headers = (form.getValuesIn("inputGroup.headers") || []) as any[];
  const queryParams = (form.getValuesIn("inputGroup.queryParams") || []) as any[];
  const pathParams = (form.getValuesIn("inputGroup.pathParams") || []) as any[];
  const bodyRaw = form.getValuesIn("inputGroup.body") as any;

  type Row = {
    id: string;
    name: string;
    label?: string;
    location: string;
    path?: string;
  };

  const rows: Row[] = [];
  const varRowIds = new Set<string>();

  const addVariableRow = (source: string, variableName: string, usage?: string) => {
    if (!variableName) return;
    const id = `var-${source}-${variableName}`;
    if (varRowIds.has(id)) return;
    varRowIds.add(id);
    rows.push({
      id,
      name: variableName,
      label: usage,
      location: `${source}变量`,
    });
  };

  headers.forEach((item) => {
    if (item && item.key) {
      rows.push({
        id: `header-${item.key}`,
        name: item.key,
        label: item.label,
        location: "HTTP请求头",
      });
    }

    if (item) {
      Object.keys(item).forEach((k) => {
        const v = (item as any)[k];
        if (typeof v === "string") {
          const regexp = /\$\{([^}]+)\}/g;
          let match: RegExpExecArray | null;
          while ((match = regexp.exec(v))) {
            const varName = match[1];
            addVariableRow("HTTP请求头", varName, item.key ? `${item.key}.${k}` : k);
          }
        }
      });
    }
  });

  queryParams.forEach((item) => {
    if (item && item.key) {
      rows.push({
        id: `query-${item.key}`,
        name: item.key,
        label: item.label,
        location: "URL查询参数",
      });
    }

    if (item) {
      Object.keys(item).forEach((k) => {
        const v = (item as any)[k];
        if (typeof v === "string") {
          const regexp = /\$\{([^}]+)\}/g;
          let match: RegExpExecArray | null;
          while ((match = regexp.exec(v))) {
            const varName = match[1];
            addVariableRow("URL查询参数", varName, item.key ? `${item.key}.${k}` : k);
          }
        }
      });
    }
  });

  pathParams.forEach((item) => {
    if (item && item.key) {
      rows.push({
        id: `path-${item.key}`,
        name: item.key,
        label: item.label,
        location: "URL路径参数",
      });
    }

    if (item) {
      Object.keys(item).forEach((k) => {
        const v = (item as any)[k];
        if (typeof v === "string") {
          const regexp = /\$\{([^}]+)\}/g;
          let match: RegExpExecArray | null;
          while ((match = regexp.exec(v))) {
            const varName = match[1];
            addVariableRow("URL路径参数", varName, item.key ? `${item.key}.${k}` : k);
          }
        }
      });
    }
  });

  const addBodyParams = (prefix: string, value: any) => {
    if (value === null || value === undefined) {
      return;
    }
    if (typeof value === "object" && !Array.isArray(value)) {
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
    if (typeof value === "string") {
      const regexp = /\$\{([^}]+)\}/g;
      let match: RegExpExecArray | null;
      while ((match = regexp.exec(value))) {
        const varName = match[1];
        addVariableRow("HTTP请求体", varName, prefix);
      }
    }
    // Body keys are structural and should not be treated as parameters unless they contain variables
  };

  if (typeof bodyRaw === "string" && bodyRaw.trim()) {
    try {
      const parsed = JSON.parse(bodyRaw);
      addBodyParams("", parsed);
    } catch {
      // ignore invalid json
    }
  }

  const columns = [
    {
      title: "参数名",
      dataIndex: "name",
    },
    {
      title: "来源",
      dataIndex: "location",
    },
    {
      title: "说明",
      dataIndex: "label",
    },
  ];

  return (
    <Table
      size="small"
      pagination={false}
      columns={columns}
      data={rows}
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

export const FormilyStepNav = connect(
  StepNav,
  mapProps()
);

export const FormilyParamsTable = connect(
  ParamsTableInner,
  mapProps()
);

export const FormilyConnectorParamsTable = connect(
  ConnectorParamsInner,
  mapProps()
);

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

export const FormilySectionTitle = connect(
  SectionTitleInner,
  mapProps()
);

const StepActionsInner: React.FC<any> = observer((props) => {
  const form = useForm();
  const stepsProp = props.steps as { key: string; title: string }[] | undefined;
  const steps =
    stepsProp ||
    [
      { key: "basic", title: "基础信息" },
      { key: "input", title: "入参配置" },
      { key: "output", title: "出参配置" },
      { key: "test", title: "接口调试" },
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
            form.setValuesIn("currentStep", steps[currentIndex - 1].key);
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
            form.setValuesIn("currentStep", steps[currentIndex + 1].key);
          }
        }}
      >
        下一步
      </Button>
    </div>
  );
});

export const FormilyStepActions = connect(
  StepActionsInner,
  mapProps()
);

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
  
  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
      <RecursionField schema={schema} onlyRenderProperties />
    </div>
  );
});

export const FormilyHorizontalLayout = connect(HorizontalLayoutInner);

// 变量高亮插件
const variableMatcher = new MatchDecorator({
  regexp: /\$\{([^}]+)\}/g,
  decoration: Decoration.mark({
    attributes: {
      style: "color: #1677ff; background: #e6f4ff; border-radius: 2px; padding: 0 2px; border: 1px solid #91caff;"
    }
  })
});

const variablePlugin = ViewPlugin.fromClass(class {
  placeholders: any
  constructor(view: EditorView) {
    this.placeholders = variableMatcher.createDeco(view)
  }
  update(update: any) {
    this.placeholders = variableMatcher.updateDeco(update, this.placeholders)
  }
}, {
  decorations: instance => instance.placeholders
});

const JsonEditorInner: React.FC<any> = (props) => {
  const { value, onChange, ...rest } = props;
  return (
    <ReactCodeMirror
      value={typeof value === 'string' ? value : (value ? JSON.stringify(value, null, 2) : '')}
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
      ...props,
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
  AuthSettingsCard: AuthSettingsCard,
  KeyValueList: KeyValueList,
  TokenAuthPanel: TokenAuthPanel
};
