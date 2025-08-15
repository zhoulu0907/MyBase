import { ENTITY_FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import {
  Button,
  Checkbox,
  Input,
  Message,
  Modal,
  Popover,
  Select,
  Space,
  Table,
  Tooltip
} from '@arco-design/web-react';
import { IconDragDotVertical, IconPlus, IconSelectAll, IconSettings } from '@arco-design/web-react/icon';
import { batchSaveFields, getEntityFields } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../modal.module.less';
import { PicklistConfig, MultiPicklistConfig, AutoCodeConfig, type AutoCodeRule } from './FieldTypeConfig';

interface FieldFormValues {
  id?: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: number;
  allowNull: number;
  constraints: string;
  isSystemField: number;
  sortOrder?: number;
  isDeleted?: boolean;
  displayName?: string;
  // 字段配置数据
  fieldConfig?: {
    options?: string[]; // 单选/多选列表的选项
    autoCodeRules?: AutoCodeRule[]; // 自动编号规则
  };
}

interface ConfigFieldModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: Partial<EntityNode>;
  successCallback: () => void;
  initialFields?: FieldFormValues[];
}

// 需要额外配置的字段类型
const FIELD_TYPES_NEED_CONFIG = ['PICKLIST', 'MULTI_PICKLIST', 'AUTO_CODE'];

// 字段类型选项
const fieldTypeOptions = Object.entries(ENTITY_FIELD_TYPE).map(([key, value]) => ({
  label: value as string,
  value: key
}));

// 自定义表格行组件，支持拖拽
const SortableTableRow = (props) => {
  const { record, children, ...restProps } = props;
  return <tr {...restProps}>{children}</tr>;
};

