import { batchSaveFields, getEntityFields, getEntityFieldsWithChildren } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { FIELD_TYPE } from '@onebase/ui-kit';
import { useAppStore } from '@/store/store_app';
import { useFieldStore } from '@/store/store_field';
import { Button, Form, Message, Modal } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import type { AutoNumberRule } from './types';
import FieldConfigPopover from './FieldConfigPopover';
import TableColumns from './TableColumns';
import SortableTable from './SortableTable';
import { arrayMove, systemFieldsLength } from './utils';
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
  isSystemField: number;
  sortOrder?: number;
  isDeleted?: boolean;
  displayName?: string;
  options?: object[];
  autoNumber?: AutoNumberRule;
  constraints?: {
    lengthEnabled: number;
    minLength: number;
    maxLength: number;
    lengthPrompt: string;
    regexEnabled: number;
    regexPattern: string;
    regexPrompt: string;
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
const FIELD_TYPES_NEED_CONFIG = [
  ENTITY_FIELD_TYPE.SELECT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE,
  ENTITY_FIELD_TYPE.AUTO_CODE.VALUE
];

const ConfigFieldModal: React.FC<ConfigFieldModalProps> = ({ visible, setVisible, entity, successCallback }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm();
  const [fields, setFields] = useState<FieldFormValues[]>([]);
  const [originFields, setOriginFields] = useState<FieldFormValues[]>([]);
  const [loading, setLoading] = useState(false);
  const [configPopoverVisible, setConfigPopoverVisible] = useState<string | null>(null);
  const [constraintsPopoverVisible, setConstraintsPopoverVisible] = useState<string | null>(null);
  const [externalErrors, setExternalErrors] = useState<Record<string, string>>({});

  const fieldTypeOptions = useFieldStore.getState().fieldTypes.map((item) => ({
    label: item.displayName,
    value: item.fieldType
  }));

  useEffect(() => {
    if (visible) {
      // 获取实体字段列表
      loadEntityFields();

      // 获取实体字段配置列表
      loadEntityFieldsWithChildren();
    } else {
      // 关闭时重置表单
      form.resetFields();
      setExternalErrors({});
    }
  }, [visible]);

  const loadEntityFields = () => {
    if (!entity.entityId) return;
    getEntityFields({ entityId: entity.entityId }).then((res: any) => {
      console.log('getEntityFields', res);
      const fieldsData = res.map((field: object, index: number) => ({
        ...field,
        sortOrder: index
      }));
      setFields(fieldsData);
      form.setFieldsValue({ fields: fieldsData });
    });
  };

  const loadEntityFieldsWithChildren = () => {
    if (!entity.entityId) return;
    getEntityFieldsWithChildren(entity.entityId).then((res: any) => {
      const transformEntity = (entity: any, isChild = false) => ({
        label: isChild ? entity.childEntityName : entity.entityName,
        value: isChild ? entity.childEntityId : entity.entityId,
        children: (isChild ? entity?.childFields || [] : entity?.parentFields || []).map((field: any) => ({
          label: field.displayName,
          value: field.fieldId
        }))
      });

      const entities = [
        transformEntity(res),
        ...(res.childEntities || []).map((child: any) => transformEntity(child, true))
      ];

      setOriginFields(entities);
    });
  };

  // 过滤掉已删除的字段和系统字段
  const activeFields = fields.filter((field) => !field.isDeleted && field.isSystemField === FIELD_TYPE.CUSTOM);

  const addField = () => {
    const newField: FieldFormValues = {
      id: 'field-' + Date.now(),
      fieldCode: '',
      fieldName: '',
      displayName: '',
      description: '',
      fieldType: ENTITY_FIELD_TYPE.TEXT.VALUE,
      defaultValue: '',
      isUnique: 0,
      isRequired: 0,
      constraints: {
        lengthEnabled: 0,
        minLength: 0,
        maxLength: 0,
        lengthPrompt: '',
        regexEnabled: 0,
        regexPattern: '',
        regexPrompt: ''
      },
      isSystemField: FIELD_TYPE.CUSTOM,
      sortOrder: fields.length + 1
    };
    const customFields = getCurrentTableData();
    const newFields = [...customFields, newField];
    setFields(newFields);
    form.setFieldsValue({ fields: newFields });
  };

  const deleteField = (id: string) => {
    const field = fields.find((f) => f.id === id);
    if (field?.isSystemField === FIELD_TYPE.SYSTEM) {
      Message.error('系统字段不能删除');
      return;
    }
    let newFields;
    if (id && id.startsWith('field-')) {
      newFields = fields.filter((f) => f.id !== id);
    } else {
      newFields = fields.map((f) => (f.id === id ? { ...f, isDeleted: true } : f));
    }

    setFields(newFields);
    form.setFieldsValue({ fields: newFields });
  };

  const updateField = (index: number, updatedField: Partial<FieldFormValues>) => {
    setFields((prevFields) => {
      const data = form.getFieldsValue().fields[index];
      const newFields = prevFields.map((field, i) => (i === index ? { ...field, ...data, ...updatedField } : field));
      form.setFieldsValue({ fields: newFields });
      return newFields;
    });
  };

  // 获取字段在数组中的索引
  const getFieldIndex = (fieldId: string, index: number) => {
    if (fieldId) {
      return fields.findIndex((field) => field.id === fieldId);
    } else {
      return index;
    }
  };

  const handleFinish = async () => {
    try {
      setLoading(true);

      const formValues = await form.validate();
      console.log('formValues', formValues);

      // 获取最新数据，再进行过滤
      const mergedFields = getCurrentTableData();
      const nonSystemFields = mergedFields.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM);

      const fieldDataList = nonSystemFields.map((field: FieldFormValues) => {
        const fieldData = {
          appId: curAppId,
          entityId: entity.entityId,
          ...field,
          isSystemField: FIELD_TYPE.CUSTOM,
          isDeleted: field.isDeleted || false
        };

        return field.id && field.id.startsWith('field-') ? { ...fieldData, id: '' } : { ...fieldData, id: field.id };
      });

      const params = {
        appId: curAppId,
        entityId: entity.entityId,
        items: fieldDataList
      };

      await batchSaveFields(params);
      Message.success('保存成功');
      setVisible(false);
      setExternalErrors({});
      successCallback();
    } catch (error) {
      // 手动渲染错误
      const errs = (error && (error as any).errors) || [];
      const map: Record<string, string> = {};
      if (typeof errs === 'object') {
        Object.keys(errs).forEach((key: any) => {
          if (key) map[key] = errs[key].message || '校验失败';
        });
      }
      setExternalErrors(map);
    } finally {
      setLoading(false);
      // form.resetFields();
    }
  };

  // 处理配置确认
  const handleConfigConfirm = (fieldType: string, fieldId: string, configData: any) => {
    const fieldIndex = fields.findIndex((field) => field.id === fieldId);
    if (fieldIndex === -1) return;

    let fieldConfig = {};
    switch (fieldType) {
      case ENTITY_FIELD_TYPE.SELECT.VALUE:
      case ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE:
        fieldConfig = { options: configData };
        break;
      case ENTITY_FIELD_TYPE.AUTO_CODE.VALUE:
        fieldConfig = { autoNumber: configData };
        break;
      case 'CONSTRAINTS':
        fieldConfig = { constraints: configData };
        break;
    }

    updateField(fieldIndex, fieldConfig);
    console.log('fieldConfig', fieldConfig);

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

  const handleCancel = () => {
    setVisible(false);
    form.resetFields();
  };

  // 渲染字段配置 popover 内容
  const renderFieldConfigContent = (fieldType: string, fieldId: string) => {
    const field = fields.find((f) => f.id === fieldId);
    return (
      <FieldConfigPopover
        fieldType={fieldType}
        fieldId={fieldId}
        field={field}
        fields={originFields}
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
    externalErrors,
    getFieldIndex,
    deleteField,
    fields
  });

  // 处理拖拽排序
  const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    // 获取最新输入的数据
    const currentFields = getCurrentTableData();

    // 仅对自定义且未删除字段进行排序
    const active = currentFields.filter((f) => !f.isDeleted && f.isSystemField === FIELD_TYPE.CUSTOM);
    const reorderedActive = arrayMove([...active], oldIndex, newIndex);

    const newFields = [...currentFields];
    let pointer = 0;
    for (let i = 0; i < newFields.length; i += 1) {
      const cur = newFields[i];
      if (!cur.isDeleted && cur.isSystemField === FIELD_TYPE.CUSTOM) {
        newFields[i] = {
          ...reorderedActive[pointer],
          sortOrder: pointer + systemFieldsLength + 1
        } as FieldFormValues;
        pointer += 1;
      }
    }

    setFields(newFields);
    form.setFieldsValue({ fields: newFields });
  };

  // 将表单数据转换为表格数据
  const getCurrentTableData = (formFields?: Partial<FieldFormValues>) => {
    const formValues = form.getFieldsValue();
    const formListFields = formFields || formValues.fields || [];

    return fields.map((originalField, index) => {
      const formField = formListFields[index];
      if (formField) {
        return {
          ...originalField,
          ...formField,
          id: originalField.id,
          isSystemField: originalField.isSystemField,
          isDeleted: originalField.isDeleted,
          sortOrder: index + systemFieldsLength + 1
        };
      }
      return originalField;
    });
  };

  return (
    <Modal
      className={styles['config-field-modal']}
      title="字段配置"
      visible={visible}
      onOk={handleFinish}
      onCancel={handleCancel}
      okText="保存"
      cancelText="取消"
      confirmLoading={loading}
      style={{ width: 1400 }}
    >
      <Form form={form} initialValues={{ fields: activeFields }}>
        <Form.List field="fields">
          {() => {
            return (
              <div className={styles['field-config-container']} id="field-config-container">
                <SortableTable data={activeFields} columns={columns} onSort={handleSort} />

                <div className={styles['add-field-section']}>
                  <Button type="dashed" icon={<IconPlus />} onClick={addField} className={styles['add-field-button']}>
                    新增字段
                  </Button>
                </div>
              </div>
            );
          }}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default ConfigFieldModal;
