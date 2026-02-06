import React from 'react';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { PluginManager } from '@ob/plugin/host';
import { createHostSDK, pluginEmitter as _pluginEmitter } from '@ob/plugin/sdk';
import { useAppEntityStore } from '@onebase/ui-kit';
import { pluginBridge } from './bridge';
import { PluginHostAPI } from './host-api';

let isInitialized = false;

export async function initPlugins() {
  if (isInitialized) return;
  isInitialized = true;

  try {
    // 开发模式：检查是否启用插件
    const enablePlugins = (window as any)?.global_config?.ENABLE_PLUGINS !== false;
    if (!enablePlugins) {
      console.log('[Plugin Loader] 插件功能已禁用');
      return;
    }

    // 1. 全局变量注入 (Polyfills)
    (window as any).React = (window as any).React ?? React;
    (window as any).ReactDOM = (window as any).ReactDOM ?? ReactDOM;
    (window as any).ReactRouterDOM = (window as any).ReactRouterDOM ?? ReactRouterDOM;
    (window as any).Arco = (window as any).Arco ?? Arco;

    // 2. 初始化事件总线
    if (!(window as any).__OB_PLUGIN_EMITTER) {
      (window as any).__OB_PLUGIN_EMITTER = _pluginEmitter;
    }
    const pluginEmitter = (window as any).__OB_PLUGIN_EMITTER;

    // 3. 调试日志 (可选)
    const { mainEntity, subEntities } = useAppEntityStore.getState();
    console.log('[plugin-editor] initial entity store', { mainEntity, subEntities });
    useAppEntityStore.subscribe((state: any) => {
      console.log('[plugin-editor] entity store updated', { mainEntity: state.mainEntity, subEntities: state.subEntities });
    });

    // 4. 构建 Host SDK Context
    // 使用 PluginHostAPI 统一管理宿主能力，避免 loader.ts 过于臃肿
    const hostAPI = PluginHostAPI.getInstance();
    const context = hostAPI.buildContext();

    // 5. 创建 SDK 实例
    // 注意：createHostSDK 会使用 context.ui 作为第二个参数，或者合并到 context 中，视具体实现而定
    // 这里我们将 ui 也包含在 context 中传递
    const sdk = createHostSDK(context as any, { ui: context.ui } as any);
    (window as any).__OB_PLUGIN_SDK = sdk;
    (window as any).__OB_PLUGIN_EMITTER = pluginEmitter;

    // 6. 插件加载流程
    const pm = new PluginManager(context as any);

    let pluginList: any[] = [];
    let source: 'server' | 'local' = 'local';
    let getPluginConfigPlainApi: any = null;

    // 6.1 尝试从服务端获取清单
    try {
      const api = await import('@onebase/platform-center');
      const { getPluginManifestApi } = api;
      getPluginConfigPlainApi = api.getPluginConfigPlainApi;

      const res = await getPluginManifestApi();
      console.log('[Plugin Loader] getPluginManifestApi res', res);
      const manifest = res || [];

      if (Array.isArray(manifest) && manifest.length > 0) {
        pluginList = manifest;
        source = 'server';
      }
    } catch (e) {
      console.warn('[Plugin Loader] Failed to load from server, falling back to local config:', e);
    }

    // 6.2 如果服务端为空，尝试本地配置
    if (pluginList.length === 0) {
      pluginList = ((window as any)?.global_config?.PLUGINS) || [];
    }

    // 7. 统一注册流程
    for (const item of pluginList) {
      // 确保 pluginId 和 name 都有值
      const pluginId = item.pluginId || item.name;
      item.pluginId = pluginId;
      item.name = pluginId;
      
      let baseUrl = item.baseUrl || '';
      // 处理 baseUrl (如果存在且是相对路径)
      if (baseUrl && !baseUrl.startsWith('http') && (window as any).global_config?.PLUGIN_URL) {
          const prefix = (window as any).global_config.PLUGIN_URL.replace(/\/$/, '');
          const path = baseUrl.startsWith('/') ? baseUrl : `/${baseUrl}`;
          baseUrl = `${prefix}${path}`;
      }
      if (baseUrl && !baseUrl.endsWith('/')) {
        baseUrl = `${baseUrl}/`;
      }
      item.baseUrl = baseUrl;

      let resources: any = item.resources || {};
      let displayName = item.displayName || pluginId;
      let routePrefix = item.routePrefix || `/${pluginId}`;
      let version = item.version;

      // 如果是静态资源类型 (static)，尝试获取 frontend.manifest.json
      if (item.type === 'static' && baseUrl) {
          try {
              const manifestUrl = `${baseUrl}frontend.manifest.json`;
              const manifestRes = await fetch(manifestUrl);
              if (manifestRes.ok) {
                  const manifestData = await manifestRes.json();
                  
                  // 使用 manifest 中的元数据
                  if (manifestData.displayName) displayName = manifestData.displayName;
                  if (manifestData.routePrefix) routePrefix = manifestData.routePrefix;
                  if (manifestData.version) version = manifestData.version;
                  
                  // 构造 JS/CSS 资源路径
                  if (manifestData.entry) {
                      if (manifestData.entry.js) resources.js = `${baseUrl}${manifestData.entry.js}`;
                      if (manifestData.entry.css) resources.css = `${baseUrl}${manifestData.entry.css}`;
                  }
              } else {
                  console.warn(`[Plugin Loader] Failed to fetch frontend.manifest.json for ${pluginId}`);
              }
          } catch (e) {
              console.warn(`[Plugin Loader] Error loading frontend.manifest.json for ${pluginId}:`, e);
          }
      } 

      // 执行注册
      pm.registerPlugin({
        name: pluginId,
        version: version,
        displayName: displayName,
        type: item.type,
        resources: resources,
        routePrefix: routePrefix,
        ...item
      } as any);
    }

    // 8. 统一加载与集成流程
    for (const item of pluginList) {
       const pluginId = item.pluginId || item.name;
       try {
         // 获取配置 (仅服务端模式)
         let config = {};
         if (source === 'server' && getPluginConfigPlainApi) {
           try {
             const confRes = await getPluginConfigPlainApi({ pluginId: pluginId, pluginVersion: item.version });
             config = confRes || {};
           } catch (err) {
             console.warn(`[Plugin Loader] Failed to fetch config for ${pluginId}`, err);
           }
         } else if (item.config) {
             // 本地配置可能直接包含 config
             config = item.config;
         }

         const p = await pm.loadPlugin(pluginId);
         if (p) {
           // 注入配置
           if (typeof (p as any).setConfig === 'function') {
             (p as any).setConfig(config);
           }
           await pluginBridge.integratePlugin(p, sdk);
         }
       } catch (err) {
         console.error(`[Plugin Loader] Failed to load plugin ${pluginId}`, err);
       }
    }

  } catch (e) {
    console.error('[Plugin Loader] Error:', e);
  }
}
