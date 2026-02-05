import { Button, Input, Select, Switch, Table } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, useField } from '@formily/react';
import React from 'react';

/** 单行数据形状 */
export interface ParamRow {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  required: boolean;
  defaultValue: string;
  description: string;
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

  const columns = [
    {
      title: '字段 Key',
      dataIndex: 'key',
      width: 140,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.key}
          placeholder="如：Content-Type"
          onChange={(v) => updateRow(index, { key: v })}
          allowClear
        />
      )
    },
    {
      title: '字段名称',
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
      title: '字段类型',
      dataIndex: 'fieldType',
      width: 120,
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
      width: 80,
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
      width: 140,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.defaultValue}
          placeholder="选填"
          onChange={(v) => updateRow(index, { defaultValue: v })}
          allowClear
        />
      )
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      ellipsis: true,
      render: (_: unknown, row: ParamRow, index: number) => (
        <Input
          value={row.description}
          placeholder="选填"
          onChange={(v) => updateRow(index, { description: v })}
          allowClear
        />
      )
    },
    {
      title: '操作',
      dataIndex: '_op',
      width: 80,
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
        scroll={{ x: 900 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加一行
      </Button>
    </div>
  );
};

export const ParamArrayTable = connect(ParamArrayTableInner);
