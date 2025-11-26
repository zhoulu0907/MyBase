import React from 'react';
import { FIELD_TYPE, FIELD_TYPE_LABEL, ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { Button, Checkbox, Form, Input, Select, Space } from '@arco-design/web-react';
import { IconSelectAll, IconSettings, IconEdit } from '@arco-design/web-react/icon';
import { createFieldRules } from '@/pages/CreateApp/pages/DataFactory/utils/rules';
import { FIELD_CONSTRAINT_LENGTH_ENABLED, FIELD_CONSTRAINT_REGEX_ENABLED } from '@onebase/ui-kit';
import { ModalPopover } from '@/components/ModalPopover';
import type { FieldFormValues, ColumnConfig, AutoCodeRule } from '../types';
import { CHECK_CONST, AUTO_CODE_INITIAL_RULES } from '../utils/const';
import { convertAutoCodeCompoToAutoNumberRule } from '../utils/transform';
import styles from '../index.module.less';

interface TableColumnsProps {
  fieldTypeOptions: { label: string; value: string }[];
  FIELD_TYPES_NEED_CONFIG: string[];
  configPopoverVisible: string | null;
  constraintsPopoverVisible: string | null;
  setConfigPopoverVisible: (id: string | null) => void;
  setConstraintsPopoverVisible: (id: string | null) => void;
  renderFieldConfigContent: (fieldType: string, fieldId: string) => React.ReactNode;
  externalErrors: Record<string, string>;
  getFieldIndex: (fieldId: string) => number;
  deleteField: (id: string) => void;
  fields: FieldFormValues[];
  handleConfigConfirm: (fieldType: string, fieldId: string, configData: unknown, dictTypeId?: string) => void;
}

// 渲染表单字段组件
const renderFormField = (
  field: string,
  record: FieldFormValues,
  _: number,
  rules: readonly unknown[],
  externalErrors: Record<string, string>,
  getFieldIndex: (fieldId: string) => number,
  children: React.ReactNode,
  disabled?: boolean
) => {
  const fieldIndex = getFieldIndex(record.id || '');
  const errorKey = `fields.${fieldIndex}.${field}`;

  return (
    <Form.Item
      field={`fields.${fieldIndex}.${field}`}
      rules={rules as unknown[]}
      className={styles.fieldFormItem}
      validateStatus={externalErrors[errorKey] ? 'error' : undefined}
      help={externalErrors[errorKey]}
    >
      {React.cloneElement(children as React.ReactElement, { disabled })}
    </Form.Item>
  );
};

// 渲染系统字段显示
const renderSystemField = (value: string) => <span className={styles.systemField}>{value}</span>;

// 渲染配置按钮
const renderConfigButton = (
  fieldType: string,
  record: FieldFormValues,
  configPopoverVisible: string | null,
  setConfigPopoverVisible: (id: string | null) => void,
  renderFieldConfigContent: (fieldType: string, fieldId: string) => React.ReactNode,
  FIELD_TYPES_NEED_CONFIG: string[]
) => {
  if (!FIELD_TYPES_NEED_CONFIG.includes(fieldType)) {
    return null;
  }

  const isVisible = configPopoverVisible === record.id;
  const fieldId = record.id || '';

  return (
    <ModalPopover
      width="350px"
      key={`config-popover-${fieldId}`}
      content={renderFieldConfigContent(fieldType, fieldId)}
      trigger="click"
      visible={isVisible}
      onVisibleChange={(visible: boolean) => {
        if (visible) {
          setConfigPopoverVisible(fieldId);
        } else {
          setConfigPopoverVisible(null);
        }
      }}
      getPopupContainer={() => document.body}
      placement="auto"
      inModal={true}
      stopPropagation={true}
    >
      <Button
        type="text"
        size="mini"
        icon={<IconSettings />}
        onClick={() => {
          setConfigPopoverVisible(isVisible ? null : fieldId);
        }}
      />
    </ModalPopover>
  );
};

// 渲染约束配置
const renderConstraintConfig = (
  record: FieldFormValues,
  constraintsPopoverVisible: string | null,
  setConstraintsPopoverVisible: (id: string | null) => void,
  renderFieldConfigContent: (fieldType: string, fieldId: string) => React.ReactNode
) => {
  if (record.isSystemField === FIELD_TYPE.SYSTEM) {
    return <span className={styles.systemField}>-</span>;
  }

  const fieldId = record.id || '';
  const isVisible = constraintsPopoverVisible === fieldId;
  const hasConstraints =
    record.constraints &&
    (record.constraints.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE ||
      record.constraints.regexEnabled === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE);

  if (hasConstraints) {
    const lengthStatus =
      record.constraints?.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE ? '已开启' : '未开启';
    const regexStatus =
      record.constraints?.regexEnabled === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE ? '已开启' : '未开启';

    return (
      <div className={styles.constraintStatus}>
        <div className={styles.constraintInfo}>
          <div>{lengthStatus}长度范围约束</div>
          <div>{regexStatus}正则表达式验证</div>
        </div>
        <ModalPopover
          width="350px"
          content={renderFieldConfigContent('CONSTRAINTS', fieldId)}
          key={`constraint-popover-${fieldId}`}
          trigger="click"
          visible={isVisible}
          onVisibleChange={(visible) => {
            if (visible) {
              setConstraintsPopoverVisible(fieldId);
            } else {
              setConstraintsPopoverVisible(null);
            }
          }}
          placement="auto"
          getPopupContainer={() => document.body}
          inModal={true}
          stopPropagation={true}
        >
          <Button
            type="text"
            size="mini"
            icon={<IconEdit />}
            onClick={(e) => {
              e.stopPropagation();
              setConstraintsPopoverVisible(isVisible ? null : fieldId);
            }}
            className={styles.editConstraintBtn}
          />
        </ModalPopover>
      </div>
    );
  }

  return (
    <ModalPopover
      width="350px"
      content={renderFieldConfigContent('CONSTRAINTS', fieldId)}
      key={`constraint-popover-${fieldId}`}
      trigger="click"
      visible={isVisible}
      onVisibleChange={(visible) => {
        if (visible) {
          setConstraintsPopoverVisible(fieldId);
        } else {
          setConstraintsPopoverVisible(null);
        }
      }}
      getPopupContainer={() => document.body}
      placement="auto"
      inModal={true}
      stopPropagation={true}
    >
      <Button
        size="mini"
        icon={<IconSelectAll />}
        onClick={(e) => {
          e.stopPropagation();
          setConstraintsPopoverVisible(isVisible ? null : fieldId);
        }}
      >
        配置字段约束
      </Button>
    </ModalPopover>
  );
};

const TableColumns = ({
  fieldTypeOptions,
  FIELD_TYPES_NEED_CONFIG,
  configPopoverVisible,
  constraintsPopoverVisible,
  setConfigPopoverVisible,
  setConstraintsPopoverVisible,
  renderFieldConfigContent,
  externalErrors,
  getFieldIndex,
  deleteField,
  handleConfigConfirm
}: TableColumnsProps): ColumnConfig[] => {
  return [
    {
      title: (
        <>
          <span className={styles.requiredDot}>*</span>
          <span>字段名称</span>
        </>
      ),
      dataIndex: 'fieldName',
      width: 175,
      align: 'center',
      render: (value: unknown, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM
          ? renderSystemField(value as string)
          : renderFormField(
              'fieldName',
              record,
              index,
              createFieldRules.fieldName,
              externalErrors,
              getFieldIndex,
              <Input placeholder="由小写字母、数字、下划线组成，须以字母开头，不超过40个字符" />,
              !record.id?.includes('field-')
            )
    },
    {
      title: (
        <>
          <span className={styles.requiredDot}>*</span>
          <span>展示名称</span>
        </>
      ),
      dataIndex: 'displayName',
      width: 175,
      align: 'center',
      render: (value: unknown, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM
          ? renderSystemField(value as string)
          : renderFormField(
              'displayName',
              record,
              index,
              createFieldRules.displayName,
              externalErrors,
              getFieldIndex,
              <Input />
            )
    },
    {
      title: (
        <>
          <span className={styles.requiredDot}>*</span>
          <span>数据类型</span>
        </>
      ),
      dataIndex: 'fieldType',
      width: 140,
      align: 'center',
      render: (_: unknown, record: FieldFormValues, index: number) => (
        <Space>
          {renderFormField(
            'fieldType',
            record,
            index,
            [{ required: true, message: '数据类型不能为空' }],
            externalErrors,
            getFieldIndex,
            <Select
              options={fieldTypeOptions}
              style={{ width: 100 }}
              showSearch
              filterOption={(input, option) => {
                return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
              }}
              onChange={(value) => {
                // 当选择自动编号类型时，如果字段还没有配置，自动创建默认规则
                if (value === ENTITY_FIELD_TYPE.AUTO_CODE.VALUE && !record.autoNumber && !record.autoNumberConfig) {
                  const defaultRules: AutoCodeRule[] = AUTO_CODE_INITIAL_RULES;
                  const autoNumberRule = convertAutoCodeCompoToAutoNumberRule(defaultRules);
                  handleConfigConfirm(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE, record.id || '', autoNumberRule);
                }
              }}
            />
          )}
          <Form.Item
            className={styles.fieldFormItem}
            shouldUpdate={(prev, next) => {
              const fieldIndex = getFieldIndex(record.id || '');
              return prev?.fields?.[fieldIndex]?.fieldType !== next?.fields?.[fieldIndex]?.fieldType;
            }}
          >
            {(values) => {
              const fieldIndex = getFieldIndex(record.id || '');
              const fieldType = values?.fields?.[fieldIndex]?.fieldType;
              return renderConfigButton(
                fieldType,
                record,
                configPopoverVisible,
                setConfigPopoverVisible,
                renderFieldConfigContent,
                FIELD_TYPES_NEED_CONFIG
              );
            }}
          </Form.Item>
        </Space>
      )
    },
    {
      title: '字段描述',
      dataIndex: 'description',
      width: 200,
      align: 'center',
      ellipsis: true,
      render: (_: unknown, record: FieldFormValues, index: number) =>
        record.isSystemField === 1
          ? renderSystemField(record.description)
          : renderFormField(
              'description',
              record,
              index,
              [],
              externalErrors,
              getFieldIndex,
              <Input placeholder="请输入字段描述" />
            )
    },
    {
      title: '字段类型',
      dataIndex: 'isSystemField',
      width: 110,
      align: 'center',
      ellipsis: true,
      render: (value: unknown) => (
        <span className={styles.systemField}>{FIELD_TYPE_LABEL[value as keyof typeof FIELD_TYPE_LABEL]}</span>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      align: 'center',
      render: (_: unknown, record: FieldFormValues, index: number) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles.systemField}>-</span>
        ) : (
          renderFormField('defaultValue', record, index, [], externalErrors, getFieldIndex, <Input />)
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      align: 'center',
      render: (_: unknown, record: FieldFormValues) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles.systemField}>-</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id || '')}.isUnique`}
            className={styles.fieldFormItem}
            triggerPropName="checked"
            normalize={(v) => (v ? CHECK_CONST.IS_TRUE : CHECK_CONST.IS_FALSE)}
            formatter={(v) => v === CHECK_CONST.IS_TRUE || v === true}
            validateStatus={externalErrors[`fields.${getFieldIndex(record.id || '')}.isUnique`] ? 'error' : undefined}
            help={externalErrors[`fields.${getFieldIndex(record.id || '')}.isUnique`]}
          >
            <Checkbox />
          </Form.Item>
        )
    },
    {
      title: '必填',
      dataIndex: 'isRequired',
      width: 60,
      align: 'center',
      render: (_: unknown, record: FieldFormValues) =>
        record.isSystemField === FIELD_TYPE.SYSTEM ? (
          <span className={styles.systemField}>-</span>
        ) : (
          <Form.Item
            field={`fields.${getFieldIndex(record.id || '')}.isRequired`}
            className={styles.fieldFormItem}
            triggerPropName="checked"
            normalize={(v) => (v ? CHECK_CONST.IS_TRUE : CHECK_CONST.IS_FALSE)}
            formatter={(v) => v === CHECK_CONST.IS_TRUE || v === true}
            validateStatus={externalErrors[`fields.${getFieldIndex(record.id || '')}.isRequired`] ? 'error' : undefined}
            help={externalErrors[`fields.${getFieldIndex(record.id || '')}.isRequired`]}
          >
            <Checkbox />
          </Form.Item>
        )
    },
    {
      title: '字段约束',
      dataIndex: 'constraints',
      width: 190,
      align: 'center',
      render: (_: unknown, record: FieldFormValues) =>
        renderConstraintConfig(
          record,
          constraintsPopoverVisible,
          setConstraintsPopoverVisible,
          renderFieldConfigContent
        )
    },
    {
      title: '操作',
      dataIndex: 'operation',
      width: 70,
      align: 'center',
      render: (_: unknown, record: FieldFormValues) => {
        return (
          record.isSystemField === FIELD_TYPE.CUSTOM && (
            <Button type="text" status="danger" size="mini" onClick={() => deleteField(record.id || '')}>
              删除
            </Button>
          )
        );
      }
    }
  ];
};

export default TableColumns;
