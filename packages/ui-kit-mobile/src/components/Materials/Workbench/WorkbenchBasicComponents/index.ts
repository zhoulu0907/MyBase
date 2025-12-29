import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '@onebase/ui-kit';
import XQuickEntry from './QuickEntry';
import XRichTextEditorWorkbench from './RichTextEditorWorkbench';
import XButtonWorkbench from './ButtonWorkbench';

export const WorkbenchBasicComp: Record<string, ComponentType<any>> = {
  XQuickEntry,
  XRichTextEditorWorkbench,
  XButtonWorkbench
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

