import React from 'react';
import { loadMaterialsPlugin } from '@onebase/ui-kit';
import { registerConfigRenderer } from '@/pages/Editor/components/config/components/MaterialConfiger/Attributes/registry';

export async function integratePlugin(plugin: any, sdk: any) {
  const components = plugin.components || {};
  const componentsToRegister = Object.keys(components)
    .map((key) => {
      const comp = components[key];
      if (!comp) return null;

      const Impl: any = comp.component;
      const Wrapped = (props: any) => React.createElement(Impl, { ...props, sdk });

      return {
        type: comp.type,
        schema: comp.schema,
        template: { ...(comp.template || {}), category: comp.template?.category || 'form' },
        fieldMap: comp.fieldMap,
        entityMap: comp.entityMap,
        component: Wrapped
      };
    })
    .filter(Boolean) as any[];

  if (componentsToRegister.length > 0) {
    loadMaterialsPlugin({
      id: plugin?.meta?.name || plugin?.meta?.id || plugin?.meta?.displayName || 'plugin',
      components: componentsToRegister
    });
  }

  const configRenderers = plugin.configRenderers || {};
  Object.keys(configRenderers).forEach((key) => {
    const renderer = configRenderers[key];
    if (renderer?.type && renderer?.component) {
      const Impl = renderer.component;
      registerConfigRenderer(renderer.type, (ctx: any) => {
        const { handlePropsChange, item, configs, isInSubTable } = ctx;
        return React.createElement(Impl, {
          label: item.name,
          value: configs[item.key],
          onChange: (v: any) => handlePropsChange(item.key, v),
          config: configs,
          sdk,
          isInSubTable
        });
      });
    }
  });
}
