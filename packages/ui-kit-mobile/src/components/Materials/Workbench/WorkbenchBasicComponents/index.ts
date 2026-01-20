import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '@onebase/ui-kit';
import XQuickEntry from './QuickEntry';
import XRichTextEditorWorkbench from './RichTextEditorWorkbench';
import XButtonWorkbench from './ButtonWorkbench';
import XWelcomeCard from './WelcomeCard';
import XTodoCenter from './TodoCenter';
import XTodoList from './TodoList';

export const WorkbenchBasicComp: Record<string, ComponentType<any>> = {
  XQuickEntry,
  XRichTextEditorWorkbench,
  XButtonWorkbench,
  XWelcomeCard,
  XTodoCenter,
  XTodoList
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

