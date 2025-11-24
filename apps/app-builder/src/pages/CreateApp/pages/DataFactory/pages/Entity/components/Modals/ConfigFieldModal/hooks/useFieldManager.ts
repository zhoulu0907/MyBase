import { useState, useCallback, useEffect } from 'react';
import { Form, Message, Modal } from '@arco-design/web-react';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import { batchSaveFields } from '@onebase/app';
import { useAppStore } from '@/store/store_app';
import { newFieldSignal } from '@/store/singals/new_field';
import { systemFieldsLength } from '../utils/transform';
import { useFieldData } from './useFieldData';
import { useFieldOperations } from './useFieldOperations';
import { useFieldValidation } from './useFieldValidation';
import type { EntityNode, FieldFormValues } from '../types';

// 统一管理所有hooks
export const useFieldManager = (
  visible: boolean,
  entity: Partial<EntityNode>,
  onSuccess?: () => void,
  setVisible?: (visible: boolean) => void
) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm();
  const [configPopoverVisible, setConfigPopoverVisible] = useState<string | null>(null);
  const [constraintsPopoverVisible, setConstraintsPopoverVisible] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 使用数据管理hook
  const fieldData = useFieldData(visible, entity, onSuccess);

  // 将表单数据转换为表格数据（参考原始版本）
  const getCurrentTableData = useCallback(
    (formFields?: Partial<FieldFormValues>[]) => {
      const formValues = form.getFieldsValue();
      const formListFields = formFields || formValues.fields || [];

      return fieldData.fields.map((originalField, index) => {
        // 通过 id 匹配表单数据
        const formField =
          formListFields.find((f: Partial<FieldFormValues>) => f?.id === originalField.id) || formListFields[index];
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
    },
    [form, fieldData.fields]
  );

  // 使用操作管理hook
  const fieldOperations = useFieldOperations(
    fieldData.fields,
    (newFields) => {
      fieldData.setFields(newFields);
      form.setFieldsValue({ fields: newFields });
    },
    getCurrentTableData
  );

  // 字段删除时，关闭相关的 popover
  const handleDeleteField = useCallback(
    (id: string) => {
      if (configPopoverVisible === id) {
        setConfigPopoverVisible(null);
      }
      if (constraintsPopoverVisible === id) {
        setConstraintsPopoverVisible(null);
      }
    },
    [configPopoverVisible, constraintsPopoverVisible, setConfigPopoverVisible, setConstraintsPopoverVisible]
  );

  const deleteFieldWithCleanup = useCallback(
    (id: string) => {
      fieldOperations.deleteField(id, handleDeleteField);
    },
    [fieldOperations, handleDeleteField]
  );

  // 使用验证管理hook
  const fieldValidation = useFieldValidation();

  // 在初始加载时更新表单值
  useEffect(() => {
    if (fieldData.fields.length > 0 && visible) {
      form.setFieldsValue({ fields: fieldData.fields });
    }
  }, [fieldData.fields, visible, form]);

  // 当弹窗关闭时重置状态
  useEffect(() => {
    if (!visible) {
      setConfigPopoverVisible(null);
      setConstraintsPopoverVisible(null);
      fieldValidation.clearErrors();
    }
  }, [visible]);

  // 处理配置确认
  const handleConfigConfirm = useCallback(
    (fieldType: string, fieldId: string, configData: unknown, dictTypeId?: string) => {
      let fieldConfig = {};
      switch (fieldType) {
        case ENTITY_FIELD_TYPE.SELECT.VALUE:
        case ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE:
          if (dictTypeId) {
            fieldConfig = { dictTypeId, options: [] };
          } else {
            fieldConfig = { options: configData };
          }
          break;
        case ENTITY_FIELD_TYPE.AUTO_CODE.VALUE:
          fieldConfig = { autoNumber: configData };
          break;
        case 'CONSTRAINTS':
          fieldConfig = { constraints: configData };
          break;
      }

      fieldOperations.updateField(fieldId, fieldConfig);

      if (fieldType === 'CONSTRAINTS') {
        setConstraintsPopoverVisible(null);
      } else {
        setConfigPopoverVisible(null);
      }
    },
    [fieldOperations]
  );

  // 处理配置取消
  const handleConfigCancel = useCallback((fieldType: string) => {
    if (fieldType === 'CONSTRAINTS') {
      setConstraintsPopoverVisible(null);
    } else {
      setConfigPopoverVisible(null);
    }
  }, []);

  // 处理取消
  const handleCancel = useCallback(() => {
    Modal.confirm({
      title: '确认取消',
      content: '确定要取消字段配置吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        setVisible?.(false);
        form.resetFields();
        setConfigPopoverVisible(null);
        setConstraintsPopoverVisible(null);
        fieldValidation.clearErrors();
      }
    });
  }, [form, setVisible]);

  // 渲染字段配置 popover 内容
  const renderFieldConfigContent = useCallback(() => {
    // 这里需要导入 FieldConfigPopoverRefactored 组件
    // 暂时返回 null，在组件中处理
    return null;
  }, []);

  // 保存
  const executeSave = useCallback(async () => {
    if (!entity?.entityId) {
      return;
    }

    setSubmitting(true);

    try {
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

      const result = await batchSaveFields(params);

      // 使用接口返回的 createdIds 标记新增字段
      if (result?.createdIds && Array.isArray(result.createdIds)) {
        result.createdIds.forEach((fieldId: string) => {
          newFieldSignal.addNewField(entity.entityId as string, fieldId);
        });
      }

      Message.success('保存成功');
      setVisible?.(false);
      fieldValidation.clearErrors();
      onSuccess?.();
    } catch (error) {
      console.error('executeSave error', error);
    } finally {
      setSubmitting(false);
    }
  }, [entity?.entityId, getCurrentTableData, curAppId, setVisible]);

  // 处理表单提交
  const handleSubmit = useCallback(async () => {
    // 防止重复点击
    if (submitting) {
      return;
    }

    try {
      // 检查资产是否存在
      if (!entity?.entityId) {
        return;
      }

      await form.validate();

      await executeSave();
    } catch (error) {
      // 手动渲染错误
      const errs = (error && (error as Record<string, unknown>).errors) || [];
      const map: Record<string, string> = {};
      if (typeof errs === 'object') {
        Object.keys(errs).forEach((key: string) => {
          if (key) map[key] = (errs as Record<string, { message?: string }>)[key]?.message || '校验失败';
        });
      }
      fieldValidation.setAllErrors(map);
    }
  }, [submitting, entity?.entityId, form, executeSave]);

  return {
    // 数据
    fields: fieldData.fields,
    activeFields: fieldData.activeFields,
    originFields: fieldData.originFields,
    loading: fieldData.loading,
    submitting,
    errors: fieldValidation.errors,

    // 操作
    addField: fieldOperations.addField,
    deleteField: deleteFieldWithCleanup,
    updateField: fieldOperations.updateField,
    moveField: fieldOperations.moveField,
    getFieldById: fieldOperations.getFieldById,
    getFieldIndex: fieldOperations.getFieldIndex,

    // 配置
    configPopoverVisible,
    constraintsPopoverVisible,
    setConfigPopoverVisible,
    setConstraintsPopoverVisible,
    handleConfigConfirm,
    handleConfigCancel,
    handleSubmit,
    handleCancel,
    renderFieldConfigContent,

    // 验证
    clearErrors: fieldValidation.clearErrors,
    setAllErrors: fieldValidation.setAllErrors,

    // 表单
    form
  };
};
