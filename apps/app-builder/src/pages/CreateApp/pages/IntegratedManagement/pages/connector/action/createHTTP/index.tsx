import { Button, Input, Message, Modal, Select, Space, Spin, Steps, Upload } from '@arco-design/web-react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider, useForm, observer } from '@formily/react';
import {
  createConnectorAction,
  debugAction,
  getConnectgorEnvironmentConfig,
  getConnectorActionByCode,
  getEnableConnectorEnvironment,
  updateConnectorAction,
  type ConnectorActionDO,
  type CreateConnectorActionReq,
  type UpdateConnectorActionReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { componentMap, FormilyFormItem } from '../../../../../../../../components/DynamicForm/componentMapper';

import { DebugParamReadOnlyTable } from './DebugParamReadOnlyTable';
import { DebugHeadersTable } from './DebugHeadersTable';
import styles from './index.module.less';
import { ActionOutputArrayTable, OutputParamArrayTable } from './OutputParamArrayTable';
import { ActionInputArrayTable, ParamArrayTable } from './ParamArrayTable';
import { SuccessConditionTable } from './SuccessConditionTable';
import { step1Schema } from './step1';
import { step2Schema } from './step2';
import { step3Schema } from './step3';
import { step4Schema } from './step4';
import type { CreateHTTPActionPageProps, HttpActionConfig } from './types';
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
  formValuesToOpenApiConfig,
  generateOutputsFromValues,
  openApiConfigToFormValues,
  scanExposedFields
} from './transform';
import {
  getTabArray,
  getTabString
} from './utils';

