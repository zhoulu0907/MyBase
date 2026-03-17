import { Button, Input, Message, Modal, Select, Space, Spin, Steps, Upload } from '@arco-design/web-react';
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
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { componentMap, FormilyFormItem } from '../../../../../../../../components/DynamicForm/componentMapper';

import { DebugParamReadOnlyTable } from './DebugParamReadOnlyTable';
import styles from './index.module.less';
import { ActionOutputArrayTable, OutputParamArrayTable } from './OutputParamArrayTable';
import { ActionInputArrayTable, ParamArrayTable } from './ParamArrayTable';
import { SuccessConditionTable } from './SuccessConditionTable';
import { step1Schema } from './step1';
import { step2Schema } from './step2';
import { step3Schema } from './step3';
import { step4Schema } from './step4';
import type { CreateHTTPActionPageProps } from './types';
import {
  buildActionNameFromOpenApi,
  buildHttpFormValuesFromOpenApi,
  getOperationKey,
  isRecord,
  parseOpenApiOperations,
  type OpenApiOperation
} from '../openapi';
import {
  actionConfigToFormValues,
  buildActionConfig,
  scanExposedFields,
  scanExposedOutputFields
} from './transform';
import {
  buildJsonBodyRows,
  getTabArray,
  getTabString,
  getTabValue
} from './utils';

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem,
    ParamArrayTable,
    ActionInputArrayTable,
    OutputParamArrayTable,
    ActionOutputArrayTable,
    DebugParamReadOnlyTable,
    SuccessConditionTable
  }
});

const STEP_LIST = [
  { key: 'basic', title: '基础信息' },
  { key: 'http', title: 'HTTP 定义' },
  { key: 'io', title: '动作出入参' },
  { key: 'debug', title: '动作调试' }
];

