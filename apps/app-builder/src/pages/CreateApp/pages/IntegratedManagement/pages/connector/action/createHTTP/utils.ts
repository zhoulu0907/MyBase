import { isRecord } from '../openapi';

export const getByPath = (obj: unknown, path: string[]) => {
  let cur: unknown = obj;
  for (const key of path) {
    if (!isRecord(cur)) return undefined;
    cur = cur[key];
  }
  return cur;
};

export const getTabValue = (values: Record<string, unknown>, key: string) => {
  const tabs = isRecord(values.tabs) ? values.tabs : undefined;
  const direct = values[key] ?? (tabs ? tabs[key] : undefined);
  if (direct !== undefined) return direct;
  if (!tabs) return undefined;

  if (key === 'requestHeaders') return getByPath(tabs, ['headers', 'requestHeaders']);
  if (key === 'queryParams') return getByPath(tabs, ['params', 'queryParams']);
  if (key === 'pathParams') return getByPath(tabs, ['params', 'pathParams']);
  if (key === 'requestBody') return getByPath(tabs, ['body', 'requestBody']);
  if (key === 'requestBodyJson') return getByPath(tabs, ['body', 'requestBodyJson']);
  if (key === 'bodyMode') return getByPath(tabs, ['body', 'bodyMode']);
  return undefined;
};

export const getTabArray = (values: Record<string, unknown>, key: string) => {
  const v = getTabValue(values, key);
  return Array.isArray(v) ? v : [];
};

export const getTabString = (values: Record<string, unknown>, key: string) => {
  const v = getTabValue(values, key);
  return typeof v === 'string' ? v : '';
};

export const buildJsonBodyRows = (jsonText: string) => {
  const raw = typeof jsonText === 'string' ? jsonText : '';
  return [
    {
      key: 'body',
      fieldName: 'body',
      fieldType: 'object',
      required: false,
      defaultValue: raw,
      description: ''
    }
  ];
};

export const buildResponseBodyFromJson = (jsonText: string) => {
  const raw = typeof jsonText === 'string' ? jsonText : '';
  return [
    {
      key: 'body',
      fieldName: 'body',
      fieldType: 'object',
      description: ''
    }
  ];
};

export const normalizeMethodForRequest = (method: unknown) => {
  if (method === 'POST_JSON') return { actualMethod: 'POST', methodMode: 'POST_JSON' as const };
  if (typeof method === 'string' && method.length > 0) return { actualMethod: method, methodMode: undefined };
  return { actualMethod: 'GET', methodMode: undefined };
};

export const extractVariables = (str: string): string[] => {
  if (!str || typeof str !== 'string') return [];
  const regex = /\$\{([^}]+)\}/g;
  const matches: string[] = [];
  let match;
  while ((match = regex.exec(str)) !== null) {
    matches.push(match[1]);
  }
  return matches;
};

export const replaceVariables = (str: string, valueMap: Map<string, string>): string => {
  if (!str || typeof str !== 'string') return str;
  return str.replace(/\$\{([^}]+)\}/g, (_, varName) => {
    const value = valueMap.get(varName);
    return value !== undefined ? value : `\${${varName}}`;
  });
};
