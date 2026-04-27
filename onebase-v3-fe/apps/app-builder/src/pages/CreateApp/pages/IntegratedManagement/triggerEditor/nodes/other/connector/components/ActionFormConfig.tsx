import DynamicForm from '@/components/DynamicForm';
import { DebugParamReadOnlyTable } from '@/pages/CreateApp/pages/IntegratedManagement/pages/connector/action/createHTTP/DebugParamReadOnlyTable';
import { createForm, type Form } from '@formily/core';
import { type ISchema } from '@formily/react';
import { getConnectorActionByCode, type FlowConnector } from '@onebase/app';
import React, { useEffect, useMemo, useState } from 'react';
import { getDefaultActionSchema } from './action-form-schema';

/** 将 getConnectorActionByCode 接口返回的 actionConfig 转为表单 values（与 createHTTP 保持一致） */
function actionConfigToFormValues(config: Record<string, unknown>): Record<string, unknown> {
  const basic = (config.basicInfo ?? {}) as Record<string, unknown>;
  const request = (config.inputConfig ?? {}) as Record<string, unknown>;
  const response = (config.outputConfig ?? {}) as Record<string, unknown>;
  const debug = (config.debugConfig ?? {}) as Record<string, unknown>;

  return {
    basic,
    tabs: {
      requestHeaders: request.requestHeaders ?? [],
      requestBody: request.requestBody ?? [],
      queryParams: request.queryParams ?? [],
      pathParams: request.pathParams ?? [],
      responseHeaders: response.responseHeaders ?? [],
      responseBody: response.responseBody ?? []
    },
    url: debug.url ?? '',
    method: debug.method ?? 'GET',
    debugParamsTabs: {
      requestHeaders: debug.requestHeaders ?? [],
      requestBody: debug.requestBody ?? [],
      queryParams: debug.queryParams ?? [],
      pathParams: debug.pathParams ?? []
    }
  };
}

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

  // 通过接口获取动作 schema，id 为 connector.id，actionName 为 actionKey
  const [actionSchema, setActionSchema] = useState<ISchema | null>(null);
  const [schemaLoading, setSchemaLoading] = useState(false);

  // 传给 DynamicForm 的额外组件，必须在条件 return 之前调用以符合 Hooks 规则
  const actionFormComponents = useMemo(() => ({ DebugParamReadOnlyTable }), []);

  useEffect(() => {
    if (!connector?.connectorUuid || !actionKey) {
      setActionSchema(null);
      return;
    }
    let cancelled = false;
    setSchemaLoading(true);
    getConnectorActionByCode(connector.connectorUuid, actionKey)
      .then((res: unknown) => {
        if (cancelled) return;
        // 新 API 返回的是 ConnectorActionDO 对象
        const actionDO = (res ?? {}) as Record<string, unknown>;
        // 解析 actionConfig JSON 字符串
        let config: Record<string, unknown> = {};
        if (typeof actionDO.actionConfig === 'string' && actionDO.actionConfig) {
          try {
            config = JSON.parse(actionDO.actionConfig);
          } catch (e) {
            console.error('Failed to parse actionConfig:', e);
          }
        }
        // 用 actionConfigToFormValues 将接口 config 转为表单值；仅当父组件未传入有效 initialValues 时才用接口数据填充，避免覆盖已保存数据
        const formValues = actionConfigToFormValues(config);
        const hasInitialFromParent = initialValues && Object.keys(initialValues).length > 0;
        if (!hasInitialFromParent) {
          form.setValues(formValues);
        }
        // schema：优先使用接口返回的 schema/formSchema，否则使用与 formValues 结构一致的默认 schema
        const schema =
          (config.schema as ISchema | undefined) ??
          (config.formSchema as ISchema | undefined) ??
          getDefaultActionSchema();
        setActionSchema(schema);
      })
      .catch(() => {
        if (!cancelled) setActionSchema(null);
      })
      .finally(() => {
        if (!cancelled) setSchemaLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [connector?.connectorUuid, actionKey, form]);

  if (schemaLoading) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>加载中...</div>;
  }
  if (!actionSchema) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>请先选择动作</div>;
  }

  return <DynamicForm schema={actionSchema} form={form} components={actionFormComponents} />;
};