const CreateHTTPActionPage: React.FC<CreateHTTPActionPageProps> = ({
  editActionName,
  onSuccess,
  defaultOpenApiModal,
  openApiImport
}) => {
  const [currentStep, setCurrentStep] = useState(0);
  const [editLoading, setEditLoading] = useState(false);
  const [openApiModalOpen, setOpenApiModalOpen] = useState(false);
  const [openApiRaw, setOpenApiRaw] = useState('');
  const [openApiOps, setOpenApiOps] = useState<OpenApiOperation[]>([]);
  const [openApiOpKey, setOpenApiOpKey] = useState<string>('');
  const [openApiParseError, setOpenApiParseError] = useState<string>('');
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

  const importOpenApiToForm = useCallback(
    (raw: string, opKey?: string) => {
      const parsed = parseOpenApiOperations(raw);
      if (parsed.operations.length === 0) {
        Message.error(parsed.error || '未解析到任何接口操作');
        return;
      }

      const key = opKey || getOperationKey(parsed.operations[0]);
      const operation = parsed.operations.find((o) => getOperationKey(o) === key) || parsed.operations[0];
      const imported = buildHttpFormValuesFromOpenApi(parsed.doc, operation);

      const current = form.values as unknown as Record<string, unknown>;
      const currentBasic = (current.basic as Record<string, unknown> | undefined) ?? {};
      const nextBasic = { ...currentBasic };
      if (!nextBasic.actionName) {
        nextBasic.actionName = buildActionNameFromOpenApi(operation);
      }

      const importedTabs = isRecord(imported.tabs) ? (imported.tabs as Record<string, unknown>) : {};
      const importedResponseTabs = isRecord(imported.responseTabs) ? (imported.responseTabs as Record<string, unknown>) : {};
      const importedSuccessCondition = isRecord(imported.successCondition) ? (imported.successCondition as Record<string, unknown>) : {};
      const nextTabs = {
        params: importedTabs.params,
        headers: importedTabs.headers,
        body: importedTabs.body
      };
      const nextResponseTabs = {
        responseHeaders: importedResponseTabs.responseHeaders ?? [],
        responseBodyTab: importedResponseTabs.responseBodyTab,
        responseBody: importedResponseTabs.responseBody ?? []
      };
      const nextSuccessCondition = {
        successConditions: Array.isArray(importedSuccessCondition.successConditions) ? importedSuccessCondition.successConditions : [],
        errorMessagePath: typeof importedSuccessCondition.errorMessagePath === 'string' ? importedSuccessCondition.errorMessagePath : ''
      };

      form.setValues({
        ...current,
        basic: nextBasic,
        tabs: nextTabs,
        responseTabs: nextResponseTabs,
        successCondition: nextSuccessCondition,
        url: imported.url,
        method: imported.method
      });
    },
    [form]
  );

  useEffect(() => {
    if (defaultOpenApiModal) {
      setOpenApiModalOpen(true);
    }
  }, [defaultOpenApiModal]);

  useEffect(() => {
    if (!openApiImport?.token) return;
    importOpenApiToForm(openApiImport.raw, openApiImport.opKey);
    setOpenApiModalOpen(false);
  }, [importOpenApiToForm, openApiImport?.opKey, openApiImport?.raw, openApiImport?.token]);

  useEffect(() => {
    if (!editActionName) return;
    const connectorId = getHashQueryParam('id');
    if (!connectorId) return;
    setEditLoading(true);
    getConnectorActionInfo(connectorId, editActionName)
      .then((res: unknown) => {
        console.log(res);
        const values = actionConfigToFormValues(res);
        form.setValues(values);
      })
      .catch((e: unknown) => {
        Message.error((e as Error)?.message ?? '获取动作详情失败');
      })
      .finally(() => setEditLoading(false));
  }, [editActionName, form]);

  useEffect(() => {
    if (!editActionName) return;
    form.setFieldState('basic.actionName', (state) => {
      state.disabled = true;
    });
  }, [editActionName, form]);

  const getTabsData = (values: Record<string, unknown>) => {
    return (key: string) => getTabValue(values, key) ?? [];
  };

  useEffect(() => {
    if (currentStep !== 2) return;
    const values = form.values as Record<string, unknown>;
    
    const exposedInputs = scanExposedFields(values);
    const exposedOutputs = scanExposedOutputFields(values);
    
    const io = isRecord(values.io) ? (values.io as Record<string, unknown>) : {};
    const existingInputs = Array.isArray(io.inputs) ? (io.inputs as unknown[]) : [];
    const existingOutputs = Array.isArray(io.outputs) ? (io.outputs as unknown[]) : [];
    
    const existingInputKeys = new Set(
      existingInputs
        .map((row) => (isRecord(row) && typeof row.key === 'string' ? row.key : ''))
        .filter((k) => k)
    );
    const existingOutputKeys = new Set(
      existingOutputs
        .map((row) => (isRecord(row) && typeof row.key === 'string' ? row.key : ''))
        .filter((k) => k)
    );
    
    const newInputs = exposedInputs.filter((f) => !existingInputKeys.has(f.key));
    const newOutputs = exposedOutputs.filter((f) => !existingOutputKeys.has(f.key));
    
    if (newInputs.length > 0 || newOutputs.length > 0) {
      const mergedInputs = [
        ...existingInputs,
        ...newInputs.map((f) => ({
          id: `input-${Date.now()}-${Math.random().toString(36).slice(2)}`,
          key: f.key,
          fieldName: f.fieldName,
          fieldType: f.fieldType,
          description: f.description,
          mapKind: f.mapKind,
          mapKey: f.mapKey,
          required: f.required,
          defaultValue: f.defaultValue
        }))
      ];
      
      const mergedOutputs = [
        ...existingOutputs,
        ...newOutputs.map((f) => ({
          id: `output-${Date.now()}-${Math.random().toString(36).slice(2)}`,
          key: f.key,
          fieldName: f.fieldName,
          fieldType: f.fieldType,
          description: f.description,
          fromKind: f.mapKind,
          fromKey: f.mapKey,
          jsonPath: f.mapKey
        }))
      ];
      
      form.setValues({
        ...values,
        io: {
          inputs: mergedInputs,
          outputs: mergedOutputs
        }
      });
    }
  }, [currentStep, form]);

  useEffect(() => {
    if (currentStep !== 3) return;
    const values = form.values as Record<string, unknown>;
    const io = isRecord(values.io) ? (values.io as Record<string, unknown>) : {};
    const inputs = Array.isArray(io.inputs) ? (io.inputs as unknown[]) : [];
    const copyWithFieldValue = (arr: unknown[]) =>
      Array.isArray(arr)
        ? arr.map((row) => {
            const r = row as Record<string, unknown>;
            return {
              ...r,
              fieldValue: r.fieldValue ?? r.defaultValue ?? '',
              inputMode: 'table'
            };
          })
        : [];

    const fallbackFromRequest = () => {
      const headers = getTabArray(values, 'requestHeaders').map((r) => ({ ...r, mapKind: 'header', mapKey: (r as any)?.key || (r as any)?.fieldName }));
      const query = getTabArray(values, 'queryParams').map((r) => ({ ...r, mapKind: 'query', mapKey: (r as any)?.key || (r as any)?.fieldName }));
      const path = getTabArray(values, 'pathParams').map((r) => ({ ...r, mapKind: 'path', mapKey: (r as any)?.key || (r as any)?.fieldName }));
      const bodyMode =
        getTabString(values, 'bodyMode') || (getTabArray(values, 'requestBody').length > 0 ? 'kv' : 'none');
      const bodySource =
        bodyMode === 'json'
          ? buildJsonBodyRows(getTabString(values, 'requestBodyJson'))
          : bodyMode === 'none'
            ? []
            : getTabArray(values, 'requestBody');
      const body = (bodySource as any[]).map((r) => ({ ...r, mapKind: 'body', mapKey: (r as any)?.key || (r as any)?.fieldName }));
      return [...headers, ...query, ...path, ...body];
    };

    const debugInputSource = inputs.length > 0 ? inputs : fallbackFromRequest();
    form.setValues({
      ...values,
      debugInputs: copyWithFieldValue(debugInputSource)
    });
  }, [currentStep, form]);

  const [saveLoading, setSaveLoading] = useState(false);
  const [debugLoading, setDebugLoading] = useState(false);
  const [debugResult, setDebugResult] = useState<unknown>(null);

  const handleDebug = async () => {
    const values = form.values as Record<string, unknown>;
    const combined = buildActionConfig(values);
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    setDebugLoading(true);
    setDebugResult(null);
    try {
      const res = await debugAction(connectorId, { actionConfig: combined });
      setDebugResult(res);
      Message.success('调试成功');
    } catch (e) {
      Message.error((e as Error)?.message ?? '调试失败');
      setDebugResult({ error: (e as Error)?.message ?? '调试失败' });
    } finally {
      setDebugLoading(false);
    }
  };

  const parseOpenApiText = (raw: string) => {
    const parsed = parseOpenApiOperations(raw);
    setOpenApiOps(parsed.operations);
    const firstKey = parsed.operations.length > 0 ? getOperationKey(parsed.operations[0]) : '';
    setOpenApiOpKey(firstKey);
    setOpenApiParseError(parsed.error ?? '');
  };

  const handleParseOpenApi = () => {
    parseOpenApiText(openApiRaw);
  };

  const handleApplyOpenApi = () => {
    const parsed = parseOpenApiOperations(openApiRaw);
    if (parsed.operations.length === 0) {
      setOpenApiParseError(parsed.error || '未解析到任何接口操作');
      return;
    }
    const operation = parsed.operations.find((o) => getOperationKey(o) === openApiOpKey) || parsed.operations[0];
    const imported = buildHttpFormValuesFromOpenApi(parsed.doc, operation);

    const current = form.values as unknown as Record<string, unknown>;
    const currentBasic = (current.basic as Record<string, unknown> | undefined) ?? {};
    const nextBasic = { ...currentBasic };
    if (!nextBasic.actionName) {
      nextBasic.actionName = buildActionNameFromOpenApi(operation);
    }

    const importedTabs = isRecord(imported.tabs) ? (imported.tabs as Record<string, unknown>) : {};
    const importedResponseTabs = isRecord(imported.responseTabs) ? (imported.responseTabs as Record<string, unknown>) : {};
    const importedSuccessCondition = isRecord(imported.successCondition) ? (imported.successCondition as Record<string, unknown>) : {};
    const nextTabs = {
      params: importedTabs.params,
      headers: importedTabs.headers,
      body: importedTabs.body
    };
    const nextResponseTabs = {
      responseHeaders: importedResponseTabs.responseHeaders ?? [],
      responseBodyTab: importedResponseTabs.responseBodyTab,
      responseBody: importedResponseTabs.responseBody ?? []
    };
    const nextSuccessCondition = {
      successConditions: Array.isArray(importedSuccessCondition.successConditions) ? importedSuccessCondition.successConditions : [],
      errorMessagePath: typeof importedSuccessCondition.errorMessagePath === 'string' ? importedSuccessCondition.errorMessagePath : ''
    };

    form.setValues({
      ...current,
      basic: nextBasic,
      tabs: nextTabs,
      responseTabs: nextResponseTabs,
      successCondition: nextSuccessCondition,
      url: imported.url,
      method: imported.method
    });
    setOpenApiModalOpen(false);
  };

  const handleOpenApiFileChange = (files: unknown) => {
    const list = Array.isArray(files) ? files : [];
    const first = list[0];
    if (!isRecord(first)) return;
    const originFile = first.originFile;
    if (!(originFile instanceof File)) return;
    originFile
      .text()
      .then((text) => {
        setOpenApiRaw(text);
        parseOpenApiText(text);
      })
      .catch(() => {
        setOpenApiParseError('读取文件失败');
      });
  };

  const handleFinish = async () => {
    const values = form.values as Record<string, unknown>;
    const combined = buildActionConfig(values);
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    try {
      await form.validate();
    } catch {
      Message.error('请先完善必填项');
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
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 12 }}>
          <Button type="outline" onClick={() => setOpenApiModalOpen(true)}>
            OpenAPI 导入
          </Button>
        </div>
        <Steps current={currentStep + 1} style={{ marginBottom: 24 }}>
          {STEP_LIST.map((item) => (
            <Steps.Step key={item.key} title={item.title} />
          ))}
        </Steps>
        <FormProvider form={form}>
          <SchemaField key={currentStep} schema={currentSchema} />
          {currentStep === 3 && (
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'flex-end' }}>
              <Button type="primary" loading={debugLoading} onClick={handleDebug}>
                调试
              </Button>
            </div>
          )}
          <div className={styles.stepFooter}>
            {onSuccess && (
              <Button type="text" onClick={onSuccess}>
                返回
              </Button>
            )}
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

        {currentStep === 3 && (
          <div style={{ marginTop: 16 }}>
            <div style={{ fontWeight: 500, marginBottom: 8 }}>调试请求信息</div>
            <pre
              style={{
                maxHeight: 200,
                overflow: 'auto',
                background: '#e6f7ff',
                padding: 12,
                borderRadius: 4,
                fontSize: 12
              }}
            >
              {(() => {
                const values = form.values as Record<string, unknown>;
                const combined = buildActionConfig(values);
                const debug = combined.debug || {};
                const lines: string[] = [];
                lines.push(`${debug.method || 'GET'} ${debug.url || ''}`);
                if (debug.methodMode) {
                  lines.push(`Method Mode: ${debug.methodMode}`);
                }
                lines.push('');
                lines.push('=== Request Headers ===');
                if (Array.isArray(debug.requestHeaders) && debug.requestHeaders.length > 0) {
                  debug.requestHeaders.forEach((h: any) => {
                    lines.push(`${h.key || h.fieldName}: ${h.fieldValue || h.defaultValue || ''}`);
                  });
                } else {
                  lines.push('(无)');
                }
                lines.push('');
                lines.push('=== Query Params ===');
                if (Array.isArray(debug.queryParams) && debug.queryParams.length > 0) {
                  debug.queryParams.forEach((p: any) => {
                    lines.push(`${p.key || p.fieldName}: ${p.fieldValue || p.defaultValue || ''}`);
                  });
                } else {
                  lines.push('(无)');
                }
                lines.push('');
                lines.push('=== Path Params ===');
                if (Array.isArray(debug.pathParams) && debug.pathParams.length > 0) {
                  debug.pathParams.forEach((p: any) => {
                    lines.push(`${p.key || p.fieldName}: ${p.fieldValue || p.defaultValue || ''}`);
                  });
                } else {
                  lines.push('(无)');
                }
                lines.push('');
                lines.push('=== Request Body ===');
                if (Array.isArray(debug.requestBody) && debug.requestBody.length > 0) {
                  debug.requestBody.forEach((b: any) => {
                    if (b.fieldType === 'object' || b.fieldType === 'array') {
                      lines.push(`${b.key || b.fieldName}: ${b.fieldValue || b.defaultValue || ''}`);
                    } else {
                      lines.push(`${b.key || b.fieldName}: ${b.fieldValue || b.defaultValue || ''}`);
                    }
                  });
                } else {
                  lines.push('(无)');
                }
                return lines.join('\n');
              })()}
            </pre>
          </div>
        )}
      </Spin>

      <Modal
        title="OpenAPI 一键导入"
        visible={openApiModalOpen}
        onCancel={() => setOpenApiModalOpen(false)}
        footer={null}
        style={{ width: 860 }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size={12}>
          <Space>
            <Upload
              accept=".json,application/json"
              autoUpload={false}
              showUploadList={false}
              onChange={handleOpenApiFileChange}
            >
              <Button>上传 JSON 文件</Button>
            </Upload>
            <Button onClick={handleParseOpenApi}>解析</Button>
          </Space>
          <Input.TextArea
            value={openApiRaw}
            onChange={setOpenApiRaw}
            placeholder="粘贴 OpenAPI JSON（v3）"
            autoSize={{ minRows: 10, maxRows: 16 }}
          />
          <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <Select
              style={{ flex: 1 }}
              placeholder="请选择一个接口"
              value={openApiOpKey || undefined}
              onChange={(v) => setOpenApiOpKey(v)}
              options={openApiOps.map((o) => {
                const k = getOperationKey(o);
                return {
                  label: `${k}${o.summary ? ` - ${o.summary}` : ''}`,
                  value: k
                };
              })}
            />
            <Button type="primary" onClick={handleApplyOpenApi}>
              应用
            </Button>
          </div>
          {openApiParseError && (
            <div style={{ color: 'var(--color-danger-6)', fontSize: 12 }}>{openApiParseError}</div>
          )}
        </Space>
      </Modal>
    </div>
  );
};

export default CreateHTTPActionPage;
