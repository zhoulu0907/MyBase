import { isRecord, upsertHeaderRow } from '../openapi';
import type { ExposedField } from './types';
import {
  buildJsonBodyRows,
  buildResponseBodyFromJson,
  extractVariables,
  getTabArray,
  getTabString,
  normalizeMethodForRequest,
  replaceVariables
} from './utils';

export const scanExposedFields = (values: Record<string, unknown>): ExposedField[] => {
  const fields: ExposedField[] = [];
  const addedKeys = new Set<string>();

  const addFieldFromVariable = (row: Record<string, unknown>, mapKind: string) => {
    if (!isRecord(row)) return;

    const defaultValue = typeof row.defaultValue === 'string' ? row.defaultValue : '';
    // 从 defaultValue 中提取 ${xxx} 变量
    const vars = extractVariables(defaultValue);
    if (vars.length === 0) return;

    vars.forEach((v) => {
      if (addedKeys.has(v)) return;
      addedKeys.add(v);

      const key = typeof row.key === 'string' ? row.key : '';
      const fieldName = typeof row.fieldName === 'string' ? row.fieldName : key;
      const fieldType = typeof row.fieldType === 'string' ? row.fieldType : 'string';
      const description = typeof row.description === 'string' ? row.description : '';
      const required = row.required === true;

      fields.push({
        key: v,                          // 变量名作为入参 key
        fieldName: fieldName || v,       // 显示名称
        fieldType,
        description,
        mapKind,
        mapKey: key,                     // 映射到底层的 key
        required,
        defaultValue
      });
    });
  };

  // 扫描各区域的变量
  const headers = getTabArray(values, 'requestHeaders');
  headers.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'header'));

  const queryParams = getTabArray(values, 'queryParams');
  queryParams.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'query'));

  const pathParams = getTabArray(values, 'pathParams');
  pathParams.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'path'));

  const bodyMode = getTabString(values, 'bodyMode');
  if (bodyMode === 'kv') {
    const bodyRows = getTabArray(values, 'requestBody');
    bodyRows.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'body'));
  } else if (bodyMode === 'json') {
    // JSON body 中也可能有 ${xxx} 变量
    const jsonBody = getTabString(values, 'requestBodyJson');
    if (jsonBody) {
      const vars = extractVariables(jsonBody);
      vars.forEach((v) => {
        if (addedKeys.has(v)) return;
        addedKeys.add(v);
        fields.push({
          key: v,
          fieldName: v,
          fieldType: 'string',
          description: '',
          mapKind: 'body',
          mapKey: v,
          required: false,
          defaultValue: `\${${v}}`
        });
      });
    }
  }

  return fields;
};

export const scanExposedOutputFields = (values: Record<string, unknown>): ExposedField[] => {
  // 输出参数不自动扫描，用户需要在 Step3 手动配置
  return [];
};

