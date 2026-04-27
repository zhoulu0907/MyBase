import type { ComponentType } from 'react';

import { WorkbenchAdvancedComp } from '../WorkbenchAdvancedComponents';
import { WorkbenchBasicComp } from '../WorkbenchBasicComponents';
import { workbenchSchema } from '../schema/schema';
import { WORKBENCH_COMPONENT_TYPE_VALUES, type WorkbenchComponentType } from '../core/componentTypes';

type WorkbenchSchemaCollection = typeof workbenchSchema;

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

const workbenchComponentImplementations = {
  ...WorkbenchBasicComp,
  ...WorkbenchAdvancedComp
};

export const WORKBENCH_COMPONENT_REGISTRY: Record<WorkbenchComponentType, WorkbenchComponentDefinition> =
  WORKBENCH_COMPONENT_TYPE_VALUES.reduce((acc, componentType) => {
    const component =
      workbenchComponentImplementations[componentType as keyof typeof workbenchComponentImplementations];
    const schema = workbenchSchema[componentType as keyof typeof workbenchSchema] as WorkbenchComponentSchema;
    if (!component || !schema) {
      throw new Error(`工作台组件 "${componentType}" 未正确注册，请检查组件实现和 schema 定义`);
    }

    acc[componentType] = { component, schema };
    return acc;
  }, {} as Record<WorkbenchComponentType, WorkbenchComponentDefinition>);

