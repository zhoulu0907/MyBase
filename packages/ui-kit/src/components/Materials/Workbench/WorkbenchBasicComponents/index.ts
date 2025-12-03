import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../componentTypes';
import XQuickEntry from './QuickEntry';
import XTodoCenter from './TodoCenter';
import XRichTextEditorWorkbench from './RichTextEditorWorkbench';

export const WorkbenchBasicComp = {
  XQuickEntry,
  XTodoCenter,
  XRichTextEditorWorkbench
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

export { QUICK_ENTRY_THEME_OPTIONS, QUICK_ENTRY_THEME_VALUES } from './constants';