export function actionConfigToFormValues(config: unknown): Record<string, unknown> {
  const cfg = isRecord(config) ? config : {};
  const basic = (isRecord(cfg.basicInfo) ? cfg.basicInfo : {}) as Record<string, unknown>;
  const request = (isRecord(cfg.inputConfig) ? cfg.inputConfig : {}) as Record<string, unknown>;
  const response = (isRecord(cfg.outputConfig) ? cfg.outputConfig : {}) as Record<string, unknown>;
  const debug = (isRecord(cfg.debugConfig) ? cfg.debugConfig : {}) as Record<string, unknown>;
  const io = (isRecord(cfg.ioConfig) ? cfg.ioConfig : {}) as Record<string, unknown>;

  const requestHeaders = (request.requestHeaders ?? []) as unknown[];
  const requestBody = (request.requestBody ?? []) as unknown[];
  const queryParams = (request.queryParams ?? []) as unknown[];
  const pathParams = (request.pathParams ?? []) as unknown[];

  let bodyMode = requestBody.length > 0 ? 'kv' : 'none';
  let requestBodyJson = '';
  let requestBodyRows = requestBody;
  const bodyRow = requestBody.find((row) => {
    if (!isRecord(row)) return false;
    const k = typeof row.key === 'string' ? row.key : '';
    const fn = typeof row.fieldName === 'string' ? row.fieldName : '';
    return k === 'body' || fn === 'body';
  });
  if (isRecord(bodyRow) && (bodyRow.fieldType === 'object' || bodyRow.fieldType === 'array')) {
    const dv = typeof bodyRow.defaultValue === 'string' ? bodyRow.defaultValue : '';
    if (dv) {
      try {
        JSON.parse(dv);
        bodyMode = 'json';
        requestBodyJson = dv;
        requestBodyRows = [];
      } catch {
        bodyMode = 'kv';
      }
    }
  }

  const responseHeaders = Array.isArray(response.responseHeaders) ? response.responseHeaders : [];
  const responseBody = Array.isArray(response.responseBody) ? response.responseBody : [];

  const oldTabs = isRecord(cfg.tabs) ? (cfg.tabs as Record<string, unknown>) : {};
  const oldResponseHeaders = Array.isArray(oldTabs.responseHeaders) ? oldTabs.responseHeaders : [];
  const oldResponseBody = Array.isArray(oldTabs.responseBody) ? oldTabs.responseBody : [];

  const finalResponseHeaders = responseHeaders.length > 0 ? responseHeaders : oldResponseHeaders;
  const finalResponseBody = responseBody.length > 0 ? responseBody : oldResponseBody;

  let responseBodyMode = 'json';
  let responseBodyJson = '';
  let responseBodyText = '';
  if (finalResponseBody.length > 0) {
    const bodyRow = finalResponseBody.find((row) => {
      if (!isRecord(row)) return false;
      const k = typeof row.key === 'string' ? row.key : '';
      const fn = typeof row.fieldName === 'string' ? row.fieldName : '';
      return k === 'body' || fn === 'body';
    });
    if (isRecord(bodyRow)) {
      const dv = typeof bodyRow.defaultValue === 'string' ? bodyRow.defaultValue : '';
      if (dv) {
        try {
          JSON.parse(dv);
          responseBodyMode = 'json';
          responseBodyJson = dv;
        } catch {
          responseBodyMode = 'text';
          responseBodyText = dv;
        }
      }
    }
  }

  return {
    basic,
    tabs: {
      params: {
        queryParams,
        pathParams
      },
      headers: {
        requestHeaders
      },
      body: {
        bodyMode,
        requestBody: requestBodyRows,
        requestBodyJson
      }
    },
    successCondition: {
      successConditions: Array.isArray(cfg.successConditions) ? cfg.successConditions : [],
      errorMessagePath: typeof cfg.errorMessagePath === 'string' ? cfg.errorMessagePath : ''
    },
    responseTabs: {
      responseHeaders: finalResponseHeaders,
      responseBodyTab: {
        responseBodyMode,
        responseBodyJson,
        responseBodyText
      },
      responseBody: finalResponseBody
    },
    io: {
      inputs: Array.isArray(io.inputs) ? io.inputs : [],
      outputs: Array.isArray(io.outputs) ? io.outputs : []
    },
    url: debug.url ?? '',
    method: debug.methodMode ?? debug.method ?? 'GET'
  };
}