const ConfigFieldModal: React.FC<ConfigFieldModalProps> = ({ visible, setVisible, entity, successCallback }) => {
  const { curAppId } = useAppStore();
  const [fields, setFields] = useState<FieldFormValues[]>([]);
  const [loading, setLoading] = useState(false);
  const [configPopoverVisible, setConfigPopoverVisible] = useState<string | null>(null);

  useEffect(() => {
    if (visible) {
      getEntityFields({ entityId: entity.entityId }).then((res) => {
        console.log('getEntityFields', res);
        setFields(
          res.map((field: object, index: number) => ({
            ...field,
            sortOrder: index
          }))
        );
      });
    }
  }, [visible]);

  // 过滤掉已删除的字段
  const activeFields = fields.filter((field) => !field.isDeleted && field.isSystemField === 1);

  const addField = () => {
    const newField: FieldFormValues = {
      fieldCode: '',
      fieldName: '',
      displayName: '',
      description: '',
      fieldType: 'TEXT',
      defaultValue: '',
      isUnique: 1,
      allowNull: 1,
      constraints: '',
      isSystemField: 1,
      sortOrder: activeFields.length
    };
    setFields([...activeFields, newField]);
  };

  const deleteField = (index: number) => {
    const field = fields[index];
    if (field.isSystemField === 0) {
      Message.error('系统字段不能删除');
      return;
    }
    setFields(fields.map((f, i) => (i === index ? { ...f, isDeleted: true } : f)));
  };

  const updateField = (index: number, updatedField: Partial<FieldFormValues>) => {
    setFields((prevFields) => prevFields.map((field, i) => (i === index ? { ...field, ...updatedField } : field)));
  };

  // 获取字段在数组中的索引
  const getFieldIndex = (fieldId: string, index: number) => {
    if (index) {
      return index;
    } else {
      return fields.findIndex((field) => field.id === fieldId);
    }
  };

  const handleSort = (newFields: FieldFormValues[]) => {
    console.log('handleSort', newFields);
    // 获取所有字段（包括已删除的）
    const allFields = [...fields];
    // 更新可见字段的排序
    const visibleFields = newFields.map((field, index) => ({ ...field, sortOrder: index }));

    // 更新所有字段的排序
    setFields(
      allFields.map((field) => {
        const visibleField = visibleFields.find((vf) => vf.id === field.id);
        return visibleField ? { ...field, sortOrder: visibleField.sortOrder } : field;
      })
    );
  };

  const handleFinish = async () => {
    try {
      setLoading(true);

      // 过滤自定义字段（排除系统字段和已删除字段）
      const customFields = fields.filter((field) => field.isSystemField === 1 && !field.isDeleted);

      // 表单校验
      for (const field of customFields) {
        if (!field.displayName || !field.displayName.trim()) {
          Message.error('展示名称不能为空');
          return;
        }
        if (!field.fieldName || !field.fieldName.trim()) {
          Message.error('字段名称不能为空');
          return;
        }
        if (!field.fieldType) {
          Message.error('数据类型不能为空');
          return;
        }
      }

      // 准备所有字段数据（包括标记为删除的）
      const allFields = fields.filter((field) => field.isSystemField === 1);
      const fieldDataList = allFields.map((field) => {
        const fieldData = {
          appId: curAppId,
          entityId: entity.entityId,
          ...field,
          isSystemField: 1,
          isDeleted: field.isDeleted || false
        };

        return field.id ? { ...fieldData, id: field.id } : fieldData;
      });

      const params = {
        appId: curAppId,
        entityId: entity.entityId,
        items: fieldDataList
      };

      await batchSaveFields(params);
      Message.success('保存成功');
      setVisible(false);
      successCallback();
    } catch (error) {
      console.error('保存字段失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 处理配置确认
  const handleConfigConfirm = (fieldType: string, fieldId: string, configData: any) => {
    const fieldIndex = fields.findIndex((field) => field.id === fieldId);
    if (fieldIndex === -1) return;

    let fieldConfig = {};
    switch (fieldType) {
      case 'PICKLIST':
      case 'MULTI_PICKLIST':
        fieldConfig = { options: configData };
        break;
      case 'AUTO_CODE':
        fieldConfig = { autoCodeRules: configData };
        break;
    }

    updateField(fieldIndex, { fieldConfig });
    // 配置完成后关闭 popover
    setConfigPopoverVisible(null);
  };

  const renderFieldConfig = (fieldType: string, fieldId: string) => {
    const field = fields.find((f) => f.id === fieldId);
    const hasConfig = field?.fieldConfig;

    return (
      <div className={styles['field-config-popover']} style={{ width: '400px', padding: '16px' }}>
        <div style={{ marginBottom: '16px' }}>
          {hasConfig ? (
            <div>
              <p style={{ color: '#52c41a', marginBottom: 8 }}>✓ 已配置</p>
              {fieldType === 'AUTO_CODE' && field.fieldConfig?.autoCodeRules && (
                <p style={{ fontSize: '12px', color: '#666' }}>规则数量: {field.fieldConfig.autoCodeRules.length}</p>
              )}
              {(fieldType === 'PICKLIST' || fieldType === 'MULTI_PICKLIST') && field.fieldConfig?.options && (
                <p style={{ fontSize: '12px', color: '#666' }}>选项数量: {field.fieldConfig.options.length}</p>
              )}
            </div>
          ) : (
            <p style={{ color: '#ff4d4f' }}>⚠ 未配置</p>
          )}
        </div>

        {/* 直接在 Popover 中渲染配置组件 */}
        {fieldType === 'PICKLIST' && (
          <PicklistConfig
            onConfirm={(options) => handleConfigConfirm('PICKLIST', fieldId, options)}
            initialOptions={field?.fieldConfig?.options}
            onCancel={() => setConfigPopoverVisible(null)}
          />
        )}

        {fieldType === 'MULTI_PICKLIST' && (
          <MultiPicklistConfig
            onConfirm={(options) => handleConfigConfirm('MULTI_PICKLIST', fieldId, options)}
            initialOptions={field?.fieldConfig?.options}
            onCancel={() => setConfigPopoverVisible(null)}
          />
        )}

        {fieldType === 'AUTO_CODE' && (
          <AutoCodeConfig
            onConfirm={(rules) => handleConfigConfirm('AUTO_CODE', fieldId, rules)}
            initialRules={field?.fieldConfig?.autoCodeRules}
            onCancel={() => setConfigPopoverVisible(null)}
          />
        )}
      </div>
    );
  };

  const columns = [
    {
      title: '',
      dataIndex: 'sortOrder',
      width: 40,
      render: (value: number, record: FieldFormValues) => {
        // 系统字段不能拖拽
        if (record.isSystemField === 0) {
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
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段名称"
            onChange={(val) => updateField(getFieldIndex(record.id, index), { fieldName: val })}
          />
        )
    },
    {
      title: '展示名称',
      dataIndex: 'displayName',
      width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入展示名称"
            onChange={(val) => updateField(getFieldIndex(record.id, index), { displayName: val })}
          />
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
            disabled={record.isSystemField === 0}
            style={{ width: 100 }}
          />
          {record.isSystemField === 1 && FIELD_TYPES_NEED_CONFIG.includes(value) && (
            <Popover
              content={renderFieldConfig(value, record.id)}
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
        record.isSystemField === 0 ? (
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
        <span className={styles['system-field']}>{value === 0 ? '系统字段' : '自定义字段'}</span>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入默认值"
            onChange={(val) => updateField(getFieldIndex(record.id, index), { defaultValue: val })}
          />
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      render: (value: number, record: FieldFormValues, index: number) =>
        record.isSystemField === 0 ? (
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
        record.isSystemField === 0 ? (
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
      // width: 120,
      render: (value: string, record: FieldFormValues, index: number) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value || '-'}</span>
        ) : (
          <Popover
            content={renderFieldConfig('CONSTRAINTS', record.id)}
            trigger="click"
            popupVisible={configPopoverVisible === record.id}
            onVisibleChange={(visible) => setConfigPopoverVisible(visible ? record.id : null)}
          >
            <Button size="mini" icon={<IconSelectAll />} onClick={() => setConfigPopoverVisible(record.id)}>
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
          record.isSystemField === 1 && (
            <Button type="text" status="danger" size="mini" onClick={() => deleteField(fieldIndex)}>
              删除
            </Button>
          )
        );
      }
    }
  ];

  return (
    <Modal
      className={styles['config-field-modal']}
      title="字段配置"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="保存"
      cancelText="取消"
      confirmLoading={loading}
      style={{ width: 1400 }}
    >
      <div className={styles['field-config-container']}>
        <ReactSortable
          list={activeFields}
          setList={handleSort}
          animation={200}
          handle={`.${styles['drag-handle']}`}
          filter={`.${styles['system-field']}`}
          tag="tbody" // 指定包装元素为tbody
        >
          <Table
            data={activeFields}
            columns={columns}
            pagination={false}
            className={styles['field-table']}
            rowClassName={(record) =>
              record.isSystemField === 0 ? styles['system-field-row'] : styles['custom-field-row']
            }
            rowKey="id"
            components={{
              body: {
                row: SortableTableRow
              }
            }}
          />
        </ReactSortable>

        <div className={styles['add-field-section']}>
          <Button type="dashed" icon={<IconPlus />} onClick={addField} className={styles['add-field-button']}>
            新增字段
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default ConfigFieldModal;
