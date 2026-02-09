import * as Arco from '@arco-design/web-react';
import { pluginEmitter } from '@ob/plugin/sdk';
import { getFieldOptionsConfig, useAppEntityStore } from '@onebase/ui-kit';
import { createClient, getRuntimeBackendURL } from '@onebase/common';
import { pluginBridge } from './bridge';

/**
 * 宿主能力服务类
 * 负责构建提供给插件的 SDK 上下文，以及处理插件触发的各类事件
 */
export class PluginHostAPI {
  // 单例模式，方便统一管理
  private static instance: PluginHostAPI;

  static getInstance() {
    if (!this.instance) {
      this.instance = new PluginHostAPI();
    }
    return this.instance;
  }

  constructor() {
    this.setupEventListeners();
  }

  /**
   * 初始化全局监听器（主要处理通过 EventBus 触发的间接调用）
   */
  private setupEventListeners() {
    // 表单字段设置监听
    pluginEmitter.on('set-field', (payload: any) => {
      const { name, value } = payload || {};
      const form = pluginBridge.getForm();
      if (name && form) {
        const currentVal = form.getFieldValue(name);
        if (currentVal !== value) {
          form.setFieldValue(name, value);
        }
      }
    });

    pluginEmitter.on('set-fields', (payload: any) => {
      const { values } = payload || {};
      const form = pluginBridge.getForm();
      if (values && form) {
        form.setFieldsValue(values);
      }
    });
  }

  /**
   * 构建提供给插件的上下文对象 (Host Context)
   * 这些方法将直接暴露给 SDK，或者通过 createHostSDK 包装后暴露
   */
  public buildContext() {
    return {
      terminal: 'PC',
      events: this.buildEventsAPI(),
      entity: this.buildEntityAPI(),
      ui: this.buildUIAPI(),
      router: this.buildRouterAPI(), // 新增能力示例：路由
      request: this.buildRequestAPI(),
    };
  }

  private buildRequestAPI() {
    // 使用 Runtime API 作为基础，空前缀确保插件可以自由控制完整路径
    // 如果插件请求 /runtime/plugin/xxx，则完整 URL 为 <runtime-backend>/runtime/plugin/xxx
    const httpClient = createClient('', getRuntimeBackendURL());

    const request = async (config: any) => {
      const { data, headers = {} } = config;
      // 兼容处理：如果是 FormData/File/Blob，确保不强制使用 application/json
      if (data instanceof FormData || data instanceof Blob || (typeof File !== 'undefined' && data instanceof File)) {
        // 显式设置为 undefined，让 Axios/Browser 自动处理（如 FormData 需要自动生成 boundary）
        Object.assign(headers, { 'Content-Type': undefined });
      }
      
      const res = await httpClient.instance.request({
        ...config,
        headers
      });
      return res.data;
    };

    // 适配 RequestAPI 接口
    return {
      request
    };
  }

  private buildEventsAPI() {
    return {
      on: (event: string, handler: (payload: any) => void) => {
        pluginEmitter.on(event as any, handler as any);
      },
      off: (event: string, handler: (payload: any) => void) => pluginEmitter.off(event as any, handler as any),
      emit: (event: string, payload?: any) => {
        pluginEmitter.emit(event as any, payload);
      }
    };
  }

  private buildEntityAPI() {
    return {
      getEntities: () => {
        const s = useAppEntityStore.getState();
        const list: any[] = [];
        if ((s as any)?.mainEntity?.entityUuid) list.push((s as any).mainEntity);
        if (s?.subEntities?.entities?.length) {
          for (const e of s.subEntities.entities) list.push(e);
        }
        return list;
      },
      getFields: (uuid: string) => {
        const s = useAppEntityStore.getState();
        if ((s as any)?.mainEntity?.entityUuid === uuid) return (s as any)?.mainEntity?.fields || [];
        const sub = s?.subEntities?.entities?.find((e: any) => e.entityUuid === uuid);
        return sub?.fields || [];
      },
      getFieldOptions: async (dataField: string[]) => {
        const s = useAppEntityStore.getState();
        return await getFieldOptionsConfig(dataField, (s as any).mainEntity, (s as any).subEntities);
      },
      setFieldValue: (name: string, value: any) => {
        pluginEmitter.emit('set-field', { name, value });
      },
      setFieldsValue: (values: Record<string, any>) => {
        pluginEmitter.emit('set-fields', { values });
      },
      subscribe: (listener: (payload: any) => void) =>
        useAppEntityStore.subscribe((state: any) => listener({ mainEntity: state.mainEntity, subEntities: state.subEntities })),
      getState: () => useAppEntityStore.getState()
    };
  }

  private buildUIAPI() {
    return {
      notify: (type: string, message: string) => {
        const fn = (Arco as any)?.Message?.[type] || (Arco as any)?.Message?.info;
        fn?.(message);
      },
      reportError: (error: unknown) => {
        const msg = typeof error === 'string' ? error : (error as any)?.message || '插件错误';
        (Arco as any)?.Message?.error?.(msg);
      }
    };
  }

  // 新增能力示例：路由控制
  private buildRouterAPI() {
    return {
      // 可以在这里通过 window.location 或者 React Router (需要 bridge 支持注入 navigate) 来实现跳转
      push: (path: string) => {
        console.log('[PluginHostAPI] Router Push:', path);
        // 暂时只打印，后续可以通过 Bridge 注入 navigate 实例来实现真实跳转
        // const navigate = pluginBridge.getNavigate(); 
        // navigate?.(path);
      }
    };
  }
}