/** 生成动作编码：ACTION_ + 8位，英文大写开头，包含英文大写和数字 */
const generateActionCode = (): string => {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  // 第一位必须是英文大写
  let code = letters.charAt(Math.floor(Math.random() * letters.length));
  // 后面7位是英文大写或数字
  for (let i = 0; i < 7; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return `ACTION_${code}`;
};

/** 认证方式映射 */
const AUTH_TYPE_NAMES: Record<string, string> = {
  none: '无认证',
  basic: 'Basic Auth',
  bearer: 'Bearer Token',
  apikey: 'API Key',
  oauth2: 'OAuth 2.0',
  custom: '自定义签名'
};

/** 调试 URL 显示组件 */
const DebugUrlText = observer(() => {
  const form = useForm();
  const values = form.values as Record<string, unknown>;
  // url 现在是相对路径（来自 HTTP 定义）
  const urlPath = typeof values.url === 'string' ? values.url : '';
  const baseUrl = typeof values.baseUrl === 'string' ? values.baseUrl : '';

  // 如果都没有配置
  if (!baseUrl && !urlPath) {
    return <span style={{ color: '#c9cdd4', fontSize: 14 }}>未设置</span>;
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <span style={{
        padding: '4px 12px',
        background: '#f7f8fa',
        border: '1px solid #e5e6eb',
        borderRadius: '4px 0 0 4px',
        color: baseUrl ? '#86909c' : '#c9cdd4',
        fontSize: 14,
        maxWidth: 300,
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap'
      }}>
        {baseUrl || '未配置域名'}
      </span>
      <span style={{
        padding: '4px 12px',
        background: '#fff',
        border: '1px solid #e5e6eb',
        borderLeft: 'none',
        borderRadius: '0 4px 4px 0',
        color: urlPath ? '#1d2129' : '#c9cdd4',
        fontSize: 14,
        flex: 1
      }}>
        {urlPath || '未设置路径'}
      </span>
    </div>
  );
});

/** 认证方式显示组件 */
const DebugAuthTypeText = observer(() => {
  const form = useForm();
  const values = form.values as Record<string, unknown>;
  const authType = typeof values.authType === 'string' ? values.authType : '';
  const authDisplay = authType ? (AUTH_TYPE_NAMES[authType] || authType) : '';

  // 如果没有配置认证方式
  if (!authType) {
    return <span style={{ color: '#c9cdd4', fontSize: 14 }}>未设置</span>;
  }

  return (
    <span style={{
      padding: '4px 12px',
      background: '#f7f8fa',
      borderRadius: '4px',
      color: authType === 'none' ? '#86909c' : '#1d2129',
      fontSize: 14,
      display: 'inline-block'
    }}>
      {authDisplay}
    </span>
  );
});

/** 请求方法显示组件 */
const DebugMethodText = observer(() => {
  const form = useForm();
  const values = form.values as Record<string, unknown>;
  const method = typeof values.method === 'string' ? values.method : '';

  if (!method) {
    return <span style={{ color: '#c9cdd4', fontSize: 14 }}>未设置</span>;
  }

  return (
    <span style={{
      padding: '4px 12px',
      background: '#f7f8fa',
      borderRadius: '4px',
      color: '#1d2129',
      fontSize: 14,
      display: 'inline-block'
    }}>
      {method}
    </span>
  );
});

/** 一键生成入参出参按钮 */
const IOGenerateButton: React.FC = () => {
  const form = useForm();

  const handleGenerate = () => {
    const values = form.values as Record<string, unknown>;

    // 1. 生成入参（从 ${xxx} 变量）
    const exposedInputs = scanExposedFields(values);

    // 2. 生成出参（从响应 JSON）
    const exposedOutputs = generateOutputsFromValues(values);

    // 3. 获取现有的入参出参
    const existingInputs = Array.isArray(values.inputs) ? (values.inputs as unknown[]) : [];
    const existingOutputs = Array.isArray(values.outputs) ? (values.outputs as unknown[]) : [];

    // 4. 合并入参（避免覆盖已有的）
    const existingInputKeys = new Set(
      existingInputs
        .map((row) => (isRecord(row) && typeof row.key === 'string' ? row.key : ''))
        .filter((k) => k)
    );
    const newInputs = exposedInputs.filter((f) => !existingInputKeys.has(f.key));

    // 5. 合并出参（避免覆盖已有的）
    const existingOutputKeys = new Set(
      existingOutputs
        .map((row) => (isRecord(row) && typeof row.key === 'string' ? row.key : ''))
        .filter((k) => k)
    );
    const newOutputs = exposedOutputs.filter((f) => !existingOutputKeys.has(f.key));

    // 6. 更新表单
    if (newInputs.length > 0 || newOutputs.length > 0) {
      form.setValues({
        ...values,
        inputs: [
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
        ],
        outputs: [
          ...existingOutputs,
          ...newOutputs.map((f) => ({
            id: `output-${Date.now()}-${Math.random().toString(36).slice(2)}`,
            key: f.key,
            fieldName: f.fieldName,
            fieldType: f.fieldType,
            description: f.description,
            fromKind: f.mapKind === 'header' ? 'header' : 'body',
            fromKey: f.mapKey,
            jsonPath: f.mapKey
          }))
        ]
      });
      Message.success(`已生成 ${newInputs.length} 个入参，${newOutputs.length} 个出参`);
    } else {
      Message.info('没有新的入参或出参需要生成');
    }
  };

  return (
    <div style={{ marginBottom: 16 }}>
      <Button type="primary" onClick={handleGenerate}>
        一键生成入参出参
      </Button>
    </div>
  );
};

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem,
    ParamArrayTable,
    ActionInputArrayTable,
    OutputParamArrayTable,
    ActionOutputArrayTable,
    DebugParamReadOnlyTable,
    DebugHeadersTable,
    SuccessConditionTable,
    IOGenerateButton,
    DebugUrlText,
    DebugAuthTypeText,
    DebugMethodText
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
  // 编辑模式下的动作 ID（用于更新）
  const [editActionId, setEditActionId] = useState<number | null>(null);

  const form = useMemo(
    () =>
      createForm({
        initialValues: {
          baseUrl: '',
          authType: ''
        }
      }),
    []
  );

  const STEP_SCHEMAS = [step1Schema, step2Schema, step3Schema, step4Schema];
  const currentSchema = STEP_SCHEMAS[currentStep] ?? step1Schema;
  const isLastStep = currentStep === STEP_LIST.length - 1;
  const isEditMode = Boolean(editActionName);

  // 每次步骤变化时打印 baseUrl 状态
  useEffect(() => {
    console.log(`[Step${currentStep}] form.values has baseUrl?`, 'baseUrl' in form.values, 'value:', form.values.baseUrl);
    console.log(`[Step${currentStep}] Object.keys:`, Object.keys(form.values));
  }, [currentStep, form.values]);

  const importOpenApiToForm = useCallback(
    (raw: string, opKey?: string) => {
      debugger
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
    const connectorUuid = getHashQueryParam('id');
    if (!connectorUuid) return;
    setEditLoading(true);

    // 使用新的 API：getConnectorActionByCode
    getConnectorActionByCode(connectorUuid, editActionName)
      .then((res: unknown) => {
        console.log('[editMode] raw response:', res);

        // API 返回的是 ConnectorActionDO 对象
        // 通过 unknown 进行类型转换
        const actionDO = isRecord(res) ? (res as unknown as ConnectorActionDO) : null;

        if (!actionDO) {
          Message.error('获取动作详情失败：响应格式错误');
          return;
        }

        // 保存动作 ID 用于更新
        if (actionDO.id) {
          setEditActionId(actionDO.id);
        }

        // 解析 actionConfig（JSON 字符串）
        let actionConfig: HttpActionConfig | null = null;
        if (typeof actionDO.actionConfig === 'string' && actionDO.actionConfig) {
          try {
            actionConfig = JSON.parse(actionDO.actionConfig);
          } catch (e) {
            console.error('[editMode] Failed to parse actionConfig:', e);
          }
        }

        let values: Record<string, unknown>;

        if (actionConfig && 'path' in actionConfig && 'method' in actionConfig) {
          // 新格式：OpenAPI 格式
          console.log('[editMode] detected new OpenAPI format');
          values = openApiConfigToFormValues(actionConfig as HttpActionConfig);
        } else if (actionConfig) {
          // 旧格式
          console.log('[editMode] detected old format');
          values = actionConfigToFormValues(actionConfig);
        } else {
          // 从其他字段构建
          console.log('[editMode] building from actionDO fields');
          values = {
            basic: {
              actionName: actionDO.actionName || '',
              actionDescription: actionDO.description || ''
            },
            url: '',
            method: 'GET',
            tabs: {
              params: { queryParams: [], pathParams: [] },
              headers: { requestHeaders: [] },
              body: { bodyMode: 'none', requestBody: [], jsonBody: { requestBodyJson: '' } }
            },
            responseTabs: {
              responseBodyTab: {
                responseBodyMode: 'json',
                responseBodyJsonWrapper: { responseBodyJson: '' }
              }
            },
            successCondition: { successConditions: [], errorMessagePath: '' },
            inputs: [],
            outputs: [],
            baseUrl: '',
            authType: ''
          };

          // 解析 inputSchema 和 outputSchema
          if (actionDO.inputSchema) {
            try {
              values.inputs = JSON.parse(actionDO.inputSchema);
            } catch (e) {
              console.warn('[editMode] Failed to parse inputSchema:', e);
            }
          }
          if (actionDO.outputSchema) {
            try {
              values.outputs = JSON.parse(actionDO.outputSchema);
            } catch (e) {
              console.warn('[editMode] Failed to parse outputSchema:', e);
            }
          }
        }

        console.log('[editMode] converted values:', values);
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

  useEffect(() => {
    if (currentStep !== 2) return;
    const values = form.values as Record<string, unknown>;

    // 扫描 Step2 中包含 ${xxx} 变量的字段
    const exposedInputs = scanExposedFields(values);
    // 输出参数不再自动扫描，用户需要在 Step3 手动配置

    const existingInputs = Array.isArray(values.inputs) ? (values.inputs as unknown[]) : [];
    const existingOutputs = Array.isArray(values.outputs) ? (values.outputs as unknown[]) : [];

    const existingInputKeys = new Set(
      existingInputs
        .map((row) => (isRecord(row) && typeof row.key === 'string' ? row.key : ''))
        .filter((k) => k)
    );

    // 只添加新的入参（不覆盖已有的）
    const newInputs = exposedInputs.filter((f) => !existingInputKeys.has(f.key));

    if (newInputs.length > 0) {
      form.setValues({
        ...values,
        inputs: [
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
        ],
        outputs: existingOutputs,  // 保持现有输出不变
        baseUrl: values.baseUrl ?? '',
        authType: values.authType ?? ''
      });
    }
  }, [currentStep, form]);

  useEffect(() => {
    if (currentStep !== 3) return;
    const values = form.values as Record<string, unknown>;

    // 检查是否已有保存的调试值
    const savedDebugHeaders = Array.isArray(values.debugHeaders) ? values.debugHeaders : [];
    const savedDebugBody = typeof values.debugBody === 'string' ? values.debugBody : '';
    const hasSavedDebugData = savedDebugHeaders.length > 0 || savedDebugBody;

    // 构建调试请求数据（如果没有保存的值）
    const buildDebugData = () => {
      const requestHeaders = getTabArray(values, 'requestHeaders');
      const bodyMode = getTabString(values, 'bodyMode') || 'none';

      // 收集所有 headers
      const headers: { key: string; value: string }[] = [];
      requestHeaders.forEach((row) => {
        if (!isRecord(row)) return;
        const key = typeof row.key === 'string' ? row.key : '';
        const value = typeof row.defaultValue === 'string' ? row.defaultValue : '';
        if (key) headers.push({ key, value });
      });

      // 构建 body JSON
      let bodyJson = '';
      if (bodyMode === 'json') {
        bodyJson = getTabString(values, 'requestBodyJson');
      } else if (bodyMode === 'kv') {
        const requestBody = getTabArray(values, 'requestBody');
        const bodyObj: Record<string, unknown> = {};
        requestBody.forEach((row) => {
          if (!isRecord(row)) return;
          const key = typeof row.key === 'string' ? row.key : '';
          const value = typeof row.defaultValue === 'string' ? row.defaultValue : '';
          if (key) bodyObj[key] = value;
        });
        if (Object.keys(bodyObj).length > 0) {
          bodyJson = JSON.stringify(bodyObj, null, 2);
        }
      }

      return { headers, bodyJson };
    };

    // 使用已保存的值或重新构建
    const debugHeaders = hasSavedDebugData ? savedDebugHeaders : buildDebugData().headers;
    const debugBody = hasSavedDebugData ? savedDebugBody : buildDebugData().bodyJson;

    // 加载环境配置获取 baseUrl 和 authType
    const loadEnvConfig = async () => {
      const connectorId = getHashQueryParam('id');

      if (!connectorId) {
        form.setValues({
          ...values,
          debugHeaders,
          debugBody,
          baseUrl: '',
          authType: ''
        });
        return;
      }

      try {
        const envName = await getEnableConnectorEnvironment(connectorId);
        console.log('[loadEnvConfig] envName:', envName);

        if (envName) {
          const res = await getConnectgorEnvironmentConfig(connectorId, envName);
          console.log('[loadEnvConfig] res:', res);

          const data = res?.data ?? res;
          const envConfig = data?.schema?.envConfig ?? {};

          const basicInfo = envConfig?.basicInfo ?? {};
          const baseUrl = basicInfo?.baseUrl ?? '';

          const authInfo = envConfig?.authInfo ?? {};
          const authType = authInfo?.authType ?? '';

          form.setValues({
            ...values,
            debugHeaders,
            debugBody,
            baseUrl,
            authType
          });
          return;
        }
      } catch (e) {
        console.warn('加载环境配置失败:', e);
      }

      form.setValues({
        ...values,
        debugHeaders,
        debugBody,
        baseUrl: '',
        authType: ''
      });
    };

    loadEnvConfig();
  }, [currentStep, form]);

  const [saveLoading, setSaveLoading] = useState(false);
  const [debugLoading, setDebugLoading] = useState(false);
  const [debugResult, setDebugResult] = useState<unknown>(null);

  const handleDebug = async () => {
    const values = form.values as Record<string, unknown>;
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }

    // 从用户编辑的 debugHeaders 和 debugBody 获取数据
    const debugHeaders = Array.isArray(values.debugHeaders) ? values.debugHeaders : [];
    const debugBody = typeof values.debugBody === 'string' ? values.debugBody : '';
    const baseUrl = typeof values.baseUrl === 'string' ? values.baseUrl : '';
    const urlPath = typeof values.url === 'string' ? values.url : '';
    const method = typeof values.method === 'string' ? values.method : 'GET';

    // 构建完整 URL
    const fullUrl = baseUrl + urlPath;

    // 构建请求头数组
    const requestHeaders = debugHeaders.map((h: { key: string; value: string }) => ({
      key: h.key,
      fieldName: h.key,
      fieldValue: h.value,
      fieldType: 'string'
    }));

    // 构建请求体
    let requestBody: unknown[] = [];
    if (debugBody) {
      try {
        JSON.parse(debugBody); // 验证 JSON 是否有效
        requestBody = [{ key: 'body', fieldName: 'body', fieldType: 'object', fieldValue: debugBody }];
      } catch {
        requestBody = [{ key: 'body', fieldName: 'body', fieldType: 'string', fieldValue: debugBody }];
      }
    }

    // 构建调试配置
    const debugConfig = {
      debug: {
        url: fullUrl,
        method,
        requestHeaders,
        requestBody,
        queryParams: [],
        pathParams: []
      }
    };

    setDebugLoading(true);
    setDebugResult(null);
    try {
      const res = await debugAction(debugConfig);
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
      method: imported.method,
      baseUrl: current.baseUrl ?? '',
      authType: current.authType ?? ''
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
    console.log('[handleFinish] form.values inputs:', values.inputs);
    console.log('[handleFinish] form.values outputs:', values.outputs);

    // 使用 OpenAPI 兼容格式保存
    const openApiConfig = formValuesToOpenApiConfig(values);
    console.log('[handleFinish] openApiConfig:', JSON.stringify(openApiConfig, null, 2));

    const connectorUuid = getHashQueryParam('id');
    if (!connectorUuid) {
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
      // 获取基本信息
      const basic = isRecord(values.basic) ? (values.basic as Record<string, unknown>) : {};
      const actionName = typeof basic.actionName === 'string' ? basic.actionName : '';
      const actionDescription = typeof basic.actionDescription === 'string' ? basic.actionDescription : '';

      // 构建 inputSchema 和 outputSchema
      const inputs = Array.isArray(values.inputs) ? values.inputs : [];
      const outputs = Array.isArray(values.outputs) ? values.outputs : [];

      if (isEditMode && editActionId) {
        // 更新模式
        const params: UpdateConnectorActionReq = {
          id: editActionId,
          actionName,
          description: actionDescription,
          inputSchema: JSON.stringify(inputs),
          outputSchema: JSON.stringify(outputs),
          actionConfig: JSON.stringify(openApiConfig)
        };
        await updateConnectorAction(params);
      } else {
        // 创建模式 - 自动生成动作编码
        const actionCode = generateActionCode();
        const params: CreateConnectorActionReq = {
          connectorUuid,
          connectorType: 'HTTP',
          actionCode,
          actionName,
          description: actionDescription,
          inputSchema: JSON.stringify(inputs),
          outputSchema: JSON.stringify(outputs),
          actionConfig: JSON.stringify(openApiConfig),
          activeStatus: 1
        };
        await createConnectorAction(params);
      }
      Message.success('保存成功');
      onSuccess?.();
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
        <Steps current={currentStep} onChange={(current) => setCurrentStep(current)} style={{ marginBottom: 24 }}>
          {STEP_LIST.map((item) => (
            <Steps.Step key={item.key} title={item.title} style={{ cursor: 'pointer' }} />
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
