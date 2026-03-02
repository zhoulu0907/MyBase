import { Button, Message, Spin, Steps } from '@arco-design/web-react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider } from '@formily/react';
import {
  debugAction,
  getConnectorActionInfo,
  saveConnectorAction,
  updateHTTPAction,
  type SaveConnectorActionReq
} from '@onebase/app';
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

export interface CreateHTTPActionPageProps {
  /** 编辑时传入动作名称（或动作标识），会调用 getConnectorActionInfo 拉取并回显 */
  editActionName?: string;
  /** 返回列表（如点击返回或完成后的回调） */
  onSuccess?: () => void;
}

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

/** 将接口返回的 actionConfig 转为表单 values */
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

const CreateHTTPActionPage: React.FC<CreateHTTPActionPageProps> = ({ editActionName, onSuccess }) => {
  const [currentStep, setCurrentStep] = useState(0);
  const [editLoading, setEditLoading] = useState(false);
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
  const isEditMode = Boolean(editActionName);

  // 编辑模式：拉取动作详情并回显
  useEffect(() => {
    if (!editActionName) return;
    const connectorId = getHashQueryParam('id');
    if (!connectorId) return;
    setEditLoading(true);
    getConnectorActionInfo(connectorId, editActionName)
      .then((res: any) => {
        console.log(res);
        const values = actionConfigToFormValues(res);
        form.setValues(values);
      })
      .catch((e: unknown) => {
        Message.error((e as Error)?.message ?? '获取动作详情失败');
      })
      .finally(() => setEditLoading(false));
  }, [editActionName, form]);

  // 编辑模式：基础信息-动作名称不允许编辑
  useEffect(() => {
    if (!editActionName) return;
    form.setFieldState('basic.actionName', (state) => {
      state.disabled = true;
    });
  }, [editActionName, form]);

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
  const [debugLoading, setDebugLoading] = useState(false);
  const [debugResult, setDebugResult] = useState<unknown>(null);

  const buildActionConfig = (values: Record<string, unknown>) => {
    const fromTabs = getTabsData(values);
    const debugTabs = (values.debugParamsTabs ?? {}) as Record<string, unknown>;
    return {
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
  };

  const handleDebug = async () => {
    const values = form.values as Record<string, unknown>;
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }

    const basic = (values.basic ?? {}) as { actionName?: string };
    const actionName = basic.actionName || editActionName;
    if (!actionName) {
      Message.error('请先填写动作名称');
      return;
    }

    const combined = buildActionConfig(values);

    setDebugLoading(true);
    try {
      const params: any = { debug: combined.debug };
      const res = await debugAction(params);
      console.log('debugAction result: ', res);
      setDebugResult(res);
      Message.success('调试成功');
    } catch (e) {
      Message.error((e as Error)?.message ?? '调试失败');
    } finally {
      setDebugLoading(false);
    }
  };

  const handleFinish = async () => {
    const values = form.values as Record<string, unknown>;
    const combined = buildActionConfig(values);
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    setSaveLoading(true);
    try {
      const params: SaveConnectorActionReq = { actionConfig: combined };
      if (isEditMode && editActionName) {
        await updateHTTPAction(connectorId, editActionName, params);
      } else {
        await saveConnectorAction(connectorId, params);
      }
      Message.success('保存成功');
    } catch (e) {
      Message.error((e as Error)?.message ?? '保存失败');
    } finally {
      setSaveLoading(false);
    }
  };

  return (
    <div className={styles.createScriptActionPage}>
      <Spin loading={editLoading} style={{ width: '100%' }} tip="加载动作详情...">
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
            {currentStep === 3 && (
              <Button type="default" loading={debugLoading} onClick={handleDebug}>
                调试
              </Button>
            )}
            {onSuccess && (
              <Button type="text" onClick={onSuccess}>
                返回
              </Button>
            )}
          </div>
        </FormProvider>

        {/* 展示debug的结果 */}
        {currentStep === 3 && debugResult != null && (
          <div style={{ marginTop: 16 }}>
            <div style={{ fontWeight: 500, marginBottom: 8 }}>调试结果</div>
            <pre
              style={{
                maxHeight: 260,
                overflow: 'auto',
                background: '#f5f5f5',
                padding: 12,
                borderRadius: 4,
                fontSize: 12
              }}
            >
              {typeof debugResult === 'string' ? debugResult : JSON.stringify(debugResult, null, 2)}
            </pre>
          </div>
        )}
      </Spin>
    </div>
  );
};

export default CreateHTTPActionPage;
