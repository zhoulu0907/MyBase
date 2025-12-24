import { createHostSDK } from '@ob/plugin/sdk';
import { PluginManager } from '@ob/plugin/host';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { useAppEntityStore } from '@onebase/ui-kit';
import { integratePlugin } from './bridge';

export async function initPlugins() {
  try {
    ;(window as any).React = (window as any).React ?? (await import('react')).default;
    ;(window as any).ReactDOM = (window as any).ReactDOM ?? ReactDOM;
    ;(window as any).ReactRouterDOM = (window as any).ReactRouterDOM ?? ReactRouterDOM;
    ;(window as any).Arco = (window as any).Arco ?? Arco;

    const getEntities = () => {
      const { mainEntity, subEntities } = useAppEntityStore.getState();
      const list: any[] = [];
      if ((mainEntity as any)?.entityUuid) list.push(mainEntity);
      if (subEntities?.entities?.length) {
        for (const e of subEntities.entities) list.push(e);
      }
      return list;
    };

    const getFields = (uuid: string) => {
      const { mainEntity, subEntities } = useAppEntityStore.getState();
      if ((mainEntity as any)?.entityUuid === uuid) return (mainEntity as any)?.fields || [];
      const sub = subEntities?.entities?.find((e: any) => e.entityUuid === uuid);
      return sub?.fields || [];
    };

    const ui = {
      notify: (type: string, message: string) => {
        const fn = (Arco as any)?.Message?.[type] || (Arco as any)?.Message?.info;
        fn?.(message);
      },
      reportError: (error: unknown) => {
        const msg = typeof error === 'string' ? error : (error as any)?.message || '插件错误';
        ;(Arco as any)?.Message?.error?.(msg);
      }
    };

    const context = { terminal: 'PC', entity: { getEntities, getFields } } as any;
    const sdk = createHostSDK(context, { ui } as any);
    const pm = new PluginManager(context);
    
    const configPlugins = ((window as any)?.global_config?.PLUGINS) || [];
    
    if (Array.isArray(configPlugins) && configPlugins.length > 0) {
      for (const pluginCfg of configPlugins) {
        pm.registerPlugin(pluginCfg as any);
      }
      for (const pluginCfg of configPlugins) {
        const p = await pm.loadPlugin(pluginCfg.name);
        await integratePlugin(p, sdk);
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
      await integratePlugin(p, sdk);
    }
  } catch (e) {
    console.error('Plugin initialization failed:', e);
  }
}
