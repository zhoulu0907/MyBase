import React from 'react';
import * as ReactDOM from 'react-dom';
import * as ReactRouterDOM from 'react-router-dom';
import * as Arco from '@arco-design/web-react';
import { PluginManager } from '@ob/plugin/host';
import { createHostSDK } from '@ob/plugin/sdk';
import { useAppEntityStore } from '@onebase/ui-kit';
import { integratePlugin } from './bridge';

export async function initPlugins() {
  try {
    (window as any).React = (window as any).React ?? React;
    (window as any).ReactDOM = (window as any).ReactDOM ?? ReactDOM;
    (window as any).ReactRouterDOM = (window as any).ReactRouterDOM ?? ReactRouterDOM;
    (window as any).Arco = (window as any).Arco ?? Arco;

    const { mainEntity, subEntities } = useAppEntityStore.getState();
    console.log('[plugin-editor] initial entity store', { mainEntity, subEntities });
    useAppEntityStore.subscribe((state: any) => {
      console.log('[plugin-editor] entity store updated', { mainEntity: state.mainEntity, subEntities: state.subEntities });
    });

    const getEntities = () => {
      const s = useAppEntityStore.getState();
      const list: any[] = [];
      if ((s as any)?.mainEntity?.entityUuid) list.push((s as any).mainEntity);
      if (s?.subEntities?.entities?.length) {
        for (const e of s.subEntities.entities) list.push(e);
      }
      return list;
    };
    const getFields = (uuid: string) => {
      const s = useAppEntityStore.getState();
      if ((s as any)?.mainEntity?.entityUuid === uuid) return (s as any)?.mainEntity?.fields || [];
      const sub = s?.subEntities?.entities?.find((e: any) => e.entityUuid === uuid);
      return sub?.fields || [];
    };
    const subscribe = (listener: (payload: any) => void) =>
      useAppEntityStore.subscribe((state: any) => listener({ mainEntity: state.mainEntity, subEntities: state.subEntities }));
    const getState = () => useAppEntityStore.getState();

    const ui = {
      notify: (type: string, message: string) => {
        const fn = (Arco as any)?.Message?.[type] || (Arco as any)?.Message?.info;
        fn?.(message);
      },
      reportError: (error: unknown) => {
        const msg = typeof error === 'string' ? error : (error as any)?.message || '插件错误';
        (Arco as any)?.Message?.error?.(msg);
      }
    };
    const context = { terminal: 'PC', entity: { getEntities, getFields, subscribe, getState } };
    const sdk = createHostSDK(context as any, { ui } as any);
    const pm = new PluginManager(context as any);

    const configPlugins = ((window as any)?.global_config?.PLUGINS) || [];
    if (Array.isArray(configPlugins) && configPlugins.length > 0) {
      for (const pluginCfg of configPlugins) {
        pm.registerPlugin(pluginCfg);
      }
      for (const pluginCfg of configPlugins) {
        const p = await pm.loadPlugin(pluginCfg.name);
        await integratePlugin(p, sdk);
      }
    } else {
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
    console.error(e);
  }
}
