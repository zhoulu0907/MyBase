import { createHostSDK, pluginEmitter as _pluginEmitter } from '@ob/plugin/sdk';
import { PluginManager } from '@ob/plugin/host';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { useAppEntityStore } from '@onebase/ui-kit';
import { pluginBridge } from './bridge';
import { PluginHostAPI } from './host-api';

export async function initPlugins() {
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

    const sdk = createHostSDK(context as any, { ui: context.ui } as any);
    (window as any).__OB_PLUGIN_SDK = sdk;
    (window as any).__OB_PLUGIN_EMITTER = pluginEmitter;
    const pm = new PluginManager(context as any);
    
    const configPlugins = ((window as any)?.global_config?.PLUGINS) || [];
    
    if (Array.isArray(configPlugins) && configPlugins.length > 0) {
      for (const pluginCfg of configPlugins) {
        pm.registerPlugin(pluginCfg as any);
      }
      for (const pluginCfg of configPlugins) {
        const p = await pm.loadPlugin(pluginCfg.name);
        await pluginBridge.integratePlugin(p, sdk);
      }
    } else {
      // Local development fallback
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
    console.error('Plugin initialization failed:', e);
  }
}
