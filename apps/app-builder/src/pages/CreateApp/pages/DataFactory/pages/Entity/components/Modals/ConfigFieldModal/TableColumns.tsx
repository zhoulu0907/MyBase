import React from 'react';
import { FIELD_TYPE, FIELD_TYPE_LABEL } from '@onebase/ui-kit';
import { Button, Checkbox, Form, Input, Popover, Select, Space, Tooltip } from '@arco-design/web-react';
import { IconSelectAll, IconSettings } from '@arco-design/web-react/icon';
import { createFieldRules } from '@/pages/CreateApp/pages/DataFactory/utils/rules';
import styles from './index.module.less';

interface FieldFormValues {
  id?: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: number;
  isRequired: number;
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
  getFieldIndex: (fieldId: string, index: number) => number;
  deleteField: (id: string) => void;
  fields: FieldFormValues[];
}

// 列配置类型
interface ColumnConfig {
  title: string;
  dataIndex: string;
  width?: number;
  ellipsis?: boolean;
  align?: 'center' | 'left' | 'right';
  render?: (value: any, record: FieldFormValues, index: number) => React.ReactNode;
}

const CHECK_CONST = { IS_TRUE: 0, IS_FALSE: 1 };

const TableColumns = ({
  fieldTypeOptions,
  FIELD_TYPES_NEED_CONFIG,
  configPopoverVisible,
  constraintsPopoverVisible,
  setConfigPopoverVisible,
  setConstraintsPopoverVisible,
  renderFieldConfigContent,
  getFieldIndex,
  deleteField
}: TableColumnsProps): ColumnConfig[] => {
  return [
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 180,
      align: 'center',
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.fieldName`}
            rules={[...createFieldRules.fieldName]}
            className={styles['field-form-item']}
          >
            {/* 不可编辑 */}
            <Input
              placeholder="由小写字母、数字、下划线组成，须以字母开头，不超过40个字符"
              disabled={!record.id?.includes('field-')}
            />
          </Form.Item>
        )
    },
    {
      title: '展示名称',
      dataIndex: 'displayName',
      width: 180,
      align: 'center',
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.displayName`}
            rules={[...createFieldRules.displayName]}
            className={styles['field-form-item']}
          >
            <Input />
          </Form.Item>
        )
    },
    {
      title: '数据类型',
      dataIndex: 'fieldType',
      width: 140,
      align: 'center',
      render: (value: string, record: FieldFormValues, index: number) => (
        <Space>
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.fieldType`}
            rules={[{ required: true, message: '数据类型不能为空' }]}
            className={styles['field-form-item']}
          >
            <Select
              options={fieldTypeOptions}
              disabled={record.isSystemField === FIELD_TYPE.SYSTEM || !record.id?.includes('field-')}
              style={{ width: 100 }}
              showSearch
              filterOption={(input, option) => {
                return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
              }}
            />
          </Form.Item>
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
      width: 200,
      align: 'center',
      ellipsis: true,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === 1 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.description`}
            className={styles['field-form-item']}
          >
            <Input placeholder="请输入字段描述" />
          </Form.Item>
        )
    },
    {
      title: '字段类型',
      dataIndex: 'isSystemField',
      width: 110,
      align: 'center',
      ellipsis: true,
      render: (value: number) => (
        <span className={styles['system-field']}>{FIELD_TYPE_LABEL[value as keyof typeof FIELD_TYPE_LABEL]}</span>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      align: 'center',
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.defaultValue`}
            className={styles['field-form-item']}
          >
            <Input />
          </Form.Item>
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      align: 'center',
      render: (value: number, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.isUnique`}
            className={styles['field-form-item']}
            triggerPropName="checked"
            normalize={(v) => (v ? CHECK_CONST.IS_TRUE : CHECK_CONST.IS_FALSE)}
            formatter={(v) => v === CHECK_CONST.IS_TRUE || v === true}
          >
            <Checkbox />
          </Form.Item>
        )
    },
    {
      title: '必填',
      dataIndex: 'isRequired',
      width: 100,
      align: 'center',
      render: (value: number, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id, index)}.isRequired`}
            className={styles['field-form-item']}
            triggerPropName="checked"
            normalize={(v) => (v ? CHECK_CONST.IS_TRUE : CHECK_CONST.IS_FALSE)}
            formatter={(v) => v === CHECK_CONST.IS_TRUE || v === true}
          >
            <Checkbox />
          </Form.Item>
        )
    },
    {
      title: '字段约束',
      dataIndex: 'constraints',
      width: 120,
      align: 'center',
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
      align: 'center',
      render: (value: unknown, record: FieldFormValues) => {
        return (
          record.isSystemField === FIELD_TYPE.CUSTOM && (
            <Button type="text" status="danger" size="mini" onClick={() => deleteField(record.id)} disabled>
              删除
            </Button>
          )
        );
      }
    }
  ];
};

export default TableColumns;
