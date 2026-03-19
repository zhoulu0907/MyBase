import { Button, Input, Select, Space, Switch, Table } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, useField, useForm } from '@formily/react';
import React from 'react';
import { extractVariables } from './utils';

/** 单行数据形状 */
export interface ParamRow {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  required: boolean;
  defaultValue: string;
  description: string;
  expose?: boolean; // 已废弃，保留用于兼容旧数据
}

export interface ActionInputRow extends ParamRow {
  mapKind?: string;
  mapKey?: string;
}

const FIELD_TYPE_OPTIONS = [
  { label: '字符串', value: 'string' },
  { label: '数字', value: 'number' },
  { label: '布尔', value: 'boolean' },
  { label: '对象', value: 'object' },
  { label: '数组', value: 'array' }
];

const defaultRow: ParamRow = {
  key: '',
  fieldName: '',
  fieldType: 'string',
  required: false,
  defaultValue: '',
  description: ''
};

const defaultActionInputRow: ActionInputRow = {
  ...defaultRow,
  mapKind: 'body',
  mapKey: ''
};

const MAP_KIND_OPTIONS = [
  { label: '请求头', value: 'header' },
  { label: '查询参数', value: 'query' },
  { label: '路径参数', value: 'path' },
  { label: '请求体', value: 'body' }
];

