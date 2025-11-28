import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../componentTypes';
import XQuickEntry from './QuickEntry';
import XTodoCenter from './TodoCenter';

export const WorkbenchComp = {
  XQuickEntry,
  XTodoCenter
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

