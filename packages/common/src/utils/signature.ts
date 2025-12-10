import { AxiosRequestConfig, Method } from 'axios';
import { getSignatureConfig } from './env';

export interface SignatureOptions {
  appKey: string;
  appSecret: string;
  queryParams?: Record<string, unknown> | undefined;
  body?: unknown;
  timestamp?: string;
  nonce?: string;
}

export interface SignatureResult {
  timestamp: string;
  nonce: string;
  sign: string;
  headers: Record<string, string>;
  canonicalQueryString: string;
  canonicalBody: string;
  canonicalHeaderString: string;
  signatureBase: string;
}

const ALNUM = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';

function isPlainObject(value: unknown): value is Record<string, unknown> {
  return Object.prototype.toString.call(value) === '[object Object]';
}

function buildSortedQueryString(params?: Record<string, unknown> | undefined): string {
  if (!params || !isPlainObject(params)) {
    return '';
  }

  const entries = Object.entries(params)
    .filter(([, value]) => value !== undefined && value !== null)
    .map(([key, value]) => [String(key), toPrimitiveString(value)] as const);

  if (entries.length === 0) {
    return '';
  }

  entries.sort(([a], [b]) => (a > b ? 1 : a < b ? -1 : 0));
  return entries.map(([key, value]) => `${key}=${value}`).join('&');
}

function toPrimitiveString(value: unknown): string {
  if (typeof value === 'string') {
    return value;
  }
  if (typeof value === 'number' || typeof value === 'bigint') {
    return String(value);
  }
  if (typeof value === 'boolean') {
    return value ? 'true' : 'false';
  }
  if (value instanceof Date) {
    return String(value.getTime());
  }
  if (value === null || value === undefined) {
    return '';
  }
  if (Array.isArray(value) || isPlainObject(value)) {
    return JSON.stringify(value);
  }
  return String(value);
}

async function sha256Hex(input: string): Promise<string> {
  const encoder = new TextEncoder();
  const data = encoder.encode(input);
  if (globalThis.crypto?.subtle) {
    const digest = await globalThis.crypto.subtle.digest('SHA-256', data);
    return Array.from(new Uint8Array(digest))
      .map((byte) => byte.toString(16).padStart(2, '0'))
      .join('');
  }
  throw new Error('当前环境不支持 SHA-256 计算，请在支持 WebCrypto 的环境中运行');
}

function generateNonce(length = 16): string {
  if (length < 10) {
    throw new Error('nonce 长度需不小于 10');
  }
  const array = new Uint8Array(length);
  if (globalThis.crypto?.getRandomValues) {
    globalThis.crypto.getRandomValues(array);
  } else {
    for (let i = 0; i < length; i += 1) {
      array[i] = Math.floor(Math.random() * 256);
    }
  }
  let nonce = '';
  for (let i = 0; i < length; i += 1) {
    nonce += ALNUM[array[i] % ALNUM.length];
  }
  return nonce;
}

function normalizeBody(body: unknown): string {
  if (body === undefined || body === null) {
    return '';
  }
  if (typeof body === 'string') {
    return body;
  }
  if (body instanceof Blob) {
    throw new Error('签名暂不支持 Blob 类型 body');
  }
  if (body instanceof ArrayBuffer) {
    throw new Error('签名暂不支持 ArrayBuffer 类型 body');
  }
  if (ArrayBuffer.isView(body)) {
    throw new Error('签名暂不支持 TypedArray 类型 body');
  }
  if (isPlainObject(body) || Array.isArray(body)) {
    return JSON.stringify(body);
  }
  return String(body);
}

