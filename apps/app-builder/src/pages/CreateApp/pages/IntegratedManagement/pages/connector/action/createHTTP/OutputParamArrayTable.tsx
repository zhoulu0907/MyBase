import { Button, Input, Select, Space, Table } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, useField, useForm } from '@formily/react';
import React from 'react';

/** 出参单行数据形状：字段 Key、字段名称、字段类型、字段描述 */
export interface OutputParamRow {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  description: string;
  jsonPath?: string;
  expose?: boolean; // 已废弃，保留用于兼容旧数据
}

export interface ActionOutputRow extends OutputParamRow {
  fromKind?: string;
  fromKey?: string;
}

const FIELD_TYPE_OPTIONS = [
  { label: '字符串', value: 'string' },
  { label: '数字', value: 'number' },
  { label: '布尔', value: 'boolean' },
  { label: '对象', value: 'object' },
  { label: '数组', value: 'array' }
];

const defaultRow: OutputParamRow = {
  key: '',
  fieldName: '',
  fieldType: 'string',
  description: '',
  jsonPath: ''
};

const defaultActionOutputRow: ActionOutputRow = {
  ...defaultRow,
  fromKind: 'body',
  fromKey: ''
};

const FROM_KIND_OPTIONS = [
  { label: '响应头', value: 'header' },
  { label: '响应体', value: 'body' }
];

