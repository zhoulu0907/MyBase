import ResizableTable from '@/components/ResizableTable';
import { Button, Input, Select, Space, Switch, Typography } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, useField } from '@formily/react';
import React, { useMemo } from 'react';

const { Text } = Typography;

export interface JsonBodyField {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  description: string;
  defaultValue: string;
  expose: boolean;
  path: string;
  required?: boolean;
}

const FIELD_TYPE_OPTIONS = [
  { label: 'String', value: 'string' },
  { label: 'Number', value: 'number' },
  { label: 'Boolean', value: 'boolean' },
  { label: 'Object', value: 'object' },
  { label: 'Array', value: 'array' }
];

const defaultField: JsonBodyField = {
  key: '',
  fieldName: '',
  fieldType: 'string',
  description: '',
  defaultValue: '',
  expose: false,
  path: '',
  required: false
};

const flattenJsonToFields = (obj: any, prefix = ''): JsonBodyField[] => {
  const fields: JsonBodyField[] = [];
  
  if (typeof obj !== 'object' || obj === null) {
    return fields;
  }
  
  Object.entries(obj).forEach(([key, value]) => {
    const path = prefix ? `${prefix}.${key}` : key;
    
    if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
      fields.push(...flattenJsonToFields(value, path));
    } else {
      const inferredType = Array.isArray(value) ? 'array' : typeof value;
      fields.push({
        id: `field-${Date.now()}-${Math.random().toString(36).slice(2)}-${path}`,
        key,
        fieldName: key,
        fieldType: inferredType,
        description: '',
        defaultValue: '',
        expose: false,
        path,
        required: false
      });
    }
  });
  
  return fields;
};

const parseJsonTemplate = (jsonStr: string): JsonBodyField[] => {
  if (!jsonStr || typeof jsonStr !== 'string') {
    return [];
  }
  
  try {
    const parsed = JSON.parse(jsonStr);
    return flattenJsonToFields(parsed);
  } catch {
    return [];
  }
};

const extractVariables = (str: string): string[] => {
  if (!str || typeof str !== 'string') return [];
  const regex = /\$\{([^}]+)\}/g;
  const matches: string[] = [];
  let match;
  while ((match = regex.exec(str)) !== null) {
    matches.push(match[1]);
  }
  return matches;
};

const JsonBodyFieldEditorInner: React.FC = () => {
  const rawField = useField() as { value?: JsonBodyField[]; setValue: (v: JsonBodyField[]) => void };
  const value = (rawField.value ?? []) as JsonBodyField[];

  const setValue = (next: JsonBodyField[]) => {
    rawField.setValue(next);
  };

  const updateRow = (index: number, patch: Partial<JsonBodyField>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    setValue(next);
  };

  const addRow = () => {
    setValue([
      ...value,
      { ...defaultField, id: `field-${Date.now()}-${Math.random().toString(36).slice(2)}` }
    ]);
  };

  const removeRow = (index: number) => {
    const next = value.filter((_, i) => i !== index);
    setValue(next);
  };

  const handleExposeChange = (index: number, expose: boolean) => {
    const row = value[index];
    const key = row.key || row.fieldName;
    let newDefaultValue = row.defaultValue;
    
    if (expose && key) {
      if (!row.defaultValue || !row.defaultValue.includes('${')) {
        newDefaultValue = `\${${key}}`;
      }
    }
    
    updateRow(index, { expose, defaultValue: newDefaultValue });
  };

  const handleKeyChange = (index: number, newKey: string) => {
    const row = value[index];
    let newDefaultValue = row.defaultValue;
    
    if (row.expose && newKey) {
      if (!row.defaultValue || !row.defaultValue.includes('${')) {
        newDefaultValue = `\${${newKey}}`;
      }
    }
    
    updateRow(index, { key: newKey, defaultValue: newDefaultValue });
  };

  const exposedFields = useMemo(() => {
    return value.filter(f => f.expose);
  }, [value]);

  const columns = [
    {
      title: 'Key',
      dataIndex: 'key',
      width: 120,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Input
          value={row.key}
          placeholder="Field key"
          onChange={(v) => handleKeyChange(index, v)}
          allowClear
        />
      )
    },
    {
      title: 'Name',
      dataIndex: 'fieldName',
      width: 120,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Input
          value={row.fieldName}
          placeholder="Display name"
          onChange={(v) => updateRow(index, { fieldName: v })}
          allowClear
        />
      )
    },
    {
      title: 'Type',
      dataIndex: 'fieldType',
      width: 100,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: 'Path',
      dataIndex: 'path',
      width: 120,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Input
          value={row.path}
          placeholder="JSON path"
          onChange={(v) => updateRow(index, { path: v })}
          allowClear
        />
      )
    },
    {
      title: 'Default',
      dataIndex: 'defaultValue',
      width: 140,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Input
          value={row.defaultValue}
          placeholder="Default value or ${var}"
          onChange={(v) => updateRow(index, { defaultValue: v })}
          allowClear
        />
      )
    },
    {
      title: 'Expose',
      dataIndex: 'expose',
      width: 80,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Switch
          checked={row.expose || false}
          onChange={(v) => handleExposeChange(index, v)}
        />
      )
    },
    {
      title: 'Required',
      dataIndex: 'required',
      width: 80,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Switch
          checked={row.required || false}
          onChange={(v) => updateRow(index, { required: v })}
        />
      )
    },
    {
      title: 'Description',
      dataIndex: 'description',
      ellipsis: true,
      render: (_: unknown, row: JsonBodyField, index: number) => (
        <Input
          value={row.description}
          placeholder="Optional"
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
      render: (_: unknown, __: JsonBodyField, index: number) => (
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
      {exposedFields.length > 0 && (
        <div style={{ marginBottom: 12, padding: 8, background: 'var(--color-fill-1)', borderRadius: 4 }}>
          <Text type="secondary">Exposed fields: </Text>
          <Text code>
            {exposedFields.map(f => `\${${f.key}}`).join(', ')}
          </Text>
        </div>
      )}
      <ResizableTable
        data={value}
        columns={columns}
        rowKey={(record: JsonBodyField) => record.id ?? `row-${record.key}-${record.path}`}
        scroll={{ x: 1000 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        Add Field
      </Button>
    </div>
  );
};

export const JsonBodyFieldEditor = connect(JsonBodyFieldEditorInner);

export const parseJsonToFields = parseJsonTemplate;
export const flattenJsonFields = flattenJsonToFields;
