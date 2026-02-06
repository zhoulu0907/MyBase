import { Input, Table } from '@arco-design/web-react';
import { connect, useField } from '@formily/react';
import React from 'react';

/** 与第二步入参一致的行结构，仅多一个可编辑的 字段值（第二步数据只读） */
export interface DebugParamReadOnlyRow {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  required: boolean;
  defaultValue: string;
  description: string;
  fieldValue?: string;
}

const FIELD_TYPE_NAMES: Record<string, string> = {
  string: '字符串',
  number: '数字',
  boolean: '布尔',
  object: '对象',
  array: '数组'
};

const DebugParamReadOnlyTableInner: React.FC = () => {
  const rawField = useField() as {
    value?: DebugParamReadOnlyRow[];
    setValue: (v: DebugParamReadOnlyRow[]) => void;
  };
  const value = (rawField.value ?? []) as DebugParamReadOnlyRow[];

  const setValue = (next: DebugParamReadOnlyRow[]) => {
    rawField.setValue(next);
  };

  const updateFieldValue = (index: number, fieldValue: string) => {
    const next = [...value];
    next[index] = { ...next[index], fieldValue };
    setValue(next);
  };

  const columns = [
    {
      title: '字段 Key',
      dataIndex: 'key',
      width: 120,
      render: (_: unknown, row: DebugParamReadOnlyRow) => row.key || '-'
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 120,
      render: (_: unknown, row: DebugParamReadOnlyRow) => row.fieldName || '-'
    },
    {
      title: '字段类型',
      dataIndex: 'fieldType',
      width: 100,
      render: (_: unknown, row: DebugParamReadOnlyRow) =>
        FIELD_TYPE_NAMES[row.fieldType] ?? row.fieldType ?? '-'
    },
    {
      title: '必填',
      dataIndex: 'required',
      width: 70,
      render: (_: unknown, row: DebugParamReadOnlyRow) => (row.required ? '是' : '否')
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      render: (_: unknown, row: DebugParamReadOnlyRow) => row.defaultValue ?? '-'
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      width: 120,
      ellipsis: true,
      render: (_: unknown, row: DebugParamReadOnlyRow) => row.description ?? '-'
    },
    {
      title: '字段值',
      dataIndex: 'fieldValue',
      width: 160,
      render: (_: unknown, row: DebugParamReadOnlyRow, index: number) => (
        <Input
          value={row.fieldValue ?? ''}
          placeholder="调试时填写的值"
          onChange={(v) => updateFieldValue(index, v)}
          allowClear
        />
      )
    }
  ];

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <Table
        data={value}
        columns={columns}
        rowKey={(record: DebugParamReadOnlyRow) =>
          record.id ?? `row-${record.key}-${record.fieldName}`
        }
        scroll={{ x: 800 }}
        pagination={false}
        size="small"
      />
    </div>
  );
};

export const DebugParamReadOnlyTable = connect(DebugParamReadOnlyTableInner);
