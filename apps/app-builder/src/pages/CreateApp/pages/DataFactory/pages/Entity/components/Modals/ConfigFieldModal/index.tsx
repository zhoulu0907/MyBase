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
  id: string;
  fieldCode: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: boolean;
  allowNull: boolean;
  constraints: string;
  isSystemField: boolean;
  sortOrder?: number;
}

interface ConfigFieldModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityNode;
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

  const addField = () => {
    const newField: FieldFormValues = {
      id: `field-${Date.now()}`,
      fieldCode: '',
      fieldName: '',
      description: '',
      fieldType: 'TEXT',
      defaultValue: '',
      isUnique: false,
      allowNull: true,
      constraints: '',
      isSystemField: false,
      sortOrder: fields.length
    };
    setFields([...fields, newField]);
  };

  const deleteField = (index: number) => {
    const field = fields[index];
    if (field.isSystemField) {
      Message.error('系统字段不能删除');
      return;
    }
    setFields(fields.filter((_, i) => i !== index));
  };

  const updateField = (fieldId: string, updatedField: Partial<FieldFormValues>) => {
    setFields((prevFields) =>
      prevFields.map((field) => (field.id === fieldId ? { ...field, ...updatedField } : field))
    );
  };

  const handleSort = (newFields: FieldFormValues[]) => {
    setFields(newFields.map((field, index) => ({ ...field, sortOrder: index })));
  };

  const handleFinish = async () => {
    try {
      setLoading(true);

      // 验证必填字段
      const customFields = fields.filter((field) => !field.isSystemField);
      for (const field of customFields) {
        if (!field.fieldCode || !field.fieldName) {
          Message.error('请填写字段编码和字段名称');
          return;
        }
      }

      // 批量保存字段
      const promises = customFields.map((field) => {
        const fieldData = {
          appId: '1',
          entityId: entity.entityId,
          fieldCode: field.fieldCode,
          fieldName: field.fieldName,
          description: field.description,
          fieldType: field.fieldType,
          isSystemField: false,
          displayName: ''
        };

        return batchSaveFields(fieldData);
      });

      await Promise.all(promises);
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
      title: '排序',
      dataIndex: 'sortOrder',
      width: 60,
      render: (value: number, record: FieldFormValues) =>
        !record.isSystemField && value ? <IconDragDotVertical className={styles['drag-handle']} /> : null
    },
    {
      title: '字段编码',
      dataIndex: 'fieldCode',
      width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段编码"
            onChange={(val) => updateField(record.id, { fieldCode: val })}
          />
        )
    },
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段名称"
            onChange={(val) => updateField(record.id, { fieldName: val })}
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
            onChange={(val) => updateField(record.id, { fieldType: val })}
            disabled={record.isSystemField}
            style={{ width: 100 }}
          />
          {!record.isSystemField && FIELD_TYPES_NEED_CONFIG.includes(value) && (
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
        record.isSystemField ? (
          <span className={styles['system-field']}>{value}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段描述"
            onChange={(val) => updateField(record.id, { description: val })}
          />
        )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入默认值"
            onChange={(val) => updateField(record.id, { defaultValue: val })}
          />
        )
    },
    {
      title: '唯一',
      dataIndex: 'isUnique',
      width: 60,
      render: (value: boolean, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox checked={value} onChange={(checked) => updateField(record.id, { isUnique: checked })} />
        )
    },
    {
      title: '允许空值',
      dataIndex: 'allowNull',
      width: 80,
      render: (value: boolean, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>-</span>
        ) : (
          <Checkbox checked={value} onChange={(checked) => updateField(record.id, { allowNull: checked })} />
        )
    },
    {
      title: '字段约束',
      dataIndex: 'constraints',
      // width: 120,
      render: (value: string, record: FieldFormValues) =>
        record.isSystemField ? (
          <span className={styles['system-field']}>{value || '-'}</span>
        ) : (
          <Input
            value={value}
            placeholder="请输入字段约束"
            onChange={(val) => updateField(record.id, { constraints: val })}
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
          !record.isSystemField &&
          value && (
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
          list={fields}
          setList={handleSort}
          animation={200}
          handle={`.${styles['drag-handle']}`}
          filter={`.${styles['system-field']}`}
        >
          <Table
            data={fields}
            columns={columns}
            pagination={false}
            className={styles['field-table']}
            rowClassName={(record) => (record.isSystemField ? styles['system-field-row'] : styles['custom-field-row'])}
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
