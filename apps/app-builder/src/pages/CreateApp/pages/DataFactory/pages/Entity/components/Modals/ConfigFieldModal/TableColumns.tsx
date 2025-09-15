import React from 'react';
import { FIELD_TYPE, FIELD_TYPE_LABEL } from '@onebase/ui-kit';
import { Button, Checkbox, Input, Popover, Select, Space, Tooltip } from '@arco-design/web-react';
import { IconDragDotVertical, IconSelectAll, IconSettings } from '@arco-design/web-react/icon';
import styles from './index.module.less';

interface FieldFormValues {
  id?: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: number;
  allowNull: number;
  constraints?: {
    lengthEnabled: number;
    minLength: number;
    maxLength: number;
    lengthPrompt: string;
    regexEnabled: number;
    regexPattern: string;
    regexPrompt: string;
  };
  isSystemField: number;
  sortOrder?: number;
  isDeleted?: boolean;
  displayName?: string;
}

interface TableColumnsProps {
  fieldTypeOptions: { label: string; value: string }[];
  FIELD_TYPES_NEED_CONFIG: string[];
  configPopoverVisible: string | null;
  constraintsPopoverVisible: string | null;
  setConfigPopoverVisible: (id: string | null) => void;
  setConstraintsPopoverVisible: (id: string | null) => void;
  renderFieldConfigContent: (fieldType: string, fieldId: string) => React.ReactNode;
  updateField: (index: number, updatedField: Partial<FieldFormValues>) => void;
  getFieldIndex: (fieldId: string, index: number) => number;
  deleteField: (index: number) => void;
  fields: FieldFormValues[];
}

// 列配置类型
interface ColumnConfig {
  title: string;
  dataIndex: string;
  width?: number;
  ellipsis?: boolean;
  render?: (value: any, record: FieldFormValues, index: number) => React.ReactNode;
}

const TableColumns = ({
  fieldTypeOptions,
  FIELD_TYPES_NEED_CONFIG,
  configPopoverVisible,
  constraintsPopoverVisible,
  setConfigPopoverVisible,
  setConstraintsPopoverVisible,
  renderFieldConfigContent,
  updateField,
  getFieldIndex,
  deleteField,
  fields
}: TableColumnsProps): ColumnConfig[] => {
  return [
    {
      title: '',
      dataIndex: 'sortOrder',
      width: 40,
      render: (value: number, record: FieldFormValues) => {
        if (record.isSystemField === FIELD_TYPE.SYSTEM) {
          return null;
        }
        return <IconDragDotVertical className={styles['drag-handle']} />;
      }
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="由小写字母、数字、下划线组成，须以字母开头，不超过40个字符"
            onChange={(val) => updateField(getFieldIndex(record.id, index), { fieldName: val })}
          />
        )
    },
    {
      title: '展示名称',
      dataIndex: 'displayName',
      width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input value={value} onChange={(val) => updateField(getFieldIndex(record.id, index), { displayName: val })} />
        )
    },
    {
      title: '数据类型',
      dataIndex: 'fieldType',
      width: 140,
      render: (value: string, record: FieldFormValues, index: number) => (
        <Space>
          <Select
            value={value}
            options={fieldTypeOptions}
            onChange={(val) => updateField(getFieldIndex(record.id, index), { fieldType: val })}
            disabled={record.isSystemField === FIELD_TYPE.SYSTEM}
            style={{ width: 100 }}
            showSearch
            filterOption={(input, option) => {
              return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
          />
          {record.isSystemField === FIELD_TYPE.CUSTOM && FIELD_TYPES_NEED_CONFIG.includes(value) && (
            <Popover
              content={renderFieldConfigContent(value, record.id)}
              trigger="click"
              popupVisible={configPopoverVisible === record.id}
              onVisibleChange={(visible) => setConfigPopoverVisible(visible ? record.id : null)}
            >
              <Tooltip content="配置">
                <Button
                  type="text"
                  size="mini"
                  icon={<IconSettings />}
                  onClick={() => setConfigPopoverVisible(record.id)}
                />
              </Tooltip>
            </Popover>
          )}
        </Space>
      )
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      width: 250,
      ellipsis: true,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === 1 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段描述"
            onChange={(val) => updateField(getFieldIndex(record.id, index), { description: val })}
          />
        )
    },
    {
      title: '字段类型',
      dataIndex: 'isSystemField',
      width: 110,
      ellipsis: true,
      render: (value: number) => (
        <span className={styles['system-field']}>{FIELD_TYPE_LABEL[value as keyof typeof FIELD_TYPE_LABEL]}</span>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Input
            value={value}
            onChange={(val) => updateField(getFieldIndex(record.id, index), { defaultValue: val })}
          />
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      render: (value: number, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox
            checked={value === 0}
            onChange={(checked) => updateField(getFieldIndex(record.id, index), { isUnique: checked ? 0 : 1 })}
          />
        )
    },
    {
      title: '允许空值',
      dataIndex: 'allowNull',
      width: 100,
      render: (value: number, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox
            checked={value === 0}
            onChange={(checked) => updateField(getFieldIndex(record.id, index), { allowNull: checked ? 0 : 1 })}
          />
        )
    },
    {
      title: '字段约束',
      dataIndex: 'constraints',
      render: (value: any, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Popover
            content={renderFieldConfigContent('CONSTRAINTS', record.id)}
            trigger="click"
            popupVisible={constraintsPopoverVisible === record.id}
            onVisibleChange={(visible) => setConstraintsPopoverVisible(visible ? record.id : null)}
          >
            <Button size="mini" icon={<IconSelectAll />} onClick={() => setConstraintsPopoverVisible(record.id)}>
              配置字段约束
            </Button>
          </Popover>
        )
    },
    {
      title: '操作',
      dataIndex: 'operation',
      width: 80,
      render: (value: unknown, record: FieldFormValues) => {
        const fieldIndex = fields.findIndex((f) => f.id === record.id);
        return (
          record.isSystemField === FIELD_TYPE.CUSTOM && (
            <Button type="text" status="danger" size="mini" onClick={() => deleteField(fieldIndex)}>
              删除
            </Button>
          )
        );
      }
    }
  ];
};

export default TableColumns;
