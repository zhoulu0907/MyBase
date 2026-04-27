import { createHostSDK, pluginEmitter as _pluginEmitter } from '@ob/plugin/sdk';
import { PluginManager } from '@ob/plugin/host';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { pluginBridge } from './bridge';
import { PluginHostAPI } from './host-api';
import { envConfig } from '@onebase/common';

let isInitialized = false;

export async function initPlugins() {
  if (isInitialized) return;
  isInitialized = true;

  try {
    ;(window as any).React = (window as any).React ?? (await import('react')).default;
    ;(window as any).ReactDOM = (window as any).ReactDOM ?? ReactDOM;
    ;(window as any).ReactRouterDOM = (window as any).ReactRouterDOM ?? ReactRouterDOM;
    ;(window as any).Arco = (window as any).Arco ?? Arco;

    if (!(window as any).__OB_PLUGIN_EMITTER) {
      (window as any).__OB_PLUGIN_EMITTER = _pluginEmitter;
    }
    const pluginEmitter = (window as any).__OB_PLUGIN_EMITTER;

    // 构建 Host SDK Context
    const hostAPI = PluginHostAPI.getInstance();
    const context = hostAPI.buildContext();

    const sdk = createHostSDK(context as any, { ui: context.ui, request: context.request } as any);
    (window as any).__OB_PLUGIN_SDK = sdk;
    (window as any).__OB_PLUGIN_EMITTER = pluginEmitter;
    const pm = new PluginManager(context as any);
    
    let loadedFromServer = false;
    try {
      // 动态导入以避免循环依赖或在非必要时加载
      const { getRuntimePluginManifestApi, getRuntimePluginConfigApi } = await import('@onebase/platform-center');
      const res = await getRuntimePluginManifestApi();
      const manifest = res?.data || [];
      
      if (Array.isArray(manifest) && manifest.length > 0) {
        loadedFromServer = true;
        
        // 1. 注册插件
        for (const item of manifest) {
          let baseUrl = item.baseUrl;
          if (!baseUrl.startsWith('http://') && !baseUrl.startsWith('https://') && envConfig?.PLUGIN_URL) {
             const prefix = envConfig.PLUGIN_URL.replace(/\/$/, '');
             const path = baseUrl.startsWith('/') ? baseUrl : `/${baseUrl}`;
             baseUrl = `${prefix}${path}`;
          }
          baseUrl = baseUrl.endsWith('/') ? baseUrl : `${baseUrl}/`;

          let resources: any = {};
          let displayName = item.pluginId;
          let routePrefix = `/${item.pluginId}`;
          let version = item.version;

          // 如果是静态资源类型 (static)，尝试获取 frontend.manifest.json
          if (item.type === 'static') {
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
                     console.warn(`[Plugin Loader] Failed to fetch frontend.manifest.json for ${item.pluginId}`);
                 }
             } catch (e) {
                 console.warn(`[Plugin Loader] Error loading frontend.manifest.json for ${item.pluginId}:`, e);
             }
          } 
          
          // 如果没有成功从 manifest 获取资源（或不是 static 类型），回退到默认逻辑
          if (!resources.js && !resources.html) {
              const entry = item.entry || (item.type === 'iframe' ? 'index.html' : 'remoteEntry.js');
              if (item.type === 'iframe') {
                 resources.html = `${baseUrl}${entry}`;
              } else {
                 resources.js = `${baseUrl}${entry}`;
              }
          }

          pm.registerPlugin({
            name: item.pluginId,
            version: version,
            displayName: displayName,
            type: item.type,
            resources: resources,
            routePrefix: routePrefix,
            ...item
          } as any);
        }

        // 2. 加载并集成
        for (const item of manifest) {
          try {
            // 获取配置
            let config = {};
            try {
              const confRes = await getRuntimePluginConfigApi({ pluginId: item.pluginId, version: item.version });
              config = confRes?.data || {};
            } catch (err) {
              console.warn(`[Plugin Loader] Failed to fetch config for ${item.pluginId}`, err);
            }

            const p = await pm.loadPlugin(item.pluginId);
            if (p) {
              // 如果插件实例支持 setConfig，注入配置
              if (typeof (p as any).setConfig === 'function') {
                (p as any).setConfig(config);
              }
              // 或者通过 context 传递配置？目前 integratePlugin 主要是 UI 集成
              await pluginBridge.integratePlugin(p, sdk);
            }
          } catch (err) {
            console.error(`[Plugin Loader] Failed to load plugin ${item.pluginId}`, err);
          }
        }
      }
    } catch (e) {
      console.warn('[Plugin Loader] Failed to load from server, falling back to local config:', e);
    }
    
    if (loadedFromServer) return;

    const configPlugins = envConfig?.PLUGINS || [];
    
    if (Array.isArray(configPlugins) && configPlugins.length > 0) {
      for (const pluginCfg of configPlugins) {
        pm.registerPlugin(pluginCfg as any);
      }
      for (const pluginCfg of configPlugins) {
        const p = await pm.loadPlugin(pluginCfg.name);
        await pluginBridge.integratePlugin(p, sdk);
      }
    } else {
      // 本地开发模式：从 PLUGIN_BASE_URL 加载示例插件
      const base = envConfig?.PLUGIN_BASE_URL;
      if (base) {
        pm.registerPlugin({
          name: 'ob-plugin-template',
          version: '0.0.0',
          displayName: '示例插件',
          routePrefix: '/ob-plugin-template',
          resources: { js: `${base}/ob-plugin-template.umd.js`, css: `${base}/ob-plugin-template.css` }
        });
        const p = await pm.loadPlugin('ob-plugin-template');
        await pluginBridge.integratePlugin(p, sdk);
      }
    }
  } catch (e) {
    console.error('Plugin initialization failed:', e);
  }
}
