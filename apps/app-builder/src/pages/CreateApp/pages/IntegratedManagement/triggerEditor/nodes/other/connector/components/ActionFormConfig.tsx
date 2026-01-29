import DynamicForm from '@/components/DynamicForm';
import { createForm, type Form } from '@formily/core';
import { type ISchema } from '@formily/react';
import { type FlowConnector } from '@onebase/app';
import React, { useEffect, useMemo } from 'react';

interface ActionFormConfigProps {
  connector: FlowConnector | null;
  actionKey: string | null;
  initialValues?: Record<string, any>;
  onChange?: (values: Record<string, any>) => void;
  formRef?: React.MutableRefObject<Form | null>;
}

export const ActionFormConfig: React.FC<ActionFormConfigProps> = ({
  connector,
  actionKey,
  initialValues,
  onChange,
  formRef
}) => {
  // 创建 Formily form 实例，当 actionKey 变化时重新创建
  const form = useMemo<Form>(() => {
    return createForm({
      initialValues: initialValues || {}
    });
  }, [actionKey]); // 当 actionKey 变化时重新创建 form

  // 将 form 实例暴露到 ref
  useEffect(() => {
    if (formRef) {
      formRef.current = form;
    }
  }, [form, formRef]);

  // 当 initialValues 变化时，更新表单值
  useEffect(() => {
    if (actionKey && initialValues) {
      form.setValues(initialValues);
    } else if (actionKey && (!initialValues || Object.keys(initialValues).length === 0)) {
      // 如果切换了 actionKey 但没有初始值，重置表单
      form.reset();
    }
  }, [initialValues, actionKey, form]);

  // 监听表单值变化
  useEffect(() => {
    let previousValues: Record<string, any> = {};
    let isInitialized = false;

    const id = form.subscribe(() => {
      // 获取当前表单值
      const currentValues = form.values || {};

      // 跳过初始化时的第一次触发
      if (!isInitialized) {
        isInitialized = true;
        previousValues = { ...currentValues };
        return;
      }

      // 只在值真正变化时触发回调
      const valuesChanged = JSON.stringify(currentValues) !== JSON.stringify(previousValues);

      if (valuesChanged) {
        console.log('ActionFormConfig values changed:', currentValues);
        previousValues = { ...currentValues };
        onChange?.(currentValues);
      }
    });

    return () => {
      form.unsubscribe(id);
      // 移除组件卸载时的自动保存逻辑，改为只在点击确定时保存
    };
  }, [form, onChange]);

  // 将 action 配置转换为 Formily schema
  const actionSchema = useMemo<ISchema | null>(() => {
    console.log('connector :', connector);
    console.log('actionKey :', actionKey);
    if (!connector?.config?.properties || !actionKey) {
      return null;
    }

    const actionConfig = connector.config.properties[actionKey];
    if (!actionConfig) {
      return null;
    }
    console.log('actionConfig :', actionConfig);

    const schema: ISchema = actionConfig;

    console.log('schema :', schema);

    return schema;
  }, [connector, actionKey]);

  if (!actionSchema) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>请先选择动作</div>;
  }

  return <DynamicForm schema={actionSchema} form={form} />;
};