function buildSignatureString({
  appKey,
  appSecret,
  queryParams,
  body,
  timestamp,
  nonce
}: Required<Omit<SignatureOptions, 'body' | 'queryParams'>> & {
  queryParams?: Record<string, unknown>;
  body?: unknown;
}): {
  canonicalQueryString: string;
  canonicalBody: string;
  canonicalHeaderString: string;
  signatureBase: string;
} {
  const canonicalQueryString = buildSortedQueryString(queryParams);
  const canonicalBody = normalizeBody(body ?? null);
  const canonicalHeaderString = buildSortedQueryString({
    appKey,
    nonce,
    timestamp
  });

  const signatureBase = `${canonicalQueryString}${canonicalBody}${canonicalHeaderString}${appSecret}`;

  return {
    canonicalQueryString,
    canonicalBody,
    canonicalHeaderString,
    signatureBase
  };
}

function parseJsonObject(input: string, label: string): Record<string, unknown> {
  try {
    const parsed = JSON.parse(input);
    if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
      throw new Error();
    }
    return parsed as Record<string, unknown>;
  } catch (error) {
    throw new Error(`${label} 需要是合法的 JSON 对象`);
  }
}

function mergeUrlAndQuery(
  rawUrl: string,
  extraQuery: Record<string, unknown>
): { finalUrl: string; mergedQuery: Record<string, unknown> } {
  const isAbsolute = /^https?:\/\//i.test(rawUrl);
  const base = isAbsolute ? undefined : window.location.origin;
  const url = new URL(rawUrl, base ?? window.location.origin);

  const originalQuery: Record<string, unknown> = {};
  url.searchParams.forEach((value, key) => {
    originalQuery[key] = value;
  });

  const mergedQuery: Record<string, unknown> = {
    ...originalQuery,
    ...extraQuery
  };

  url.search = '';
  Object.entries(mergedQuery).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return;
    }
    url.searchParams.append(key, String(value));
  });

  // Keep relative URLs intact so that local dev proxies (例如 Vite server) still work.
  const finalUrl = isAbsolute ? url.toString() : `${url.pathname}${url.search}${url.hash}`;
  return { finalUrl, mergedQuery };
}

function prepareRequestBody(
  method: Method,
  rawBody: string
): {
  bodyForSignature?: string;
  bodyForRequest?: string;
  isJson: boolean;
} {
  if (method === 'GET') {
    return { isJson: false };
  }

  try {
    // const parsed = JSON.parse(rawBody);
    // const canonical = JSON.stringify(parsed);
    return { bodyForSignature: rawBody, bodyForRequest: rawBody, isJson: true };
  } catch (error) {
    return { bodyForSignature: rawBody, bodyForRequest: rawBody, isJson: false };
  }
}

async function generateApiSignature(options: SignatureOptions): Promise<SignatureResult> {
  const { appKey, appSecret } = options;

  if (!appKey) {
    throw new Error('缺少 appKey');
  }
  if (!appSecret) {
    throw new Error('缺少 appSecret');
  }

  const timestamp = options.timestamp ?? String(Date.now());
  const nonce = options.nonce ?? generateNonce(16);

  const { canonicalQueryString, canonicalBody, canonicalHeaderString, signatureBase } = buildSignatureString({
    appKey,
    appSecret,
    queryParams: options.queryParams,
    body: options.body,
    timestamp,
    nonce
  });

  const sign = await sha256Hex(signatureBase);

  return {
    timestamp,
    nonce,
    sign,
    headers: {
      appKey,
      timestamp,
      nonce,
      sign
    },
    canonicalQueryString,
    canonicalBody,
    canonicalHeaderString,
    signatureBase
  };
}

export const generateSignature = async (config: AxiosRequestConfig) => {
  const { appKey, appSecret } = getSignatureConfig();

  const parsedQuery = parseJsonObject(JSON.stringify(config.params), 'Query 参数');
  const url = config.baseURL + (config.url || '');

  const { mergedQuery } = mergeUrlAndQuery(url, parsedQuery);

  const bodyPrep = prepareRequestBody(config.method as Method, config.data);

  const signature = await generateApiSignature({
    appKey: appKey,
    appSecret: appSecret,
    queryParams: mergedQuery,
    body: bodyPrep.bodyForSignature
  });

  return signature;
};
