import React from 'react';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { PluginManager } from '@ob/plugin/host';
import { createHostSDK, pluginEmitter as _pluginEmitter } from '@ob/plugin/sdk';
import { useAppEntityStore } from '@onebase/ui-kit';
import { pluginBridge } from './bridge';
import { PluginHostAPI } from './host-api';

export async function initPlugins() {
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

    // 6. 加载插件
    const pm = new PluginManager(context as any);
    const configPlugins = ((window as any)?.global_config?.PLUGINS) || [];

    if (Array.isArray(configPlugins) && configPlugins.length > 0) {
      for (const pluginCfg of configPlugins) {
        pm.registerPlugin(pluginCfg);
      }
      for (const pluginCfg of configPlugins) {
        const p = await pm.loadPlugin(pluginCfg.name);
        await pluginBridge.integratePlugin(p, sdk);
      }
    } else {
      // 默认加载模板插件 (开发模式)
      const base = (window as any)?.global_config?.PLUGIN_BASE_URL || 'http://localhost:3001';
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
  } catch (e) {
    console.error('[Plugin Loader] Error:', e);
  }
}
