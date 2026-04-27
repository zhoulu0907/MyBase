import React from 'react';
import type { FormInstance } from '@arco-design/web-react';
import { loadMaterialsPlugin } from '@onebase/ui-kit';
import { registerConfigRenderer } from '@/pages/Editor/components/config/components/MaterialConfiger/Attributes/registry';

// =================================================================================================
// 运行时桥接 (Runtime Bridge)
// =================================================================================================

/**
 * 插件桥接上下文接口
 * 用于在宿主应用和插件之间共享运行时对象（例如表单实例）
 */
export interface BridgeContext {
  form?: FormInstance;
  [key: string]: any;
}

/**
 * 插件桥接管理类（单例）
 * 管理宿主应用与插件之间的通信上下文，并负责插件资源的注册与集成
 */
class PluginBridge {
  private context: BridgeContext = {};

  /**
   * 注册或更新桥接上下文
   * @param ctx 部分上下文对象，将与现有上下文合并
   */
  registerContext(ctx: Partial<BridgeContext>) {
    this.context = { ...this.context, ...ctx };
    console.log('[PluginBridge] Context updated:', this.context);
  }

  /**
   * 获取完整的上下文对象
   */
  getContext() {
    return this.context;
  }

  /**
   * 获取当前的表单实例 (Helper 方法)
   */
  getForm(): FormInstance | undefined {
    const form = this.context.form;
    if (!form) {
      console.warn('[PluginBridge] Warning: Form instance not found in context');
    }
    return form;
  }

  // =================================================================================================
  // 插件集成 (Plugin Integration)
  // =================================================================================================

  /**
   * 集成已加载的插件到应用中
   * 负责注册插件组件和配置渲染器
   * 
   * @param plugin 已加载的插件模块
   * @param sdk 宿主 SDK 实例，将注入到插件组件中
   */
  async integratePlugin(plugin: any, sdk: any) {
    if (!plugin) return;

    this.registerPluginComponents(plugin, sdk);
    this.registerPluginConfigRenderers(plugin, sdk);
  }

  /**
   * 注册插件组件到 UI Kit 的物料系统
   */
  private registerPluginComponents(plugin: any, sdk: any) {
    const components = plugin.components || {};
    const componentsToRegister = Object.keys(components)
      .map((key) => {
        const comp = components[key];
        if (!comp) return null;

        const Impl: any = comp.component;
        // 包装组件以注入 SDK 属性
        const Wrapped = (props: any) => React.createElement(Impl, { ...props, sdk });

        return {
          type: comp.type,
          schema: comp.schema,
          template: { 
            ...(comp.template || {}), 
            category: comp.template?.category || 'form' 
          },
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
  }

  /**
   * 注册插件提供的自定义配置渲染器
   */
  private registerPluginConfigRenderers(plugin: any, sdk: any) {
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
}

export const pluginBridge = new PluginBridge();
