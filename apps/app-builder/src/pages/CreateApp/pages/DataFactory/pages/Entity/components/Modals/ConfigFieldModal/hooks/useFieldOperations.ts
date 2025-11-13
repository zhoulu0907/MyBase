import { useCallback } from 'react';
import { Message } from '@arco-design/web-react';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import { arrayMove, systemFieldsLength } from '../utils/transform';
import type { FieldFormValues, FieldOperations } from '../types';

// 字段操作管理hook
export const useFieldOperations = (
  fields: FieldFormValues[],
  setFields: (fields: FieldFormValues[]) => void,
  getCurrentTableData: () => FieldFormValues[]
): FieldOperations => {
  // 添加字段
  const addField = useCallback(() => {
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

    // 通过 getCurrentTableData 获取最新的列表数据
    const customFields = getCurrentTableData();
    const newFields = [...customFields, newField];
    setFields(newFields);
  }, [fields.length, getCurrentTableData, setFields]);

  // 删除字段
  const deleteField = useCallback(
    (id: string, onDelete?: (id: string) => void) => {
      // 通过 getCurrentTableData 获取最新的列表数据
      const customFields = getCurrentTableData();
      const field = customFields.find((f) => f.id === id);

      if (field?.isSystemField === FIELD_TYPE.SYSTEM) {
        Message.error('系统字段不能删除');
        return;
      }

      let newFields: FieldFormValues[];
      if (id && id.startsWith('field-')) {
        // 新添加的字段直接删除
        newFields = customFields.filter((f) => f.id !== id);
      } else {
        // 已存在的字段标记为删除
        newFields = customFields.map((f) => (f.id === id ? { ...f, isDeleted: true } : f));
      }

      setFields(newFields);

      // 通知删除回调，用于关闭相关的 popover
      onDelete?.(id);
    },
    [getCurrentTableData, setFields]
  );

  // 更新字段
  const updateField = useCallback(
    (id: string, updates: Partial<FieldFormValues>) => {
      // 通过 getCurrentTableData 获取最新的列表数据
      const customFields = getCurrentTableData();
      const fieldIndex = customFields.findIndex((field) => field.id === id);

      if (fieldIndex === -1) return;

      customFields[fieldIndex] = { ...customFields[fieldIndex], ...updates };
      setFields(customFields);
    },
    [getCurrentTableData, setFields]
  );

  // 拖拽排序
  const moveField = useCallback(
    (oldIndex: number, newIndex: number) => {
      // 通过 getCurrentTableData 获取最新的列表数据
      const customFields = getCurrentTableData();

      // 仅对自定义且未删除字段进行排序
      const active = customFields.filter((f) => !f.isDeleted && f.isSystemField === FIELD_TYPE.CUSTOM);
      const reorderedActive = arrayMove([...active], oldIndex, newIndex);

      const newFields = [...customFields];
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
    },
    [getCurrentTableData, setFields]
  );

  // 根据ID获取字段
  const getFieldById = useCallback(
    (id: string) => {
      // 首先尝试从 fieldData.fields 中获取原始字段数据
      const originalField = fields.find((field) => field.id === id);
      if (!originalField) return undefined;

      // 通过 getCurrentTableData 获取最新的列表数据
      const customFields = getCurrentTableData();
      const updatedField = customFields.find((field) => field.id === id);

      // 返回合并后的字段数据，确保包含所有配置信息
      return updatedField || originalField;
    },
    [getCurrentTableData, fields]
  );

  // 根据ID获取字段索引（在activeFields中的索引）
  const getFieldIndex = useCallback(
    (id: string) => {
      // 通过 getCurrentTableData 获取最新的列表数据
      const customFields = getCurrentTableData();
      return customFields.findIndex((field) => field.id === id);
    },
    [getCurrentTableData]
  );

  return {
    addField,
    deleteField,
    updateField,
    moveField,
    getFieldById,
    getFieldIndex
  };
};
