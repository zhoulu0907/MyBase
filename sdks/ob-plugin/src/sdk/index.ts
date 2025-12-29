import { type Context, type HostSDK, type UIAPI, type Entity, type Field, type EventsAPI } from './types';
import EventEmitter from 'eventemitter3';

/**
 * 创建一个最小可用的 SDK 实例（供插件调用）。
 * 宿主在生产环境应替换 ui 的具体实现。
 */
export function createHostSDK(
  context: Omit<Context, 'entity'> & { entity?: Partial<Context['entity']> },
  overrides?: {
    ui?: UIAPI;
    entities?: Entity[];
    fields?: Record<string, Field[]>;
  }
): HostSDK {
  const defaultUI: UIAPI = {
    reportError: (error: unknown) => {
      // 默认仅输出到控制台，宿主可替换为真实上报
      // eslint-disable-next-line no-console
      console.error('[plugin-error]', error);
    }
  };

  const entities = overrides?.entities || [];
  const fields = overrides?.fields || {};

  const events: EventsAPI = context.events || ((): EventsAPI => {
    const emitter = new EventEmitter();
    return {
      on: (event: string, handler: (payload: any) => void) => emitter.on(event, handler as any),
      off: (event: string, handler: (payload: any) => void) => emitter.off(event, handler as any),
      emit: (event: string, payload?: any) => emitter.emit(event, payload)
    };
  })();

  const defaultEntityAPI = {
    listFields: () => [],
    getEntities: () => entities,
    getFields: (id: string) => fields[id] || [],
    setFieldValue: (name: string, value: any) => {
      events.emit('set-field', { name, value });
    },
    setFieldsValue: (values: Record<string, any>) => {
      events.emit('set-fields', { values });
    },
    setSubRowFieldValue: (tableName: string, rowIndex: number, fieldName: string, value: any) => {
      events.emit('set-subrow-field', { tableName, rowIndex, fieldName, value });
    },
    setSubRowFieldsValue: (tableName: string, rowIndex: number, values: Record<string, any>) => {
      events.emit('set-subrow-fields', { tableName, rowIndex, values });
    }
  };

  const finalContext: Context = {
    ...context,
    entity: {
      ...defaultEntityAPI,
      ...(context.entity || {})
    },
    events
  };
  
  return {
    context: finalContext,
    ui: overrides?.ui ?? defaultUI
  };
}

export * from './types';
export * from './constants';
export * from './utils';
export * from './define';
export * from './builder';
export * from './emitter';
