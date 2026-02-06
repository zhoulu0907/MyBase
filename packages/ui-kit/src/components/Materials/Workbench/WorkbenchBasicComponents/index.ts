import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../core/componentTypes';
import XQuickEntry from './QuickEntry';
import XTodoCenter from './TodoCenter';
import XRichTextEditorWorkbench from './RichTextEditorWorkbench';
import XInformationList from './InformationList';
import XTodoList from './TodoList';
import XWelcomeCard from './WelcomeCard';
import XButtonWorkbench from './ButtonWorkbench';
import XImageWorkbench from './ImageWorkbench';

export const WorkbenchBasicComp = {
  XQuickEntry,
  XTodoCenter,
  XRichTextEditorWorkbench,
  XInformationList,
  XTodoList,
  XWelcomeCard,
  XButtonWorkbench,
  XImageWorkbench
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

