import ResizableTable from '@/components/ResizableTable';
import { Button, Input, Select, Space, Typography } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { connect, useField } from '@formily/react';
import React, { useEffect } from 'react';
import type { SuccessConditionRow } from './types';

const { Text } = Typography;

const FIELD_TYPE_OPTIONS = [
  { label: 'HTTP状态码', value: 'statusCode' },
  { label: '响应头', value: 'responseHeader' },
  { label: '响应体', value: 'responseBody' }
];

const DATA_TYPE_OPTIONS = [
  { label: '字符串', value: 'string' },
  { label: '数字', value: 'number' },
  { label: '布尔', value: 'boolean' }
];

const OPERATOR_OPTIONS = [
  { label: '等于', value: 'eq' },
  { label: '不等于', value: 'ne' },
  { label: '在...之内', value: 'in' },
  { label: '不在...之内', value: 'notIn' },
  { label: '大于', value: 'gt' },
  { label: '小于', value: 'lt' },
  { label: '大于等于', value: 'gte' },
  { label: '小于等于', value: 'lte' }
];

const VALUE_SOURCE_OPTIONS = [
  { label: '自定义', value: 'custom' }
];

const defaultRow: SuccessConditionRow = {
  fieldType: 'statusCode',
  fieldName: '',
  dataType: 'number',
  operator: 'eq',
  valueSource: 'custom',
  value: '200'
};

const SuccessConditionTableInner: React.FC = () => {
  const rawField = useField() as { value?: SuccessConditionRow[]; setValue: (v: SuccessConditionRow[]) => void };
  const value = (rawField.value ?? []) as SuccessConditionRow[];

  const setValue = (next: SuccessConditionRow[]) => {
    rawField.setValue(next);
  };

  useEffect(() => {
    if (value.length === 0) {
      setValue([
        {
          id: `cond-${Date.now()}-${Math.random().toString(36).slice(2)}`,
          fieldType: 'statusCode',
          fieldName: '',
          dataType: 'number',
          operator: 'eq',
          valueSource: 'custom',
          value: '200'
        }
      ]);
    }
  }, []);

  const updateRow = (index: number, patch: Partial<SuccessConditionRow>) => {
    const next = [...value];
    next[index] = { ...next[index], ...patch };
    
    if (patch.fieldType === 'statusCode') {
      next[index].fieldName = '';
      if (!patch.dataType) next[index].dataType = 'number';
      if (!patch.value) next[index].value = '200';
    }
    
    setValue(next);
  };

  const addRow = () => {
    setValue([
      ...value,
      { ...defaultRow, id: `cond-${Date.now()}-${Math.random().toString(36).slice(2)}` }
    ]);
  };

  const removeRow = (index: number) => {
    const next = value.filter((_, i) => i !== index);
    setValue(next);
  };

  const columns = [
    {
      title: '字段类型',
      dataIndex: 'fieldType',
      width: 170,
      render: (_: unknown, row: SuccessConditionRow, index: number) => (
        <Select
          value={row.fieldType}
          options={FIELD_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { fieldType: v as SuccessConditionRow['fieldType'] })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 160,
      render: (_: unknown, row: SuccessConditionRow, index: number) => {
        if (row.fieldType === 'statusCode') {
          return <Text type="secondary">状态码</Text>;
        }
        return (
          <Input
            value={row.fieldName}
            placeholder={row.fieldType === 'responseHeader' ? '响应头名称' : 'JSON路径'}
            onChange={(v) => updateRow(index, { fieldName: v })}
            allowClear
          />
        );
      }
    },
    {
      title: '数据类型',
      dataIndex: 'dataType',
      width: 130,
      render: (_: unknown, row: SuccessConditionRow, index: number) => (
        <Select
          value={row.dataType}
          options={DATA_TYPE_OPTIONS}
          onChange={(v) => updateRow(index, { dataType: v as SuccessConditionRow['dataType'] })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '运算符',
      dataIndex: 'operator',
      width: 120,
      render: (_: unknown, row: SuccessConditionRow, index: number) => (
        <Select
          value={row.operator}
          options={OPERATOR_OPTIONS}
          onChange={(v) => updateRow(index, { operator: v as SuccessConditionRow['operator'] })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '值来源',
      dataIndex: 'valueSource',
      width: 100,
      render: (_: unknown, row: SuccessConditionRow, index: number) => (
        <Select
          value={row.valueSource}
          options={VALUE_SOURCE_OPTIONS}
          onChange={(v) => updateRow(index, { valueSource: v as SuccessConditionRow['valueSource'] })}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '值',
      dataIndex: 'value',
      ellipsis: true,
      render: (_: unknown, row: SuccessConditionRow, index: number) => (
        <Input
          value={row.value}
          placeholder="请输入值"
          onChange={(v) => updateRow(index, { value: v })}
          allowClear
        />
      )
    },
    {
      title: '',
      dataIndex: '_op',
      width: 60,
      fixed: 'right' as const,
      render: (_: unknown, __: SuccessConditionRow, index: number) => (
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
      <div style={{ marginBottom: 12 }}>
        <Text style={{ fontWeight: 500 }}>调用成功规范</Text>
      </div>
      <ResizableTable
        data={value}
        columns={columns}
        rowKey={(record: SuccessConditionRow) => record.id ?? `row-${Date.now()}`}
        scroll={{ x: 1000 }}
        pagination={false}
        size="small"
      />
      <Button type="dashed" long icon={<IconPlus />} onClick={addRow} style={{ marginTop: 8 }}>
        添加配置
      </Button>
    </div>
  );
};

export const SuccessConditionTable = connect(SuccessConditionTableInner);