const OutputParamArrayTableInner: React.FC = () => {
  const rawField = useField() as { value?: OutputParamRow[]; setValue: (v: OutputParamRow[]) => void };
  const value = (rawField.value ?? []) as OutputParamRow[];

  const setValue = (next: OutputParamRow[]) => {
    rawField.setValue(next);
  };

  const updateRow = (index: number, patch: Partial<OutputParamRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const addRow = () => {
    setValue([
      ...value,
      { ...defaultRow, id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}` }
    ]);
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
      width: 120,
      render: (_: unknown, row: OutputParamRow, index: number) => (
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
      width: 120,
      render: (_: unknown, row: OutputParamRow, index: number) => (
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
      render: (_: unknown, row: OutputParamRow, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: 'JSON路径',
      dataIndex: 'jsonPath',
      width: 180,
      render: (_: unknown, row: OutputParamRow, index: number) => (
        <Input
          value={row.jsonPath}
          placeholder="$.data.field"
          onChange={(v) => updateRow(index, { jsonPath: v })}
          allowClear
        />
      )
    },
    {
      title: '描述',
      dataIndex: 'description',
      width: 150,
      render: (_: unknown, row: OutputParamRow, index: number) => (
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
      width: 60,
      fixed: 'right' as const,
      render: (_: unknown, __: OutputParamRow, index: number) => (
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
        rowKey={(record: OutputParamRow) => record.id ?? `row-${record.key}-${record.fieldName}`}
        scroll={{ x: 800 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加行
      </Button>
    </div>
  );
};

export const OutputParamArrayTable = connect(OutputParamArrayTableInner);

const ActionOutputArrayTableInner: React.FC = () => {
  const form = useForm() as unknown as { values: Record<string, any> };
  const rawField = useField() as { value?: ActionOutputRow[]; setValue: (v: ActionOutputRow[]) => void };
  const value = (rawField.value ?? []) as ActionOutputRow[];

  const setValue = (next: ActionOutputRow[]) => {
    rawField.setValue(next);
  };

  const updateRow = (index: number, patch: Partial<ActionOutputRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const addRow = () => {
    setValue([
      ...value,
      { ...defaultActionOutputRow, id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}` }
    ]);
  };

  const removeRow = (index: number) => {
    const next = value.filter((_, i) => i !== index);
    setValue(next);
  };

  const responseTabs = form.values?.responseTabs || {};
  const responseHeaders = Array.isArray(responseTabs?.responseHeaders) ? responseTabs.responseHeaders : [];
  const responseBodyTab = responseTabs?.responseBodyTab || {};
  const responseBodyJson = typeof responseBodyTab?.responseBodyJson === 'string' ? responseBodyTab.responseBodyJson : '';
  const responseBodyText = typeof responseBodyTab?.responseBodyText === 'string' ? responseBodyTab.responseBodyText : '';
  const responseBodyMode = responseBodyTab?.responseBodyMode || 'json';

  const getOptionsByKind = (kind?: string) => {
    if (kind === 'header') return responseHeaders;
    if (kind === 'body') {
      const jsonBody = responseBodyMode === 'json' ? responseBodyJson : responseBodyText;
      if (!jsonBody) return [];
      try {
        const parsed = JSON.parse(jsonBody);
        return flattenObject(parsed);
      } catch {
        return [];
      }
    }
    return [];
  };

  const flattenObject = (obj: any, prefix = '$'): { key: string; path: string; type: string }[] => {
    const result: { key: string; path: string; type: string }[] = [];
    
    if (typeof obj !== 'object' || obj === null) {
      return result;
    }
    
    Object.entries(obj).forEach(([key, value]) => {
      const path = prefix === '$' ? `$.${key}` : `${prefix}.${key}`;
      
      if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
        result.push(...flattenObject(value, path));
      } else {
        result.push({
          key,
          path,
          type: Array.isArray(value) ? 'array' : typeof value
        });
      }
    });
    
    return result;
  };

  const buildKeyOptions = (kind?: string) => {
    const rows = getOptionsByKind(kind);
    return (Array.isArray(rows) ? rows : [])
      .map((r) => ({
        label: `${r.path} (${r.type})`,
        value: r.path
      }));
  };

  const findTargetRow = (kind?: string, path?: string) => {
    if (!path) return undefined;
    const rows = getOptionsByKind(kind);
    return (Array.isArray(rows) ? rows : []).find((r: any) => r?.path === path);
  };

  const generateOutputsFromResponse = () => {
    const newRows: ActionOutputRow[] = [];
    const addedKeys = new Set<string>();

    const addRows = (rows: any[], fromKind: string) => {
      (Array.isArray(rows) ? rows : []).forEach((r) => {
        const key = r?.key || r?.fieldName;
        const path = r?.path || r?.jsonPath;
        if (!key && !path) return;

        const rowKey = key || path?.split('.').pop() || '';
        if (addedKeys.has(rowKey)) return;
        addedKeys.add(rowKey);

        newRows.push({
          id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}`,
          key: rowKey,
          fieldName: r?.fieldName || key || path?.split('.').pop() || '',
          fieldType: r?.fieldType || r?.type || 'string',
          description: r?.description || '',
          jsonPath: path,
          fromKind,
          fromKey: key || path
        });
      });
    };

    addRows(responseHeaders, 'header');

    if (responseBodyMode === 'json' || responseBodyMode === 'text') {
      const jsonBody = responseBodyMode === 'json' ? responseBodyJson : responseBodyText;
      if (jsonBody) {
        try {
          const parsed = JSON.parse(jsonBody);
          const flattened = flattenObject(parsed);
          addRows(flattened, 'body');
        } catch {
          // ignore
        }
      }
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
      width: 120,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Input value={row.key} onChange={(v) => updateRow(index, { key: v })} allowClear />
      )
    },
    {
      title: '名称',
      dataIndex: 'fieldName',
      width: 120,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Input value={row.fieldName} onChange={(v) => updateRow(index, { fieldName: v })} allowClear />
      )
    },
    {
      title: '类型',
      dataIndex: 'fieldType',
      width: 100,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '来源',
      dataIndex: 'fromKind',
      width: 100,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Select
          value={row.fromKind}
          options={FROM_KIND_OPTIONS}
          onChange={(v) => updateRow(index, { fromKind: v, fromKey: '' })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: 'JSON路径',
      dataIndex: 'fromKey',
      width: 160,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Select
          value={row.fromKey}
          options={buildKeyOptions(row.fromKind)}
          onChange={(v) => {
            const target = findTargetRow(row.fromKind, v);
            updateRow(index, {
              fromKey: v,
              jsonPath: v,
              fieldType: (target?.fieldType as string) || (target?.type as string) || row.fieldType,
              description: (target?.description as string) || row.description
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
      ellipsis: true,
      render: (_: unknown, row: ActionOutputRow, index: number) => (
        <Input value={row.description} onChange={(v) => updateRow(index, { description: v })} allowClear />
      )
    },
    {
      title: '',
      dataIndex: '_op',
      width: 60,
      fixed: 'right' as const,
      render: (_: unknown, __: ActionOutputRow, index: number) => (
        <Button type="text" status="danger" icon={<IconDelete />} onClick={() => removeRow(index)} />
      )
    }
  ];

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <div style={{ marginBottom: 8 }}>
        <Space>
          <Button type="outline" size="small" onClick={generateOutputsFromResponse}>
            从响应字段生成
          </Button>
        </Space>
      </div>
      <Table
        data={value}
        columns={columns}
        rowKey={(record: ActionOutputRow) => record.id ?? `row-${record.key}-${record.fieldName}`}
        scroll={{ x: 900 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加行
      </Button>
    </div>
  );
};

export const ActionOutputArrayTable = connect(ActionOutputArrayTableInner);
