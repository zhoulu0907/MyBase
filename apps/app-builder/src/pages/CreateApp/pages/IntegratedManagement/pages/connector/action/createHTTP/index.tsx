import { Button, Message, Steps } from '@arco-design/web-react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider } from '@formily/react';
import { saveConnectorAction, type SaveConnectorActionReq } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useMemo, useState } from 'react';
import { componentMap, FormilyFormItem } from '../../../../../../../../components/DynamicForm/componentMapper';

import { DebugParamReadOnlyTable } from './DebugParamReadOnlyTable';
import styles from './index.module.less';
import { OutputParamArrayTable } from './OutputParamArrayTable';
import { ParamArrayTable } from './ParamArrayTable';
import { step1Schema } from './step1';
import { step2Schema } from './step2';
import { step3Schema } from './step3';
import { step4Schema } from './step4';

export interface CreateHTTPActionPageProps {}

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem,
    ParamArrayTable,
    OutputParamArrayTable,
    DebugParamReadOnlyTable
  }
});

const STEP_LIST = [
  { key: 'basic', title: '基础信息' },
  { key: 'params', title: '请求参数' },
  { key: 'output', title: '出参配置' },
  { key: 'debug', title: '动作调试' }
];

const CreateHTTPActionPage: React.FC<CreateHTTPActionPageProps> = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const form = useMemo(
    () =>
      createForm({
        initialValues: {}
      }),
    []
  );

  const STEP_SCHEMAS = [step1Schema, step2Schema, step3Schema, step4Schema];
  const currentSchema = STEP_SCHEMAS[currentStep] ?? step1Schema;
  const isLastStep = currentStep === STEP_LIST.length - 1;

  const getTabsData = (values: Record<string, unknown>) => {
    const tabs = values.tabs as Record<string, unknown> | undefined;
    return (key: string) => values[key] ?? tabs?.[key] ?? [];
  };

  // 进入第四步时，从第二步的 tabs 同步到 debugParamsTabs，供只读展示 + 仅字段值可编辑
  useEffect(() => {
    if (currentStep !== 3) return;
    const values = form.values as Record<string, unknown>;
    const tabs = (values.tabs ?? {}) as Record<string, unknown[]>;
    const copyWithFieldValue = (arr: unknown[]) =>
      Array.isArray(arr)
        ? arr.map((row) =>
            typeof row === 'object' && row !== null
              ? { ...row, fieldValue: (row as Record<string, unknown>).fieldValue ?? '' }
              : { fieldValue: '' }
          )
        : [];
    form.setValues({
      ...values,
      debugParamsTabs: {
        requestHeaders: copyWithFieldValue(tabs.requestHeaders ?? []),
        requestBody: copyWithFieldValue(tabs.requestBody ?? []),
        queryParams: copyWithFieldValue(tabs.queryParams ?? []),
        pathParams: copyWithFieldValue(tabs.pathParams ?? [])
      }
    });
  }, [currentStep, form]);

  const [saveLoading, setSaveLoading] = useState(false);

  const handleFinish = async () => {
    const values = form.values as Record<string, unknown>;
    const fromTabs = getTabsData(values);
    const debugTabs = (values.debugParamsTabs ?? {}) as Record<string, unknown>;
    const combined = {
      // 步骤1：基础信息
      basic: values.basic ?? {},
      // 步骤2：请求头 / 请求体 / 查询参数 / 路径参数（可编辑）
      request: {
        requestHeaders: fromTabs('requestHeaders'),
        requestBody: fromTabs('requestBody'),
        queryParams: fromTabs('queryParams'),
        pathParams: fromTabs('pathParams')
      },
      // 步骤3：出参配置（响应头 / 响应体）
      response: {
        responseHeaders: fromTabs('responseHeaders'),
        responseBody: fromTabs('responseBody')
      },
      debug: {
        // 步骤4：动作调试（接口地址 + 接口方法 + 执行动作入参填写，仅字段值可编辑）
        url: values.url ?? '',
        method: values.method ?? 'GET',

        requestHeaders: debugTabs.requestHeaders ?? [],
        requestBody: debugTabs.requestBody ?? [],
        queryParams: debugTabs.queryParams ?? [],
        pathParams: debugTabs.pathParams ?? []
      }
    };
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    setSaveLoading(true);
    try {
      const params: SaveConnectorActionReq = { actionConfig: combined };
      await saveConnectorAction(connectorId, params);
      Message.success('保存成功');
    } catch (e) {
      Message.error((e as Error)?.message ?? '保存失败');
    } finally {
      setSaveLoading(false);
    }
  };

  return (
    <div className={styles.createScriptActionPage}>
      <Steps current={currentStep + 1} style={{ marginBottom: 24 }}>
        {STEP_LIST.map((item) => (
          <Steps.Step key={item.key} title={item.title} />
        ))}
      </Steps>
      <FormProvider form={form}>
        <SchemaField key={currentStep} schema={currentSchema} />
        <div className={styles.stepFooter}>
          <Button disabled={currentStep === 0} onClick={() => setCurrentStep((s) => s - 1)}>
            上一步
          </Button>
          <Button
            type="primary"
            loading={saveLoading}
            onClick={isLastStep ? handleFinish : () => setCurrentStep((s) => s + 1)}
          >
            {isLastStep ? '完成' : '下一步'}
          </Button>
        </div>
      </FormProvider>
    </div>
  );
};

export default CreateHTTPActionPage;
