import ResizableTable from '@/components/ResizableTable';
import { Input, Radio } from '@arco-design/web-react';
import { connect, useField } from '@formily/react';
import React from 'react';

export interface DebugParamReadOnlyRow {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  required: boolean;
  defaultValue: string;
  description: string;
  fieldValue?: string;
  inputMode?: 'table' | 'json';
}

const FIELD_TYPE_NAMES: Record<string, string> = {
  string: 'String',
  number: 'Number',
  boolean: 'Boolean',
  object: 'Object',
  array: 'Array'
};

const tryParseJson = (value: string): { isJson: boolean; parsed: unknown } => {
  if (!value || typeof value !== 'string') {
    return { isJson: false, parsed: null };
  }
  const trimmed = value.trim();
  if ((trimmed.startsWith('{') && trimmed.endsWith('}')) || 
      (trimmed.startsWith('[') && trimmed.endsWith(']'))) {
    try {
      const parsed = JSON.parse(trimmed);
      return { isJson: true, parsed };
    } catch {
      return { isJson: false, parsed: null };
    }
  }
  return { isJson: false, parsed: null };
};

const formatJsonIfPossible = (value: string): string => {
  const { isJson, parsed } = tryParseJson(value);
  if (isJson && parsed !== null) {
    return JSON.stringify(parsed, null, 2);
  }
  return value;
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

  const globalMode = value.length > 0 ? (value[0].inputMode || 'table') : 'table';

  const updateAllInputMode = (mode: 'table' | 'json') => {
    const next = value.map(row => ({ ...row, inputMode: mode }));
    setValue(next);
  };

  const updateRow = (index: number, patch: Partial<DebugParamReadOnlyRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const updateFieldValue = (index: number, fieldValue: string) => {
    updateRow(index, { fieldValue });
  };

  const buildJsonFromTable = (): string => {
    const obj: Record<string, unknown> = {};
    value.forEach(row => {
      const k = row.key || row.fieldName;
      if (!k) return;
      let v: unknown = row.fieldValue ?? row.defaultValue ?? '';
      if (row.fieldType === 'object' || row.fieldType === 'array') {
        const { isJson, parsed } = tryParseJson(String(v));
        if (isJson) v = parsed;
      } else if (row.fieldType === 'number') {
        const n = Number(v);
        if (!isNaN(n)) v = n;
      } else if (row.fieldType === 'boolean') {
        v = v === 'true' || v === true;
      }
      obj[k] = v;
    });
    return JSON.stringify(obj, null, 2);
  };

  const parseJsonToTable = (jsonStr: string) => {
    try {
      const parsed = JSON.parse(jsonStr);
      if (typeof parsed !== 'object' || parsed === null) return;
      const next = value.map(row => {
        const k = row.key || row.fieldName;
        if (k && k in parsed) {
          let v = parsed[k];
          if (typeof v === 'object') {
            v = JSON.stringify(v);
          }
          return { ...row, fieldValue: String(v) };
        }
        return row;
      });
      setValue(next);
    } catch {
      // ignore parse error
    }
  };

  const [jsonValue, setJsonValue] = React.useState(() => buildJsonFromTable());

  React.useEffect(() => {
    if (globalMode === 'json') {
      setJsonValue(buildJsonFromTable());
    }
  }, [globalMode]);

  const columns = [
    {
      title: 'Key',
      dataIndex: 'key',
      width: 120,
      render: (_: unknown, row: DebugParamReadOnlyRow) => row.key || row.fieldName || '-'
    },
    {
      title: 'Type',
      dataIndex: 'fieldType',
      width: 80,
      render: (_: unknown, row: DebugParamReadOnlyRow) =>
        FIELD_TYPE_NAMES[row.fieldType] ?? row.fieldType ?? '-'
    },
    {
      title: 'Required',
      dataIndex: 'required',
      width: 80,
      render: (_: unknown, row: DebugParamReadOnlyRow) => (row.required ? 'Yes' : 'No')
    },
    {
      title: 'Value',
      dataIndex: 'fieldValue',
      render: (_: unknown, row: DebugParamReadOnlyRow, index: number) => {
        if (row.fieldType === 'object' || row.fieldType === 'array') {
          return (
            <Input.TextArea
              value={row.fieldValue ?? row.defaultValue ?? ''}
              placeholder='{"key": "value"}'
              onChange={(v) => updateFieldValue(index, v)}
              autoSize={{ minRows: 1, maxRows: 6 }}
              style={{ fontFamily: 'monospace', fontSize: 12 }}
            />
          );
        }
        return (
          <Input
            value={row.fieldValue ?? row.defaultValue ?? ''}
            placeholder="Input value"
            onChange={(v) => updateFieldValue(index, v)}
            allowClear
          />
        );
      }
    }
  ];

  if (value.length === 0) {
    return <div style={{ color: '#999', padding: '8px 0' }}>No input parameters</div>;
  }

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Radio.Group
          size="small"
          value={globalMode}
          onChange={(v) => {
            const mode = v as 'table' | 'json';
            if (mode === 'json') {
              setJsonValue(buildJsonFromTable());
            } else {
              parseJsonToTable(jsonValue);
            }
            updateAllInputMode(mode);
          }}
        >
          <Radio value="table">Table</Radio>
          <Radio value="json">JSON</Radio>
        </Radio.Group>
      </div>
      
      {globalMode === 'table' ? (
        <ResizableTable
          data={value}
          columns={columns}
          rowKey={(record: DebugParamReadOnlyRow) =>
            record.id ?? `row-${record.key}-${record.fieldName}`
          }
          scroll={{ x: 600 }}
          pagination={false}
          size="small"
        />
      ) : (
        <Input.TextArea
          value={jsonValue}
          onChange={setJsonValue}
          placeholder='{"param1": "value1", "param2": "value2"}'
          autoSize={{ minRows: 6, maxRows: 16 }}
          style={{ fontFamily: 'monospace', fontSize: 12 }}
        />
      )}
    </div>
  );
};

export const DebugParamReadOnlyTable = connect(DebugParamReadOnlyTableInner);
