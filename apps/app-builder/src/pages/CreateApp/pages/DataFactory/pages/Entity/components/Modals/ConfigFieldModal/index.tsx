import { ENTITY_FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
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
import { IconDragDotVertical, IconPlus, IconSettings } from '@arco-design/web-react/icon';
import { batchSaveFields, getEntityFields } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../modal.module.less';

interface FieldFormValues {
  id?: string;
  fieldCode: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: boolean;
  allowNull: boolean;
  constraints: string;
  isSystemField: number;
  sortOrder?: number;
  isDeleted?: boolean;
}

interface ConfigFieldModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: Partial<EntityNode>;
  successCallback: () => void;
  initialFields?: FieldFormValues[];
}

// 需要额外配置的字段类型
const FIELD_TYPES_NEED_CONFIG = [
  ENTITY_FIELD_TYPE.PICKLIST,
  ENTITY_FIELD_TYPE.MULTI_PICKLIST,
  ENTITY_FIELD_TYPE.AUTO_CODE
];

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
  const activeFields = fields.filter((field) => !field.isDeleted);

  const addField = () => {
    const newField: FieldFormValues = {
      fieldCode: '',
      fieldName: '',
      description: '',
      fieldType: 'TEXT',
      defaultValue: '',
      isUnique: false,
      allowNull: true,
      constraints: '',
      isSystemField: 1,
      sortOrder: fields.length
    };
    setFields([...fields, newField]);
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
  const getFieldIndex = (fieldId: string) => {
    return fields.findIndex((field) => field.id === fieldId);
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
        if (!field.fieldCode || !field.fieldCode.trim()) {
          Message.error('字段编码不能为空');
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
          appId: '1',
          entityId: entity.entityId,
          fieldCode: field.fieldCode,
          fieldName: field.fieldName,
          description: field.description,
          fieldType: field.fieldType,
          isSystemField: 1,
          displayName: entity.entityName,
          isDeleted: field.isDeleted || false
        };

        return field.id ? { ...fieldData, id: field.id } : fieldData;
      });

      const params = {
        appId: '1',
        entityId: entity.entityId,
        items: fieldDataList
      };

      await batchSaveFields(params);
      Message.success('保存成功');
      setVisible(false);
      successCallback();
    } catch (error) {
      console.error('保存字段失败:', error);
      Message.error('保存失败');
    } finally {
      setLoading(false);
    }
  };

  const renderFieldConfig = (fieldType: string) => {
    switch (fieldType) {
      case ENTITY_FIELD_TYPE.PICKLIST:
        return (
          <div className={styles['field-config-popover']}>
            <h4>单选列表配置</h4>
            <p>请配置选项列表...</p>
            {/* 这里可以添加具体的配置表单 */}
          </div>
        );
      case ENTITY_FIELD_TYPE.MULTI_PICKLIST:
        return (
          <div className={styles['field-config-popover']}>
            <h4>多选列表配置</h4>
            <p>请配置选项列表...</p>
            {/* 这里可以添加具体的配置表单 */}
          </div>
        );
      case ENTITY_FIELD_TYPE.AUTO_CODE:
        return (
          <div className={styles['field-config-popover']}>
            <h4>自动编码配置</h4>
            <p>请配置编码规则...</p>
            {/* 这里可以添加具体的配置表单 */}
          </div>
        );
      default:
        return null;
    }
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
      title: '字段编码',
      dataIndex: 'fieldCode',
      width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段编码"
            onChange={(val) => updateField(getFieldIndex(record.id), { fieldCode: val })}
          />
        )
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段名称"
            onChange={(val) => updateField(getFieldIndex(record.id), { fieldName: val })}
          />
        )
    },
    {
      title: '数据类型',
      dataIndex: 'fieldType',
      width: 140,
      render: (value: string, record: FieldFormValues) => (
        <Space>
          <Select
            value={value}
            options={fieldTypeOptions}
            onChange={(val) => updateField(getFieldIndex(record.id), { fieldType: val })}
            disabled={record.isSystemField === 0}
            style={{ width: 100 }}
          />
          {record.isSystemField === 1 && FIELD_TYPES_NEED_CONFIG.includes(value) && (
            <Popover
              content={renderFieldConfig(value)}
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
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段描述"
            onChange={(val) => updateField(getFieldIndex(record.id), { description: val })}
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
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入默认值"
            onChange={(val) => updateField(getFieldIndex(record.id), { defaultValue: val })}
          />
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      render: (value: boolean, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox
            checked={value}
            onChange={(checked) => updateField(getFieldIndex(record.id), { isUnique: checked })}
          />
        )
    },
    {
      title: '允许空值',
      dataIndex: 'allowNull',
      width: 100,
      render: (value: boolean, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox
            checked={value}
            onChange={(checked) => updateField(getFieldIndex(record.id), { allowNull: checked })}
          />
        )
    },
    {
      title: '字段约束',
      dataIndex: 'constraints',
      // width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField === 0 ? (
          <span className={styles['system-field']}>{value || '-'}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段约束"
            onChange={(val) => updateField(getFieldIndex(record.id), { constraints: val })}
          />
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