const ParamArrayTableInner: React.FC = () => {
  const rawField = useField() as { value?: ParamRow[]; setValue: (v: ParamRow[]) => void };
  const value = (rawField.value ?? []) as ParamRow[];

  const setValue = (next: ParamRow[]) => {
    rawField.setValue(next);
  };

  const updateRow = (index: number, patch: Partial<ParamRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const addRow = () => {
    setValue([...value, { ...defaultRow, id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}` }]);
  };

  const removeRow = (index: number) => {
    const next = value.filter((_, i) => i !== index);
    setValue(next);
  };

  const handleKeyChange = (index: number, newKey: string) => {
    updateRow(index, { key: newKey });
  };

  const columns = [
    {
      title: '键名',
      dataIndex: 'key',
      width: 140,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.key}
          placeholder="例如 Content-Type"
          onChange={(v) => handleKeyChange(index, v)}
          allowClear
        />
      )
    },
    {
      title: '名称',
      dataIndex: 'fieldName',
      width: 140,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.fieldName}
          placeholder="显示名称"
          onChange={(v) => updateRow(index, { fieldName: v })}
          allowClear
        />
      )
    },
    {
      title: '类型',
      dataIndex: 'fieldType',
      width: 100,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '必填',
      dataIndex: 'required',
      width: 70,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Switch
          checked={row.required}
          onChange={(v) => updateRow(index, { required: v })}
        />
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 180,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.defaultValue}
          placeholder="可使用 ${变量名}"
          onChange={(v) => updateRow(index, { defaultValue: v })}
          allowClear
        />
      )
    },
    {
      title: '描述',
      dataIndex: 'description',
      width: 150,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.description}
          placeholder="可选"
          onChange={(v) => updateRow(index, { description: v })}
          allowClear
        />
      )
    },
    {
      title: '',
      dataIndex: '_op',
      width: 50,
      fixed: 'right' as const,
      render: (_: unknown, __: ParamRow, index: number) => (
        <Button
          type="text"
          status="danger"
          icon={<IconDelete />}
          onClick={() => removeRow(index)}
        />
      )
    }
  ];

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <Table
        data={value}
        columns={columns}
        rowKey={(record: ParamRow) => record.id ?? `row-${record.key}-${record.fieldName}`}
        scroll={{ x: 830 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加行
      </Button>
    </div>
  );
};

export const ParamArrayTable = connect(ParamArrayTableInner);

const ActionInputArrayTableInner: React.FC = () => {
  const form = useForm() as unknown as { values: Record<string, any> };
  const rawField = useField() as { value?: ActionInputRow[]; setValue: (v: ActionInputRow[]) => void };
  const value = (rawField.value ?? []) as ActionInputRow[];

  const setValue = (next: ActionInputRow[]) => {
    rawField.setValue(next);
  };

  const updateRow = (index: number, patch: Partial<ActionInputRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const addRow = () => {
    setValue([
      ...value,
      { ...defaultActionInputRow, id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}` }
    ]);
  };

  const removeRow = (index: number) => {
    const next = value.filter((_, i) => i !== index);
    setValue(next);
  };

  const tabs = form.values?.tabs || {};
  const requestHeaders = Array.isArray(tabs?.headers?.requestHeaders) ? tabs.headers.requestHeaders : [];
  const queryParams = Array.isArray(tabs?.params?.queryParams) ? tabs.params.queryParams : [];
  const pathParams = Array.isArray(tabs?.params?.pathParams) ? tabs.params.pathParams : [];
  const bodyMode = tabs?.body?.bodyMode;
  const requestBody = Array.isArray(tabs?.body?.requestBody) ? tabs.body.requestBody : [];

  const getOptionsByKind = (kind?: string) => {
    if (kind === 'header') return requestHeaders;
    if (kind === 'query') return queryParams;
    if (kind === 'path') return pathParams;
    if (kind === 'body') {
      if (bodyMode !== 'kv') return [];
      return requestBody;
    }
    return [];
  };

  const buildKeyOptions = (kind?: string) => {
    const rows = getOptionsByKind(kind);
    return (Array.isArray(rows) ? rows : [])
      .map((r) => ({
        label: (r?.fieldName || r?.key || '').toString(),
        value: (r?.key || r?.fieldName || '').toString()
      }))
      .filter((o) => o.value);
  };

  const findTargetRow = (kind?: string, mapKey?: string) => {
    if (!mapKey) return undefined;
    const rows = getOptionsByKind(kind);
    return (Array.isArray(rows) ? rows : []).find((r) => (r?.key || r?.fieldName) === mapKey);
  };

  const generateInputsFromRequest = () => {
    const newRows: ActionInputRow[] = [];
    const addedKeys = new Set<string>();

    const addRowsFromVariables = (rows: any[], mapKind: string) => {
      (Array.isArray(rows) ? rows : []).forEach((r) => {
        const key = r?.key || r?.fieldName;
        const defaultValue = r?.defaultValue || '';
        if (!key) return;

        // 从 defaultValue 中提取 ${xxx} 变量
        const vars = extractVariables(defaultValue);
        vars.forEach((v) => {
          if (addedKeys.has(v)) return;
          addedKeys.add(v);

          newRows.push({
            id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}`,
            key: v,  // 变量名作为入参 key
            fieldName: r?.fieldName || v,
            fieldType: r?.fieldType || 'string',
            required: typeof r?.required === 'boolean' ? r.required : false,
            defaultValue: defaultValue,
            description: r?.description || '',
            mapKind,
            mapKey: key  // 映射到底层的 key
          });
        });
      });
    };

    addRowsFromVariables(requestHeaders, 'header');
    addRowsFromVariables(queryParams, 'query');
    addRowsFromVariables(pathParams, 'path');
    if (bodyMode === 'kv') {
      addRowsFromVariables(requestBody, 'body');
    }

    // 合并已有行（避免覆盖用户已配置的）
    const existingKeys = new Set(value.map((r) => r.key));
    const filteredNewRows = newRows.filter((r) => !existingKeys.has(r.key));
    setValue([...value, ...filteredNewRows]);
  };

  const columns = [
    {
      title: '键名',
      dataIndex: 'key',
      width: 140,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Input value={row.key} onChange={(v) => updateRow(index, { key: v })} allowClear />
      )
    },
    {
      title: '名称',
      dataIndex: 'fieldName',
      width: 140,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Input value={row.fieldName} onChange={(v) => updateRow(index, { fieldName: v })} allowClear />
      )
    },
    {
      title: '类型',
      dataIndex: 'fieldType',
      width: 100,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '必填',
      dataIndex: 'required',
      width: 70,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Switch checked={row.required} onChange={(v) => updateRow(index, { required: v })} />
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 160,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Input value={row.defaultValue} onChange={(v) => updateRow(index, { defaultValue: v })} allowClear />
      )
    },
    {
      title: '映射到',
      dataIndex: 'mapKind',
      width: 100,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Select
          value={row.mapKind}
          options={MAP_KIND_OPTIONS.filter((o) => (o.value === 'body' ? bodyMode === 'kv' : true))}
          onChange={(v) => updateRow(index, { mapKind: v, mapKey: '' })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '映射键',
      dataIndex: 'mapKey',
      width: 160,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Select
          value={row.mapKey}
          options={buildKeyOptions(row.mapKind)}
          onChange={(v) => {
            const target = findTargetRow(row.mapKind, v);
            updateRow(index, {
              mapKey: v,
              fieldType: (target?.fieldType as string) || row.fieldType,
              description: (target?.description as string) || row.description,
              required: typeof target?.required === 'boolean' ? target.required : row.required
            });
          }}
          style={{ width: '100%' }}
          allowClear
        />
      )
    },
    {
      title: '描述',
      dataIndex: 'description',
      width: 180,
      render: (_: unknown, row: ActionInputRow, index: number) => (
        <Input value={row.description} onChange={(v) => updateRow(index, { description: v })} allowClear />
      )
    },
    {
      title: '',
      dataIndex: '_op',
      width: 50,
      fixed: 'right' as const,
      render: (_: unknown, __: ActionInputRow, index: number) => (
        <Button type="text" status="danger" icon={<IconDelete />} onClick={() => removeRow(index)} />
      )
    }
  ];

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <div style={{ marginBottom: 8 }}>
        <Space>
          <Button type="outline" size="small" onClick={generateInputsFromRequest}>
            从变量生成
          </Button>
        </Space>
      </div>
      <Table
        data={value}
        columns={columns}
        rowKey={(record: ActionInputRow) => record.id ?? `row-${record.key}-${record.fieldName}`}
        scroll={{ x: 1200 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加行
      </Button>
    </div>
  );
};

export const ActionInputArrayTable = connect(ActionInputArrayTableInner);