export const buildActionConfig = (values: Record<string, unknown>) => {
  const methodInput = values.method ?? 'GET';
  const { actualMethod, methodMode } = normalizeMethodForRequest(methodInput);

  const bodyMode =
    getTabString(values, 'bodyMode') || (getTabArray(values, 'requestBody').length > 0 ? 'kv' : 'none');
  const requestBody =
    bodyMode === 'json'
      ? buildJsonBodyRows(getTabString(values, 'requestBodyJson'))
      : bodyMode === 'none'
        ? []
        : getTabArray(values, 'requestBody');
  const requestHeaders = getTabArray(values, 'requestHeaders');
  const queryParams = getTabArray(values, 'queryParams');
  const pathParams = getTabArray(values, 'pathParams');

  const responseTabs = isRecord(values.responseTabs) ? (values.responseTabs as Record<string, unknown>) : {};
  const responseHeaders = Array.isArray(responseTabs.responseHeaders) ? responseTabs.responseHeaders : [];
  const responseBodyTab = isRecord(responseTabs.responseBodyTab) ? (responseTabs.responseBodyTab as Record<string, unknown>) : {};
  const responseBodyMode = typeof responseBodyTab.responseBodyMode === 'string' ? responseBodyTab.responseBodyMode : 'json';
  const responseBodyJson = typeof responseBodyTab.responseBodyJson === 'string' ? responseBodyTab.responseBodyJson : '';
  const responseBodyText = typeof responseBodyTab.responseBodyText === 'string' ? responseBodyTab.responseBodyText : '';
  const responseBody =
    responseBodyMode === 'json' && responseBodyJson
      ? buildResponseBodyFromJson(responseBodyJson)
      : responseBodyMode === 'text' && responseBodyText
        ? [{ key: 'body', fieldName: 'body', fieldType: 'string', defaultValue: responseBodyText, description: '' }]
        : Array.isArray(responseTabs.responseBody)
          ? responseTabs.responseBody
          : [];

  const io = isRecord(values.io) ? (values.io as Record<string, unknown>) : {};
  const ioInputs = Array.isArray(io.inputs) ? (io.inputs as unknown[]) : [];
  const ioOutputs = Array.isArray(io.outputs) ? (io.outputs as unknown[]) : [];
  const debugInputs = Array.isArray(values.debugInputs) ? (values.debugInputs as unknown[]) : [];

  const normalizeDebugRows = (arr: unknown[]) =>
    Array.isArray(arr)
      ? arr.map((row) =>
          typeof row === 'object' && row !== null
            ? {
                ...row,
                fieldValue:
                  (row as Record<string, unknown>).fieldValue ?? (row as Record<string, unknown>).defaultValue ?? ''
              }
            : { fieldValue: '' }
        )
      : [];

  const debugRequestHeaders = normalizeDebugRows(requestHeaders);
  const debugQueryParams = normalizeDebugRows(queryParams);
  const debugPathParams = normalizeDebugRows(pathParams);
  const debugRequestBody = normalizeDebugRows(requestBody);

  const debugInputValueMap = new Map<string, string>();
  (Array.isArray(debugInputs) ? debugInputs : []).forEach((row) => {
    if (!isRecord(row)) return;
    const k = typeof row.key === 'string' ? row.key : '';
    const v = typeof row.fieldValue === 'string' ? row.fieldValue : typeof row.defaultValue === 'string' ? row.defaultValue : '';
    if (k) debugInputValueMap.set(k, v);
  });

  const applyMappedValue = (arr: any[], mapKey: string, v: string) => {
    const idx = arr.findIndex((r) => isRecord(r) && ((r.key as string) === mapKey || (r.fieldName as string) === mapKey));
    if (idx >= 0) {
      arr[idx] = { ...arr[idx], fieldValue: v };
    }
  };

  const applyVariableReplacement = (arr: any[]) => {
    arr.forEach((row) => {
      if (!isRecord(row)) return;
      const fieldValue = typeof row.fieldValue === 'string' ? row.fieldValue : '';
      const defaultValue = typeof row.defaultValue === 'string' ? row.defaultValue : '';
      if (fieldValue && fieldValue.includes('${')) {
        row.fieldValue = replaceVariables(fieldValue, debugInputValueMap);
      }
      if (defaultValue && defaultValue.includes('${')) {
        row.defaultValue = replaceVariables(defaultValue, debugInputValueMap);
      }
    });
  };

  const mappingInputs = ioInputs.length > 0 ? ioInputs : debugInputs;
  mappingInputs.forEach((row) => {
    if (!isRecord(row)) return;
    const inputKey = typeof row.key === 'string' ? row.key : '';
    const mapKind = typeof row.mapKind === 'string' ? row.mapKind : '';
    const mapKey = typeof row.mapKey === 'string' ? row.mapKey : '';
    if (!inputKey || !mapKind || !mapKey) return;
    const v = debugInputValueMap.get(inputKey) ?? '';
    switch (mapKind) {
      case 'header':
        applyMappedValue(debugRequestHeaders as any[], mapKey, v);
        break;
      case 'query':
        applyMappedValue(debugQueryParams as any[], mapKey, v);
        break;
      case 'path':
        applyMappedValue(debugPathParams as any[], mapKey, v);
        break;
      case 'body':
        applyMappedValue(debugRequestBody as any[], mapKey, v);
        break;
    }
  });

  applyVariableReplacement(debugRequestHeaders as any[]);
  applyVariableReplacement(debugQueryParams as any[]);
  applyVariableReplacement(debugPathParams as any[]);
  applyVariableReplacement(debugRequestBody as any[]);

  const withPostJsonHeaders = methodMode === 'POST_JSON'
    ? { requestHeaders: upsertHeaderRow(debugRequestHeaders, 'Content-Type', 'application/json') }
    : { requestHeaders: debugRequestHeaders };

  const debugRequestHeadersFinal = withPostJsonHeaders.requestHeaders;

  const successCondition = isRecord(values.successCondition) ? (values.successCondition as Record<string, unknown>) : {};

  return {
    basic: values.basic ?? {},
    request: {
      requestHeaders: withPostJsonHeaders.requestHeaders,
      requestBody: requestBody,
      queryParams: queryParams,
      pathParams: pathParams
    },
    response: {
      responseHeaders,
      responseBody
    },
    successConditions: Array.isArray(successCondition.successConditions) ? successCondition.successConditions : [],
    errorMessagePath: typeof successCondition.errorMessagePath === 'string' ? successCondition.errorMessagePath : '',
    ioConfig: {
      inputs: ioInputs,
      outputs: ioOutputs
    },
    debug: {
      url: values.url ?? '',
      method: actualMethod,
      methodMode,

      requestHeaders: debugRequestHeadersFinal,
      requestBody: debugRequestBody,
      queryParams: debugQueryParams,
      pathParams: debugPathParams
    }
  };
};
