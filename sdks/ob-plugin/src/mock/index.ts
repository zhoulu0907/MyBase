import { type HostSDK, type Context, type UIAPI, type Entity, type Field } from '../sdk/types';
// 使用 context.events 进行监听，避免不同包实例导致事件总线不一致
import { CONFIG_TYPES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../sdk/constants';

/**
 * 创建基于宿主的简单 Mock，实现基础上下文与错误上报
 */
export function createMockHostSDK(
  context: Omit<Context, 'entity'> & { entity?: Partial<Context['entity']> },
  options?: {
    ui?: UIAPI;
    entities?: Entity[];
    fields?: Record<string, Field[]>;
  }
): HostSDK {
  const defaultUI: UIAPI = {
    reportError: (error: unknown) => {
      // Mock 环境：控制台输出并携带终端信息
      // eslint-disable-next-line no-console
      console.warn(`[mock-error][${context.terminal}]`, error);
    }
  };

  const entities = options?.entities || [];
  const fields = options?.fields || {};

  const defaultEntityAPI = {
    listFields: () => [],
    getEntities: () => entities,
    getFields: (id: string) => fields[id] || []
  };

  const finalContext: Context = {
    ...context,
    entity: {
      ...defaultEntityAPI,
      ...(context.entity || {})
    }
  };

  try {
    const anyGlobal = globalThis as any;
    if (!anyGlobal.__ob_mock_emitter_subscribed && (finalContext as any)?.events?.on) {
      (finalContext as any).events.on('set-field', (payload: any) => {
        console.log('[mock-emitter] set-field', payload);
      });
      (finalContext as any).events.on('set-fields', (payload: any) => {
        console.log('[mock-emitter] set-fields', payload);
      });
      (finalContext as any).events.on('set-subrow-field', (payload: any) => {
        console.log('[mock-emitter] set-subrow-field', payload);
      });
      (finalContext as any).events.on('set-subrow-fields', (payload: any) => {
        console.log('[mock-emitter] set-subrow-fields', payload);
      });
      anyGlobal.__ob_mock_emitter_subscribed = true;
    }
  } catch {}

  return {
    context: finalContext,
    ui: options?.ui ?? defaultUI
  };
}

type EditItem = string | { key: string; name: string; type: string };

export function buildCommonFormEditData(extra: EditItem[] = []): EditItem[] {
  return [
    CONFIG_TYPES.LABEL_INPUT,
    CONFIG_TYPES.PLACEHOLDER_INPUT,
    CONFIG_TYPES.TOOLTIP_INPUT,
    CONFIG_TYPES.FIELD_DATA,
    CONFIG_TYPES.DEFAULT_VALUE,
    CONFIG_TYPES.VERIFY,
    CONFIG_TYPES.STATUS_RADIO,
    CONFIG_TYPES.TEXT_ALIGN,
    CONFIG_TYPES.FORM_LAYOUT,
    CONFIG_TYPES.SECURITY,
    CONFIG_TYPES.WIDTH_RADIO,
    ...extra
  ];
}

export function buildCommonFormDefaultConfig(overrides: Record<string, any> = {}): Record<string, any> {
  const base = {
    label: { text: '文字识别', display: true },
    placeholder: '',
    tooltip: '',
    dataField: [],
    defaultValueConfig: { type: 'CUSTOM', customValue: '', formulaValue: '' },
    verify: { required: false, noRepeat: false, lengthLimit: false, minLength: 0, maxLength: 0 },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    align: 'left',
    layout: 'vertical',
    security: { display: false, type: 'none' },
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF]
  };
  return { ...base, ...overrides };
}

export function textEditData(): EditItem[] {
  return buildCommonFormEditData();
}

export function numberEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.NUMBER_FORMAT]);
}

export function switchEditData(): EditItem[] {
  return buildCommonFormEditData([]);
}

export function dateEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.DATE_TYPE, CONFIG_TYPES.DATE_RANGE, CONFIG_TYPES.DATE_FORMAT]);
}

export function dateRangeEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.DATE_TYPE, CONFIG_TYPES.DATE_RANGE, CONFIG_TYPES.DATE_FORMAT]);
}

export function imgUploadEditData(): EditItem[] {
  return buildCommonFormEditData([
    CONFIG_TYPES.UPLOAD_SIZE,
    CONFIG_TYPES.UPLOAD_LIMIT,
    CONFIG_TYPES.UPLOAD_COMPRESS,
    CONFIG_TYPES.IMAGE_HANDLE
  ]);
}
