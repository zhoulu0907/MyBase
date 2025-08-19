import { ENTITY_FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import { Button, Message, Modal, Table } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { batchSaveFields, getEntityFields } from '@onebase/app';
import React, { forwardRef, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../modal.module.less';
import { type AutoCodeRule } from './FieldTypeConfig';
import FieldConfigPopover from './FieldConfigPopover';
import TableColumns from './TableColumns';

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
  fieldConfig?: {
    options?: string[];
    autoCodeRules?: AutoCodeRule[];
    constraints?: {
      lengthRange: {
        enabled: boolean;
        minLength: number;
        maxLength: number;
        hintMessage: string;
      };
      regexValidation: {
        enabled: boolean;
        pattern: string;
        hintMessage: string;
      };
    };
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
const SortableTableRow = (props: any) => {
  const { record, children, ...restProps } = props;
  return <tr {...restProps}>{children}</tr>;
};

const ConfigFieldModal: React.FC<ConfigFieldModalProps> = ({ visible, setVisible, entity, successCallback }) => {
  const { curAppId } = useAppStore();
  const [fields, setFields] = useState<FieldFormValues[]>([]);
  const [loading, setLoading] = useState(false);
  const [configPopoverVisible, setConfigPopoverVisible] = useState<string | null>(null);
  const [constraintsPopoverVisible, setConstraintsPopoverVisible] = useState<string | null>(null);

  useEffect(() => {
    if (visible) {
      getEntityFields({ entityId: entity.entityId }).then((res: any) => {
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
      // id: 'field-' + Date.now(),
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
    const allFields = [...fields];
    const visibleFields = newFields.map((field, index) => ({ ...field, sortOrder: index }));

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
      case 'CONSTRAINTS':
        fieldConfig = { constraints: configData };
        break;
    }

    updateField(fieldIndex, { fieldConfig });

    if (fieldType === 'CONSTRAINTS') {
      setConstraintsPopoverVisible(null);
    } else {
      setConfigPopoverVisible(null);
    }
  };

  const handleConfigCancel = (fieldType: string) => {
    if (fieldType === 'CONSTRAINTS') {
      setConstraintsPopoverVisible(null);
    } else {
      setConfigPopoverVisible(null);
    }
  };

  // 渲染字段配置 popover 内容
  const renderFieldConfigContent = (fieldType: string, fieldId: string) => {
    const field = fields.find((f) => f.id === fieldId);
    return (
      <FieldConfigPopover
        fieldType={fieldType}
        fieldId={fieldId}
        field={field}
        onConfirm={handleConfigConfirm}
        onCancel={handleConfigCancel}
      />
    );
  };

  // 使用表格列组件
  const columns = TableColumns({
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
  });

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
          tag="tr"
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
