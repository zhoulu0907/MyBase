import { Input, Table } from '@arco-design/web-react';
import { connect, useField } from '@formily/react';
import React from 'react';

export interface DebugHeaderRow {
  key: string;
  value: string;
}

const DebugHeadersTableInner: React.FC = () => {
  const rawField = useField() as {
    value?: DebugHeaderRow[];
    setValue: (v: DebugHeaderRow[]) => void;
  };
  const value = (rawField.value ?? []) as DebugHeaderRow[];

  const updateRow = (index: number, patch: Partial<DebugHeaderRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    rawField.setValue(next);
  };

  if (value.length === 0) {
    return <div style={{ color: '#86909c', padding: '8px 0', fontSize: 13 }}>无请求头</div>;
  }

  const columns = [
    {
      title: 'Header Name',
      dataIndex: 'key',
      width: 180,
      render: (v: string) => (
        <span style={{ fontFamily: 'monospace', color: '#1d2129', fontWeight: 500 }}>{v}</span>
      )
    },
    {
      title: 'Header Value',
      dataIndex: 'value',
      render: (v: string, _row: DebugHeaderRow, index: number) => (
        <Input
          value={v}
          placeholder="输入值或 ${变量名}"
          onChange={(val) => updateRow(index, { value: val })}
          allowClear
          style={{ fontFamily: v?.includes('${') ? 'monospace' : undefined }}
        />
      )
    }
  ];

  return (
    <Table
      data={value}
      columns={columns}
      rowKey={(record: DebugHeaderRow, index: number) => `header-${record.key}-${index}`}
      pagination={false}
      size="small"
      borderCell
    />
  );
};

export const DebugHeadersTable = connect(DebugHeadersTableInner);