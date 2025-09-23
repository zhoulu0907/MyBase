import { useAppStore } from '@/store/store_app';
import { useFieldStore } from '@/store/store_field';
import { Button, Message, Modal, Table } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { batchSaveFields, getEntityFields, getEntityFieldsWithChildren } from '@onebase/app';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
// import { ReactSortable } from 'react-sortablejs';
import FieldConfigPopover from './FieldConfigPopover';
import TableColumns from './TableColumns';
import SortableTable from './SortableTable';
import styles from './index.module.less';
import type { AutoNumberRule } from './types';

interface FieldFormValues {
  id?: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: number;
  allowNull: number;
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

// 自定义表格行组件，支持拖拽
// const SortableTableRow = (props: any) => {
//   const { record, children, ...restProps } = props;

//   // 为可拖拽的行添加 data-id 属性
//   const rowProps = {
//     ...restProps,
//     'data-id': record.id
//   };

//   return <tr {...rowProps}>{children}</tr>;
// };

const ConfigFieldModal: React.FC<ConfigFieldModalProps> = ({ visible, setVisible, entity, successCallback }) => {
  const { curAppId } = useAppStore();
  const [fields, setFields] = useState<FieldFormValues[]>([]);
  const [originFields, setOriginFields] = useState<FieldFormValues[]>([]);
  const [loading, setLoading] = useState(false);
  const [configPopoverVisible, setConfigPopoverVisible] = useState<string | null>(null);
  const [constraintsPopoverVisible, setConstraintsPopoverVisible] = useState<string | null>(null);

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
    }
  }, [visible]);

  const loadEntityFields = () => {
    getEntityFields({ entityId: entity.entityId }).then((res: any) => {
      console.log('getEntityFields', res);
      setFields(
        res.map((field: object, index: number) => ({
          ...field,
          sortOrder: index
        }))
      );
    });
  };

  const loadEntityFieldsWithChildren = () => {
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
      isUnique: 1,
      allowNull: 1,
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
      sortOrder: activeFields.length
    };
    setFields([...activeFields, newField]);
  };

  const deleteField = (index: number) => {
    const field = fields[index];
    if (field.isSystemField === FIELD_TYPE.SYSTEM) {
      Message.error('系统字段不能删除');
      return;
    }
    if (field.id && field.id.startsWith('field-')) {
      setFields(fields.filter((f, i) => i !== index));
      return;
    } else {
      setFields(fields.map((f, i) => (i === index ? { ...f, isDeleted: true } : f)));
    }
  };

  const updateField = (index: number, updatedField: Partial<FieldFormValues>) => {
    setFields((prevFields) => prevFields.map((field, i) => (i === index ? { ...field, ...updatedField } : field)));
  };

  // 获取字段在数组中的索引
  const getFieldIndex = (fieldId: string, index: number) => {
    if (fieldId) {
      return fields.findIndex((field) => field.id === fieldId);
    } else {
      return index;
    }
  };

  const handleSort = (newFields: FieldFormValues[]) => {
    console.log('handleSort', newFields);
    // const allFields = [...fields];
    // const visibleFields = newFields.map((field, index) => ({ ...field, sortOrder: index }));

    // setFields(
    //   allFields.map((field) => {
    //     const visibleField = visibleFields.find((vf) => vf.id === field.id);
    //     return visibleField ? { ...field, sortOrder: visibleField.sortOrder } : field;
    //   })
    // );
  };

  const handleFinish = async () => {
    try {
      setLoading(true);

      const customFields = fields.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM && !field.isDeleted);

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
        if (field.fieldName && !/^[a-z][a-z0-9_]{0,39}$/.test(field.fieldName)) {
          Message.error('请输入符合规范的字段名称');
          return;
        }
        if (!field.fieldType) {
          Message.error('数据类型不能为空');
          return;
        }
      }

      const allFields = fields.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM);
      const fieldDataList = allFields.map((field) => {
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

    // const isEnabled = configData.length > 0 ? 0 : 1;

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
        {/* <ReactSortable
          list={activeFields}
          setList={handleSort}
          animation={200}
          handle={`.${styles['drag-handle']}`}
          filter={`.${styles['system-field']}`}
        >
          <Table
            data={activeFields}
            columns={columns}
            pagination={false}
            className={styles['field-table']}
            rowClassName={(record) =>
              record.isSystemField === FIELD_TYPE.SYSTEM ? styles['system-field-row'] : styles['custom-field-row']
            }
            rowKey="id"
            components={{
              body: {
                row: SortableTableRow
              }
            }}
          />
        </ReactSortable> */}

        <SortableTable
          data={activeFields}
          columns={columns}
          rowKey="id"
          onSort={(newData) => {
            setFields(newData);
          }}
          pagination={false}
          disabledRowKeys={activeFields.filter((r) => r.isSystemField === FIELD_TYPE.SYSTEM).map((r) => r.id || '')}
        />

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
