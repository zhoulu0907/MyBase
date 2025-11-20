import type { ComponentType } from 'react';

import { WorkbenchComp } from './WorkbenchBasicComponents';
import WorkbenchSchema from './WorkbenchBasicComponents/schema';
import { WORKBENCH_COMPONENT_TYPE_VALUES, type WorkbenchComponentType } from './componentTypes';

type WorkbenchSchemaCollection = typeof WorkbenchSchema;

type WorkbenchComponentSchemaBase = {
  config: {
    id?: string;
    cpName?: string;
    status?: string;
    width?: string;
    [key: string]: unknown;
  };
  editData?: unknown;
  [key: string]: unknown;
};

export type WorkbenchComponentSchema = WorkbenchSchemaCollection[keyof WorkbenchSchemaCollection] &
  WorkbenchComponentSchemaBase;

interface WorkbenchComponentDefinition {
  component: ComponentType<any>;
  schema: WorkbenchComponentSchema;
}

/**
 * 工作台组件注册表
 * 用于自动注册工作台组件的组件和 schema，避免手动维护
 */

export const WORKBENCH_COMPONENT_REGISTRY: Record<WorkbenchComponentType, WorkbenchComponentDefinition> =
  WORKBENCH_COMPONENT_TYPE_VALUES.reduce((acc, componentType) => {
    const component = WorkbenchComp[componentType as keyof typeof WorkbenchComp];
    const schema = WorkbenchSchema[componentType as keyof typeof WorkbenchSchema] as WorkbenchComponentSchema;

    if (!component || !schema) {
      throw new Error(`工作台组件 "${componentType}" 未正确注册，请检查 WorkbenchComp 和 schema 定义`);
    }

    acc[componentType] = { component, schema };
    return acc;
  }, {} as Record<WorkbenchComponentType, WorkbenchComponentDefinition>);

