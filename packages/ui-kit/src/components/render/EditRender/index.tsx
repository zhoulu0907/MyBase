import {
  getComponentConfig,
  getComponentImpl,
  getComponentDescriptor,
  WORKBENCH_COMPONENT_MAP,
  getWorkbenchComponentConfig,
  WorkbenchComponentType,
  hasWorkbenchComponentSchema
} from 'src/components/Materials';

import React from 'react';

/**
 * 组件渲染的通用属性
 */
interface ComponentRenderProps {
  /** 组件ID */
  cpId: string;
  /** 组件类型 */
  cpType: string;
  /** 组件schema映射 */
  pageComponentSchema: any;
  /** 组件预览状态 */
  runtime: boolean;
}

/**
 * ComponentRender 组件
 * 用于渲染传入的组件，支持适配各类组件
 */
const ComponentEditRender: React.FC<ComponentRenderProps> = ({ cpId, cpType, pageComponentSchema, runtime }) => {
  // 判断是否为工作台组件类型
  const isWorkbenchType = hasWorkbenchComponentSchema(cpType);
  
  // 获取组件配置
  const componentConfig = isWorkbenchType ? getWorkbenchComponentConfig(pageComponentSchema, cpType as WorkbenchComponentType) : getComponentConfig(pageComponentSchema, cpType);

  const renderComponent = () => {
    const Impl: any = getComponentImpl(cpType as any) ?? (WORKBENCH_COMPONENT_MAP as any)[cpType];
    let descriptor: any;
    try {
      descriptor = getComponentDescriptor(cpType as any);
    } catch {
      descriptor = undefined;
    }

    if (!Impl) return <div>未知组件类型: {cpType}</div>;

    const baseProps: any = { cpName: cpId, id: cpId, ...componentConfig };

    if (descriptor) {
      if (descriptor.template.category === 'form' || descriptor.template.category === 'show') {
        baseProps.runtime = runtime;
      }
      if (descriptor.template.category === 'layout') {
        baseProps.runtime = runtime;
      }
      if (descriptor.template.category === 'list') {
        baseProps.runtime = runtime;
      }
    } else {
      // Workbench 组件
      baseProps.runtime = runtime;
    }

    return <Impl {...baseProps} />;
  };

  return <>{renderComponent()}</>;
};

export default ComponentEditRender;
